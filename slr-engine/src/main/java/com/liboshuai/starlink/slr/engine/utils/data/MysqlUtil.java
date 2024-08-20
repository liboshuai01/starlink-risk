package com.liboshuai.starlink.slr.engine.utils.data;

import com.liboshuai.starlink.slr.engine.common.ParameterConstants;
import com.liboshuai.starlink.slr.engine.serialize.MysqlCdcDeserializationSchema;
import com.liboshuai.starlinkRisk.common.pojo.RuleCdcPO;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author: liboshuai
 * @Date: 2023-10-25 13:11
 **/
public class MysqlUtil {

    /**
     * MySql cdc 读取数据
     */
    public static DataStream<RuleCdcPO> read(
            StreamExecutionEnvironment env,
            ParameterTool parameterTool) {

        String hostname = parameterTool.get(ParameterConstants.MYSQL_HOSTNAME);
        String port = parameterTool.get(ParameterConstants.MYSQL_PORT);
        String username = parameterTool.get(ParameterConstants.MYSQL_USERNAME);
//        String password = CryptoUtils.decrypt(parameterTool.get(ParameterConstants.MYSQL_PASSWORD));
        String password = parameterTool.get(ParameterConstants.MYSQL_PASSWORD);
        String database = parameterTool.get(ParameterConstants.MYSQL_DATABASE);
        String table = parameterTool.get(ParameterConstants.MYSQL_TABLE_RULE);

        MySqlSource<RuleCdcPO> ruleCdcSource = MySqlSource.<RuleCdcPO>builder()
                .hostname(hostname)
                .port(Integer.parseInt(port))
                .username(username)
                .password(password)
                .databaseList(database)
                .tableList(table)
                .startupOptions(StartupOptions.initial())
                .deserializer(new MysqlCdcDeserializationSchema())
                .build();

        return env.fromSource(ruleCdcSource, WatermarkStrategy.noWatermarks(), "ruleSource");
    }
}
