package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class EventAttributeEntity extends BaseEntity {
    private static final long serialVersionUID = -2462792763696684790L;

    /**
     * 事件编号
     */
    private String eventCode;
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 字段描述
     */
    private String fieldDesc;
}
