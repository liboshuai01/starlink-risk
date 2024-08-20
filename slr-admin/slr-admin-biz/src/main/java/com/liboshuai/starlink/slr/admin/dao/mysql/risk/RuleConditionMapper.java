package com.liboshuai.starlink.slr.admin.dao.mysql.risk;

import com.liboshuai.starlink.slr.admin.pojo.entity.risk.RuleConditionEntity;
import com.liboshuai.starlink.slr.framework.mybatis.core.mapper.BaseMapperX;
import com.liboshuai.starlink.slr.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RuleConditionMapper extends BaseMapperX<RuleConditionEntity> {

    default List<RuleConditionEntity> selectListByRuleCode(String ruleCode) {
        return selectList(new LambdaQueryWrapperX<RuleConditionEntity>()
                .eq(RuleConditionEntity::getRuleCode, ruleCode)
                .orderByAsc(RuleConditionEntity::getId));
    }
}
