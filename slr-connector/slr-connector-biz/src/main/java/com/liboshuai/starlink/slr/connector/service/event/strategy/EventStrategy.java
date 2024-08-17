package com.liboshuai.starlink.slr.connector.service.event.strategy;

/**
 * 上送事件策略抽象类
 */
public interface EventStrategy {
    /**
     * 数据的前置处理
     */
    void processAfter();
}
