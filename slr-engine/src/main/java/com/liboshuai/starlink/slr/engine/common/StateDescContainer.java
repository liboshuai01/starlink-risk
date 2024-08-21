package com.liboshuai.starlink.slr.engine.common;

import com.liboshuai.starlink.slr.admin.api.dto.risk.RuleJsonDTO;
import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.List;

/**
 * @Author: liboshuai
 * @Date: 2023-10-25 11:14
 **/
public class StateDescContainer {

    /**
     * 规则广播流状态定义
     */
    public static MapStateDescriptor<String, RuleCdcDTO> broadcastRuleStateDesc =
            new MapStateDescriptor<>("broadcastRule", String.class, RuleCdcDTO.class);

    /**
     * 游戏抽奖最后预警状态定义
     */
    public static ValueStateDescriptor<Long> gameLotteryLastWarningTimeStateDesc =
            new ValueStateDescriptor<>("gameLotteryLastWarningTime", Long.class);

    /**
     * 游戏抽奖最近一段时间抽奖次数状态定义
     */
    public static ValueStateDescriptor<List<Tuple2<Long, Long>>> gameLotteryTimeNuLStateDesc =
            new ValueStateDescriptor<>("gameLotteryTimeNu", Types.LIST(Types.TUPLE(TypeInformation.of(Long.class), TypeInformation.of(Long.class))));

    /**
     * 风控规则状态定义
     */
    public static MapStateDescriptor<String, RuleJsonDTO> riskRuleStateDesc = new MapStateDescriptor<>("riskRule", String.class, RuleJsonDTO.class);
}
