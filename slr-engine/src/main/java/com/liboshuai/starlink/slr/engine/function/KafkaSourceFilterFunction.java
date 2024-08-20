package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.pojo.EventBean;
import com.liboshuai.starlink.slr.engine.utils.log.ConsoleLogUtil;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.util.CollectionUtil;
import org.apache.flink.util.StringUtils;

/**
 * 过滤源数据的非法数据
 */
public class KafkaSourceFilterFunction implements FilterFunction<EventBean> {

    @Override
    public boolean filter(EventBean eventBean) {
        if (eventBean == null) {
            ConsoleLogUtil.warning("《数据源过滤器》：eventBean对象-数据源为空，计算跳过");
            return false;
        }
        if (StringUtils.isNullOrWhitespaceOnly(eventBean.getEvent_id())) {
            ConsoleLogUtil.warning("《数据源过滤器》：event_id字段-事件ID为空，计算跳过");
            return false;
        }
        if (CollectionUtil.isNullOrEmpty(eventBean.getProperties())) {
            ConsoleLogUtil.warning("《数据源过滤器》：properties字段-属性为空，计算跳过");
            return false;
        }
        return true;
    }
}
