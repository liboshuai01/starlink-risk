package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 规则json字符串DTO对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class RuleJsonEntity extends BaseEntity {
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
