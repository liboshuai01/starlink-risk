package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.api.dto.*;
import com.liboshuai.starlink.slr.engine.api.enums.RuleStatusEnum;
import com.liboshuai.starlink.slr.engine.common.ParameterConstants;
import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
import com.liboshuai.starlink.slr.engine.exception.BusinessException;
import com.liboshuai.starlink.slr.engine.processor.Processor;
import com.liboshuai.starlink.slr.engine.processor.impl.ProcessorOne;
import com.liboshuai.starlink.slr.engine.utils.jdbc.JdbcUtil;
import com.liboshuai.starlink.slr.engine.utils.parameter.ParameterUtil;
import com.liboshuai.starlink.slr.engine.utils.string.JsonUtil;
import groovy.lang.GroovyClassLoader;
import io.debezium.data.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.CollectionUtil;
import org.apache.flink.util.Collector;
import org.apache.flink.util.StringUtils;

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
    private Long onlineRuleCount;

    /**
     * 银行数据
     */
    private Map<String, String> bankMapState;

    /**
     * 注意千万不要在open方法中对状态进行赋值操作，因为在processElement等方法中并不能获取到
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        log.warn("调用一次open方法");
        processorByRuleCodeMap = new ConcurrentHashMap<>();
        groovyClassLoader = new GroovyClassLoader();
        RECENT_EVENT_LIST_STATE_DESC
                .enableTimeToLive(StateTtlConfig.newBuilder(Time.minutes(15)).neverReturnExpired().build());
        recentEventListState = getRuntimeContext().getListState(RECENT_EVENT_LIST_STATE_DESC);
        oldRuleListState = getRuntimeContext().getMapState(OLD_RULE_MAP_STATE_DESC);
        // 查询在线规则数量
        onlineRuleCount = queryOnlineRuleCount();
        // 查询银行数据
        bankMapState = queryBank();
    }

    /**
     * TODO：测试的时候先不使用groovy，还是直接使用java代码，方便调试
     */
    @Override
    public void processElement(EventKafkaDTO eventKafkaDTO,
                               KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.ReadOnlyContext ctx,
                               Collector<String> out) throws Exception {
        log.warn("调用一次processElement方法, eventKafkaDTO: {}, out: {}", eventKafkaDTO, out);
        // 等待所有运算机初始化完成
        waitForInitAllProcessor();
        // 将事件放入缓存列表中
        recentEventListState.add(eventKafkaDTO);
        // 数据遍历经过每个规则运算机
        for (Map.Entry<String, Processor> stringProcessorEntry : processorByRuleCodeMap.entrySet()) {
            String ruleCode = stringProcessorEntry.getKey();
            Processor processor = stringProcessorEntry.getValue();
            if (!oldRuleListState.contains(ruleCode)) {
                // 新规则需要先将缓存的最近历史事件数据处理一遍
                for (EventKafkaDTO historyEventKafkaDTO : recentEventListState.get()) {
                    processor.processElement(historyEventKafkaDTO, out);
                }
                oldRuleListState.put(ruleCode, null);
            } else {
                // 否则直接处理当前一条事件数据即可
                processor.processElement(eventKafkaDTO, out);
            }
        }
        // 注册定时器（窗口大小1分钟）
        // long fireTime = Long.parseLong(timestamp) - Long.parseLong(timestamp) % 60000 + 60000; （简化写法）
        long fireTime = getWindowStartWithOffset(ctx.timestamp(), 0, 60 * 1000) + 60 * 1000;
        log.warn("ctx.timestamp(): {}, fireTime: {}", ctx.timestamp(), fireTime);
        ctx.timerService().registerProcessingTimeTimer(fireTime);
    }

    @Override
    public void processBroadcastElement(RuleCdcDTO ruleCdcDTO,
                                        KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.Context ctx,
                                        Collector<String> out) throws Exception {
        log.warn("调用一次processBroadcastElement方法, ruleCdcDTO: {}, out: {}", ruleCdcDTO, out);
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
            Processor processor = mockProcessor(getRuntimeContext(), ruleInfoDTO);
            processorByRuleCodeMap.put(ruleCode, processor);
            log.warn("上线或更新一个运算机，规则编号为:{}", ruleCode);
        } else if (Objects.equals(op, Envelope.Operation.UPDATE.code())
                && Objects.equals(ruleInfoDTO.getStatus(), RuleStatusEnum.DISABLE.getCode())) {
            // 在更新，且状态为下线时，则下线一个运算机
            processorByRuleCodeMap.remove(ruleCode);
            log.warn("下线一个运算机，规则编号为:{}", ruleCode);
        }
        log.warn("运算机map, processorByRuleCodeMap: {}", processorByRuleCodeMap);
    }

    @Override
    public void onTimer(long timestamp,
                        KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.OnTimerContext ctx,
                        Collector<String> out) throws Exception {
        log.warn("调用一次onTimer方法, timestamp: {}, out: {}", timestamp, out);
        // 数据遍历经过每个规则运算机
        for (Map.Entry<String, Processor> stringProcessorEntry : processorByRuleCodeMap.entrySet()) {
            String ruleCode = stringProcessorEntry.getKey();
            Processor processor = stringProcessorEntry.getValue();
            // 调用定时器
            processor.onTimer(timestamp, out);
        }
    }

    /**
     * 构造运算机对象
     */
    private Processor buildProcessor(RuntimeContext runtimeContext, RuleInfoDTO ruleInfoDTO)
            throws InstantiationException, IllegalAccessException, IOException {
        String ruleModelGroovyCode = ruleInfoDTO.getRuleModelGroovyCode();
        if (StringUtils.isNullOrWhitespaceOnly(ruleModelGroovyCode)) {
            throw new BusinessException("运算机模型代码 ruleModelGroovyCode 必须非空");
        }
        Class aClass = groovyClassLoader.parseClass(ruleModelGroovyCode);
        Processor processor = (Processor) aClass.newInstance();
        processor.open(runtimeContext, ruleInfoDTO);
        return processor;
    }

    /**
     * mock运算机对象
     */
    private Processor mockProcessor(RuntimeContext runtimeContext, RuleInfoDTO ruleInfoDTO)
            throws InstantiationException, IllegalAccessException, IOException {
        Processor processor = new ProcessorOne();
        processor.open(runtimeContext, ruleInfoDTO);
        return processor;
    }

    /**
     * 查询上线的规则数量
     */
    private Long queryOnlineRuleCount() throws IOException {
        // 获取规则表名
        String tableName = ParameterUtil.getParameters().get(ParameterConstants.MYSQL_TABLE_RULE_COUNT);
        // 查询规则数据
        String sql = "select online_count from " + tableName + " where deleted = 0";
        RuleOnlineCountDTO ruleOnlineCountDTO = JdbcUtil.queryOne(
                sql, new JdbcUtil.BeanPropertyRowMapper<>(RuleOnlineCountDTO.class)
        );
        if (Objects.isNull(ruleOnlineCountDTO)) {
            throw new BusinessException("Mysql Jdbc 查询上线的规则数量为空！");
        }
        log.warn("Mysql Jdbc 查询上线的规则数量: {}", ruleOnlineCountDTO.getOnlineCount());
        return ruleOnlineCountDTO.getOnlineCount();
    }

    /**
     * 查询银行数据
     */
    private Map<String, String> queryBank() throws Exception {
        Map<String, String> bankMap = new HashMap<>();
        // 获取规则表名
        String tableName = ParameterUtil.getParameters().get(ParameterConstants.MYSQL_TABLE_BANK);
        // 查询规则数据
        String sql = "select bank, name from " + tableName + " where is_deleted = 0";
        List<BankDTO> bankDTOList = JdbcUtil.queryList(sql, new JdbcUtil.BeanPropertyRowMapper<>(BankDTO.class));
        if (CollectionUtil.isNullOrEmpty(bankDTOList)) {
            log.warn("Mysql Jdbc 预加载的银行对象集合bankDTOList为空！");
            return new HashMap<>();
        }
        log.warn("Mysql Jdbc 预加载的银行对象集合bankDTOList: {}", JsonUtil.toJsonString(bankDTOList));
        for (BankDTO bankDTO : bankDTOList) {
            bankMap.put(bankDTO.getBank(), bankDTO.getName());
        }
        return bankMap;
    }

    /**
     * 等待所有运算机初始化完成
     */
    private void waitForInitAllProcessor() throws IOException, InterruptedException {
        long currentOnlineRuleCount = getCurrentOnlineRuleCount();
        while (true) {
            log.warn("事件处理流-onlineRuleCount:{}, currentOnlineRuleCount: {}", onlineRuleCount, currentOnlineRuleCount);
            if (onlineRuleCount == currentOnlineRuleCount) {
                break;
            }
            TimeUnit.SECONDS.sleep(1);
            onlineRuleCount = queryOnlineRuleCount();
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

    private long getWindowStartWithOffset(long timestamp, long offset, long windowSize) {
        final long remainder = (timestamp - offset) % windowSize;
        // handle both positive and negative cases
        if (remainder < 0) {
            return timestamp - (remainder + windowSize);
        } else {
            return timestamp - remainder;
        }
    }
}