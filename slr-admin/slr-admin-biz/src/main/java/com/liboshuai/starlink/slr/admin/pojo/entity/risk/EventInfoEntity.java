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
@TableName("slr_event_info")
public class EventInfoEntity extends BaseDO {

    private static final long serialVersionUID = 1606329461650813780L;

    @TableId
    private Long id; // ID

    private String eventCode; // 事件编号

    private String channel; // 渠道

    private String eventName; // 事件名称

    private String eventDesc; // 事件描述

    private String eventAttribute; // 事件属性
}