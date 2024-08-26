package com.liboshuai.starlink.slr.admin.dao.mysql.risk;

import com.liboshuai.starlink.slr.framework.mybatis.core.mapper.BaseMapperX;
import com.liboshuai.starlink.slr.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RuleInfoMapper extends BaseMapperX<RuleInfoEntity> {

    default RuleInfoEntity selectOneByRuleCode(String ruleCode) {
        return selectOne(new LambdaQueryWrapperX<RuleInfoEntity>()
                .eq(RuleInfoEntity::getRuleCode, ruleCode)
                .orderByAsc(RuleInfoEntity::getId));
    }
}
