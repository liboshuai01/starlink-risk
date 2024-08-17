package com.liboshuai.starlink.slr.connector.service.event.strategy;

import com.liboshuai.starlink.slr.admin.api.dto.EventErrorDTO;
import com.liboshuai.starlink.slr.admin.api.dto.EventUploadDTO;

import java.util.List;

/**
 * 上送事件策略抽象类
 */
public interface EventStrategy {
    /**
     * 数据的前置处理
     */
    void processAfter(List<EventUploadDTO> eventUploadDTOList, List<EventErrorDTO> eventErrorDTOList);
}
