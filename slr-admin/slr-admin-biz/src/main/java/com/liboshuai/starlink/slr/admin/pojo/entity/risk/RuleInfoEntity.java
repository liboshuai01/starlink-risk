package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("slr_rule_info")
public class RuleInfoEntity extends BaseDO {
    private static final long serialVersionUID = -3133308431453118692L;

    @TableId
    private Long id; // ID

    private String channel; // 渠道

    private String ruleCode; // 规则编号

    private String ruleName; // 规则名称

    private String ruleDesc; // 规则描述

    private Integer status; // 状态：0-停用，1-启用

    private String conditionCodeList; // 规则条件编号列表

    private String conditionOperator; // 规则条件组合操作符
}
