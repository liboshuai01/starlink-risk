package com.liboshuai.starlink.slr.engine.processor;

import com.liboshuai.starlink.slr.engine.api.dto.EventKafkaDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleInfoDTO;
import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 运算机通用接口
 */
public interface Processor {

    /**
     * 初始化
     */
    void open(RuntimeContext runtimeContext, RuleInfoDTO ruleInfoDTO);

    /**
     * 处理单条数据
     */
    void processElement(EventKafkaDTO eventKafkaDTO, Collector<String> out) throws Exception;

    /**
     * 定时器
     */
    void onTimer(long timestamp, KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.OnTimerContext ctx, Collector<String> out) throws Exception;
}
