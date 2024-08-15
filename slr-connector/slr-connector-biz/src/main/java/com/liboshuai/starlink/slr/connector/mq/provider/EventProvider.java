package com.liboshuai.starlink.slr.connector.mq.provider;

import com.liboshuai.starlink.slr.connector.api.dto.EventDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Component
public class EventProvider {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${slr-connector.kafka.source_topic}")
    private String sourceTopic;

    /**
     * 批量上送事件信息到kafka
     */
    public void batchSend(List<EventDTO> eventDTOList) {
        eventDTOList.forEach(eventDTO -> kafkaTemplate.send(sourceTopic, eventDTO));
    }
}
