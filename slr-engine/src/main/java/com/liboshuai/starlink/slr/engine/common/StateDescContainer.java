package com.liboshuai.starlink.slr.engine.common;


import com.liboshuai.starlink.slr.engine.pojo.EventBean;
import com.liboshuai.starlink.slr.engine.pojo.RuleMeta;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.MapStateDescriptor;

/**
 * @Author: liboshuai
 * @Date: 2023-10-25 11:14
 * 状态描述
 **/
public class StateDescContainer {

    /**
     * 规则广播流状态定义
     */
    public final static MapStateDescriptor<String, RuleMeta> RULE_META_STATE_DES =
            new MapStateDescriptor<>("rule_meta_state", String.class, RuleMeta.class);

    /**
     * 存储最近30s的历史行为数据
     */
    public final static ListStateDescriptor<EventBean> EVENTS_BUFFER_STATE_DES =
            new ListStateDescriptor<>("events_buffer", EventBean.class);

    /**
     * 存储非新规则
     */
    public final static MapStateDescriptor<String, Object> NOT_NEW_RULES_DES =
            new MapStateDescriptor<>("not_new_rules", String.class, Object.class);

    /**
     * 存储所有规则
     */
    public final static MapStateDescriptor<String, RuleMeta> RULE_METAS_DES =
            new MapStateDescriptor<>("rule_metas", String.class, RuleMeta.class);
}
