package com.liboshuai.starlink.slr.admin.dao.mysql.risk;

import com.liboshuai.starlink.slr.admin.pojo.entity.risk.EventInfoEntity;
import com.liboshuai.starlink.slr.framework.mybatis.core.mapper.BaseMapperX;
import com.liboshuai.starlink.slr.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EventInfoMapper extends BaseMapperX<EventInfoEntity> {

    default List<EventInfoEntity> selectListByEventCode(List<String> eventCodeList) {
        return selectList(new LambdaQueryWrapperX<EventInfoEntity>()
                .in(EventInfoEntity::getEventCode, eventCodeList)
                .orderByAsc(EventInfoEntity::getId));
    }

}
