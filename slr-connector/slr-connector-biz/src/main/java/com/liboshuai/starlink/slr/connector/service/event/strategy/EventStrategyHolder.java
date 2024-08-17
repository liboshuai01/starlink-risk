package com.liboshuai.starlink.slr.connector.service.event.strategy;

import com.liboshuai.starlink.slr.admin.api.constants.DefaultConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * EventStrategyHolder 是一个用于管理和获取事件策略的容器类。
 * 它根据渠道编号将不同的策略实现映射到一个策略映射表中。
 */
@Component
public class EventStrategyHolder {

    // 用于存储渠道编号和对应策略实现的映射
    private final Map<String, EventStrategy> strategyMap = new HashMap<>();

    /**
     * 构造函数，通过 Spring 自动注入所有实现了 EventStrategy 接口的策略实例。
     *
     * @param eventStrategyList 包含所有策略实现的列表
     */
    @Autowired
    public EventStrategyHolder(List<EventStrategy> eventStrategyList) {
        // 构建策略映射
        eventStrategyList.forEach(eventStrategy -> {
            // 获取策略类上的 EventStrategyTag 注解
            EventStrategyTag eventStrategyTag = eventStrategy.getClass().getAnnotation(EventStrategyTag.class);
            if (Objects.nonNull(eventStrategyTag)) {
                // 将每个渠道编号与策略实现关联
                for (String channel : eventStrategyTag.channels()) {
                    strategyMap.put(channel, eventStrategy);
                }
            }
        });
    }

    /**
     * 根据渠道编号获取对应的策略实现。
     * 如果未找到对应的策略，则返回默认策略。
     *
     * @param channel 渠道编号
     * @return 对应的事件策略实现
     */
    public EventStrategy getByChannel(String channel) {
        return strategyMap.getOrDefault(channel, strategyMap.get(DefaultConstants.DEFAULT_STRATEGY));
    }
}
