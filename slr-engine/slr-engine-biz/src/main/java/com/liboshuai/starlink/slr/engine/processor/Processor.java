package com.liboshuai.starlink.slr.engine.processor;

import com.liboshuai.starlink.slr.engine.api.dto.EventKafkaDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleInfoDTO;
import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 运算机通用接口
 */
public interface Processor {

    /**
     * 运算机初始化
     */
    void init(KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.Context ctx, RuleInfoDTO ruleInfoDTO);

    /**
     * 运算机核心处理实现方法
     */
    void process(EventKafkaDTO eventKafkaDTO, Collector<String> out) throws Exception;

    /**
     * 是否跨历史
     */
    Boolean isCrossHistory();
}
