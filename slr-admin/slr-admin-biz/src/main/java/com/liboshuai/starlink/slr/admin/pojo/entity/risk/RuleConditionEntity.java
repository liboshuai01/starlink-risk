package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("slr_rule_condition")
public class RuleConditionEntity extends BaseDO {

    private static final long serialVersionUID = -8064333448905774995L;

    private String conditionCode; // 条件编号

    private String ruleCode; // 规则编号

    private String eventCode; // 事件编号

    private String count; // 次数

    private LocalDateTime beginTime; // 开始时间

    private LocalDateTime endTime; // 结束时间

}