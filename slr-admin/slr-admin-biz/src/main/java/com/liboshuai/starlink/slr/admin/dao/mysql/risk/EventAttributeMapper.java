package com.liboshuai.starlink.slr.admin.dao.mysql.risk;

import com.liboshuai.starlink.slr.admin.pojo.entity.risk.EventAttributeEntity;
import com.liboshuai.starlink.slr.admin.pojo.entity.risk.EventInfoEntity;
import com.liboshuai.starlink.slr.admin.pojo.entity.risk.RuleInfoEntity;
import com.liboshuai.starlink.slr.framework.mybatis.core.mapper.BaseMapperX;
import com.liboshuai.starlink.slr.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EventAttributeMapper extends BaseMapperX<EventAttributeEntity> {

    default List<EventAttributeEntity> selectListByEventCode(List<String> eventCodeList) {
        return selectList(new LambdaQueryWrapperX<EventAttributeEntity>()
                .in(EventAttributeEntity::getEventCode, eventCodeList)
                .orderByAsc(EventAttributeEntity::getId));
    }
}
