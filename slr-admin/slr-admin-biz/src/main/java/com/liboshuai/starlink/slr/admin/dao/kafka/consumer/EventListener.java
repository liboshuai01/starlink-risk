package com.liboshuai.starlink.slr.admin.dao.kafka.consumer;

import com.liboshuai.starlink.slr.admin.common.constants.KafkaTopicConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class EventListener {

    @KafkaListener(topics = KafkaTopicConstants.SLR_EVENT)
    public void event(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        log.info("事件内容：{}", record.value());
        ack.acknowledge();
    }
}
