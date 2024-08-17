package com.liboshuai.starlink.slr.admin.api.dto;

import com.liboshuai.starlink.slr.admin.api.constants.ChannelConstants;
import com.liboshuai.starlink.slr.admin.api.enums.ChannelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 上送kafka的事件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EventUploadDTO implements Serializable {

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
     * 事件时间 {yyyy-MM-dd HH:mm:ss}
     */
    private String eventTime;

    /**
     * 渠道
     * {@link ChannelConstants}
     * {@link ChannelEnum}
     */
    private String channel;

}
