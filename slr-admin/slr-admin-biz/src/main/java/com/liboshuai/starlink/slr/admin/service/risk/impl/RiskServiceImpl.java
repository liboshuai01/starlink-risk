package com.liboshuai.starlink.slr.admin.service.risk.impl;

import com.liboshuai.starlink.slr.admin.api.constants.ErrorCodeConstants;
import com.liboshuai.starlink.slr.admin.common.component.snowflake.SnowflakeId;
import com.liboshuai.starlink.slr.admin.convert.risk.EventAttributeConvert;
import com.liboshuai.starlink.slr.admin.convert.risk.EventInfoConvert;
import com.liboshuai.starlink.slr.admin.convert.risk.RuleConditionConvert;
import com.liboshuai.starlink.slr.admin.convert.risk.RuleInfoConvert;
import com.liboshuai.starlink.slr.admin.dao.mysql.risk.*;
import com.liboshuai.starlink.slr.admin.pojo.entity.risk.*;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.EventAttributeVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.EventInfoVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.RuleConditionVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.RuleInfoVO;
import com.liboshuai.starlink.slr.admin.service.risk.RiskService;
import com.liboshuai.starlink.slr.engine.api.dto.EventAttributeDTO;
import com.liboshuai.starlink.slr.engine.api.dto.EventInfoDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleConditionDTO;
import com.liboshuai.starlink.slr.engine.api.dto.RuleInfoDTO;
import com.liboshuai.starlink.slr.framework.common.exception.util.ServiceExceptionUtil;
import com.liboshuai.starlink.slr.framework.common.util.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RiskServiceImpl implements RiskService {

    @Resource
    private SnowflakeId snowflakeId;
    @Resource
    private RuleJsonMapper ruleJsonMapper;
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
        eventAttributeEntity.setAttributeCode(attributeCode);
        eventAttributeMapper.insert(eventAttributeEntity);
        return attributeCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void putRule(String ruleCode) {
        String ruleJson = "{\"channel\":\"game\",\"ruleCode\":\"ruleCode01\",\"modelCode\":\"modelCode01\",\"ruleName\":\"规则01\",\"ruleDesc\":\"高频抽奖规则\",\"conditionOperator\":\"and\",\"warningMessage\":\"[异常高频抽奖]${bankName}：${campaignName}(${campaignId})中游戏用户(${keyId})最近${windowSize}内抽奖数量为${eventValueSum}，超过${eventThreshold}次，请您及时查看原因！\",\"warningIntervalValue\":\"5\",\"warningIntervalUnit\":\"minute\",\"status\":1,\"ruleConditionList\":[{\"conditionCode\":\"conditionCode01\",\"ruleCode\":\"ruleCode01\",\"eventCode\":\"eventCode01\",\"eventThreshold\":\"10\",\"windowSizeValue\":\"20\",\"windowSizeUnit\":\"minute\",\"beginTime\":\"2024-08-2114:24:32\",\"endTime\":\"2024-10-2114:24:36\",\"eventInfo\":{\"eventCode\":\"eventCode02\",\"channel\":\"game\",\"eventName\":\"充值\",\"eventDesc\":\"游戏充值\",\"eventAttribute\":[{\"attributeCode\":\"attributeCode01\",\"eventCode\":\"eventCode01\",\"fieldName\":\"campaignId\",\"fieldDesc\":\"活动Id\"},{\"attributeCode\":\"attributeCode02\",\"eventCode\":\"eventCode01\",\"fieldName\":\"campaignName\",\"fieldDesc\":\"活动名称\"}]}},{\"conditionCode\":\"conditionCode02\",\"ruleCode\":\"ruleCode01\",\"eventCode\":\"eventCode02\",\"eventThreshold\":\"10\",\"windowSizeValue\":\"20\",\"windowSizeUnit\":\"minute\",\"beginTime\":\"2024-08-2114:24:32\",\"endTime\":\"2024-10-2114:24:36\",\"eventInfo\":{\"eventCode\":\"eventCode02\",\"channel\":\"game\",\"eventName\":\"充值\",\"eventDesc\":\"游戏充值\",\"eventAttribute\":[{\"attributeCode\":\"attributeCode03\",\"eventCode\":\"eventCode02\",\"fieldName\":\"campaignId\",\"fieldDesc\":\"活动Id\",\"fieldType\":\"string\"},{\"attributeCode\":\"attributeCode04\",\"eventCode\":\"eventCode02\",\"fieldName\":\"campaignName\",\"fieldDesc\":\"活动名称\",\"fieldType\":\"string\"}]}}],\"ruleModel\":\"代码省略\"}";
        // 插入规则json到数据库
        ruleJsonMapper.insert(RuleJsonEntity.builder()
                .ruleCode(ruleCode)
                .ruleJson(ruleJson)
                .build());
        // TODO: 动态生成doris查询sql，并发布查询结果到redis

    }

    private RuleInfoDTO buildRuleInfoDTO(String ruleCode) {
        // 数据库查询数据
        RuleInfoEntity ruleInfoEntity = ruleInfoMapper.selectOneByRuleCode(ruleCode);
        if (Objects.isNull(ruleInfoEntity)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RULE_INFO_NOT_EXISTS, ruleCode);
        }
        List<RuleConditionEntity> ruleConditionEntityList = ruleConditionMapper.selectListByRuleCode(ruleCode);
        if (CollectionUtils.isEmpty(ruleConditionEntityList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.RULE_CONDITION_NOT_EXISTS, ruleCode);
        }
        List<String> eventCodeList = ruleConditionEntityList.stream()
                .map(RuleConditionEntity::getEventCode)
                .collect(Collectors.toList());
        List<EventInfoEntity> eventInfoEntityList = eventInfoMapper.selectListByEventCode(eventCodeList);
        if (CollectionUtils.isEmpty(eventInfoEntityList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_INFO_NOT_EXISTS, eventCodeList);
        }
        List<EventAttributeEntity> eventAttributeEntityList = eventAttributeMapper.selectListByEventCode(eventCodeList);

        // entity转dto
        RuleInfoDTO ruleInfoDTO = RuleInfoConvert.INSTANCE.entity2Dto(ruleInfoEntity);
        List<RuleConditionDTO> ruleConditionDTOList = RuleConditionConvert.INSTANCE.batchEntity2Dto(ruleConditionEntityList);
        List<EventInfoDTO> eventInfoDTOList = EventInfoConvert.INSTANCE.batchEntity2Dto(eventInfoEntityList);
        List<EventAttributeDTO> eventAttributeDTOList = EventAttributeConvert.INSTANCE.batchEntity2Dto(eventAttributeEntityList);

        // 设置EventInfoDTO对象的eventAttributeDTOList属性
        Map<String, List<EventAttributeDTO>> attributeByEventCodeMap;
        if (!CollectionUtils.isEmpty(eventAttributeDTOList)) {
            attributeByEventCodeMap = eventAttributeDTOList.stream()
                    .collect(Collectors.groupingBy(EventAttributeDTO::getEventCode));
        } else {
            attributeByEventCodeMap = new HashMap<>();
        }
//        eventInfoDTOList = eventInfoDTOList.stream()
//                .peek(eventInfoDTO -> eventInfoDTO.setEventAttributeDTOList(
//                        attributeByEventCodeMap.get(eventInfoDTO.getEventCode()))
//                )
//                .collect(Collectors.toList());
//
//        // 设置RuleConditionDTO对象的eventInfoDTO属性
//        Map<String, List<EventInfoDTO>> eventInfoByEventCodeMap = eventInfoDTOList.stream()
//                .collect(Collectors.groupingBy(EventInfoDTO::getEventCode));
//        ruleConditionDTOList = ruleConditionDTOList.stream()
//                .peek(ruleConditionDTO -> ruleConditionDTO.setEventInfoDTO(
//                        eventInfoByEventCodeMap.get(ruleConditionDTO.getEventCode()).get(0)
//                ))
//                .collect(Collectors.toList());
//
//        // 设置RuleInfoDTO对象的ruleConditionDTOList属性
//        ruleInfoDTO.setRuleConditionDTOList(ruleConditionDTOList);
        return ruleInfoDTO;
    }
}