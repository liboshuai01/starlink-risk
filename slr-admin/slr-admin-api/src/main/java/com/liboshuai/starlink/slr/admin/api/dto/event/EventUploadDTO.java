package com.liboshuai.starlink.slr.admin.api.dto.event;

import com.liboshuai.starlink.slr.admin.api.constants.event.ChannelConstants;
import com.liboshuai.starlink.slr.admin.api.enums.event.ChannelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 上送事件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EventUploadDTO implements Serializable {

    private static final long serialVersionUID = -3125924174631531244L;

    /**
     * 渠道
     * {@link ChannelConstants}
     * {@link ChannelEnum}
     */
    private String channel;

    /**
     * 上送事件详情集合
     */
    private List<EventDetailDTO> eventDetailDTOList;

}
