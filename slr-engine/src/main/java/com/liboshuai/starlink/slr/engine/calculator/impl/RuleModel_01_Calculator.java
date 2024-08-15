//package com.liboshuai.starlink.slr.engine.calculator.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.liboshuai.starlink.slr.engine.calculator.RuleCalculator;
//import com.liboshuai.starlink.slr.engine.pojo.EventBean;
//import com.liboshuai.starlink.slr.engine.util.StateValueCompareUtil;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.api.common.state.MapState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.state.ValueState;
//import org.apache.flink.api.common.state.ValueStateDescriptor;
//import org.apache.flink.util.Collector;
//import org.roaringbitmap.longlong.Roaring64Bitmap;
//import org.slf4j.Logger;
//
//public class RuleModel_01_Calculator implements RuleCalculator {
//
//    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RuleModel_01_Calculator.class);
//
//
//    ValueState<Integer> idxState;
//    ValueState<Integer> seqCntState;
//    MapState<String, Integer> mapState;
//
//    JSONObject message;
//
//    String rule_id;
//
//    JSONObject paramObject;
//
//    Roaring64Bitmap preSelectUsersBitmap;
//
//    @Override
//    public void init(RuntimeContext runtimeContext, String ruleParamJson , Roaring64Bitmap preSelectUsersBitmap) {
//
//        // 将预圈选人群bitmap，放到成员变量上
//        this.preSelectUsersBitmap = preSelectUsersBitmap;
//
//
//        // 解析规则参数
//        paramObject = JSON.parseObject(ruleParamJson);
//
//        rule_id = paramObject.getString("rule_id");
//
//        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, Integer>(rule_id + "-dy_cnt", String.class, Integer.class));
//
//        idxState = runtimeContext.getState(new ValueStateDescriptor<Integer>(rule_id + "-idx", Integer.class));
//        seqCntState = runtimeContext.getState(new ValueStateDescriptor<Integer>(rule_id + "-seq", Integer.class));
//
//        message = new JSONObject();
//        message.put("rule_id", rule_id);
//
//    }
//
//    @Override
//    public void calc(EventBean eventBean, Collector<String> out) throws Exception {
//
//        long userId = eventBean.getUser_id();
//
//        // 判断该行为的用户是否是本规则的预圈选人群，如果否，则直接返回，啥也不做
//        if(!preSelectUsersBitmap.contains(userId)) return;
//
//
//        String eventId = eventBean.getEvent_id();
//
//        /**
//         * 二、 对规则的动态画像 事件次数统计条件进行处理
//         */
//        JSONArray dcArray = paramObject.getJSONArray("dynamic_cnt_condition");
//        for (int i = 0; i < dcArray.size(); i++) {
//            JSONObject cntObject = dcArray.getJSONObject(i);
//            String eid = cntObject.getString("event_id");
//
//            if (eid.equals(eventId)) {
//                // 从状态中取出当前条件事件的发生次数
//                Integer oldValue = mapState.get(eid);
//                oldValue = oldValue == null ? 0 : oldValue;
//
//                // 更新本条件事件的发生次数
//                mapState.put(eid, oldValue + 1);
//
//            }
//
//        }
//
//        /**
//         * 三、 对规则的动态画像 序列次数统计条件进行处理
//         */
//        JSONObject seqObject = paramObject.getJSONObject("dynamic_seq_condition");
//        // 取出事件序列： [B ,D ,K]
//        JSONArray eventSeq = seqObject.getJSONArray("event_seq");
//
//        // 取出索引状态的值
//        int idx = idxState.value() == null ? 0 : idxState.value();
//
//        if (eventId.equals(eventSeq.getString(idx))) {
//            idx++;
//            if (idx == eventSeq.size()) {
//                idx = 0;
//                seqCntState.update(seqCntState.value() == null ? 1 : seqCntState.value() + 1);
//            }
//            idxState.update(idx);
//        }
//
//
//        /**
//         * 四、 处理触发条件事件
//         */
//        String triggerEventId = paramObject.getString("trigger_event_id");
//
//
//        if (triggerEventId.equals(eventId)) {
//            // 判断 动态画像 事件次数 统计条件是否都满足
//            for (int i = 0; i < dcArray.size(); i++) {
//
//                JSONObject cntObject = dcArray.getJSONObject(i);
//                String eid = cntObject.getString("event_id");
//                String compareType = cntObject.getString("compare_type");
//                double compareValue = cntObject.getDouble("compare_value");
//
//                Integer stateValue = mapState.get(eid);
//
//                boolean compare = StateValueCompareUtil.compare(stateValue, compareValue, compareType);
//
//                if (!compare) return;
//            }
//
//
//            // 判断 动态画像  行为序列 次数 统计条件是否满足
//            Integer stateValue = seqCntState.value();
//            String compareType = seqObject.getString("compare_type");
//            Double compareValue = seqObject.getDouble("compare_value");
//
//            boolean compare = StateValueCompareUtil.compare(stateValue, compareValue, compareType);
//
//            // 如果本条件还满足，则要触发消息
//            if(compare){
//
//                message.put("user_id",eventBean.getUser_id());
//                message.put("hit_time",eventBean.getAction_time());
//
//                out.collect(message.toJSONString());
//            }
//
//        }
//
//
//    }
//
//
//
//    @Override
//    public void offline() {
//
//        log.warn("规则运算机:{} ,被下线，准备清理状态",this.rule_id);
//
//        idxState.clear();
//        seqCntState.clear();
//        mapState.clear();
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
//        return true;
//    }
//}
