package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlinkRisk.common.pojo.ChannelDataPO;
import com.liboshuai.starlinkRisk.common.pojo.SourcePO;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liboshuai
 * @Date: 2023-10-17 13:28
 **/
public class SimpleKeyProcessFunction extends KeyedProcessFunction<String, SourcePO, String> {
    /**
     * 预警的时间跨度
     */
    private final long timeSpan;
    /**
     * 预警的阈值
     */
    private final long threshold;
    /**
     * 定义一个ListState来保存每个用户在最近10分钟内的抽奖时间列表
     */
    private ListState<Tuple2<Long, Long>> timeNumberListState;
    /**
     * 定义一个ValueState来保存每个用户最近一次发送预警的时间
     */
    private ValueState<Long> lastWarningTimeState;

    public SimpleKeyProcessFunction(long timeSpan, long threshold) {
        this.timeSpan = timeSpan;
        this.threshold = threshold;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        // 初始化状态变量
        timeNumberListState = getRuntimeContext().getListState(new ListStateDescriptor<>("timeNumberListState", Types.TUPLE(Types.LONG, Types.LONG)));
        lastWarningTimeState = getRuntimeContext().getState(new ValueStateDescriptor<>("lastWarningTime", Long.class));
    }

    @Override
    public void processElement(SourcePO sourcePO, KeyedProcessFunction<String, SourcePO, String>.Context context, Collector<String> collector) throws Exception {
        ChannelDataPO channelDataPO = sourcePO.getChannelData();
        // 获取当前用户ID、抽奖次数、抽奖时间
        String userId = channelDataPO.getUserId();
        Long lotteryNumber = channelDataPO.getLotteryNumber();
        long timestamp = sourcePO.getEventTimestamp();

        // 没有参数抽奖的用户，直接跳过计算
        if (lotteryNumber == null || lotteryNumber < 1) {
            return;
        }

        // 获取当前用户最近一次发送预警的时间，如果为空则初始化为0
        Long lastWarningTime = lastWarningTimeState.value();
        if (lastWarningTime == null) {
            lastWarningTime = 0L;
        }

        // 将当前抽奖时间添加到列表中
        timeNumberListState.add(Tuple2.of(timestamp, lotteryNumber));

        // 遍历列表中的时间，删除超过10分钟的旧的时间，并计算最近10分钟内的抽奖次数
        int lotteryNumberSum = 0;
        Iterator<Tuple2<Long, Long>> iterator = timeNumberListState.get().iterator();
        while (iterator.hasNext()) {
            Tuple2<Long, Long> timeNumberTuple2 = iterator.next();
            if (timestamp - timeNumberTuple2.f0 > TimeUnit.MINUTES.toMillis(10)) {
                iterator.remove();
            } else {
                lotteryNumberSum += timeNumberTuple2.f1;
            }
        }

        // 如果抽奖次数大于100，并且距离上次发送预警已经超过30分钟，发送一个预警事件，并更新最近一次发送预警的时间
        if (lotteryNumberSum > threshold && timestamp - lastWarningTime > TimeUnit.MINUTES.toMillis(30)) {
            collector.collect(splicingWarnMessage(userId, lotteryNumberSum));
            // 更新状态变量
            lastWarningTimeState.update(timestamp);
        }

    }

    /**
     * 拼接告警信息
     */
    private String splicingWarnMessage(String userId, long lotteryNumberSum) {
        return String.format("[异常高频抽奖]活动名称{带传入}中游戏用户%s最近%d分钟内抽奖数量为%d，超过%d次，请您及时查看原因！",
                userId, timeSpan, lotteryNumberSum, threshold);
    }
}
