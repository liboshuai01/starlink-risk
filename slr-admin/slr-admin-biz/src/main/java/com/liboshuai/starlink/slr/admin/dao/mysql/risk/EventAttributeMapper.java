package com.liboshuai.starlink.slr.admin.dao.mysql.risk;

import com.liboshuai.starlink.slr.admin.pojo.entity.risk.EventAttributeEntity;
import com.liboshuai.starlink.slr.admin.pojo.entity.risk.RuleInfoEntity;
import com.liboshuai.starlink.slr.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventAttributeMapper extends BaseMapperX<EventAttributeEntity> {
}
