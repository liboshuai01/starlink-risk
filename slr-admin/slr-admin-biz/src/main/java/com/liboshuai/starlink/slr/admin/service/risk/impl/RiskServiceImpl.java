package com.liboshuai.starlink.slr.admin.service.risk.impl;

import com.liboshuai.starlink.slr.admin.api.constants.ErrorCodeConstants;
import com.liboshuai.starlink.slr.admin.common.component.snowflake.SnowflakeId;
import com.liboshuai.starlink.slr.admin.convert.risk.EventAttributeConvert;
import com.liboshuai.starlink.slr.admin.convert.risk.EventInfoConvert;
import com.liboshuai.starlink.slr.admin.convert.risk.RuleConditionConvert;
import com.liboshuai.starlink.slr.admin.convert.risk.RuleInfoConvert;
import com.liboshuai.starlink.slr.admin.dao.mysql.risk.EventAttributeMapper;
import com.liboshuai.starlink.slr.admin.dao.mysql.risk.EventInfoMapper;
import com.liboshuai.starlink.slr.admin.dao.mysql.risk.RuleConditionMapper;
import com.liboshuai.starlink.slr.admin.dao.mysql.risk.RuleInfoMapper;
import com.liboshuai.starlink.slr.admin.pojo.entity.risk.EventAttributeEntity;
import com.liboshuai.starlink.slr.admin.pojo.entity.risk.EventInfoEntity;
import com.liboshuai.starlink.slr.admin.pojo.entity.risk.RuleConditionEntity;
import com.liboshuai.starlink.slr.admin.pojo.entity.risk.RuleInfoEntity;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.EventAttributeVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.EventInfoVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.RuleConditionVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.RuleInfoVO;
import com.liboshuai.starlink.slr.admin.service.risk.RiskService;
import com.liboshuai.starlink.slr.framework.common.exception.util.ServiceExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RiskServiceImpl implements RiskService {

    @Resource
    private SnowflakeId snowflakeId;
    @Resource
    private RuleInfoMapper ruleInfoMapper;
    @Resource
    private RuleConditionMapper ruleConditionMapper;
    @Resource
    private EventInfoMapper eventInfoMapper;
    @Resource
    private EventAttributeMapper eventAttributeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addRuleInfo(RuleInfoVO ruleInfoVO) {
        String ruleCode = snowflakeId.nextIdStr();
        RuleInfoEntity ruleInfoEntity = RuleInfoConvert.INSTANCE.vo2Entity(ruleInfoVO);
        ruleInfoEntity.setRuleCode(ruleCode);
        ruleInfoMapper.insert(ruleInfoEntity);
        return ruleCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addRuleCondition(RuleConditionVO ruleConditionVO) {
        String conditionCode = snowflakeId.nextIdStr();
        RuleConditionEntity ruleConditionEntity = RuleConditionConvert.INSTANCE.vo2Entity(ruleConditionVO);
        ruleConditionEntity.setConditionCode(conditionCode);
        ruleConditionMapper.insert(ruleConditionEntity);
        return conditionCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addEventInfo(EventInfoVO eventInfoVO) {
        String eventCode = snowflakeId.nextIdStr();
        EventInfoEntity eventInfoEntity = EventInfoConvert.INSTANCE.vo2Entity(eventInfoVO);
        eventInfoEntity.setEventCode(eventCode);
        eventInfoMapper.insert(eventInfoEntity);
        return eventCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addEventAttribute(EventAttributeVO eventAttributeVO) {
        String attributeCode = snowflakeId.nextIdStr();
        EventAttributeEntity eventAttributeEntity = EventAttributeConvert.INSTANCE.vo2Entity(eventAttributeVO);
        eventAttributeEntity.setEventCode(attributeCode);
        eventAttributeMapper.insert(eventAttributeEntity);
        return attributeCode;
    }

    @Override
    public void putRule(String ruleCode) {
        // TODO: 查询逻辑待续
        RuleInfoEntity ruleInfoEntity = ruleInfoMapper.selectOneByRuleCode(ruleCode);
        List<RuleConditionEntity> ruleConditionEntityList = ruleConditionMapper.selectListByRuleCode(ruleCode);
        if (CollectionUtils.isEmpty(ruleConditionEntityList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RULE_CONDITION_NOT_EXISTS);
        }
        List<String> eventCodeList = ruleConditionEntityList.stream()
                .map(RuleConditionEntity::getEventCode)
                .collect(Collectors.toList());
        List<EventInfoEntity> eventInfoEntityList = eventInfoMapper.selectListByEventCode(eventCodeList);
        if (CollectionUtils.isEmpty(eventInfoEntityList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_INFO_NOT_EXISTS);
        }

        // TODO：组合规则参数为json

    }
}