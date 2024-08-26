package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 在线规则数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class RuleOnlineCountEntity extends BaseEntity {
    private static final long serialVersionUID = -6940398101611093673L;

    /**
     * 规则在线数量
     */
    private Long onlineCount;

}
