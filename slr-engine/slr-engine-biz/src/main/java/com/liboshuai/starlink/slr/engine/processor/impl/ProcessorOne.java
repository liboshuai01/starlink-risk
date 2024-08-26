//package com.liboshuai.starlink.slr.engine.processor.impl;
//
//import com.liboshuai.starlink.slr.engine.api.constants.GlobalConstants;
//import com.liboshuai.starlink.slr.engine.api.constants.RedisKeyConstants;
//import com.liboshuai.starlink.slr.engine.api.dto.EventKafkaDTO;
//import com.liboshuai.starlink.slr.engine.api.dto.RuleConditionDTO;
//import com.liboshuai.starlink.slr.engine.api.dto.RuleInfoDTO;
//import com.liboshuai.starlink.slr.engine.api.enums.RuleConditionOperatorTypeEnum;
//import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
//import com.liboshuai.starlink.slr.engine.exception.BusinessException;
//import com.liboshuai.starlink.slr.engine.processor.Processor;
//import com.liboshuai.starlink.slr.engine.utils.data.RedisUtil;
//import com.liboshuai.starlink.slr.engine.utils.date.DateUtil;
//import com.liboshuai.starlink.slr.engine.utils.string.StringUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.api.common.state.MapState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.state.ValueState;
//import org.apache.flink.api.common.state.ValueStateDescriptor;
//import org.apache.flink.api.common.typeinfo.Types;
//import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
//import org.apache.flink.util.CollectionUtil;
//import org.apache.flink.util.Collector;
//import org.apache.flink.util.StringUtils;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
///**
// * 运算机one
// */
//@Slf4j
//public class ProcessorOne implements Processor {
//
//    /**
//     * 规则信息
//     */
//    private ValueState<RuleInfoDTO> ruleInfoDTOValueState;
//
//    /**
//     * smallValue（窗口步长）: key为eventCode,value为eventValue
//     */
//    private MapState<String, Long> smallMapState;
//
//    /**
//     * bigValue（窗口大小）: key为eventCode，value为一个一个步长的eventValue累加值
//     */
//    private MapState<String, List<Long>> bigMapState;
//
//    /**
//     * 最近一次预警时间
//     */
//    private ValueState<Long> lastWarningTimeState;
//
//    @Override
//    public void open(RuntimeContext runtimeContext, RuleInfoDTO ruleInfoDTO) {
//        String ruleCode = ruleInfoDTO.getRuleCode();
//        ruleInfoDTOValueState = runtimeContext.getState(
//                new ValueStateDescriptor<>("ruleInfoDTOValueState_" + ruleCode, RuleInfoDTO.class)
//        );
//        smallMapState = runtimeContext.getMapState(
//                new MapStateDescriptor<>("smallMapState_" + ruleCode, String.class, Long.class)
//        );
//        bigMapState = runtimeContext.getMapState(
//                new MapStateDescriptor<>("bigMapState_" + ruleCode, Types.STRING, Types.LIST(Types.LONG))
//        );
//        lastWarningTimeState = runtimeContext.getState(
//                new ValueStateDescriptor<>("lastWarningTimeState_" + ruleCode, Long.class)
//        );
//    }
//
//    @Override
//    public void processElement(EventKafkaDTO eventKafkaDTO, Collector<String> out) throws Exception {
//        RuleInfoDTO ruleInfoDTO = ruleInfoDTOValueState.value();
//        if (Objects.isNull(ruleInfoDTO)) {
//            throw new BusinessException("运算机 ruleInfoDTO 必须非空");
//        }
//        // 获取当前事件时间戳
//        String timestamp = eventKafkaDTO.getTimestamp();
//        LocalDateTime eventTime = DateUtil.convertTimestamp2LocalDateTime(Long.parseLong(timestamp));
//        // 获取规则条件
//        List<RuleConditionDTO> ruleConditionList = ruleInfoDTO.getRuleConditionGroup();
//        if (CollectionUtil.isNullOrEmpty(ruleConditionList)) {
//            throw new BusinessException("运算机 ruleConditionList 必须非空");
//        }
//        // 多个规则条件进行窗口值累加
//        for (RuleConditionDTO ruleConditionDTO : ruleConditionList) {
//            // 划分为跨历史时间段 和 不跨历史时间段
//            if (ruleConditionDTO.getIsCrossHistory()) { // 跨历史时间段
//                LocalDateTime crossHistoryTimeline = ruleConditionDTO.getCrossHistoryTimeline();
//                // 匹配到事件时，进行事件值累加
//                if (Objects.equals(eventKafkaDTO.getEventCode(), ruleConditionDTO.getEventCode())
//                        && eventTime.isAfter(crossHistoryTimeline)) {
//                    if (smallMapState.get(eventKafkaDTO.getEventCode()) == null) {
//                        // 跨历史时间段，当状态值为空时从redis获取初始值
//                        String key = RedisKeyConstants.DORIS_HISTORY_VALUE
//                                + GlobalConstants.REDIS_KEY_SEPARATOR + ruleConditionDTO.getRuleCode()
//                                + GlobalConstants.REDIS_KEY_SEPARATOR + ruleConditionDTO.getEventCode();
//                        String keyCode = eventKafkaDTO.getKeyCode();
//                        String initValue = RedisUtil.hget(key, keyCode);
//                        if (StringUtils.isNullOrWhitespaceOnly(initValue)) {
//                            throw new BusinessException(StringUtil.format("从redis获取初始值必须非空, key:{}, hashKey: {}", key, keyCode));
//                        }
//                        smallMapState.put(eventKafkaDTO.getEventCode(), Long.parseLong(initValue));
//                    }
//                    smallMapState.put(eventKafkaDTO.getEventCode(),
//                            smallMapState.get(eventKafkaDTO.getEventCode()) + Long.parseLong(eventKafkaDTO.getEventValue()));
//                }
//            } else { // 非跨历史时间段
//                // 匹配到事件时，进行事件值累加
//                if (Objects.equals(eventKafkaDTO.getEventCode(), ruleConditionDTO.getEventCode())) {
//                    if (smallMapState.get(eventKafkaDTO.getEventCode()) == null) {
//                        // 非跨历史时间段，当状态值为空时直接初始化为0
//                        smallMapState.put(eventKafkaDTO.getEventCode(), 0L);
//                    }
//                    smallMapState.put(eventKafkaDTO.getEventCode(),
//                            smallMapState.get(eventKafkaDTO.getEventCode()) + Long.parseLong(eventKafkaDTO.getEventValue()));
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onTimer(long timestamp, KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
//        RuleInfoDTO ruleInfoDTO = ruleInfoDTOValueState.value();
//        if (Objects.isNull(ruleInfoDTO)) {
//            throw new BusinessException("运算机 ruleInfoDTO 必须非空");
//        }
//        // 获取规则条件
//        List<RuleConditionDTO> ruleConditionList = ruleInfoDTO.getRuleConditionGroup();
//        if (CollectionUtil.isNullOrEmpty(ruleConditionList)) {
//            throw new BusinessException("运算机 ruleConditionList 必须非空");
//        }
//        // 将规则条件根据事件编号存储到map中，方便后续操作
//        Map<String, RuleConditionDTO> ruleConditionMapByEventCode = new HashMap<>();
//        for (RuleConditionDTO ruleConditionDTO : ruleConditionList) {
//            ruleConditionMapByEventCode.put(ruleConditionDTO.getEventCode(), ruleConditionDTO);
//        }
//        // 将smallMapState的值临时转移到普通的smallMap中，方便数据操作
//        Map<String, Long> smallMap = new HashMap<>();
//        Iterator<Map.Entry<String, Long>> smallIterator = smallMapState.iterator();
//        while (smallIterator.hasNext()) {
//            Map.Entry<String, Long> next = smallIterator.next();
//            smallMap.put(next.getKey(), next.getValue());
//        }
//        // 将bigMapState的值临时转移到普通的bigMap中，方便数据操作
//        Map<String, List<Long>> bigMap = new HashMap<>();
//        Iterator<Map.Entry<String, List<Long>>> bigIterator = bigMapState.iterator();
//        while (bigIterator.hasNext()) {
//            Map.Entry<String, List<Long>> next = bigIterator.next();
//            bigMap.put(next.getKey(), next.getValue());
//        }
//        // 将每个事件窗口步长数据集累加的值，添加到窗口大小数据集中bigMap中
//        for (Map.Entry<String, Long> smallMapEntry : smallMap.entrySet()) {
//            String eventCode = smallMapEntry.getKey();
//            Long eventValue = smallMapEntry.getValue();
//            List<Long> oldEventValueList = bigMap.get(eventCode);
//            if (CollectionUtil.isNullOrEmpty(oldEventValueList)) {
//                oldEventValueList = new ArrayList<>();
//            }
//            oldEventValueList.add(eventValue);
//            bigMap.put(eventCode, oldEventValueList);
//        }
//        // 当前窗口步长的数据已经添加到窗口中了，清空状态
//        smallMapState.clear();
//        // 清理窗口大小之外的数据
//        for (Map.Entry<String, List<Long>> bigMapEntry : bigMap.entrySet()) {
//            String eventCode = bigMapEntry.getKey();
//            List<Long> eventValueList = bigMapEntry.getValue();
//            Long windowSize = ruleConditionMapByEventCode.get(eventCode).getWindowSize();
//            if (eventValueList.size() > windowSize) {
//                eventValueList = eventValueList.subList(eventValueList.size() - 20, eventValueList.size());
//            }
//            bigMap.put(eventCode, eventValueList);
//        }
//        // 将bigMap更新到bigMapState中
//        for (Map.Entry<String, List<Long>> bigMapEntry : bigMap.entrySet()) {
//            String eventCode = bigMapEntry.getKey();
//            List<Long> eventValueList = bigMapEntry.getValue();
//            bigMapState.put(eventCode, eventValueList);
//        }
//        // 判断是否触发规则事件阈值
//        Map<String, Boolean> eventCodeAndWarnResult = new HashMap<>();
//        for (Map.Entry<String, List<Long>> bigMapEntry : bigMap.entrySet()) {
//            String eventCode = bigMapEntry.getKey();
//            List<Long> eventValueList = bigMapEntry.getValue();
//            long eventValueSum = eventValueList.stream().mapToLong(Long::longValue).sum();
//            Long eventThreshold = ruleConditionMapByEventCode.get(eventCode).getEventThreshold();
//            eventCodeAndWarnResult.put(eventCode, eventValueSum > eventThreshold);
//        }
//        Integer conditionOperator = ruleInfoDTO.getCombinedConditionOperator();
//        // 根据规则中事件条件表达式组合判断事件结果 与预警频率 判断否是触发预警
//        boolean eventResult = evaluateEventResults(eventCodeAndWarnResult, conditionOperator);
//        if (eventResult && (timestamp - lastWarningTimeState.value() >= ruleInfoDTO.getWarnInterval())) {
//            lastWarningTimeState.update(timestamp);
//            // TODO: 进行预警信息拼接组合
//            out.collect("事件[{}]触发了[{}]规则，事件值超过阈值[{}]，请尽快处理");
//        }
//    }
//
//    public static boolean evaluateEventResults(Map<String, Boolean> eventCodeAndWarnResult, Integer conditionOperator) {
//        // 初始化结果变量
//        boolean result = conditionOperator.equals(RuleConditionOperatorTypeEnum.AND.getCode());
//
//        // 遍历 Map
//        for (Boolean eventResult : eventCodeAndWarnResult.values()) {
//            if (conditionOperator.equals(RuleConditionOperatorTypeEnum.AND.getCode())) {
//                // 对于 AND，只有当所有结果都为 true 时，结果才为 true
//                result = eventResult;
//                // 提前结束循环，如果结果已经为 false
//                if (!result) {
//                    break;
//                }
//            } else if (conditionOperator.equals(RuleConditionOperatorTypeEnum.OR.getCode())) {
//                // 对于 OR，只要有一个结果为 true，结果就为 true
//                result = eventResult;
//                // 提前结束循环，如果结果已经为 true
//                if (result) {
//                    break;
//                }
//            }
//        }
//
//        return result;
//    }
//}
