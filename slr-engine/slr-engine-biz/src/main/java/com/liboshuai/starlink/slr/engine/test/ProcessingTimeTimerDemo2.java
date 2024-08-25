package com.liboshuai.starlink.slr.engine.test;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @program: flink-demo
 * @description: 通过onTimer实现一个累加功能，一分钟一个窗口累计，然后下一个窗口累加上一个窗口的值
 * @author: ZhangYitian
 * @create: 2022-01-08 09:14
 */
public class ProcessingTimeTimerDemo2 {

    public static void main(String[] args) throws Exception {
        // 创建流处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 从指定的socket端口读取数据流，数据格式为字符串
        DataStreamSource<String> line = env.socketTextStream("rocky", 21111);

        // 将输入的字符串数据映射为Tuple2，Tuple2的第一个元素是字符串，第二个元素是整数
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndCount = line.map(
                (MapFunction<String, Tuple2<String, Integer>>) value -> {
                    // 按逗号分割字符串，假设格式为 "key,value"
                    String[] fields = value.split(",");
                    // 返回一个Tuple2，包含key和对应的整数值
                    return Tuple2.of(fields[0], Integer.parseInt(fields[1]));
                });

        // 按照Tuple2的第一个元素（key）进行分组
        KeyedStream<Tuple2<String, Integer>, String> keyed = wordAndCount.keyBy(t -> t.f0);

        // 使用KeyedProcessFunction进行处理
        keyed.process(new KeyedProcessFunction<String, Tuple2<String, Integer>, Tuple2<String, Integer>>() {

            private static final long serialVersionUID = 1228853193388866948L;

            // 声明一个状态变量，用于存储累加的计数
            private transient ValueState<Integer> counter;

            // 该方法在每个并行实例的生命周期内被调用，用于初始化状态
            @Override
            public void open(Configuration parameters) throws Exception {
                // 创建ValueState的描述符，指定状态名称和类型
                ValueStateDescriptor<Integer> valueStateDescriptor = new ValueStateDescriptor<>("wc-state", Integer.class);
                // 获取状态上下文并初始化counter
                counter = getRuntimeContext().getState(valueStateDescriptor);
            }

            // 处理每一个输入元素
            @Override
            public void processElement(Tuple2<String, Integer> value, Context ctx, Collector<Tuple2<String, Integer>> out) throws Exception {
                // 获取当前的处理时间
                long currentProcessingTime = ctx.timerService().currentProcessingTime();
                // 计算下一分钟的时间戳（整分钟）
                long fireTime = currentProcessingTime - currentProcessingTime % 60000 + 60000;
                // 注册一个定时器，指定时间到达时触发onTimer方法
                ctx.timerService().registerProcessingTimeTimer(fireTime);

                // 获取当前输入的计数值
                Integer currentCount = value.f1;
                // 获取当前状态中的历史计数值
                Integer historyCount = counter.value();

                // 如果历史计数值为空，则初始化为0
                if (historyCount == null) {
                    historyCount = 0;
                }
                // 计算总计数
                int totalCount = historyCount + currentCount;
                // 更新状态
                counter.update(totalCount);
            }

            // 当定时器触发时调用该方法
            @Override
            public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<String, Integer>> out) throws Exception {
                // 获取当前状态中的计数值
                Integer currentValue = counter.value();
                // 获取当前key
                String currentKey = ctx.getCurrentKey();

                // 如果需要实现每分钟重置计数，可以在此处清空状态
                // counter.update(0);

                // 输出当前key和计数值到下游
                out.collect(Tuple2.of(currentKey, currentValue));
            }
        }).print(); // 打印输出结果

        // 启动执行环境
        env.execute();
    }
}
