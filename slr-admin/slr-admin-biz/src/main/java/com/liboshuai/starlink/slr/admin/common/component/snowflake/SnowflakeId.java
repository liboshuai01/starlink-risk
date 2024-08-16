package com.liboshuai.starlink.slr.admin.common.component.snowflake;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 根据hutool封装的雪花算法ID生成器
 */
@Component
public class SnowflakeId {


    private final cn.hutool.core.lang.Snowflake snowflake;

    @Autowired
    public SnowflakeId(SnowflakeIdProperties properties) {
        long workerIdLong = Long.parseLong(properties.getWorkerId());
        long datacenterIdLong = Long.parseLong(properties.getDatacenterId());
        snowflake = IdUtil.getSnowflake(workerIdLong, datacenterIdLong);
    }

    public long nextId() {
        return snowflake.nextId();
    }

    public String nextIdStr() {
        return snowflake.nextIdStr();
    }
}
