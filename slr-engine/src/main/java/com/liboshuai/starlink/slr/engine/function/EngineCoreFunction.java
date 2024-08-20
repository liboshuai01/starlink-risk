package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlinkRisk.common.constants.RuleChannelConstants;
import com.liboshuai.starlinkRisk.common.constants.RuleScenarioConstants;
import com.liboshuai.starlinkRisk.common.constants.RuleStateConstants;
import com.liboshuai.starlinkRisk.common.pojo.RulePO;
import com.liboshuai.starlinkRisk.common.utils.json.JsonUtil;
import com.liboshuai.starlink.slr.engine.common.ParameterConstants;
import com.liboshuai.starlink.slr.engine.common.StateDescContainer;
import com.liboshuai.starlink.slr.engine.pojo.EventBean;
import com.liboshuai.starlink.slr.engine.pojo.RuleMeta;
import com.liboshuai.starlink.slr.engine.utils.jdbc.JdbcUtil;
import com.liboshuai.starlink.slr.engine.utils.log.ConsoleLogUtil;
import com.liboshuai.starlink.slr.engine.utils.parameter.ParameterUtil;
import com.liboshuai.starlink.slr.engine.utils.rule.RuleUtil;
import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 计算引擎核心
 */
@Slf4j
public class EngineCoreFunction extends KeyedBroadcastProcessFunction<Long, EventBean, RuleMeta, String> {
    /**
     * Groovy代码加载器
     */
    GroovyClassLoader groovyClassLoader;

    /**
     * 存储最近30s的历史行为数据
     */
    ListState<EventBean> eventsBuffer;

    /**
     * 存储非新规则
     */
    MapState<String, Object> notNewRules;

    /**
     * 运算机池
     */
    ConcurrentHashMap<String, RuleCalculator> ruleCalculatorPool = new ConcurrentHashMap<>();
    /**
     * 预加载规则map：key-场景，value-规则对象
     */
    private Map<String, RulePO> preloadRuleMap;


    /**
     * 初始化
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        // Groovy代码加载器
        groovyClassLoader = new GroovyClassLoader();
        // 存储最近30s的历史行为数据
        StateDescContainer.EVENTS_BUFFER_STATE_DES
                .enableTimeToLive(StateTtlConfig.newBuilder(Time.seconds(30))
                        .neverReturnExpired().build()); // 设置状态过期时间为30秒
        eventsBuffer = getRuntimeContext().getListState(StateDescContainer.EVENTS_BUFFER_STATE_DES);
        // 存储非新规则map
        notNewRules = getRuntimeContext().getMapState(StateDescContainer.NOT_NEW_RULES_DES);
        // 预加载规则
        preloadRule();
    }

    @Override
    public void processElement(EventBean eventBean,
                               KeyedBroadcastProcessFunction<Long, EventBean, RuleMeta, String>.ReadOnlyContext ctx,
                               Collector<String> out) throws Exception {
        // 如果运算机池为空，则对运算机池进行初始化
        if (ruleCalculatorPool.isEmpty()) {
            ReadOnlyBroadcastState<String, RuleMeta> ruleMetaReadOnlyBroadcastState =
                    ctx.getBroadcastState(StateDescContainer.RULE_METAS_DES);
            for (Map.Entry<String, RuleMeta> ruleMetaEntry : ruleMetaReadOnlyBroadcastState.immutableEntries()) {
                addRuleCalculatorPool(ruleMetaEntry.getKey(), ruleMetaEntry.getValue());
            }
        }

        // 将接收到的事件放入到事件缓存中
        eventsBuffer.add(eventBean);

        // 遍历运算机中的每一条规则
        for (Map.Entry<String, RuleCalculator> ruleCalculatorEntry : ruleCalculatorPool.entrySet()) {
            String ruleId = ruleCalculatorEntry.getKey();
            RuleCalculator ruleCalculator = ruleCalculatorEntry.getValue();

            // 判断：运算机是否为新上线的
            if (!notNewRules.contains(ruleId)) {
                // 判断：运算机是否需要补充历史数据
                if (ruleCalculator.isNeedHistoryEvents()) {
                    // 新的上线运算机 并且 需要补充历史数据
                    for (EventBean historyEventBean : eventsBuffer.get()) {
                        // 将缓存的历史数据，交给运算机池先计算一遍
                        ruleCalculator.calc(historyEventBean, out);
                    }
                } else {
                    // 新的上线运算机 但 不需要补充历史数据
                    // 直接计算新接收到的事件
                    ruleCalculator.calc(eventBean, out);
                }
                // 将补充完历史数据的运算机对象，放入到“非新”规则Map中
                notNewRules.put(ruleId, null);
            } else {
                // 非新上线的运算机
                // 直接计算新接收到的事件
                ruleCalculator.calc(eventBean, out);
            }
        }
    }

    @Override
    public void processBroadcastElement(RuleMeta ruleMeta,
                                        KeyedBroadcastProcessFunction<Long, EventBean, RuleMeta, String>.Context ctx,
                                        Collector<String> out) throws Exception {
        BroadcastState<String, RuleMeta> ruleMetaBroadcastState = ctx.getBroadcastState(StateDescContainer.RULE_METAS_DES);
        // 如果运算机池为空，则对运算机池进行初始化
        if (ruleCalculatorPool.isEmpty()) {
            for (Map.Entry<String, RuleMeta> ruleMetaEntry : ruleMetaBroadcastState.immutableEntries()) {
                addRuleCalculatorPool(ruleMetaEntry.getKey(), ruleMetaEntry.getValue());
            }
        }
        String ruleId = ruleMeta.getRule_id();
        String op = ruleMeta.getOp();
        int status = ruleMeta.getStatus();

        // 根据cdc接收到的规则元数据操作类型 + 规则状态值，对“运算机池”做增删改
        if ((Objects.equals(op, "c") || Objects.equals(op, "r") || Objects.equals(op, "u")) && status == 1) {
            // 更新“运算机池”中的“运算机”
            RuleCalculator ruleCalculator = ruleCalculatorPool.getOrDefault(ruleId, null);
            if (Objects.nonNull(ruleCalculator)) {
                // 如果运算机已经存在于“运算机池中”，则先下线
                ruleCalculator.offline();
            }
            addRuleCalculatorPool(ruleId, ruleMeta);
            ConsoleLogUtil.info("上线或更新了一个规则运算机: {}", ruleId);
            // 备份收到的新规则元数据，到广播状态中
            ruleMetaBroadcastState.put(ruleId, ruleMeta);
        } else if ((Objects.equals(op, "d") || (Objects.equals(op, "u") && status == 0))) {
            // 删除“运算机池”中的“运算机”
            RuleCalculator ruleCalculator = ruleCalculatorPool.get(ruleId);
            if (Objects.nonNull(ruleCalculator)) {
                // 如果运算机已经存在于“运算机池中”，则先下线
                ruleCalculator.offline();
            }
            ruleCalculatorPool.remove(ruleId);
            ConsoleLogUtil.info("下线了一个规则运算机: {}", ruleId);
            // 从广播状态中，移除备份的规则元数据对象
            ruleMetaBroadcastState.remove(ruleId);
        }
    }

    /**
     * 添加规则实例化对象到运算机池中
     */
    private void addRuleCalculatorPool(String ruleId, RuleMeta ruleMeta)
            throws InstantiationException, IllegalAccessException {
        // 根据规则元数据，构造规则运算机对象
        RuleCalculator ruleCalculator = RuleUtil.generateRuleCalculator(groovyClassLoader, ruleMeta);
        // 初始化运算机
        ruleCalculator.init(getRuntimeContext(),
                ruleMeta.getRule_param_json(), ruleMeta.getPre_select_users_bitmap());
        // 存放运算机到运算机池子中
        ruleCalculatorPool.put(ruleId, ruleCalculator);
    }

    /**
     * 预加载规则配置，防止广播流延迟
     */
    private void preloadRule() {
        preloadRuleMap = new HashMap<>();
        // 获取规则表名
        String tableName = ParameterUtil.getParameters().get(ParameterConstants.MYSQL_TABLE_RULE);
        // 查询规则数据
        String sql = "select * from " + tableName + " where channel = ? and scenario = ? and (state = ? or state = ?)";
        Optional<RulePO> optionalRulePO = JdbcUtil.queryOne(sql, new JdbcUtil.BeanPropertyRowMapper<>(RulePO.class),
                RuleChannelConstants.GAME, RuleScenarioConstants.GAME_LOTTERY,
                RuleStateConstants.ENABLE, RuleStateConstants.DEACTIVATE_REVIEW);
        if (!optionalRulePO.isPresent()) {
            ConsoleLogUtil.warning("Mysql Jdbc 预加载的规则对象rulePO为空！");
            return;
        }
        RulePO rulePO = optionalRulePO.get();
        ConsoleLogUtil.info("Mysql Jdbc 预加载的规则对象rulePO: {}", JsonUtil.obj2JsonStr(rulePO));
        preloadRuleMap.put(RuleScenarioConstants.GAME_LOTTERY, rulePO);
    }
}