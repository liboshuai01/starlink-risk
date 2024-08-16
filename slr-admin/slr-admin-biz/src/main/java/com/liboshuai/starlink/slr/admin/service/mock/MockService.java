package com.liboshuai.starlink.slr.admin.service.mock;

public interface MockService {
    /**
     * 生成测试数据并写入文件
     * @param startMillis 开始的时间戳
     * @param durationMillis 时间跨度
     * @param perSecondCount 每秒生成的时间戳数量
     */
    void generatorDataToFile(long startMillis, long durationMillis, int perSecondCount);
}
