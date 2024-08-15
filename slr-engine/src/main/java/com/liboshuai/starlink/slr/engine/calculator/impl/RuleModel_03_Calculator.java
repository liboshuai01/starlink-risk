//package com.liboshuai.starlink.slr.engine.calculator.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.liboshuai.starlink.slr.engine.calculator.RuleCalculator;
//import com.liboshuai.starlink.slr.engine.pojo.EventBean;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.util.Collector;
//import org.roaringbitmap.longlong.Roaring64Bitmap;
//
//
//
//public class RuleModel_03_Calculator implements RuleCalculator {
//
//    JSONObject message;
//    String trigger_event_id;
//    String rule_id;
//
//    @Override
//    public void init(RuntimeContext runtimeContext, String ruleParamJson, Roaring64Bitmap preSelectUsersBitmap) {
//
//        JSONObject paramObject = JSON.parseObject(ruleParamJson);
//        rule_id = paramObject.getString("rule_id");
//
//        message = new JSONObject();
//        message.put("rule_id",rule_id);
//
//        trigger_event_id = paramObject.getString("trigger_event_id");
//
//
//    }
//
//    @Override
//    public void calc(EventBean eventBean, Collector<String> out) throws Exception {
//
//        if(eventBean.getEvent_id().equals(trigger_event_id)){
//            message.put("user_id",eventBean.getUser_id());
//            message.put("hit_time",eventBean.getAction_time());
//            out.collect(message.toJSONString());
//        }
//
//
//
//    }
//
//    @Override
//    public void offline() {
//
//    }
//
//    @Override
//    public String getRuleId() {
//        return this.rule_id;
//    }
//
//    @Override
//    public boolean isNeedHistoryEvents() {
//        return false;
//    }
//
//    @Override
//    public boolean isNeedPreSelect() {
//        return false;
//    }
//}
