package com.liboshuai.starlink.slr.admin.pojo.vo.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventAttributeVO implements Serializable {

    private static final long serialVersionUID = 1606329461650813780L;

    @Schema(description = "属性编号")
    private String attributeCode; // 属性编号

    @Schema(description = "事件编号")
    private String eventCode; // 事件编号

    @Schema(description = "字段名称")
    private String fieldName; // 字段名称

    @Schema(description = "字段描述")
    private String fieldDesc; // 字段描述

    @Schema(description = "字段类型")
    private String fieldType; // 字段类型

}