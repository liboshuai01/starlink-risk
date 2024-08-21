package com.liboshuai.starlink.slr.admin.api.dto.event;

import com.liboshuai.starlink.slr.admin.api.constants.event.ChannelConstants;
import com.liboshuai.starlink.slr.admin.api.enums.event.ChannelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 上送事件Kafka DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EventKafkaDTO implements Serializable {

    private static final long serialVersionUID = -3125924174631531244L;

    /**
     * 渠道
     * {@link ChannelConstants}
     * {@link ChannelEnum}
     */
    private String channel;

    /**
     * 时间戳（毫秒级别13位）
     */
    private String timestamp;

    /**
     * key编号
     */
    private String keyCode;

    /**
     * key名称
     */
    private String keyValue;

    /**
     * 事件编号
     */
    private String eventCode;

    /**
     * 事件值
     */
    private String eventValue;

    /**
     * 属性
     */
    private Map<String, Object> attribute;

}
