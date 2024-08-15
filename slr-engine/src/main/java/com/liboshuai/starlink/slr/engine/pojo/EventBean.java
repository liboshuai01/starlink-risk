package com.liboshuai.starlink.slr.engine.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventBean {

    private long user_id;
    private String event_id;
    private Map<String,String> properties;
    private long action_time;

}
