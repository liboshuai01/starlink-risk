package com.liboshuai.starlink.slr.engine.processor.impl;

import com.liboshuai.starlink.slr.engine.api.dto.EventKafkaDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleConditionDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleInfoDTO;
import com.liboshuai.starlink.slr.engine.exception.BusinessException;
import com.liboshuai.starlink.slr.engine.processor.Processor;
import com.liboshuai.starlink.slr.engine.utils.data.RedisUtil;
import com.liboshuai.starlink.slr.engine.utils.date.DateUtil;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.util.CollectionUtil;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 运算机one
 */
public class ProcessorOne implements Processor {

    private RedisUtil redisUtil;

    /**
     * 规则信息
     */
    private ValueState<RuleInfoDTO> ruleInfoDTOValueState;

    /**
     * 各个事件完成次数map
     */
    private MapState<String, Long> eventFinishCountByCodeMapState;


    @Override
    public void open(RuntimeContext runtimeContext, RuleInfoDTO ruleInfoDTO) {
        ruleInfoDTOValueState = runtimeContext.getState(
                new ValueStateDescriptor<>("ruleInfoDTOValueState", RuleInfoDTO.class)
        );
        eventFinishCountByCodeMapState = runtimeContext.getMapState(
                new MapStateDescriptor<>("eventFinishCountByCodeMapState", String.class, Long.class)
        );
    }

    @Override
    public void process(EventKafkaDTO eventKafkaDTO, Collector<String> out) throws Exception {
        RuleInfoDTO ruleInfoDTO = ruleInfoDTOValueState.value();
        if (Objects.isNull(ruleInfoDTO)) {
            throw new BusinessException("运算机 ruleInfoDTO 必须非空");
        }
        List<RuleConditionDTO> ruleConditionList = ruleInfoDTO.getRuleConditionList();
        if (CollectionUtil.isNullOrEmpty(ruleConditionList)) {
            throw new BusinessException("运算机 ruleConditionList 必须非空");
        }
        for (RuleConditionDTO ruleConditionDTO : ruleConditionList) {
            // TODO: 完成滑动窗口逻辑

        }

        String historyTimelineStr = ruleInfoDTO.getHistoryTimeline();
        LocalDateTime historyTimeline = DateUtil.convertStr2LocalDateTime(historyTimelineStr);

        String timestamp = eventKafkaDTO.getTimestamp();
        LocalDateTime eventTime = DateUtil.convertTimestamp2LocalDateTime(Long.parseLong(timestamp));

//        if (eventTime.isAfter(historyTimeline))
    }

    @Override
    public void onTime() {

    }

    @Override
    public Boolean isCrossHistory() throws IOException {
        RuleInfoDTO ruleInfoDTO = ruleInfoDTOValueState.value();
        return ruleInfoDTO.getCrossHistory();
    }
}
