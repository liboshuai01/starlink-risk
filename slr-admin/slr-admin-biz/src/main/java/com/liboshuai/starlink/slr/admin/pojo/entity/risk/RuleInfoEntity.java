package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("slr_rule_info")
public class RuleInfoEntity extends BaseDO {
    private static final long serialVersionUID = -3133308431453118692L;

    private String channel; // 渠道

    private String ruleCode; // 规则编号

    private String ruleName; // 规则名称

    private String ruleDesc; // 规则描述

    private Integer status; // 状态：0-停用，1-启用

    private String conditionOperator; // 规则条件组合操作符
}
