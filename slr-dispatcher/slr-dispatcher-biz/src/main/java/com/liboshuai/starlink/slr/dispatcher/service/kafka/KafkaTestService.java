package com.liboshuai.starlink.slr.dispatcher.service.kafka;

import com.liboshuai.starlink.slr.dispatcher.pojo.vo.KafkaInfoVO;

public interface KafkaTestService {
    /**
     * 获取Kafka信息，包含是否可连接，并获取broker列表、topic列表、消费组列表等
     */
    KafkaInfoVO getKafkaInfo();
}
