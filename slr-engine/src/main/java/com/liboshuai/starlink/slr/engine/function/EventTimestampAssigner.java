package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.pojo.EventBean;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;

/**
 * author: liboshuai
 * description: 自定义水印时间戳生成器
 * date: 2023
 */
public class EventTimestampAssigner implements SerializableTimestampAssigner<EventBean> {

    private static final long serialVersionUID = 3978382324707642081L;

    /**
     * author: liboshuai
     * description: 提取事件流的event_time字段
     */
    @Override
    public long extractTimestamp(EventBean eventBean, long l) {
        return eventBean.getAction_time();
    }
}
