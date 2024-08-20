package com.liboshuai.starlink.slr.admin.api.dto.risk;

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

    private static final long serialVersionUID = 7349966693249473414L;

    private String attributeCode; // 属性编号

    private String eventCode; // 事件编号

    private String fieldName; // 字段名称

    private String fieldDesc; // 字段描述

    private String fieldType; // 字段类型

}
