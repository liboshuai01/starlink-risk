package com.liboshuai.starlink.slr.engine.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 事件信息DTO对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EventInfoDTO implements Serializable {
    private static final long serialVersionUID = 1782667359139301103L;

    /**
     * 事件编号
     */
    private String eventCode;
    /**
     * 渠道
     */
    private String channel;
    /**
     * 事件名称
     */
    private String eventName;
    /**
     * 事件描述
     */
    private String eventDesc;
    /**
     * 事件属性组
     */
    private List<EventAttributeDTO> eventAttributeGroup;
}
