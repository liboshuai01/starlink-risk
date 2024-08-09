package com.liboshuai.starlink.slr.connector.service.kafka;

import com.liboshuai.starlink.slr.connector.pojo.vo.KafkaInfoVO;

public interface KafkaTestService {
    /**
     * 获取Kafka信息，包含是否可连接，并获取broker列表、topic列表、消费组列表等
     */
    KafkaInfoVO getKafkaInfo();
}
