package com.liboshuai.starlink.slr.engine.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 规则基本信息DTO对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RuleInfoDTO implements Serializable {
    private static final long serialVersionUID = 3075195061361364547L;

    /**
     * 渠道
     */
    private String channel;
    /**
     * 规则编号
     */
    private String ruleCode;
    /**
     * 模型编号
     */
    private String modelCode;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 规则描述
     */
    private String ruleDesc;
    /**
     * 规则条件组合操作符: 0-and；1-or
     */
    private Integer combinedConditionOperator;
    /**
     * 预警信息
     */
    private String warnMessage;
    /**
     * 预警间隔
     */
    private Long warnInterval;
    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;
    /**
     * 规则条件组
     */
    private List<RuleConditionDTO> ruleConditionGroup;
    /**
     * 规则模型groovy代码
     */
    private String ruleModelGroovyCode;
}
