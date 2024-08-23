package com.liboshuai.starlink.slr.engine.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EventAttributeDTO implements Serializable {
    private static final long serialVersionUID = -2462792763696684790L;

    private String attributeCode;
    private String eventCode;
    private String fieldName;
    private String fieldDesc;
}
