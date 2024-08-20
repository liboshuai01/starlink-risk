package com.liboshuai.starlink.slr.engine.serialize;

import com.liboshuai.starlink.slr.engine.utils.log.ConsoleLogUtil;
import com.liboshuai.starlinkRisk.common.pojo.ChannelDataPO;
import com.liboshuai.starlinkRisk.common.pojo.SourcePO;
import com.liboshuai.starlinkRisk.common.pojo.SourceSerializePO;
import com.liboshuai.starlinkRisk.common.utils.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;

/**
 * author: liboshuai
 * description: 自定义Kafka反序列化类
 * date: 2023
 */
@Slf4j
public class KafkaSourceDeserializationSchema implements KafkaDeserializationSchema<SourcePO> {

    private static final String ENCODEING = "UTF8";

    /**
     * author: liboshuai
     * description: 判断当前位置是否到达数据流的末尾
     *
     * @param o:
     * @return boolean
     */
    @Override
    public boolean isEndOfStream(SourcePO o) {
        return false;
    }

    /**
     * author: liboshuai
     * description: 自定义反序列化的主要逻辑
     *
     * @param consumerRecord:
     * @return java.lang.Object
     */
    @Override
    public SourcePO deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        SourcePO sourcePO = new SourcePO();
        if (consumerRecord != null) {
            String value = new String(consumerRecord.value(), ENCODEING);
            try {
                SourceSerializePO sourceSerializePO = JsonUtil.jsonStr2Obj(value, SourceSerializePO.class);
                BeanUtils.copyProperties(sourceSerializePO, sourcePO);
                sourcePO.setChannelData(JsonUtil.jsonStr2Obj(sourceSerializePO.getChannelData(), ChannelDataPO.class));
            } catch (Exception e) {
                ConsoleLogUtil.log4j2Error("反序列化SourcePO时发生异常的原因：", e);
                return null;
            }
        }
        ConsoleLogUtil.debug("源数据算子: {}", JsonUtil.obj2JsonStr(sourcePO));
        return sourcePO;
    }

    /**
     * author: liboshuai
     * description: 指定反序列之后的数据类型
     */
    @Override
    public TypeInformation<SourcePO> getProducedType() {
        return TypeInformation.of(SourcePO.class);
    }
}
