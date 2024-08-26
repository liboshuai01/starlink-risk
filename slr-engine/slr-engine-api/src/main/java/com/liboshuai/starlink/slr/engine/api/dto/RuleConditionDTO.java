package com.liboshuai.starlink.slr.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 规则条件DTO对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RuleConditionDTO implements Serializable {
    private static final long serialVersionUID = 1371182535943297209L;

    /**
     * 规则编号
     */
    private String ruleCode;
    /**
     * 事件编号
     */
    private String eventCode;
    /**
     * 事件阈值
     */
    private Long eventThreshold;
    /**
     * 条件类型：0-固定范围条件；1-滑动窗口条件
     */
    private Integer conditionType;
    /**
     * 固定范围开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;
    /**
     * 固定范围结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    /**
     * 滑动窗口大小
     */
    private Long windowSize;
    /**
     * 是否跨历史（其中周期规则固定为跨历史）：0-否，1-是
     */
    private Boolean isCrossHistory;
    /**
     * 跨历史时间点
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime crossHistoryTimeline;
    /**
     * 事件信息
     */
    private EventInfoDTO eventInfo;
}
