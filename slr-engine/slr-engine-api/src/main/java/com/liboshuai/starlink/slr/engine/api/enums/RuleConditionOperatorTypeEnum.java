package com.liboshuai.starlink.slr.engine.api.enums;

import lombok.Getter;

/**
 * 规则条件操作符号枚举
 */
@Getter
public enum RuleConditionOperatorTypeEnum {
    AND("AND", "与"),
    OR("OR","或")
    ;
    private final String code;
    private final String name;
    RuleConditionOperatorTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
