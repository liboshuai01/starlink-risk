package com.liboshuai.starlink.slr.engine.serialize;

import com.liboshuai.starlinkRisk.common.utils.json.JsonUtil;
import com.liboshuai.starlink.slr.engine.pojo.EventBean;
import com.liboshuai.starlink.slr.engine.utils.log.ConsoleLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * author: liboshuai
 * description: 自定义Kafka反序列化类
 * date: 2023
 */
@Slf4j
public class KafkaSourceDeserializationSchema implements KafkaDeserializationSchema<EventBean> {

    private static final String ENCODE = "UTF8";

    /**
     * author: liboshuai
     * description: 判断当前位置是否到达数据流的末尾
     *
     * @param o:
     * @return boolean
     */
    @Override
    public boolean isEndOfStream(EventBean o) {
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
    public EventBean deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        EventBean eventBean = new EventBean();
        if (consumerRecord != null) {
            String value = new String(consumerRecord.value(), ENCODE);
            try {
                eventBean =  JsonUtil.jsonStr2Obj(value, EventBean.class);
            } catch (Exception e) {
                ConsoleLogUtil.log4j2Error("反序列化SourcePO时发生异常的原因：", e);
                return null;
            }
        }
        ConsoleLogUtil.debug("源数据算子: {}", JsonUtil.obj2JsonStr(eventBean));
        return eventBean;
    }

    /**
     * author: liboshuai
     * description: 指定反序列之后的数据类型
     */
    @Override
    public TypeInformation<EventBean> getProducedType() {
        return TypeInformation.of(EventBean.class);
    }
}
