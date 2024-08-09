package com.liboshuai.starlink.slr.dispatcher.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * Kafka信息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class KafkaInfoVO implements Serializable {
    private String bootstrapServers;
    private boolean success;
    private String errorMessage;
    private List<String> brokers;
    private List<String> topics;
    private List<String> consumerGroups;
}
