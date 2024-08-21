package com.liboshuai.starlink.slr.engine.serialize;

import com.alibaba.fastjson2.JSONObject;
import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
import com.liboshuai.starlink.slr.engine.utils.FastJsonUtil;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

public class MysqlCdcDeserializationSchema implements DebeziumDeserializationSchema<RuleCdcDTO> {

    private static final long serialVersionUID = -4554108517291370408L;

    /**
     * 获取数据库名、表名
     */
    private JSONObject getDatabaseTableJson(SourceRecord sourceRecord) {
        String topic = sourceRecord.topic();
        String[] fields = topic.split("\\.");
        String database = fields[1];
        String tableName = fields[2];
        JSONObject ret = new JSONObject();
        ret.put("database", database);
        ret.put("table", tableName);
        return ret;
    }

    /**
     * 获取before、after数据
     */
    public JSONObject getDataJson(SourceRecord sourceRecord, String fieldName) {
        Struct value = (Struct) sourceRecord.value();
        Struct struct = value.getStruct(fieldName);
        JSONObject ret = new JSONObject();
        if (struct != null) {
            Schema schema = struct.schema();
            List<Field> fields = schema.fields();
            for (Field field : fields) {
                Object obj = struct.get(field);
                ret.put(field.name(), obj);
            }
        }
        return ret;
    }

    public String getOP(SourceRecord sourceRecord) {
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
//        String type = operation.toString().toLowerCase();
        return operation.code();
    }

    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<RuleCdcDTO> collector) {
        //1.创建 JSON 对象用于存储最终数据
        JSONObject result = new JSONObject();
        //2.获取库名、表名放入 source
        JSONObject databaseTableJson = getDatabaseTableJson(sourceRecord);
        //3.获取"before"数据
        JSONObject beforeJson = getDataJson(sourceRecord, "before");
        //4.获取"after"数据
        JSONObject afterJson = getDataJson(sourceRecord, "after");
        //5.获取操作类型 CREATE UPDATE DELETE 进行符合 Debezium-op 的字母
        String type = getOP(sourceRecord);
        //6.将字段写入 JSON 对象
        result.put("source", databaseTableJson);
        result.put("before", beforeJson);
        result.put("after", afterJson);
        result.put("op", type);
        RuleCdcDTO RuleCdcDTO = FastJsonUtil.jsonStr2ObjSnakeCase(result.toJSONString(), RuleCdcDTO.class);
        //7.输出数据
        collector.collect(RuleCdcDTO);
    }

    @Override
    public TypeInformation<RuleCdcDTO> getProducedType() {
        // 表示返回String类型
        return TypeInformation.of(RuleCdcDTO.class);
    }
}
