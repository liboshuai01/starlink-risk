package com.liboshuai.starlink.slr.engine.calculator;

import com.liboshuai.starlink.slr.engine.api.dto.EventKafkaDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleInfoDTO;
import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;

public interface Calculator {

    void init(KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.Context ctx, RuleInfoDTO ruleInfoDTO);

}
