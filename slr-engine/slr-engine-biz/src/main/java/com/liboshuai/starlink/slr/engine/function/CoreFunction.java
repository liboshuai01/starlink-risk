package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.api.dto.*;
import com.liboshuai.starlink.slr.engine.api.enums.RuleStatusEnum;
import com.liboshuai.starlink.slr.engine.processor.Processor;
import com.liboshuai.starlink.slr.engine.common.ParameterConstants;
import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
import com.liboshuai.starlink.slr.engine.exception.BusinessException;
import com.liboshuai.starlink.slr.engine.utils.jdbc.JdbcUtil;
import com.liboshuai.starlink.slr.engine.utils.parameter.ParameterUtil;
import com.liboshuai.starlink.slr.engine.utils.string.JsonUtil;
import groovy.lang.GroovyClassLoader;
import io.debezium.data.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.CollectionUtil;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.liboshuai.starlink.slr.engine.common.StateDescContainer.*;

/**
 * 计算引擎核心function
 */
@Slf4j
public class CoreFunction extends KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String> {

    private static final long serialVersionUID = -5913085790319815064L;

    /**
     * 运算机map：key-规则编号，value-运算机
     */
    private Map<String, Processor> processorByRuleCodeMap;

    /**
     * groovy加载器
     */
    private GroovyClassLoader groovyClassLoader;

    /**
     * 最近15分钟时间事件数据缓存
     */
    private ListState<EventKafkaDTO> recentEventListState;

    /**
     * 旧规则列表
     */
    private MapState<String, Object> oldRuleListState;

    /**
     * 在线规则数量
     */
    private ValueState<Long> onlineRuleCountState;

    /**
     * 银行数据
     */
    private MapState<String, String> bankMapState;


    @Override
    public void open(Configuration parameters) throws Exception {
        processorByRuleCodeMap = new ConcurrentHashMap<>();
        groovyClassLoader = new GroovyClassLoader();
        RECENT_EVENT_LIST_STATE_DESC
                .enableTimeToLive(StateTtlConfig.newBuilder(Time.minutes(15)).neverReturnExpired().build());
        recentEventListState = getRuntimeContext().getListState(RECENT_EVENT_LIST_STATE_DESC);
        oldRuleListState = getRuntimeContext().getMapState(OLD_RULE_MAP_STATE_DESC);
        onlineRuleCountState = getRuntimeContext().getState(ONLINE_RULE_COUNT_STATE_DESC);
        bankMapState = getRuntimeContext().getMapState(BANK_MAP_STATE_DESC);
        // 查询在线规则数量
        queryOnlineRuleCount();
        // 查询银行数据
        queryBank();
    }

    @Override
    public void processElement(EventKafkaDTO eventKafkaDTO,
                               KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.ReadOnlyContext ctx,
                               Collector<String> out) throws Exception {
        log.warn("事件处理流-eventKafkaDTO: {}", JsonUtil.toJsonString(eventKafkaDTO));
        // 等待所有运算机初始化完成
        waitForInitAllProcessor();
        // 将事件放入缓存列表中
        recentEventListState.add(eventKafkaDTO);
        // 数据遍历经过每个规则运算机
        for (Map.Entry<String, Processor> stringProcessorEntry : processorByRuleCodeMap.entrySet()) {
            String ruleCode = stringProcessorEntry.getKey();
            Processor processor = stringProcessorEntry.getValue();
            if (processor.isCrossHistory() && !oldRuleListState.contains(ruleCode)) {
                // 若需要跨历史且为新规则，则需要先将历史事件数据处理完毕
                for (EventKafkaDTO historyEventKafkaDTO : recentEventListState.get()) {
                    processor.process(historyEventKafkaDTO, out);
                }
            } else {
                // 否则直接处理当前一条事件数据即可
                processor.process(eventKafkaDTO, out);
            }
        }
    }

    private void waitForInitAllProcessor() throws IOException, InterruptedException {
        long currentOnlineRuleCount = getCurrentOnlineRuleCount();
        while (true) {
            log.warn("事件处理流-onlineRuleCount:{}, currentOnlineRuleCount: {}",
                    onlineRuleCountState.value(), currentOnlineRuleCount);
            if (onlineRuleCountState.value() == currentOnlineRuleCount) {
                break;
            }
            TimeUnit.SECONDS.sleep(1);
            queryOnlineRuleCount();
            currentOnlineRuleCount = getCurrentOnlineRuleCount();
        }
    }

    /**
     * 获取当前在线规则的数量
     */
    private long getCurrentOnlineRuleCount() {
        Set<String> ruleCodeCount = processorByRuleCodeMap.keySet();
        return ruleCodeCount.size();
    }

    @Override
    public void processBroadcastElement(RuleCdcDTO ruleCdcDTO,
                                        KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.Context ctx,
                                        Collector<String> out) throws Exception {
        log.warn("processBroadcastElement 数据: {}", JsonUtil.toJsonString(ruleCdcDTO));
        if (ruleCdcDTO == null) {
            throw new BusinessException("Mysql Cdc 广播流 ruleCdcDTO 必须非空");
        }
        // cdc数据更新
        String op = ruleCdcDTO.getOp();
        RuleJsonDTO ruleCdcDTOAfter = ruleCdcDTO.getAfter();
        String ruleCode = ruleCdcDTOAfter.getRuleCode();
        String ruleJson = ruleCdcDTOAfter.getRuleJson();
        RuleInfoDTO ruleInfoDTO = JsonUtil.parseObject(ruleJson, RuleInfoDTO.class);
        if (Objects.isNull(ruleInfoDTO)) {
            throw new BusinessException("Mysql Cdc 广播流 ruleInfoDTO 必须非空");
        }
        if ((Objects.equals(op, Envelope.Operation.READ.code()) || Objects.equals(op, Envelope.Operation.CREATE.code())
                || Objects.equals(op, Envelope.Operation.UPDATE.code()))
                && Objects.equals(ruleInfoDTO.getStatus(), RuleStatusEnum.ENABLE.getCode())) {
            // 在读取、创建、更新，且状态为上线时，则上线一个运算机
            Processor processor = buildProcessor(ruleInfoDTO, ctx);
            processorByRuleCodeMap.put(ruleCode, processor);
            log.warn("上线或更新一个运算机，规则编号为:{}", ruleCode);
        } else if (Objects.equals(op, Envelope.Operation.UPDATE.code())
                && Objects.equals(ruleInfoDTO.getStatus(), RuleStatusEnum.DISABLE.getCode())) {
            // 在更新，且状态为下线时，则下线一个运算机
            processorByRuleCodeMap.remove(ruleCode);
            log.warn("下线一个运算机，规则编号为:{}", ruleCode);
        }
    }

    /**
     * 构造运算机对象
     */
    private Processor buildProcessor(RuleInfoDTO ruleInfoDTO,
                                     KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.Context ctx)
            throws InstantiationException {
        RuleModelDTO ruleModelDTO = ruleInfoDTO.getRuleModel();
        if (Objects.isNull(ruleModelDTO)) {
            throw new BusinessException("运算机模型 ruleModelDTO 必须非空");
        }
        String ruleModel = ruleModelDTO.getRuleModel();
        Class aClass = groovyClassLoader.parseClass(ruleModel);
        Processor processor;
        try {
            processor = (Processor) aClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        processor.init(ctx, ruleInfoDTO);
        return processor;
    }

    /**
     * 查询上线的规则数量
     */
    private void queryOnlineRuleCount() throws IOException {
        // 获取规则表名
        String tableName = ParameterUtil.getParameters().get(ParameterConstants.MYSQL_TABLE_RULE_COUNT);
        // 查询规则数据
        String sql = "select rule_count from " + tableName + " where deleted = 0";
        RuleCountDTO ruleCountDTO = JdbcUtil.queryOne(sql, new JdbcUtil.BeanPropertyRowMapper<>(RuleCountDTO.class));
        if (Objects.isNull(ruleCountDTO)) {
            throw new BusinessException("Mysql Jdbc 查询上线的规则数量为空！");
        }
        log.warn("Mysql Jdbc 查询上线的规则数量: {}", ruleCountDTO.getRuleCount());
        onlineRuleCountState.update(ruleCountDTO.getRuleCount());
    }

    /**
     * 查询银行数据
     */
    private void queryBank() throws Exception {
        // 获取规则表名
        String tableName = ParameterUtil.getParameters().get(ParameterConstants.MYSQL_TABLE_BANK);
        // 查询规则数据
        String sql = "select bank, name from " + tableName + " where is_deleted = 0";
        List<BankDTO> bankDTOList = JdbcUtil.queryList(sql, new JdbcUtil.BeanPropertyRowMapper<>(BankDTO.class));
        if (CollectionUtil.isNullOrEmpty(bankDTOList)) {
            log.warn("Mysql Jdbc 预加载的银行对象集合bankDTOList为空！");
            return;
        }
        log.warn("Mysql Jdbc 预加载的银行对象集合bankDTOList: {}", JsonUtil.toJsonString(bankDTOList));
        for (BankDTO bankDTO : bankDTOList) {
            bankMapState.put(bankDTO.getBank(), bankDTO.getName());
        }
    }
}