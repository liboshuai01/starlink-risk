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
public class RuleInfoDTO implements Serializable {
    private static final long serialVersionUID = -6940398101611093673L;

    private String channel; // 渠道

    private String ruleCode; // 规则编号

    private String ruleName; // 规则名称

    private String ruleDesc; // 规则描述

    private Integer status; // 状态：0-停用，1-启用

    private String conditionOperator; // 规则条件组合操作符

    private List<RuleConditionDTO> ruleConditionDTOList; // 规则条件集合

}
