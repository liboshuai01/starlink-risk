package com.liboshuai.starlink.slr.engine.utils.date;


import com.liboshuai.starlink.slr.engine.common.RuleUnitEnum;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author liboshuai
 * @Date 2023/10/31 0:45
 * 时间工具类
 */
public class TimeUtil {
    /**
     * 将时间转换为毫秒
     *
     * @param time 时间
     * @param unit 单位
     * @return 毫秒值
     */
    public static long toMillis(long time, RuleUnitEnum unit) {
        if (Objects.isNull(unit)) {
            throw new IllegalArgumentException("Invalid windowSizeUnit is null！");
        }
        switch (unit) {
            case MILLISECOND:
                return time;
            case SECOND:
                return TimeUnit.SECONDS.toMillis(time);
            case MINUTE:
                return TimeUnit.MINUTES.toMillis(time);
            case HOUR:
                return TimeUnit.HOURS.toMillis(time);
            case DAY:
                return TimeUnit.DAYS.toMillis(time);
            case WEEK:
                return TimeUnit.DAYS.toMillis(time * 7);
            case MONTH:
                // Assuming 30 days in a month
                return TimeUnit.DAYS.toMillis(time * 30);
            case YEAR:
                // Assuming 365 days in a year
                return TimeUnit.DAYS.toMillis(time * 365);
            default:
                throw new IllegalArgumentException("Invalid windowSizeUnit: " + unit);
        }
    }
}
