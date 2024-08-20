package com.liboshuai.starlink.slr.engine;

import com.liboshuai.starlink.slr.engine.common.ParameterConstants;
import com.liboshuai.starlink.slr.engine.common.StateDescContainer;
import com.liboshuai.starlink.slr.engine.function.EngineCoreFunction;
import com.liboshuai.starlink.slr.engine.function.EventTimestampAssigner;
import com.liboshuai.starlink.slr.engine.function.KafkaSourceFilterFunction;
import com.liboshuai.starlink.slr.engine.function.UserIdKeySelector;
import com.liboshuai.starlink.slr.engine.pojo.EventBean;
import com.liboshuai.starlink.slr.engine.pojo.RuleMeta;
import com.liboshuai.starlink.slr.engine.utils.data.KafkaUtil;
import com.liboshuai.starlink.slr.engine.utils.data.MysqlUtil;
import com.liboshuai.starlink.slr.engine.utils.parameter.ParameterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EngineApplication {
    public static void main(String[] args) throws Exception {
        //流式计算上下文环境/
        final StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        //ParameterTool 注册为 global
        ParameterTool parameterTool = ParameterUtil.getParameters(args);
        env.getConfig().setGlobalJobParameters(parameterTool);
        // 配置上下文环境
        ParameterUtil.envWithConfig(env, parameterTool);

        // 获取规则配置数据流
        DataStream<RuleMeta> ruleSource = MysqlUtil.read(env, parameterTool);
        // 获取规则广播流
        BroadcastStream<RuleMeta> broadcastStream = ruleSource.broadcast(StateDescContainer.RULE_META_STATE_DES);

        // 获取业务数据流
        KeyedStream<EventBean, Long> sourcePOStringKeyedStream = KafkaUtil
                //读取Kafka
                .read(env, parameterTool)
                .filter(new KafkaSourceFilterFunction()).uid("filter-data")
                //注册水印
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                //水印生成器: 实现一个延迟10秒的固定延迟水印
                                .<EventBean>forBoundedOutOfOrderness(Duration.ofMillis(
                                        TimeUnit.SECONDS.toMillis(parameterTool.getInt(
                                                ParameterConstants.FLINK_MAXOUTOFORDERNESS
                                        ))
                                ))
                                //时间戳生成器：提取事件流的event_time字段
                                .withTimestampAssigner(new EventTimestampAssigner())
                                // 空闲检查时间，防止水位线停止推进
                                .withIdleness(Duration.ofSeconds(5))
                ).uid("register-watermark")
                // 用户id分组
                .keyBy(new UserIdKeySelector());

        // 事件流连接广播流，并进行核心计算处理
        SingleOutputStreamOperator<String> warnMessageDs = sourcePOStringKeyedStream.connect(broadcastStream)
                .process(new EngineCoreFunction()).uid("engine-core-function");

        // 将告警信息写入kafka
        KafkaUtil.writer(warnMessageDs, parameterTool);

        env.execute();
    }
}
