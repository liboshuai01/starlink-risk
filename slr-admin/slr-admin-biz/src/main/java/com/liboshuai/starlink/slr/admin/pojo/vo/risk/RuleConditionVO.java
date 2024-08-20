package com.liboshuai.starlink.slr.admin.pojo.vo.risk;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.liboshuai.starlink.slr.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;
import static com.liboshuai.starlink.slr.framework.common.util.date.DateUtils.TIME_ZONE_DEFAULT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleConditionVO implements Serializable {

    private static final long serialVersionUID = -8064333448905774995L;

    @Schema(description = "条件编号")
    private String conditionCode; // 条件编号

    @Schema(description = "规则编号")
    private String ruleCode; // 规则编号

    @Schema(description = "事件编号")
    private String eventCode; // 事件编号

    @Schema(description = "次数")
    private String count; // 次数

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = TIME_ZONE_DEFAULT)
    private LocalDateTime beginTime; // 开始时间

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = TIME_ZONE_DEFAULT)
    private LocalDateTime endTime; // 结束时间

}