package com.liboshuai.starlink.slr.engine.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 在线规则数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RuleOnlineCountDTO implements Serializable {
    private static final long serialVersionUID = -6940398101611093673L;

    /**
     * 规则在线数量
     */
    private Long onlineCount;

}
