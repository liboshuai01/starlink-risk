package com.liboshuai.starlink.slr.engine.api.enums;

import lombok.Getter;

/**
 * 规则状态枚举
 */
@Getter
public enum RuleStatusEnum {
    DISABLE(0, "停用"),
    ENABLE(1,"启用")
    ;
    private final Integer code;
    private final String name;
    RuleStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
