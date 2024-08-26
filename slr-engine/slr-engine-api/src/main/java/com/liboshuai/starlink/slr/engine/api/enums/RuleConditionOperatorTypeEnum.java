package com.liboshuai.starlink.slr.engine.api.enums;

import lombok.Getter;

/**
 * 规则条件操作符号枚举
 */
@Getter
public enum RuleConditionOperatorTypeEnum {
    AND(0, "与"),
    OR(1,"或")
    ;
    private final Integer code;
    private final String name;
    RuleConditionOperatorTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
