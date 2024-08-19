package com.liboshuai.starlink.slr.connector.service.event.strategy;

import com.liboshuai.starlink.slr.admin.api.dto.event.EventDetailDTO;

import java.util.List;

/**
 * 上送事件策略抽象类
 */
public interface EventStrategy {
    /**
     * 数据的前置处理
     */
    void processAfter(List<EventDetailDTO> eventDetailDTOList);
}
