package com.liboshuai.starlink.slr.admin.controller.risk;

import com.liboshuai.starlink.slr.admin.pojo.vo.risk.EventAttributeVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.EventInfoVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.RuleConditionVO;
import com.liboshuai.starlink.slr.admin.pojo.vo.risk.RuleInfoVO;
import com.liboshuai.starlink.slr.admin.service.risk.RiskService;
import com.liboshuai.starlink.slr.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Tag(name = "风控")
@RequestMapping("/risk")
public class RiskController {

    @Resource
    private RiskService riskService;

    @PostMapping("/addRuleInfo")
    @Operation(summary = "新增规则信息")
    public CommonResult<String> addRuleInfo(@RequestBody RuleInfoVO ruleInfoVO) {
        String ruleCode = riskService.addRuleInfo(ruleInfoVO);
        return CommonResult.success(ruleCode);
    }

    @PostMapping("/addRuleCondition")
    @Operation(summary = "新增规则条件")
    public CommonResult<String> addRule(@RequestBody RuleConditionVO ruleConditionVO) {
        String conditionCode = riskService.addRuleCondition(ruleConditionVO);
        return CommonResult.success(conditionCode);
    }

    @PostMapping("/addEventInfo")
    @Operation(summary = "新增事件信息")
    public CommonResult<String> addEventInfo(@RequestBody EventInfoVO eventInfoVO) {
        String eventCode = riskService.addEventInfo(eventInfoVO);
        return CommonResult.success(eventCode);
    }

    @PostMapping("/addEventAttribute")
    @Operation(summary = "新增事件属性")
    public CommonResult<String> addEventAttribute(@RequestBody EventAttributeVO eventAttributeVO) {
        String attributeCode = riskService.addEventAttribute(eventAttributeVO);
        return CommonResult.success(attributeCode);
    }

    @GetMapping("/putRule")
    @Operation(summary = "上线规则")
    public CommonResult<Boolean> putRule(String ruleCode) {
        riskService.putRule(ruleCode);
        return CommonResult.success(true);
    }
}
