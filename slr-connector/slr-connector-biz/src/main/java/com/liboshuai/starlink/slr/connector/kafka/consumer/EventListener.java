package com.liboshuai.starlink.slr.connector.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventListener {

//    @KafkaListener(
//            topics = "${slr-connector.kafka.source_topic}",
//            groupId = "${spring.kafka.consumer.group-id}"
//    )
//    public void setCommitType(ConsumerRecord<String, Object> record, Acknowledgment ack) {
//        log.info("消费kafka的消息");
//        log.info("内容：" + record.value());
//        log.info("分区：" + record.partition());
//        log.info("偏移量：" + record.offset());
//        log.info("创建消息的时间戳：" + record.timestamp());
//        ack.acknowledge();
//    }
}
