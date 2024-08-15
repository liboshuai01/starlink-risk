package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.calculator.RuleCalculator;
import com.liboshuai.starlink.slr.engine.pojo.EventBean;
import com.liboshuai.starlink.slr.engine.pojo.RuleMeta;
import com.liboshuai.starlink.slr.engine.util.RuleCalculatorUtil;
import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class CoreFunction extends KeyedBroadcastProcessFunction<Long, EventBean, RuleMeta, String> {

    HashMap<String, RuleCalculator> ruleCalculatorPool = new HashMap<>();

    ListState<EventBean> eventsBuffer;
    MapState<String, Object> notNewRules;

    GroovyClassLoader groovyClassLoader;

    Object lock;


    @Override
    public void open(Configuration parameters) throws Exception {
        lock = new Object();

        groovyClassLoader = new GroovyClassLoader();

        // 申请一个状态，来缓存最近30s的行为数据
        ListStateDescriptor<EventBean> desc = new ListStateDescriptor<>("events_buffer", EventBean.class);
        desc.enableTimeToLive(StateTtlConfig.newBuilder(Time.seconds(30)).neverReturnExpired().build());

        eventsBuffer = getRuntimeContext().getListState(desc);


        // 申请一个状态，来记录工作过的运算机规则id
        notNewRules = getRuntimeContext().getMapState(new MapStateDescriptor<String, Object>("not_new_rules", String.class, Object.class));

    }

    @Override
    public void processElement(EventBean eventBean, KeyedBroadcastProcessFunction<Long, EventBean, RuleMeta, String>.ReadOnlyContext ctx, Collector<String> out) throws Exception {

        /* *
         * 一、故障重启后的恢复逻辑
         */
        // 一进来就判断，运算机池是否为空
        // 如果为空，说明：
        //   要么是系统第一次启动，要么是系统failover后的restart
        synchronized (lock) {
            if (ruleCalculatorPool.isEmpty()) {
                ReadOnlyBroadcastState<String, RuleMeta> ruleMetas = ctx.getBroadcastState(new MapStateDescriptor<String, RuleMeta>("rule_metas", String.class, RuleMeta.class));

                for (Map.Entry<String, RuleMeta> entry : ruleMetas.immutableEntries()) {
                    String ruleId = entry.getKey();
                    RuleMeta ruleMeta = entry.getValue();

                    // 根据元数据，构造规则运算机对象
                    RuleCalculator ruleCalculator = RuleCalculatorUtil.generateRuleCalculator(groovyClassLoader, ruleMeta, getRuntimeContext());
                    // 放入运算机池
                    ruleCalculatorPool.put(ruleId, ruleCalculator);
                }
            }

            /**
             * 二、以下是正常工作逻辑
             */


            if (eventBean.getEvent_id().equals("ERR") && (RandomUtils.nextInt(1, 10) % 2 == 0)) {
                throw new RuntimeException("哈哈哈,搞死你,全杀了");
            }


            // 1. 先将当前收到的行为事件，放入事件缓存
            eventsBuffer.add(eventBean);

            // 2. 然后开始遍历运算机池中的每一个规则
            for (Map.Entry<String, RuleCalculator> entry : ruleCalculatorPool.entrySet()) {
                String ruleId = entry.getKey();
                RuleCalculator ruleCalculator = entry.getValue();

                // 判断该运算机是否属于需要补历史数据的类型，以及是否是一个新上线的运算机
                if (ruleCalculator.isNeedHistoryEvents() && !notNewRules.contains(ruleId)) {
                    // 新的，需要补充历史数据的运算机对象，需要把eventBuffer中的数据，交给它处理
                    for (EventBean historyBean : eventsBuffer.get()) {
                        ruleCalculator.calc(historyBean, out);
                    }

                    // 将补完数据的运算机对象，放入“非新”集合
                    notNewRules.put(ruleId, null);
                } else {
                    // 别的运算机，则直接处理本次收到的事件即可
                    ruleCalculator.calc(eventBean, out);
                }


            }
        }
    }

    @Override
    public void processBroadcastElement(RuleMeta ruleMeta, KeyedBroadcastProcessFunction<Long, EventBean, RuleMeta, String>.Context ctx, Collector<String> out) throws Exception {

        BroadcastState<String, RuleMeta> ruleMetas = ctx.getBroadcastState(new MapStateDescriptor<String, RuleMeta>("rule_metas", String.class, RuleMeta.class));

        /**
         * 一、故障后的恢复逻辑
         */
        synchronized (lock) {
            if (ruleCalculatorPool.isEmpty()) {
                for (Map.Entry<String, RuleMeta> entry : ruleMetas.immutableEntries()) {
                    String ruleId = entry.getKey();
                    RuleMeta meta = entry.getValue();

                    // 根据元数据，构造规则运算机对象
                    RuleCalculator ruleCalculator = RuleCalculatorUtil.generateRuleCalculator(groovyClassLoader, meta, getRuntimeContext());
                    // 放入运算机池
                    ruleCalculatorPool.put(ruleId, ruleCalculator);
                }
            }
        }


        String ruleId = ruleMeta.getRule_id();
        String op = ruleMeta.getOp();
        int status = ruleMeta.getStatus();

        // 根据收到的规则元数据操作类型，来对系统的  “运算机池” 做对应的操作
        //  op = c / r / u  且 status = 1    ==>  构造运算机对象，并用规则参数对它初始化，放入运算机池
        if ((op.equals("c") || op.equals("r") || op.equals("u")) && status == 1) {


            RuleCalculator calculator = ruleCalculatorPool.get(ruleId);
            if (calculator != null) {
                // 如果此前该运算机已存在，说明现在是新上线, 那么先下线
                calculator.offline();
            }

            // 生成运算机对象
            RuleCalculator ruleCalculator = RuleCalculatorUtil.generateRuleCalculator(groovyClassLoader, ruleMeta, getRuntimeContext());

            // 将初始化好的运算机对象，放入运算机池
            ruleCalculatorPool.put(ruleId, ruleCalculator);

            log.info("上线或更新了一个规则运算机: {}", ruleId);


            // 备份收到的新规则元数据，到广播状态中
            ruleMetas.put(ruleId, ruleMeta);

        }
        //  op = d  或   op=u && status = 0  ==> 从运算机池把该规则的运算机对象移除
        else if ((op.equals("d") || (op.equals("u") && status == 0))) {

            RuleCalculator calculator = ruleCalculatorPool.get(ruleId);
            if (calculator != null) {
                calculator.offline();
            }
            ruleCalculatorPool.remove(ruleId);
            log.info("下线了一个规则运算机: {}", ruleId);

            // 从广播状态中，移除备份的规则元数据对象
            ruleMetas.remove(ruleId);


        } else {
            log.error("收到不支持的规则元数据操作类型：" + op);
        }
    }

    @Override
    public void onTimer(long timestamp, KeyedBroadcastProcessFunction<Long, EventBean, RuleMeta, String>.OnTimerContext ctx, Collector<String> out) throws Exception {

        for (Map.Entry<String, RuleCalculator> entry : ruleCalculatorPool.entrySet()) {

            RuleCalculator ruleCalculator = entry.getValue();
            ruleCalculator.onTimer(timestamp,ctx,out);
        }

    }
}