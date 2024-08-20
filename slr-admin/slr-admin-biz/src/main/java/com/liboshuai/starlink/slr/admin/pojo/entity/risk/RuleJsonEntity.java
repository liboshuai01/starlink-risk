package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("slr_rule_json")
public class RuleJsonEntity extends BaseDO {

    private static final long serialVersionUID = 8848773086432812465L;

    private String ruleCode; // 规则编号

    private String ruleJson; // 规则json

}
