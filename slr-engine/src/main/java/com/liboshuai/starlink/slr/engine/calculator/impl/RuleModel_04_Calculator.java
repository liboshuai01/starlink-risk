//package com.liboshuai.starlink.slr.engine.calculator.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.googlecode.aviator.AviatorEvaluator;
//import com.googlecode.aviator.Expression;
//import com.liboshuai.starlink.slr.engine.calculator.RuleCalculator;
//import com.liboshuai.starlink.slr.engine.pojo.EventBean;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.util.Collector;
//import org.roaringbitmap.longlong.Roaring64Bitmap;
//
//import java.util.HashMap;
//
//public class RuleModel_04_Calculator implements RuleCalculator {
//    String rule_id;
//    String targetEventId;
//    String target_property1;
//    String target_property2;
//    String target_property3;
//
//    JSONObject paramObject;
//    HashMap<String, Object> dataMap = new HashMap<>();
//    JSONObject message;
//    @Override
//    public void init(RuntimeContext runtimeContext, String ruleParamJson, Roaring64Bitmap preSelectUsersBitmap) {
//
//        this.paramObject = JSON.parseObject(ruleParamJson);
//        this.rule_id = paramObject.getString("rule_id");
//
//        this.targetEventId = paramObject.getString("target_event_id");
//        this.target_property1 = paramObject.getString("target_property1");
//        this.target_property2 = paramObject.getString("target_property2");
//        this.target_property3 = paramObject.getString("target_property3");
//
//
//        message = new JSONObject();
//        message.put("rule_id",rule_id);
//
//
//    }
//
//    @Override
//    public void calc(EventBean eventBean, Collector<String> out) throws Exception {
//
//        String eventId = eventBean.getEvent_id();
//
//        if(!eventId.equals(targetEventId)) return ;
//
//        // 取规则要求的各个属性值
//        Double p1 = Double.parseDouble(eventBean.getProperties().get(target_property1));
//        Double p2 = Double.parseDouble(eventBean.getProperties().get(target_property2));
//        Double p3 = Double.parseDouble(eventBean.getProperties().get(target_property3));
//
//        dataMap.put(target_property1,p1);
//        dataMap.put(target_property2,p2);
//        dataMap.put(target_property3,p3);
//
//
//
//        // 取出规则参数中的表达式
//        JSONArray array = paramObject.getJSONArray("expressions");
//
//
//        JSONArray resArray = new JSONArray();
//        message.put("res",resArray);
//
//        message.put("user_id",eventBean.getUser_id());
//        message.put("hit_time",eventBean.getAction_time());
//
//
//
//        // 表达式运算
//        for (int i = 0; i < array.size(); i++) {
//            JSONObject expObject = array.getJSONObject(i);
//
//            int  id = expObject.getIntValue("id");
//            String expression = expObject.getString("expression");
//
//            // 执行表达式
//            Expression exp = AviatorEvaluator.compile(expression);
//            Object res = exp.execute(dataMap);
//
//            JSONObject resObject = new JSONObject();
//            resObject.put("id",id);
//            resObject.put("res",res);
//
//            resArray.add(resObject);
//
//        }
//
//
//        out.collect(message.toJSONString());
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
