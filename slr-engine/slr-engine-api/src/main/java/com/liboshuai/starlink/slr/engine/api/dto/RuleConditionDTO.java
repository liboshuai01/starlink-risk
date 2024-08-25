package com.liboshuai.starlink.slr.engine.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RuleConditionDTO implements Serializable {
    private static final long serialVersionUID = 1371182535943297209L;

    private String conditionCode;
    private String ruleCode;
    private String eventCode;
    private String eventThreshold;
    private String windowSizeValue;
    private String windowSizeUnit;
    // todo: 数据库待补充
    private String windowSize;
    private String beginTime;
    private String endTime;
    private EventInfoDTO eventInfo;
}
