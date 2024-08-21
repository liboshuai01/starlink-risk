package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.admin.api.constants.DefaultConstants;
import com.liboshuai.starlink.slr.admin.api.dto.event.EventKafkaDTO;
import com.liboshuai.starlink.slr.engine.common.StateDescContainer;
import com.liboshuai.starlink.slr.engine.utils.log.ConsoleLogUtil;
import com.liboshuai.starlink.slr.framework.common.util.json.JsonUtils;
import com.liboshuai.starlinkRisk.common.constants.KeyConstants;
import com.liboshuai.starlinkRisk.common.pojo.RuleCdcPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 计算引擎核心
 */
@Slf4j
public class CoreFunction extends KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcPO, String> {

    private static final long serialVersionUID = -5913085790319815064L;

    @Override
    public void processElement(EventKafkaDTO eventKafkaDTO,
                               KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcPO, String>.ReadOnlyContext ctx,
                               Collector<String> out) throws Exception {
        ReadOnlyBroadcastState<String, RuleCdcPO> broadcastState = ctx.getBroadcastState(StateDescContainer.broadcastRuleStateDesc);
        ConsoleLogUtil.info("processElement-获取广播流状态: {}",
                JsonUtils.toJsonString(broadcastState.get(KeyConstants.CDC_RULE_MAP_STATE_KEY)));
        ConsoleLogUtil.info("processElement-获取业务数据: {}",JsonUtils.toJsonString(eventKafkaDTO));
        out.collect(JsonUtils.toJsonString(eventKafkaDTO));
    }

    @Override
    public void processBroadcastElement(RuleCdcPO ruleCdcPO,
                                        KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcPO, String>.Context ctx,
                                        Collector<String> out) throws Exception {
        BroadcastState<String, RuleCdcPO> broadcastState = ctx.getBroadcastState(StateDescContainer.broadcastRuleStateDesc);
        ConsoleLogUtil.info("processBroadcastElement-更新广播流状态之前: {}",
                JsonUtils.toJsonString(broadcastState.get(KeyConstants.CDC_RULE_MAP_STATE_KEY)));
        broadcastState.put(DefaultConstants.BROADCAST_STATE_KEY, ruleCdcPO);
        ConsoleLogUtil.info("processBroadcastElement-更新广播流状态之后: {}",
                JsonUtils.toJsonString(broadcastState.get(KeyConstants.CDC_RULE_MAP_STATE_KEY)));
    }

}