package com.liboshuai.starlink.slr.admin.api.dto.risk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RuleConditionDTO implements Serializable {
    private static final long serialVersionUID = 6654391504906658357L;

    private String conditionCode; // 条件编号

    private String ruleCode; // 规则编号

    private String eventCode; // 事件编号

    private String count; // 次数

    private LocalDateTime beginTime; // 开始时间

    private LocalDateTime endTime; // 结束时间

    private EventInfoDTO eventInfoDTO; // 事件
}
