package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 规则基本信息DTO对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class RuleInfoEntity extends BaseEntity {
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
     * 规则有效开始时间
     */
    private String expireBeginTime;
    /**
     * 规则有效结束时间
     */
    private String expireEndTime;
    /**
     * 规则条件组合操作符: 0-and；1-or
     */
    private Integer combinedConditionOperator;
    /**
     * 预警信息
     */
    private String warnMessage;
    /**
     * 预警间隔值（仅用于前端展示）
     */
    private Long warnIntervalValue;
    /**
     * 预警间隔单位（仅用于前端展示: 0-MILLISECOND; 1-SECOND; 2-MINUTE; 3-HOUR; 4-DAY; 5-WEEK; 6-MONTH; 7-YEAR）
     */
    private String warnIntervalUnit;
    /**
     * 预警间隔
     */
    private Long warnInterval;
    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;

}
