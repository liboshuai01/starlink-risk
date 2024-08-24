package com.liboshuai.starlink.slr.engine.api.enums;

import lombok.Getter;

/**
 * 规则类型枚举
 */
@Getter
public enum RuleTypeEnum {
    RANGE_RULE(0, "范围规则"),
    PERIODIC_RULE(1,"周期规则")
    ;
    private final Integer code;
    private final String name;
    RuleTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
