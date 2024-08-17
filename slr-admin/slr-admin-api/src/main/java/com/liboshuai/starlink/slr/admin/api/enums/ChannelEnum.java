package com.liboshuai.starlink.slr.admin.api.enums;

import com.liboshuai.starlink.slr.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ChannelEnum  implements IntArrayValuable {

    GAME(0,"游戏"),
    HJF(1, "花积分");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(ChannelEnum::getCode).toArray();

    /**
     * 编号
     */
    private final Integer code;
    /**
     * 名称
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
