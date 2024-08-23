package com.liboshuai.starlink.slr.engine.api.dto;

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
    private static final long serialVersionUID = 1782667359139301103L;

    private String eventCode;
    private String channel;
    private String eventName;
    private String eventDesc;
    private List<EventAttributeDTO> eventAttribute;
}
