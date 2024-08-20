package com.liboshuai.starlink.slr.admin.api.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 上送事件详情DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EventDetailDTO implements Serializable {

    private static final long serialVersionUID = -3125924174631531244L;
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 事件时间戳（毫秒级别13位）
     */
    private Long eventTimestamp;

    /**
     * 事件属性
     */
    private Map<String, Object> eventAttributes;

}
