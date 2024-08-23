package com.liboshuai.starlink.slr.connector.service.event;

import com.liboshuai.starlink.slr.connector.pojo.vo.event.KafkaInfoVO;
import com.liboshuai.starlink.slr.engine.api.dto.EventKafkaDTO;

public interface EventService {
    /**
     * 获取Kafka信息，包含是否可连接，并获取broker列表、topic列表、消费组列表等
     */
    KafkaInfoVO kafkaInfo();


    /**
     * 上送事件数据到kafka
     */
    void uploadKafka(EventKafkaDTO eventKafkaDTO);
}
