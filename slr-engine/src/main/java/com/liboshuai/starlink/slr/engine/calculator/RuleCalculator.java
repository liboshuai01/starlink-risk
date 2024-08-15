package com.liboshuai.starlink.slr.engine.calculator;

import com.liboshuai.starlink.slr.engine.pojo.EventBean;
import com.liboshuai.starlink.slr.engine.pojo.RuleMeta;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.roaringbitmap.longlong.Roaring64Bitmap;

/**
 * @Author: 深似海
 * @Site: <a href="www.51doit.com">多易教育</a>
 * @QQ: 657270652
 * @Date: 2024/5/14
 * @Desc: 学大数据，上多易教育
 *
 * 规则运算机接口
 *
 **/
public interface RuleCalculator {

    // 封装的就是demo1中的卸载flink的open中的逻辑
    public void init(RuntimeContext runtimeContext, String ruleParamJson , Roaring64Bitmap preSelectUsersBitmap);

    // 封装的就是demo1中的写在flink的processElement中的逻辑
    public void calc(EventBean eventBean, Collector<String> out) throws Exception;

    public void offline();

    public String getRuleId();


    public boolean isNeedHistoryEvents();

    public boolean isNeedPreSelect();


    default void onTimer(long timestamp, KeyedBroadcastProcessFunction<Long, EventBean, RuleMeta, String>.OnTimerContext ctx, Collector<String> out){

    };
}
