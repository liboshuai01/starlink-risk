package com.liboshuai.starlink.slr.engine.processor.impl;

import com.liboshuai.starlink.slr.engine.api.constants.GlobalConstants;
import com.liboshuai.starlink.slr.engine.api.constants.RedisKeyConstants;
import com.liboshuai.starlink.slr.engine.api.dto.EventKafkaDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleConditionDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleInfoDTO;
import com.liboshuai.starlink.slr.engine.api.enums.RuleConditionOperatorTypeEnum;
import com.liboshuai.starlink.slr.engine.exception.BusinessException;
import com.liboshuai.starlink.slr.engine.processor.Processor;
import com.liboshuai.starlink.slr.engine.utils.data.RedisUtil;
import com.liboshuai.starlink.slr.engine.utils.date.DateUtil;
import com.liboshuai.starlink.slr.engine.utils.string.JsonUtil;
import com.liboshuai.starlink.slr.engine.utils.string.StringUtil;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.util.CollectionUtil;
import org.apache.flink.util.Collector;
import org.apache.flink.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 运算机one
 */
public class ProcessorOne implements Processor {

    private static final Logger log = LoggerFactory.getLogger(ProcessorOne.class);

    /**
     * 规则信息
     */
    private RuleInfoDTO ruleInfo;

    /**
     * smallValue（窗口步长）: key为eventCode,value为eventValue
     */
    private MapState<String, Long> smallMapState;

    /**
     * bigValue（窗口大小）: key为eventCode，小map的key为时间戳，小map的value为一个一个步长的eventValue累加值
     */
    private MapState<String, Map<Long, Long>> bigMapState;

    /**
     * 最近一次预警时间
     */
    private ValueState<Long> lastWarningTimeState;

    /**
     * 注意千万不要在open方法中对状态进行赋值操作，否则进行的赋值，在processElement等方法中并不能获取到
     */
    @Override
    public void open(RuntimeContext runtimeContext, RuleInfoDTO ruleInfoDTO) throws IOException {
        log.warn("调用ProcessorOne对象的open方法, ruleInfoDTO={}", ruleInfoDTO);
        String ruleCode = ruleInfoDTO.getRuleCode();
        ruleInfo = ruleInfoDTO;
        smallMapState = runtimeContext.getMapState(
                new MapStateDescriptor<>("smallMapState_" + ruleCode, String.class, Long.class)
        );
        bigMapState = runtimeContext.getMapState(
                new MapStateDescriptor<>("bigMapState_" + ruleCode, Types.STRING, Types.MAP(Types.LONG, Types.LONG))
        );
        lastWarningTimeState = runtimeContext.getState(
                new ValueStateDescriptor<>("lastWarningTimeState_" + ruleCode, Long.class)
        );
    }

    @Override
    public void processElement(EventKafkaDTO eventKafkaDTO, Collector<String> out) throws Exception {
        // TODO: 调试使用，待删除
        log.warn("调用ProcessorOne对象的processElement方法, eventKafkaDTO={}, out={}", eventKafkaDTO, out);
        logSmallMapState(smallMapState, "processElement","after");
        logBigMapState(bigMapState, "processElement","after");
        if (Objects.isNull(ruleInfo)) {
            throw new BusinessException("运算机 ruleInfoDTO 必须非空");
        }
        // 获取当前事件时间戳
        LocalDateTime eventTime = DateUtil.convertTimestamp2LocalDateTime(System.currentTimeMillis());
        // 获取规则条件
        List<RuleConditionDTO> ruleConditionList = ruleInfo.getRuleConditionGroup();
        if (CollectionUtil.isNullOrEmpty(ruleConditionList)) {
            throw new BusinessException("运算机 ruleConditionList 必须非空");
        }
        // 多个规则条件进行窗口值累加
        for (RuleConditionDTO ruleConditionDTO : ruleConditionList) {
            // 划分为跨历史时间段 和 不跨历史时间段
            if (ruleConditionDTO.getIsCrossHistory()) { // 跨历史时间段
                LocalDateTime crossHistoryTimeline = ruleConditionDTO.getCrossHistoryTimeline();
                // 匹配到事件时，进行事件值累加
                if (Objects.equals(eventKafkaDTO.getEventCode(), ruleConditionDTO.getEventCode())
                        && eventTime.isAfter(crossHistoryTimeline)) {
                    if (smallMapState.get(eventKafkaDTO.getEventCode()) == null) {
                        // 跨历史时间段，当状态值为空时从redis获取初始值
                        String key = RedisKeyConstants.DORIS_HISTORY_VALUE
                                + GlobalConstants.REDIS_KEY_SEPARATOR + ruleConditionDTO.getRuleCode()
                                + GlobalConstants.REDIS_KEY_SEPARATOR + ruleConditionDTO.getEventCode();
                        String keyCode = eventKafkaDTO.getKeyCode();
                        String initValue = RedisUtil.hget(key, keyCode);
                        if (StringUtils.isNullOrWhitespaceOnly(initValue)) {
                            throw new BusinessException(StringUtil.format("从redis获取初始值必须非空, key:{}, hashKey: {}", key, keyCode));
                        }
                        smallMapState.put(eventKafkaDTO.getEventCode(), Long.parseLong(initValue));
                    }
                    smallMapState.put(eventKafkaDTO.getEventCode(),
                            smallMapState.get(eventKafkaDTO.getEventCode()) + Long.parseLong(eventKafkaDTO.getEventValue()));
                }
            } else { // 非跨历史时间段
                // 匹配到事件时，进行事件值累加
                if (Objects.equals(eventKafkaDTO.getEventCode(), ruleConditionDTO.getEventCode())) {
                    if (smallMapState.get(eventKafkaDTO.getEventCode()) == null) {
                        // 非跨历史时间段，当状态值为空时直接初始化为0
                        smallMapState.put(eventKafkaDTO.getEventCode(), 0L);
                    }
                    smallMapState.put(eventKafkaDTO.getEventCode(),
                            smallMapState.get(eventKafkaDTO.getEventCode()) + Long.parseLong(eventKafkaDTO.getEventValue()));
                }
            }
        }
        // TODO: 调试使用，待删除
        logSmallMapState(smallMapState, "processElement","before");
        logBigMapState(bigMapState, "processElement","before");
    }

    @Override
    public void onTimer(long timestamp, Collector<String> out) throws Exception {
        log.warn("调用ProcessorOne对象的onTimer方法, timestamp={}, out={}", timestamp, out);
        if (Objects.isNull(ruleInfo)) {
            throw new BusinessException("运算机 ruleInfoDTO 必须非空");
        }
        // TODO: 调试使用，待删除
        logSmallMapState(smallMapState, "onTimer", "before");
        logBigMapState(bigMapState, "onTimer","before");
        // 获取规则条件
        List<RuleConditionDTO> ruleConditionList = ruleInfo.getRuleConditionGroup();
        if (CollectionUtil.isNullOrEmpty(ruleConditionList)) {
            throw new BusinessException("运算机 ruleConditionList 必须非空");
        }
        // 将规则条件根据事件编号存储到map中，方便后续操作
        Map<String, RuleConditionDTO> ruleConditionMapByEventCode = new HashMap<>();
        for (RuleConditionDTO ruleConditionDTO : ruleConditionList) {
            ruleConditionMapByEventCode.put(ruleConditionDTO.getEventCode(), ruleConditionDTO);
        }
        // 将每个事件窗口步长数据集累加的值，添加到窗口大小数据集中bigMapState中
        updateBigMapWithSmallMap(timestamp);
        // 清理窗口大小之外的数据
        cleanupWindowData(timestamp, ruleConditionMapByEventCode);
        // 判断是否触发规则事件阈值
        boolean eventResult = evaluateEventThresholds(ruleConditionMapByEventCode);
        // 根据规则中事件条件表达式组合判断事件结果 与预警频率 判断否是触发预警
        if (lastWarningTimeState.value() == null) {
            lastWarningTimeState.update(0L);
        }
        if (eventResult && (timestamp - lastWarningTimeState.value() >= ruleInfo.getWarnInterval())) {
            lastWarningTimeState.update(timestamp);
            // TODO: 进行预警信息拼接组合
            log.warn("用户[{}]触发了[{}]规则，事件值超过阈值[{}]，请尽快处理");
            out.collect("事件[{}]触发了[{}]规则，事件值超过阈值[{}]，请尽快处理");
        }
        // TODO: 调试使用，待删除
        logSmallMapState(smallMapState, "onTimer","after");
        logBigMapState(bigMapState, "onTimer","after");
    }

    /**
     * 判断是否触发规则事件阈值
     */
    private boolean evaluateEventThresholds(Map<String, RuleConditionDTO> ruleConditionMapByEventCode) throws Exception {
        Map<String, Boolean> eventCodeAndWarnResult = new HashMap<>();
        for (Map.Entry<String, Map<Long, Long>> bigMapEntry : bigMapState.entries()) {
            String eventCode = bigMapEntry.getKey();
            Map<Long, Long> timestampAndEventValueMap = bigMapEntry.getValue();
            long eventValueSum = timestampAndEventValueMap.values().stream().mapToLong(Long::longValue).sum();
            Long eventThreshold = ruleConditionMapByEventCode.get(eventCode).getEventThreshold();
            eventCodeAndWarnResult.put(eventCode, eventValueSum > eventThreshold);
        }
        boolean eventResult = evaluateEventResults(eventCodeAndWarnResult, ruleInfo.getCombinedConditionOperator());
        return eventResult;
    }

    /**
     * 将每个事件窗口步长数据集累加的值，添加到窗口大小数据集中bigMapState中
     */
    private void updateBigMapWithSmallMap(long timestamp) throws Exception {
        for (Map.Entry<String, Long> smallMapEntry : smallMapState.entries()) {
            String eventCode = smallMapEntry.getKey();
            Long eventValue = smallMapEntry.getValue();
            Map<Long, Long> timestampAndEventValueMap = bigMapState.get(eventCode);
            if (CollectionUtil.isNullOrEmpty(timestampAndEventValueMap)) {
                timestampAndEventValueMap = new HashMap<>();
            }
            timestampAndEventValueMap.put(timestamp, eventValue);
            bigMapState.put(eventCode, timestampAndEventValueMap);
        }
        // 当前窗口步长的数据已经添加到窗口中了，清空状态
        smallMapState.clear();
    }

    /**
     * 清理窗口大小之外的数据
     */
    private void cleanupWindowData(long timestamp, Map<String, RuleConditionDTO> ruleConditionMapByEventCode) throws Exception {
        for (Map.Entry<String, Map<Long, Long>> bigMapEntry : bigMapState.entries()) {
            String eventCode = bigMapEntry.getKey();
            Map<Long, Long> timestampAndEventValueMap = bigMapEntry.getValue();
            Long windowSize = ruleConditionMapByEventCode.get(eventCode).getWindowSize();
            long twentyMinutesAgo = timestamp - windowSize;
            Iterator<Map.Entry<Long, Long>> iterator = timestampAndEventValueMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Long> next = iterator.next();
                Long time = next.getKey();
                if (time < twentyMinutesAgo) {
                    iterator.remove();
                }
            }
            bigMapState.put(eventCode, timestampAndEventValueMap);
        }
    }

    private void logSmallMapState(MapState<String, Long> smallMapState, String methodName, String type) throws Exception {
        Map<String, Long> smallMap = new HashMap<>();
        Iterator<Map.Entry<String, Long>> iterator = smallMapState.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> next = iterator.next();
            smallMap.put(next.getKey(), next.getValue());
        }
        log.warn("{}-{}-smallMap: {}", methodName, type, JsonUtil.toJsonString(smallMap));
    }

    private void logBigMapState(MapState<String, Map<Long, Long>> bigMapState, String methodName, String type) throws Exception {
        Map<String, Map<Long, Long>> bigMap = new HashMap<>();
        Iterator<Map.Entry<String, Map<Long, Long>>> iterator = bigMapState.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Map<Long, Long>> next = iterator.next();
            bigMap.put(next.getKey(), next.getValue());
        }
        log.warn("{}-{}-bigMap: {}", methodName, type, JsonUtil.toJsonString(bigMap));
    }

    /**
     * 评估事件结果，根据给定的条件操作符返回最终结果。
     * @param eventCodeAndWarnResult 包含事件代码及其对应的警告结果的映射
     * @param conditionOperator 条件操作符，支持 AND 和 OR
     * @return 根据条件操作符计算后的最终结果（true 或 false）
     */
    public static boolean evaluateEventResults(Map<String, Boolean> eventCodeAndWarnResult, Integer conditionOperator) {
        if (CollectionUtil.isNullOrEmpty(eventCodeAndWarnResult)) {
            return false;
        }
        if (CollectionUtil.isNullOrEmpty(eventCodeAndWarnResult.values())) {
            return false;
        }
        // 初始化结果变量，根据条件操作符判断初始值
        boolean result = conditionOperator.equals(RuleConditionOperatorTypeEnum.AND.getCode());

        // 遍历事件结果的 Map
        for (Boolean eventResult : eventCodeAndWarnResult.values()) {
            if (conditionOperator.equals(RuleConditionOperatorTypeEnum.AND.getCode())) {
                // 对于 AND，只有当所有结果都为 true 时，结果才为 true
                result = eventResult;
                // 提前结束循环，如果结果已经为 false
                if (!result) {
                    break;
                }
            } else if (conditionOperator.equals(RuleConditionOperatorTypeEnum.OR.getCode())) {
                // 对于 OR，只要有一个结果为 true，结果就为 true
                result = eventResult;
                // 提前结束循环，如果结果已经为 true
                if (result) {
                    break;
                }
            }
        }
        // 返回最终的评估结果
        return result;
    }

}
