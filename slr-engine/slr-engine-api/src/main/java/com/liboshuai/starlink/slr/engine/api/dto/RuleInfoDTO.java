package com.liboshuai.starlink.slr.engine.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RuleInfoDTO implements Serializable {
    private static final long serialVersionUID = 3075195061361364547L;

    private String channel;
    private String ruleCode;
    private String modelCode;
    private Integer ruleType;
    private String ruleName;
    private String ruleDesc;
    private Boolean crossHistory;
    private String historyTimeline;
    private String expireBeginTime;
    private String expireEndTime;
    private String conditionOperator;
    private String warningMessage;
    private String warningIntervalValue;
    private String warningIntervalUnit;
    private Integer status;
    private List<RuleConditionDTO> ruleConditionList;
    private RuleModelDTO ruleModel;
}
