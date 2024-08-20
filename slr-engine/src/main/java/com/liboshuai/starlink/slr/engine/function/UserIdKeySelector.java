package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.pojo.EventBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.functions.KeySelector;

/**
 * author: liboshuai
 * description: 按照用户ID进行分组
 * date: 2024
 */
@Slf4j
public class UserIdKeySelector implements KeySelector<EventBean, Long> {

    @Override
    public Long getKey(EventBean eventBean) {
        return eventBean.getUser_id();
    }
}
