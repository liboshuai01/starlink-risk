package com.liboshuai.starlink.slr.engine.processor.impl;

import com.liboshuai.starlink.slr.engine.api.dto.EventKafkaDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleConditionDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleInfoDTO;
import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
import com.liboshuai.starlink.slr.engine.exception.BusinessException;
import com.liboshuai.starlink.slr.engine.processor.Processor;
import com.liboshuai.starlink.slr.engine.utils.data.RedisUtil;
import com.liboshuai.starlink.slr.engine.utils.date.DateUtil;
import com.liboshuai.starlink.slr.engine.utils.string.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.CollectionUtil;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 运算机one
 */
@Slf4j
public class ProcessorOne implements Processor {

    private RedisUtil redisUtil;

    /**
     * 规则信息
     */
    private ValueState<RuleInfoDTO> ruleInfoDTOValueState;

    /**
     * smallValue
     */
    private ValueState<Long> smallValueState;

    /**
     * bigValue
     */
    private ListState<Long> bigListValue;


    @Override
    public void open(RuntimeContext runtimeContext, RuleInfoDTO ruleInfoDTO) {
        ruleInfoDTOValueState = runtimeContext.getState(
                new ValueStateDescriptor<>("ruleInfoDTOValueState", RuleInfoDTO.class)
        );
        smallValueState = runtimeContext.getState(
                new ValueStateDescriptor<>("smallMapState", Long.class)
        );
        bigListValue = runtimeContext.getListState(
                new ListStateDescriptor<>("smallValueState", Long.class)
        );
    }

    @Override
    public void process(EventKafkaDTO eventKafkaDTO, Collector<String> out) throws Exception {
        // TODO: 从redis中获取初始值进行计算
        RuleInfoDTO ruleInfoDTO = ruleInfoDTOValueState.value();
        if (Objects.isNull(ruleInfoDTO)) {
            throw new BusinessException("运算机 ruleInfoDTO 必须非空");
        }
        // 获取跨历史时间
        LocalDateTime historyTimeline = DateUtil.convertStr2LocalDateTime("1970-01-01 00:00:00");
        if (ruleInfoDTO.getCrossHistory()) {
            String historyTimelineStr = ruleInfoDTO.getHistoryTimeline();
            historyTimeline = DateUtil.convertStr2LocalDateTime(historyTimelineStr);
        }
        // 获取当前事件时间戳
        String timestamp = eventKafkaDTO.getTimestamp();
        LocalDateTime eventTime = DateUtil.convertTimestamp2LocalDateTime(Long.parseLong(timestamp));
        // 获取规则条件
        List<RuleConditionDTO> ruleConditionList = ruleInfoDTO.getRuleConditionList();
        if (CollectionUtil.isNullOrEmpty(ruleConditionList)) {
            throw new BusinessException("运算机 ruleConditionList 必须非空");
        }
        // TODO: 周期规则暂时只支持一个规则条件，后续看看能不能支持多个
        RuleConditionDTO ruleConditionDTO = ruleConditionList.get(0);
        String windowSizeValue = ruleConditionDTO.getWindowSizeValue();
        String windowSizeUnit = ruleConditionDTO.getWindowSizeUnit();
        // 根据窗口值与单位计算获取窗口毫秒值
        int windowSize = getWindowSize(windowSizeUnit, windowSizeValue);
        log.warn("windowSize: {}", windowSize);
        if (Objects.equals(eventKafkaDTO.getEventCode(), ruleConditionDTO.getEventCode())
                && eventTime.isAfter(historyTimeline)) {
            if (smallValueState.value() == null) {
                smallValueState.update(0L);
            }
            smallValueState.update(Long.parseLong(eventKafkaDTO.getEventValue()) + smallValueState.value());
        }
    }

    /**
     * 根据窗口值与单位计算获取窗口毫秒值
     */
    private static int getWindowSize(String windowSizeUnit, String windowSizeValue) {
        int windowSize = 0;
        switch (windowSizeUnit) {
            case "MILLISECOND":
                windowSize = Integer.parseInt(windowSizeValue);
                break;
            case "SECOND":
                windowSize = Integer.parseInt(windowSizeValue) * 1000;
                break;
            case "MINUTE":
                windowSize = Integer.parseInt(windowSizeValue) * 60 * 1000;
                break;
            case "HOUR":
                windowSize = Integer.parseInt(windowSizeValue) * 60 * 60 * 1000;
                break;
            case "DAY":
                windowSize = Integer.parseInt(windowSizeValue) * 24 * 60 * 60 * 1000;
                break;
            case "WEEK":
                windowSize = Integer.parseInt(windowSizeValue) * 7 * 24 * 60 * 60 * 1000;
                break;
            case "MONTH":
                windowSize = Integer.parseInt(windowSizeValue) * 30 * 24 * 60 * 60 * 1000;
                break;
            case "YEAR":
                windowSize = Integer.parseInt(windowSizeValue) * 365 * 24 * 60 * 60 * 1000;
                break;
            default:
                throw new BusinessException(StringUtil.format("时间窗口单位 [{}] 不存在", windowSizeUnit));
        }
        return windowSize;
    }

    @Override
    public void onTimer(long timestamp, KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
        RuleInfoDTO ruleInfoDTO = ruleInfoDTOValueState.value();
        if (Objects.isNull(ruleInfoDTO)) {
            throw new BusinessException("运算机 ruleInfoDTO 必须非空");
        }
        // 获取规则条件
        List<RuleConditionDTO> ruleConditionList = ruleInfoDTO.getRuleConditionList();
        if (CollectionUtil.isNullOrEmpty(ruleConditionList)) {
            throw new BusinessException("运算机 ruleConditionList 必须非空");
        }
        // TODO: 周期规则暂时只支持一个规则条件，后续看看能不能支持多个
        RuleConditionDTO ruleConditionDTO = ruleConditionList.get(0);

        bigListValue.add(smallValueState.value());
        smallValueState.clear();
        List<Long> tmpList = new ArrayList<>();
        Iterator<Long> iterator = bigListValue.get().iterator();
        while (iterator.hasNext()) {
            tmpList.add(iterator.next());
        }
        if (tmpList.size() > Long.parseLong(ruleConditionDTO.getWindowSize())) {
            tmpList.remove(0);
        }
        bigListValue.update(tmpList);

        long sum = tmpList.stream().mapToLong(Long::valueOf).sum();
        String eventThreshold = ruleConditionDTO.getEventThreshold();
        if (Long.parseLong(eventThreshold) > sum) {
            // TODO: 预警信息生成待编写
            out.collect("触发预警了");
        }
    }

    @Override
    public Boolean isCrossHistory() throws IOException {
        RuleInfoDTO ruleInfoDTO = ruleInfoDTOValueState.value();
        return ruleInfoDTO.getCrossHistory();
    }
}
