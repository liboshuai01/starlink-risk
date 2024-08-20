package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.utils.log.ConsoleLogUtil;
import com.liboshuai.starlinkRisk.common.pojo.ChannelDataPO;
import com.liboshuai.starlinkRisk.common.pojo.SourcePO;
import com.liboshuai.starlinkRisk.common.utils.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.springframework.beans.BeanUtils;

/**
 * @Author: liboshuai
 * @Date: 2023-10-27 10:48
 **/
@Slf4j
public class LotteryNumberAggFunction implements AggregateFunction<SourcePO, SourcePO, SourcePO> {

    @Override
    public SourcePO createAccumulator() {
        ChannelDataPO channelDataPO = new ChannelDataPO();
        channelDataPO.setLotteryNumber(0L);
        SourcePO sourcePO = new SourcePO();
        sourcePO.setChannelData(channelDataPO);
        return sourcePO;
    }

    @Override
    public SourcePO add(SourcePO value, SourcePO accSourcePO) {
        SourcePO sourcePO = new SourcePO();
        BeanUtils.copyProperties(value, sourcePO);
        ChannelDataPO channelDataPO = new ChannelDataPO();
        ChannelDataPO newChannelData = sourcePO.getChannelData();
        BeanUtils.copyProperties(newChannelData, channelDataPO);
        // 累加抽奖数量
        channelDataPO.setLotteryNumber(newChannelData.getLotteryNumber() + accSourcePO.getChannelData().getLotteryNumber());
        sourcePO.setChannelData(channelDataPO);
        ConsoleLogUtil.debug("add :{}", JsonUtil.obj2JsonStr(sourcePO));
        return sourcePO;
    }

    @Override
    public SourcePO getResult(SourcePO accSourcePO) {
        return accSourcePO;
    }

    @Override
    public SourcePO merge(SourcePO a, SourcePO b) {
        return null;
    }
}
