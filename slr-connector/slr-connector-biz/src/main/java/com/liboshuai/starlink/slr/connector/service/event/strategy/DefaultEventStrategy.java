package com.liboshuai.starlink.slr.connector.service.event.strategy;

import com.liboshuai.starlink.slr.admin.api.annotaion.StrategyIdentifier;
import com.liboshuai.starlink.slr.admin.api.constants.DefaultConstants;
import com.liboshuai.starlink.slr.admin.api.enums.ChannelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认事件上送策略类
 */
@Slf4j
@Component
@StrategyIdentifier({DefaultConstants.DEFAULT_STRATEGY})
public class DefaultEventStrategy implements EventStrategy {
    /**
     * 数据的前置处理
     */
    @Override
    public void processAfter() {
        // 暂时没有处理逻辑

    }
}
