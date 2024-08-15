package com.liboshuai.starlink.slr.connector.service.event;

import com.liboshuai.starlink.slr.connector.api.dto.EventDTO;
import com.liboshuai.starlink.slr.connector.pojo.vo.KafkaInfoVO;

import java.util.List;

public interface EventService {
    /**
     * 获取Kafka信息，包含是否可连接，并获取broker列表、topic列表、消费组列表等
     */
    KafkaInfoVO kafkaInfo();

    void batchUpload(List<EventDTO> eventDTOList);
}
