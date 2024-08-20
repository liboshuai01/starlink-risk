package com.liboshuai.starlink.slr.engine.utils.data;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.liboshuai.starlink.slr.engine.common.ParameterConstants;
import com.liboshuai.starlink.slr.engine.pojo.RuleMeta;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.roaringbitmap.longlong.Roaring64Bitmap;

import java.nio.ByteBuffer;

/**
 * @Author: liboshuai
 * @Date: 2023-10-25 13:11
 **/
@Slf4j
public class MysqlUtil {

    /**
     * MySql cdc 读取数据
     */
    public static DataStream<RuleMeta> read(
            StreamExecutionEnvironment env,
            ParameterTool parameterTool) {

        String hostname = parameterTool.get(ParameterConstants.MYSQL_HOSTNAME);
        String port = parameterTool.get(ParameterConstants.MYSQL_PORT);
        String username = parameterTool.get(ParameterConstants.MYSQL_USERNAME);
//        String password = CryptoUtils.decrypt(parameterTool.get(ParameterConstants.MYSQL_PASSWORD));
        String password = parameterTool.get(ParameterConstants.MYSQL_PASSWORD);
        String database = parameterTool.get(ParameterConstants.MYSQL_DATABASE);
        String table = parameterTool.get(ParameterConstants.MYSQL_TABLE_RULE);

        MySqlSource<String> ruleCdcSource = MySqlSource.<String>builder()
                .hostname(hostname)
                .port(Integer.parseInt(port))
                .username(username)
                .password(password)
                .databaseList(database)
                .tableList(table)
                .startupOptions(StartupOptions.initial())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();
        DataStreamSource<String> dataStreamSource = env.fromSource(ruleCdcSource, WatermarkStrategy.noWatermarks(), "ruleSource");

        // 对抓取到的ruleMeta变更原始json数据，进行解析、提取
        SingleOutputStreamOperator<RuleMeta> ruleMetaBeans = dataStreamSource.map(json -> {

            JSONObject jsonObject = JSON.parseObject(json);
            String op = jsonObject.getString("op");

            JSONObject dataObj;
            if (op.equals("d")) {
                dataObj = jsonObject.getJSONObject("before");
            } else {
                dataObj = jsonObject.getJSONObject("after");
            }

            byte[] bytes = dataObj.getBytes("pre_select_users");

            String jsonString = dataObj.toJSONString();
            RuleMeta ruleMeta = JSON.parseObject(jsonString, RuleMeta.class);

            // 填充op字段
            ruleMeta.setOp(op);

            // 反序列化 bitmap的字节数组
            byte[] preSelectUsersBytes = ruleMeta.getPre_select_users();
            Roaring64Bitmap bitmap = Roaring64Bitmap.bitmapOf();

            if (preSelectUsersBytes != null) {
                bitmap.deserialize(ByteBuffer.wrap(preSelectUsersBytes));
                log.info("反序列化得到的bitmap是: {}", bitmap);
            }
            // 填充bitmap字段
            ruleMeta.setPreSelectBitmap(bitmap);

            return ruleMeta;
        });


        return ruleMetaBeans;
    }
}
