package com.liboshuai.starlink.slr.admin.pojo.entity.risk;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liboshuai.starlink.slr.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("slr_event_attribute")
public class EventAttributeEntity extends BaseDO {

    private static final long serialVersionUID = 7349966693249473414L;

    @TableId
    private Long id; // ID

    private String attributeCode; // 属性编号

    private String eventCode; // 事件编号

    private String fieldName; // 字段名称

    private String fieldDesc; // 字段描述

    private String fieldType; // 字段类型

}
