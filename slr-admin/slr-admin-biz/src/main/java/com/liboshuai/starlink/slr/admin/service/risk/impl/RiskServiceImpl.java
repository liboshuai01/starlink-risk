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
        String ruleJson = "{\"channel\":\"game\",\"rule_code\":\"ruleCode01\",\"model_code\":\"modelCode01\",\"rule_type\":1,\"rule_name\":\"规则01\",\"rule_desc\":\"高频抽奖规则\",\"cross_history\":true,\"history_timeline\":\"2024-08-24 21:27:36\",\"expire_begin_time\":\"2024-08-24 21:27:36\",\"expire_end_time\":\"2024-09-24 21:27:36\",\"condition_operator\":\"and\",\"warning_message\":\"[异常高频抽奖]${bankName}：${campaignName}(${campaignId})中游戏用户(${keyId})最近${windowSize}内抽奖数量为${eventValueSum}，超过${eventThreshold}次，请您及时查看原因！\",\"warning_interval_value\":\"5\",\"warning_interval_unit\":\"minute\",\"status\":1,\"rule_condition_list\":[{\"condition_code\":\"conditionCode01\",\"rule_code\":\"ruleCode01\",\"event_code\":\"eventCode01\",\"event_threshold\":\"10\",\"window_size_value\":\"20\",\"window_size_unit\":\"minute\",\"begin_time\":\"2024-08-21 14:24:32\",\"end_time\":\"2024-10-21 14:24:36\",\"event_info\":{\"event_code\":\"eventCode02\",\"channel\":\"game\",\"event_name\":\"充值\",\"event_desc\":\"游戏充值\",\"event_attribute\":[{\"attribute_code\":\"attributeCode01\",\"event_code\":\"eventCode01\",\"field_name\":\"campaignId\",\"field_desc\":\"活动ID\"},{\"attribute_code\":\"attributeCode02\",\"event_code\":\"eventCode01\",\"field_name\":\"campaignName\",\"field_desc\":\"活动名称\"}]}},{\"condition_code\":\"conditionCode02\",\"rule_code\":\"ruleCode01\",\"event_code\":\"eventCode02\",\"event_threshold\":\"10\",\"window_size_value\":\"20\",\"window_size_unit\":\"minute\",\"begin_time\":\"2024-08-21 14:24:32\",\"end_time\":\"2024-10-21 14:24:36\",\"event_info\":{\"event_code\":\"eventCode02\",\"channel\":\"game\",\"event_name\":\"充值\",\"event_desc\":\"游戏充值\",\"event_attribute\":[{\"attribute_code\":\"attributeCode03\",\"event_code\":\"eventCode02\",\"field_name\":\"campaignId\",\"field_desc\":\"活动ID\",\"field_type\":\"String\"},{\"attribute_code\":\"attributeCode04\",\"event_code\":\"eventCode02\",\"field_name\":\"campaignName\",\"field_desc\":\"活动名称\",\"field_type\":\"String\"}]}}],\"rule_model\":\"代码省略\"}";
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

    /**
     * 根据窗口值与单位计算获取窗口毫秒值
     */
    private static int getWindowSize(String windowSizeUnit, String windowSizeValue) {
        int windowSize = 0;
        switch (windowSizeUnit) {
            case "MILLISECOND":
                windowSize = Integer.parseInt(windowSizeValue);
                break;
            case "SECOND":
                windowSize = Integer.parseInt(windowSizeValue) * 1000;
                break;
            case "MINUTE":
                windowSize = Integer.parseInt(windowSizeValue) * 60 * 1000;
                break;
            case "HOUR":
                windowSize = Integer.parseInt(windowSizeValue) * 60 * 60 * 1000;
                break;
            case "DAY":
                windowSize = Integer.parseInt(windowSizeValue) * 24 * 60 * 60 * 1000;
                break;
            case "WEEK":
                windowSize = Integer.parseInt(windowSizeValue) * 7 * 24 * 60 * 60 * 1000;
                break;
            case "MONTH":
                windowSize = Integer.parseInt(windowSizeValue) * 30 * 24 * 60 * 60 * 1000;
                break;
            case "YEAR":
                windowSize = Integer.parseInt(windowSizeValue) * 365 * 24 * 60 * 60 * 1000;
                break;
            default:
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.WINDOW_UNIT_NOT_EXISTS, windowSizeUnit);
        }
        return windowSize;
    }
}