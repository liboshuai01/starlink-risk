package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 规则条件DTO对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class RuleConditionEntity extends BaseEntity {
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
    private LocalDateTime beginTime;
    /**
     * 固定范围结束时间
     */
    private LocalDateTime endTime;
    /**
     * 滑动窗口大小值（仅用于前端展示）
     */
    private Long windowSizeValue;
    /**
     * 滑动窗口大小单位（仅用于前端展示）: 0-MILLISECOND; 1-SECOND; 2-MINUTE; 3-HOUR; 4-DAY; 5-WEEK; 6-MONTH; 7-YEAR
     */
    private String windowSizeUnit;
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
    private LocalDateTime crossHistoryTimeline;

}
