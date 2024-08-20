package com.liboshuai.starlink.slr.engine.pojo;

import java.util.HashMap;
import java.util.Objects;

public class UserAction {
    private long user_id;
    private String event_id;
    private long action_time;
    private HashMap<String,String> properties;

    // setter and getter

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public long getAction_time() {
        return action_time;
    }

    public void setAction_time(long action_time) {
        this.action_time = action_time;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAction that = (UserAction) o;
        return user_id == that.user_id && action_time == that.action_time && Objects.equals(event_id, that.event_id) && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, event_id, action_time, properties);
    }

    @Override
    public String toString() {
        return "UserAction{" +
                "user_id=" + user_id +
                ", event_id='" + event_id + '\'' +
                ", action_time=" + action_time +
                ", properties=" + properties +
                '}';
    }
}
