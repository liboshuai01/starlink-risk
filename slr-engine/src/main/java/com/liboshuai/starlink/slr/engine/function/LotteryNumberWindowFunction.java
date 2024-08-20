package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.utils.log.ConsoleLogUtil;
import com.liboshuai.starlinkRisk.common.pojo.SourcePO;
import com.liboshuai.starlinkRisk.common.pojo.WindowPO;
import com.liboshuai.starlinkRisk.common.utils.date.DateUtil;
import com.liboshuai.starlinkRisk.common.utils.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * @Author: liboshuai
 * @Date: 2023-10-27 10:56
 **/
@Slf4j
public class LotteryNumberWindowFunction implements WindowFunction<SourcePO, WindowPO, String, TimeWindow> {
    private static WindowPO getWindowPO(SourcePO newSourcePO, String windowStartTime, String windowEndTime) {
        WindowPO windowPO = new WindowPO();
        windowPO.setWindowStartTime(windowStartTime);
        windowPO.setWindowEndTime(windowEndTime);
        windowPO.setSourcePO(newSourcePO);
        return windowPO;
    }

    @Override
    public void apply(String userId, TimeWindow window, Iterable<SourcePO> input, Collector<WindowPO> out) {
        SourcePO newSourcePO = input.iterator().next();
        // 获取窗口开始时间
        String windowStartTime = DateUtil.convertTimestamp2String(window.getStart());
        // 获取窗口结束时间
        String windowEndTime = DateUtil.convertTimestamp2String(window.getEnd());
        // 获取windowPO对象
        WindowPO windowPO = getWindowPO(newSourcePO, windowStartTime, windowEndTime);
        ConsoleLogUtil.debug("apply: {}", JsonUtil.obj2JsonStr(windowPO));
        out.collect(windowPO);
    }
}
