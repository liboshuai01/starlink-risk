package com.liboshuai.starlink.slr.engine;


import com.liboshuai.starlink.slr.engine.api.dto.EventKafkaDTO;
import com.liboshuai.starlink.slr.engine.common.ParameterConstants;
import com.liboshuai.starlink.slr.engine.common.StateDescContainer;
import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
import com.liboshuai.starlink.slr.engine.function.CoreFunction;
import com.liboshuai.starlink.slr.engine.utils.string.JsonUtil;
import com.liboshuai.starlink.slr.engine.utils.data.KafkaUtil;
import com.liboshuai.starlink.slr.engine.utils.data.MysqlUtil;
import com.liboshuai.starlink.slr.engine.utils.parameter.ParameterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
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
        Configuration configuration = new Configuration();
        configuration.setInteger("rest.port",8989);
        final StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment(configuration);
        //ParameterTool 注册为 global
        ParameterTool parameterTool = ParameterUtil.getParameters(args);
        env.getConfig().setGlobalJobParameters(parameterTool);
        // 配置上下文环境
        ParameterUtil.envWithConfig(env, parameterTool);

        // 获取规则配置数据流
        DataStream<RuleCdcDTO> ruleSource = MysqlUtil.read(env, parameterTool);
        // 获取规则广播流
        BroadcastStream<RuleCdcDTO> broadcastStream = ruleSource.broadcast(StateDescContainer.broadcastRuleStateDesc);
        // 获取业务数据流
        KeyedStream<EventKafkaDTO, String> eventKafkaDTOStringKeyedStream = KafkaUtil.read(env, parameterTool) // 读取数据
                .map(s -> JsonUtil.jsonStr2Obj(s, EventKafkaDTO.class)) // 转换string为eventKafkaDTO对象
                .assignTimestampsAndWatermarks(buildWatermarkStrategy(parameterTool)) // 定义水位线
                .uid("register-watermark")
                .keyBy(EventKafkaDTO::getKeyCode);// keyBy分组

        // 连接业务数据流和规则配置流
        SingleOutputStreamOperator<String> warnMessageStream = eventKafkaDTOStringKeyedStream.connect(broadcastStream)
                .process(new CoreFunction()).uid("engine-core-function");
        // 将告警信息写入kafka
        KafkaUtil.writer(warnMessageStream, parameterTool);
        env.execute();
    }

    /**
     * 构建水位线
     */
    private static WatermarkStrategy<EventKafkaDTO> buildWatermarkStrategy(ParameterTool parameterTool) {
        return WatermarkStrategy
                //水印生成器: 实现一个延迟10秒的固定延迟水印
                .<EventKafkaDTO>forBoundedOutOfOrderness(Duration.ofMillis(
                        TimeUnit.SECONDS.toMillis(parameterTool.getInt(
                                ParameterConstants.FLINK_MAXOUTOFORDERNESS
                        ))
                ))
                //时间戳生成器：提取事件流的event_time字段
                .withTimestampAssigner((eventKafkaDTO, eventKafkaTimestamp) -> Long.parseLong(eventKafkaDTO.getTimestamp()))
                // 空闲检查时间，防止水位线停止推进
                .withIdleness(Duration.ofSeconds(5));
    }
}
