package com.liboshuai.starlink.slr.engine.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 规则模型DTO对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RuleModelDTO implements Serializable {
    private static final long serialVersionUID = -3205613240738671165L;

    /**
     * 模型编号
     */
    private String modelCode;
    /**
     * 规则模型groovy代码
     */
    private String groovy;
    /**
     * 模型版本号
     */
    private Long version;
}