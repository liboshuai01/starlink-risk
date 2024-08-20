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
public class EventInfoVO implements Serializable {

    private static final long serialVersionUID = 1606329461650813780L;

    @Schema(description = "事件编号")
    private String eventCode; // 事件编号

    @Schema(description = "渠道")
    private String channel; // 渠道

    @Schema(description = "事件名称")
    private String eventName; // 事件名称

    @Schema(description = "事件描述")
    private String eventDesc; // 事件描述

}