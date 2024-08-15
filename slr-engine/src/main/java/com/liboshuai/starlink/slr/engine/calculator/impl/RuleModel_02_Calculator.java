//package com.liboshuai.starlink.slr.engine.calculator.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.liboshuai.starlink.slr.engine.calculator.RuleCalculator;
//import com.liboshuai.starlink.slr.engine.pojo.EventBean;
//import com.liboshuai.starlink.slr.engine.util.StateValueCompareUtil;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.api.common.state.ValueState;
//import org.apache.flink.api.common.state.ValueStateDescriptor;
//import org.apache.flink.util.Collector;
//import org.roaringbitmap.longlong.Roaring64Bitmap;
//import redis.clients.jedis.Jedis;
//
//public class RuleModel_02_Calculator implements RuleCalculator {
//
//
//    Roaring64Bitmap preSelectUsersBitmap;
//
//    JSONObject paramObject;
//    String rule_id;
//    JSONObject message;
//
//    ValueState<Integer> cntState;
//
//    Jedis jedis;
//
//    @Override
//    public void init(RuntimeContext runtimeContext, String ruleParamJson, Roaring64Bitmap preSelectUsersBitmap) {
//
//        this.preSelectUsersBitmap = preSelectUsersBitmap;
//        this.paramObject = JSON.parseObject(ruleParamJson);
//
//        this.rule_id = paramObject.getString("rule_id");
//
//        this.message = new JSONObject();
//        this.message.put("rule_id", rule_id);
//
//        this.cntState = runtimeContext.getState(new ValueStateDescriptor<Integer>(rule_id + "-cnt", Integer.class));
//
//        this.jedis = new Jedis("doitedu", 6379);
//
//    }
//
//    @Override
//    public void calc(EventBean eventBean, Collector<String> out) throws Exception {
//
//        long userId = eventBean.getUser_id();
//        if (!preSelectUsersBitmap.contains(userId)) return;
//
//        String eventId = eventBean.getEvent_id();
//        long actionTime = eventBean.getAction_time();
//
//
//        /**
//         * 处理统计条件
//         */
//        JSONObject crossConditionObject = paramObject.getJSONObject("cross_history_static_condition");
//        String conditionEventId = crossConditionObject.getString("event_id");
//
//        // 跨区间条件的历史段统计截止点
//        Long crossStatisticEnd = paramObject.getLong("cross_statistic_end");
//
//        if (eventId.equals(conditionEventId) && actionTime > crossStatisticEnd) {
//            // 先判断计数状态中是否已经有值
//            Integer stateValue = cntState.value();
//
//            // 如果状态中的值为null，则需要去从redis中获取历史基础值
//            if (stateValue == null) {
//                String redisValueStr = jedis.hget(rule_id, userId + "");
//                int redisValue = redisValueStr == null ? 0 : Integer.parseInt(redisValueStr);
//                cntState.update(redisValue + 1);
//            } else {
//                cntState.update(stateValue + 1);
//            }
//        }
//
//
//        /**
//         * 处理触发条件
//         */
//        if (eventId.equals(paramObject.getString("trigger_event_id"))) {
//
//            // 先取状态的计数值
//            Integer cnt = cntState.value();
//            if (cnt == null) {
//                // 如果状态中的计数值为null，则要去redis中查询
//                String redisValue = jedis.hget(rule_id, userId + "");
//                if (redisValue != null) cnt = Integer.parseInt(redisValue);
//            }
//
//            double compareValue = crossConditionObject.getDoubleValue("compare_value");
//            String compareType = crossConditionObject.getString("compare_type");
//            // 如果满足条件，则输出结果
//            if (StateValueCompareUtil.compare(cnt, compareValue, compareType)) {
//                message.put("user_id", userId);
//                message.put("hit_time", actionTime);
//                out.collect(message.toJSONString());
//            }
//        }
//
//
//    }
//
//    @Override
//    public void offline() {
//
//        jedis.close();
//    }
//
//    @Override
//    public String getRuleId() {
//        return this.rule_id;
//    }
//
//    @Override
//    public boolean isNeedHistoryEvents() {
//        return true;
//    }
//
//    @Override
//    public boolean isNeedPreSelect() {
//        return true;
//    }
//}
