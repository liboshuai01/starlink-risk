package com.liboshuai.starlink.slr.admin.service.risk;

import com.liboshuai.starlink.slr.admin.pojo.vo.risk.EventAttributeVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.EventInfoVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.RuleConditionVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.RuleInfoVO;

/**
 * 风控service
 */
public interface RiskService {

    String addRuleInfo(RuleInfoVO ruleInfoVO);

    String addRuleCondition(RuleConditionVO ruleConditionVO);

    String addEventInfo(EventInfoVO eventInfoVO);

    String addEventAttribute(EventAttributeVO eventAttributeVO);

    void putRule(String ruleCode);
}
