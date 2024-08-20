package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlinkRisk.common.pojo.SourcePO;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.functions.KeySelector;

/**
 * author: liboshuai
 * description: 按照渠道进行分组
 * date: 2023
 */
@Slf4j
public class SourcePOKeyBy implements KeySelector<SourcePO, String> {

    @Override
    public String getKey(SourcePO sourcePO) {
        return sourcePO.getChannelData().getUserId();
    }
}
