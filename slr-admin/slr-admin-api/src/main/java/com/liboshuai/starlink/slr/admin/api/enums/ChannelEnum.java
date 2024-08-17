package com.liboshuai.starlink.slr.admin.api.enums;

import com.liboshuai.starlink.slr.admin.api.constants.ChannelConstants;
import com.liboshuai.starlink.slr.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 渠道枚举
 * {@link ChannelConstants}
 */
@Getter
@AllArgsConstructor
public enum ChannelEnum {

    GAME("game","游戏"),
    HJF("hjf", "花积分"),
    MALL("mall", "商场")
    ;

    /**
     * 编号
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;
}
