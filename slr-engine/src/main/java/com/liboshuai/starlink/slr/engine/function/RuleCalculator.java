package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.pojo.EventBean;
import com.liboshuai.starlink.slr.engine.pojo.RuleMeta;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.roaringbitmap.longlong.Roaring64Bitmap;

/**
 * 运算机 接口
 */
public interface RuleCalculator {

    /**
     * 初始化
     */
    void init(RuntimeContext runtimeContext, String ruleParamJson , Roaring64Bitmap preSelectUsersBitmap);

    /**
     * 计算
     */
    void calc(EventBean eventBean, Collector<String> out) throws Exception;

    /**
     * 下线
     */
    void offline();

    /**
     * 获取规则ID
     */
    String getRuleId();

    /**
     * 是否需要历史行为数据
     */
    boolean isNeedHistoryEvents();

    /**
     * 是否需要预圈选人群
     */
    boolean isNeedPreSelect();

    /**
     * 定时器
     */
    default void onTimer(long timestamp,
                         KeyedBroadcastProcessFunction<Long, EventBean, RuleMeta, String>.OnTimerContext ctx,
                         Collector<String> out){

    };

}

