package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 事件信息DTO对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class EventInfoEntity extends BaseEntity {
    private static final long serialVersionUID = 1782667359139301103L;

    /**
     * 事件编号
     */
    private String eventCode;
    /**
     * 渠道
     */
    private String channel;
    /**
     * 事件名称
     */
    private String eventName;
    /**
     * 事件描述
     */
    private String eventDesc;
}
