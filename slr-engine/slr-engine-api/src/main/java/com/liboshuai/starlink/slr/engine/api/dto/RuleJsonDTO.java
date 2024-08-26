package com.liboshuai.starlink.slr.engine.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 规则json字符串DTO对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RuleJsonDTO implements Serializable {
    private static final long serialVersionUID = -6940398101611093673L;

    /**
     * 规则编号
     */
    private String ruleCode;

    /**
     * 规则json
     */
    private String ruleJson;

}
