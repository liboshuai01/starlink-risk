package com.liboshuai.starlink.slr.admin.api.dto;

import com.liboshuai.starlink.slr.admin.api.enums.ChannelEnum;
import com.liboshuai.starlink.slr.framework.common.validation.InEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 上送kafka的事件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EventDTO implements Serializable {

    private static final long serialVersionUID = -3125924174631531244L;
    /**
     * 用户ID
     */
    @NotBlank
    private String userId;

    /**
     * 用户名称
     */
    @NotBlank
    private String username;

    /**
     * 事件ID
     */
    @NotBlank
    private String eventId;

    /**
     * 事件时间 {yyyy-MM-dd HH:mm:ss}
     */
    @NotBlank
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "eventTime 必须符合 yyyy-MM-dd HH:mm:ss 格式")
    private String eventTime;

    /**
     * 渠道 {@link ChannelEnum}
     */
    @NotBlank
    @InEnum(ChannelEnum.class)
    private String channel;

}
