package com.liboshuai.starlink.slr.admin.api.dto.risk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EventInfoDTO implements Serializable {

    private static final long serialVersionUID = 1606329461650813780L;

    private String eventCode; // 事件编号

    private String channel; // 渠道

    private String eventName; // 事件名称

    private String eventDesc; // 事件描述

    private List<EventAttributeDTO> eventAttributeDTOList; // 事件属性
}