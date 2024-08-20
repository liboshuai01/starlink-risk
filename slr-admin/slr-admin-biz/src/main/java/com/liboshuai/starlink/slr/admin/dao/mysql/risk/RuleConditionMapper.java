package com.liboshuai.starlink.slr.admin.dao.mysql.risk;

import com.liboshuai.starlink.slr.admin.pojo.entity.risk.EventInfoEntity;
import com.liboshuai.starlink.slr.admin.pojo.entity.risk.RuleConditionEntity;
import com.liboshuai.starlink.slr.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RuleConditionMapper extends BaseMapperX<RuleConditionEntity> {
}
