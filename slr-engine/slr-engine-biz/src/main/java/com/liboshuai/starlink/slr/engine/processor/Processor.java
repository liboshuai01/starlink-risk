package com.liboshuai.starlink.slr.engine.processor;

import com.liboshuai.starlink.slr.engine.api.dto.EventKafkaDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleInfoDTO;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.util.Collector;

import java.io.IOException;

/**
 * 运算机通用接口
 */
public interface Processor {

    /**
     * 初始化
     */
    void open(RuntimeContext runtimeContext, RuleInfoDTO ruleInfoDTO) throws IOException;

    /**
     * 处理单条数据
     */
    void processElement(EventKafkaDTO eventKafkaDTO, Collector<String> out) throws Exception;

    /**
     * 定时器
     */
    void onTimer(long timestamp, Collector<String> out) throws Exception;
}
