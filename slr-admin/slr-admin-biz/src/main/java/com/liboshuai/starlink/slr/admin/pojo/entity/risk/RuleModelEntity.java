package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 规则模型DTO对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class RuleModelEntity extends BaseEntity {
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