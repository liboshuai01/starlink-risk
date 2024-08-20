package com.liboshuai.starlink.slr.engine.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 事件对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventBean {

    /**
     * 用户ID
     */
    private long user_id;
    /**
     * 事件ID
     */
    private String event_id;
    /**
     * 事件属性
     */
    private Map<String,String> properties;
    /**
     * 事件时间
     */
    private long action_time;

}