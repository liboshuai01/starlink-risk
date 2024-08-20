package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlinkRisk.common.pojo.WindowPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.functions.KeySelector;

/**
 * author: liboshuai
 * description: 按照渠道进行分组
 * date: 2023
 */
@Slf4j
public class WindowPoUserIdKeyBy implements KeySelector<WindowPO, String> {

    @Override
    public String getKey(WindowPO windowPO) {
        return windowPO.getSourcePO().getChannelData().getUserId();
    }
}
