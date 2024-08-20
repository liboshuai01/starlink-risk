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
public class RuleInfoVO implements Serializable {
    private static final long serialVersionUID = -3133308431453118692L;

    @Schema(description = "渠道")
    private String channel; // 渠道

    @Schema(description = "规则编号")
    private String ruleCode; // 规则编号

    @Schema(description = "规则名称")
    private String ruleName; // 规则名称

    @Schema(description = "规则描述")
    private String ruleDesc; // 规则描述

    @Schema(description = "状态：0-停用，1-启用")
    private Integer status; // 状态：0-停用，1-启用

    @Schema(description = "规则条件组合操作符")
    private String conditionOperator; // 规则条件组合操作符

}
