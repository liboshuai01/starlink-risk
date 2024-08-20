package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.utils.log.ConsoleLogUtil;
import com.liboshuai.starlinkRisk.common.constants.RuleChannelConstants;
import com.liboshuai.starlinkRisk.common.pojo.ChannelDataPO;
import com.liboshuai.starlinkRisk.common.pojo.SourcePO;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.util.StringUtils;

/**
 * 过滤源数据的非法数据
 */
public class KafkaSourceFilterFunction implements FilterFunction<SourcePO> {

    @Override
    public boolean filter(SourcePO sourcePO) {
        if (sourcePO == null) {
            ConsoleLogUtil.warning("《数据源过滤器》：sourcePO对象-数据源为空，计算跳过");
            return false;
        }
        if (StringUtils.isNullOrWhitespaceOnly(sourcePO.getBank())) {
            ConsoleLogUtil.warning("《数据源过滤器》：bank字段-银行为空，计算跳过");
            return false;
        }
        if (StringUtils.isNullOrWhitespaceOnly(sourcePO.getChannel())) {
            ConsoleLogUtil.warning("《数据源过滤器》：channel字段-渠道为空，计算跳过");
            return false;
        }
        if (!sourcePO.getChannel().equalsIgnoreCase(RuleChannelConstants.GAME)) {
            // 过滤非游戏渠道的数据
            return false;
        }
        ChannelDataPO channelData = sourcePO.getChannelData();
        if (channelData == null) {
            ConsoleLogUtil.warning("《数据源过滤器》：渠道对象-数据为空，计算跳过");
            return false;
        }
        if (StringUtils.isNullOrWhitespaceOnly(channelData.getCampaignId())) {
            ConsoleLogUtil.warning("《数据源过滤器》：campaignId-活动ID为空，计算跳过");
            return false;
        }
        if (StringUtils.isNullOrWhitespaceOnly(channelData.getUserId())) {
            ConsoleLogUtil.warning("《数据源过滤器》：userId字段-用户ID为空，计算跳过");
            return false;
        }
        if (channelData.getLotteryNumber() == null || channelData.getLotteryNumber() < 1) {
//            ConsoleLogUtil.warning("《数据源过滤器》：lotteryNumber字段-抽奖次数为空或小于1，计算跳过");
            return false;
        }
        return true;
    }
}
