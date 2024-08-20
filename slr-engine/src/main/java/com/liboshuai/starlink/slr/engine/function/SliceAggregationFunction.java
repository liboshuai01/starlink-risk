package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.common.ParameterConstants;
import com.liboshuai.starlink.slr.engine.common.StateDescContainer;
import com.liboshuai.starlink.slr.engine.utils.jdbc.JdbcUtil;
import com.liboshuai.starlink.slr.engine.utils.log.ConsoleLogUtil;
import com.liboshuai.starlink.slr.engine.utils.parameter.ParameterUtil;
import com.liboshuai.starlinkRisk.common.constants.*;
import com.liboshuai.starlinkRisk.common.pojo.*;
import com.liboshuai.starlinkRisk.common.utils.date.DateUtil;
import com.liboshuai.starlinkRisk.common.utils.date.TimeUtil;
import com.liboshuai.starlinkRisk.common.utils.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.CollectionUtil;
import org.apache.flink.util.Collector;
import org.apache.flink.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @Author: liboshuai
 * @Date: 2023-10-27 22:12
 **/
@Slf4j
public class SliceAggregationFunction extends KeyedBroadcastProcessFunction<String, WindowPO, RuleCdcPO, SinkPO> {

    /**
     * 最近一段时间的所有抽奖数据
     */
    private ValueState<List<Tuple2<Long, Long>>> timeNumberListState;
    private List<Tuple2<Long, Long>> spliteList;
    /**
     * 定义一个ValueState来保存每个用户最近一次发送预警的时间
     */
    private ValueState<Long> lastWarningTimeState;
    /**
     * cdc规则map：key-场景，value-规则对象
     */
    private Map<String, RulePO> ruleMap;
    /**
     * 预加载规则map：key-场景，value-规则对象
     */
    private Map<String, RulePO> preloadRuleMap;
    /**
     * 银行数据map：key-银行号，value-银行名称
     */
    private Map<String, String> bankMap;


    /**
     * 检查规则值内容，错误内容则跳过计算
     * true-不通过；false-通过
     */
    private static boolean checkRule(RulePO rulePO) {
        if (rulePO == null) {
            ConsoleLogUtil.warning("规则值对象为空！暂未获取到规则数据，计算跳过！");
            return true;
        }
        if (!Objects.equals(rulePO.getChannel(), RuleChannelConstants.GAME)) {
            ConsoleLogUtil.warning("规则值渠道为{}，应为game！计算跳过，请排查规则配置！", rulePO.getChannel());
            return true;
        }
        if (!Objects.equals(rulePO.getScenario(), RuleScenarioConstants.GAME_LOTTERY)) {
            ConsoleLogUtil.warning("规则值渠道为{}，应为game_lottery！计算跳过，请排查规则配置！", rulePO.getScenario());
            return true;
        }
        if (rulePO.getWindowSize() == null) {
            ConsoleLogUtil.warning("规则值windowSize为空！计算跳过，请排查规则配置！");
            return true;
        }
        if (rulePO.getWindowSlide() == null) {
            ConsoleLogUtil.warning("规则值windowSlide为空！计算跳过，请排查规则配置！");
            return true;
        }
        if (rulePO.getThreshold() == null) {
            ConsoleLogUtil.warning("规则值threshold为空！计算跳过，请排查规则配置！");
            return true;
        }
        if (rulePO.getWarningInterval() == null) {
            ConsoleLogUtil.warning("规则值warningInterval为空！计算跳过，请排查规则配置！");
            return true;
        }
        if (StringUtils.isNullOrWhitespaceOnly(rulePO.getWarningMessage())) {
            ConsoleLogUtil.warning("规则值warningMessage为空！计算跳过，请排查规则配置！");
            return true;
        }
        if (Objects.isNull(rulePO.getState())) {
            ConsoleLogUtil.warning("规则值state为空！计算跳过，请排查规则配置！");
            return true;
        }
        if (!Objects.equals(rulePO.getState(), RuleStateConstants.ENABLE)
                && !Objects.equals(rulePO.getState(), RuleStateConstants.DEACTIVATE_REVIEW)) {
            ConsoleLogUtil.warning("规则值state不为启动、停用待审核状态！计算跳过，请排查规则配置！");
            return true;
        }
        return false;
    }

    private static RuleCdcPO getMap(BroadcastState<String, RuleCdcPO> broadcastState) throws Exception {
        return broadcastState.get(KeyConstants.CDC_RULE_MAP_STATE_KEY);
    }

    /**
     * 获取毫秒级别的预警间隔值
     */
    private static long getWarningIntervalMills(RulePO currentRule) {
        Long warningInterval = currentRule.getWarningInterval(); // 预警间隔
        String warningIntervalUnit = currentRule.getWarningIntervalUnit(); // 预警间隔单位
        RuleUnitEnum unitEnum = RuleUnitEnum.fromEnUnit(warningIntervalUnit);
        if (Objects.isNull(unitEnum)) {
            throw new IllegalArgumentException("[预警间隔单位]错误！错误值为: " + warningIntervalUnit);
        }
        return TimeUtil.toMillis(warningInterval, unitEnum);
    }

    /**
     * 初始化状态
     */
    @Override
    public void open(Configuration parameters) {
        timeNumberListState = getRuntimeContext().getState(StateDescContainer.gameLotteryTimeNuLStateDesc);
        lastWarningTimeState = getRuntimeContext().getState(StateDescContainer.gameLotteryLastWarningTimeStateDesc);
        spliteList = new ArrayList<>();
        ruleMap = new HashMap<>();
        // 预加载规则配置
        preloadRule();
        // 预加载银行数据
        preloadBank();
    }

    @Override
    public void processElement(WindowPO windowPO, KeyedBroadcastProcessFunction<String, WindowPO, RuleCdcPO,
            SinkPO>.ReadOnlyContext ctx, Collector<SinkPO> out) throws Exception {
        // 初始化规则
        RulePO currentRule = initRule(ctx);
        // 检查规则值内容，不符合的规则，则跳过计算
        if (checkRule(currentRule)) return;
        // 计算累计抽奖数
        Long lotteryNumberSum = getAccLotteryNumber(windowPO, currentRule, ctx.timestamp());
        // 发送预警信息
        sendWarnMessage(ctx, windowPO, currentRule, lotteryNumberSum, out);
    }

    /**
     * 处理广播流数据
     */
    @Override
    public void processBroadcastElement(RuleCdcPO ruleCdcPO, KeyedBroadcastProcessFunction<String, WindowPO, RuleCdcPO,
            SinkPO>.Context ctx, Collector<SinkPO> out) throws Exception {
        BroadcastState<String, RuleCdcPO> broadcastState = ctx.getBroadcastState(StateDescContainer.broadcastRuleStateDesc);
        ConsoleLogUtil.info("更新广播流状态broadcastState之前: {}", JsonUtil.obj2JsonStr(getMap(broadcastState)));
        broadcastState.put(KeyConstants.CDC_RULE_MAP_STATE_KEY, ruleCdcPO);
        ConsoleLogUtil.info("更新广播流状态broadcastState之后: {}", JsonUtil.obj2JsonStr(getMap(broadcastState)));
    }

    /**
     * 初始化规则
     */
    private RulePO initRule(KeyedBroadcastProcessFunction<String, WindowPO, RuleCdcPO, SinkPO>.ReadOnlyContext ctx) throws Exception {
        ReadOnlyBroadcastState<String, RuleCdcPO> broadcastState = ctx.getBroadcastState(StateDescContainer.broadcastRuleStateDesc);
        // 根据广播流维护状态值
        changeRuleState(broadcastState);
        // 如果广播流中没有规则数据，且预加载规则不为空，则对规则进行预加载
        if (!broadcastState.contains(KeyConstants.CDC_RULE_MAP_STATE_KEY) && !CollectionUtil.isNullOrEmpty(preloadRuleMap)) {
            ruleMap.put(RuleScenarioConstants.GAME_LOTTERY, preloadRuleMap.get(RuleScenarioConstants.GAME_LOTTERY));
            preloadRuleMap.clear();
        }
        RulePO currentRule = ruleMap.get(RuleScenarioConstants.GAME_LOTTERY);
        return currentRule;
    }

    /**
     * 发送预警信息
     */
    private void sendWarnMessage(KeyedBroadcastProcessFunction<String, WindowPO, RuleCdcPO, SinkPO>.ReadOnlyContext ctx,
                                 WindowPO windowPO, RulePO currentRule, Long lotteryNumberSum, Collector<SinkPO> out) throws IOException {
        // 获取业务数据字段
        SourcePO sourcePO = windowPO.getSourcePO(); // 业务数据
        String bank = sourcePO.getBank(); // 银行号
        String bankName = bankMap.get(bank); // 银行名称
        String channel = sourcePO.getChannel(); // 渠道
        ChannelDataPO channelData = sourcePO.getChannelData(); // 渠道数据
        String campaignId = channelData.getCampaignId(); // 活动ID
        String userId = channelData.getUserId(); // 用户ID
        // 获取规则数据字段
        Long windowSize = currentRule.getWindowSize(); // 窗口大小
        String windowSizeUnit = currentRule.getWindowSizeUnit(); // 窗口大小单位
        Long threshold = currentRule.getThreshold(); // 阈值
        long warningIntervalMills = getWarningIntervalMills(currentRule); // 预警间隔（毫秒）
        String warningMessage = currentRule.getWarningMessage(); // 预警信息
        // 获取当前时间戳
        long currentTimestamp = ctx.timestamp();
        // 获取当前用户最近一次发送预警的时间，如果为空则初始化为0
        long lastWarningTime = lastWarningTimeState.value() == null ? 0L : lastWarningTimeState.value();
        // 如果抽奖次数大于阈值，并且距离上次发送预警已经超过指定分钟，发送一个预警事件，并更新最近一次发送预警的时间
        if (lotteryNumberSum > threshold && currentTimestamp - lastWarningTime >= warningIntervalMills) {
            // 拼接告警信息
            String warnMessage = splicingWarnMessage(warningMessage, bank, bankName, channel, campaignId, userId,
                    windowSize, windowSizeUnit, lotteryNumberSum, threshold, DateUtil.convertTimestamp2String(currentTimestamp));
            SinkPO sinkPO = new SinkPO();
            sinkPO.setBank(bank);
            sinkPO.setChannel(RuleChannelConstants.GAME);
            sinkPO.setAlertMessage(warnMessage);
            sinkPO.setUserId(userId);
            sinkPO.setCampaignId(campaignId);
            ConsoleLogUtil.info("状态里面共存了多少数据：{}", spliteList.size());
            ConsoleLogUtil.debug("状态数据为：{}", JsonUtil.obj2JsonStr(spliteList));
            ConsoleLogUtil.info("触发预警信息sinkPO: {}", JsonUtil.obj2JsonStr(sinkPO));
            // 发送预警事件
            out.collect(sinkPO);
            // 记录最新的预警时间
            lastWarningTimeState.update(currentTimestamp);
        }
    }

    /**
     * 获取累计抽奖数
     */
    private Long getAccLotteryNumber(WindowPO windowPO, RulePO currentRule, long currentTimestamp) throws Exception {
        ConsoleLogUtil.debug("当前时间戳: {}", currentTimestamp);
        ConsoleLogUtil.debug("当前数据是: {}", JsonUtil.obj2JsonStr(windowPO));
        // 获取窗口大小（毫秒）
        Long windowSize = currentRule.getWindowSize(); // 窗口大小
        String windowSizeUnit = currentRule.getWindowSizeUnit(); // 窗口大小单位
        RuleUnitEnum ruleUnitEnum = RuleUnitEnum.fromEnUnit(windowSizeUnit);
        if (Objects.isNull(ruleUnitEnum)) {
            throw new IllegalArgumentException("[窗口大小单位]错误！错误值为: " + windowSizeUnit);
        }
        long windowSizeMillis = TimeUtil.toMillis(windowSize, ruleUnitEnum); // 窗口大小转换为毫秒
        // 获取抽奖次数
        Long lotteryNumber = windowPO.getSourcePO().getChannelData().getLotteryNumber();
        // 定义总抽奖次数
        Long lotteryNumberSum = 0L;
        // 添加当前<当前时间, 当前抽奖次数>到状态list
        spliteList.clear();
        List<Tuple2<Long, Long>> tuple2List = timeNumberListState.value();
        if (tuple2List != null) {
            spliteList.addAll(tuple2List);
        }
        spliteList.add(Tuple2.of(currentTimestamp, lotteryNumber));
        // 遍历列表中的时间，删除超过指定分钟的旧的时间，并计算最近指定分钟内的抽奖次数
        Iterator<Tuple2<Long, Long>> iterator = spliteList.iterator();
        while (iterator.hasNext()) {
            Tuple2<Long, Long> tuple2 = iterator.next();
            if (currentTimestamp - tuple2.f0 > windowSizeMillis) {
                iterator.remove();
            } else {
                lotteryNumberSum += tuple2.f1;
            }
        }
        ConsoleLogUtil.debug("状态里面共存了多少数据：{}", spliteList.size());
        ConsoleLogUtil.debug("状态数据为：{}", JsonUtil.obj2JsonStr(spliteList));
        timeNumberListState.clear();
        timeNumberListState.update(spliteList);
        return lotteryNumberSum;
    }

    /**
     * 预加载规则配置，防止广播流延迟
     */
    private void preloadRule() {
        preloadRuleMap = new HashMap<>();
        // 获取规则表名
        String tableName = ParameterUtil.getParameters().get(ParameterConstants.MYSQL_TABLE_RULE);
        // 查询规则数据
        String sql = "select * from " + tableName + " where channel = ? and scenario = ? and (state = ? or state = ?)";
        Optional<RulePO> optionalRulePO = JdbcUtil.queryOne(sql, new JdbcUtil.BeanPropertyRowMapper<>(RulePO.class),
                RuleChannelConstants.GAME, RuleScenarioConstants.GAME_LOTTERY,
                RuleStateConstants.ENABLE, RuleStateConstants.DEACTIVATE_REVIEW);
        if (!optionalRulePO.isPresent()) {
            ConsoleLogUtil.warning("Mysql Jdbc 预加载的规则对象rulePO为空！");
            return;
        }
        RulePO rulePO = optionalRulePO.get();
        ConsoleLogUtil.info("Mysql Jdbc 预加载的规则对象rulePO: {}", JsonUtil.obj2JsonStr(rulePO));
        preloadRuleMap.put(RuleScenarioConstants.GAME_LOTTERY, rulePO);
    }

    /**
     * 预加载银行数据，防止广播流延迟
     */
    private void preloadBank() {
        bankMap = new HashMap<>();
        // 获取规则表名
        String tableName = ParameterUtil.getParameters().get(ParameterConstants.MYSQL_TABLE_BANK);
        // 查询规则数据
        String sql = "select bank, name from " + tableName + " where is_deleted = '0'";
        List<BankPO> bankPOList = JdbcUtil.queryList(sql, new JdbcUtil.BeanPropertyRowMapper<>(BankPO.class));
        if (CollectionUtil.isNullOrEmpty(bankPOList)) {
            ConsoleLogUtil.warning("Mysql Jdbc 预加载的银行对象集合bankPOList为空！");
            return;
        }
        ConsoleLogUtil.info("Mysql Jdbc 预加载的银行对象集合bankPOList: {}", JsonUtil.obj2JsonStr(bankPOList));
        for (BankPO bankPO : bankPOList) {
            bankMap.put(bankPO.getBank(), bankPO.getName());
        }
        ConsoleLogUtil.info("Mysql Jdbc 预加载的银行bankMap {}", JsonUtil.obj2JsonStr(bankMap));
    }

    /**
     * 拼接告警信息
     */
    private String splicingWarnMessage(String template, String bank, String bankName, String channel, String campaignId, String userId,
                                       long windowSize, String windowSizeUnit, long lotteryNumberSum, long threshold, String eventTime) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put(RuleWarnTemplateConstants.BANK, bank);
        valuesMap.put(RuleWarnTemplateConstants.BANK_NAME, bankName);
        valuesMap.put(RuleWarnTemplateConstants.CHANNEL, channel);
        valuesMap.put(RuleWarnTemplateConstants.CAMPAIGN_ID, campaignId);
        valuesMap.put(RuleWarnTemplateConstants.USER_ID, userId);
        valuesMap.put(RuleWarnTemplateConstants.WINDOW_SIZE, windowSize + RuleUnitEnum.convertEnUnitToCnUnit(windowSizeUnit));
        valuesMap.put(RuleWarnTemplateConstants.ACTUAL_VALUE, String.valueOf(lotteryNumberSum));
        valuesMap.put(RuleWarnTemplateConstants.THRESHOLD, String.valueOf(threshold));
        valuesMap.put(RuleWarnTemplateConstants.EVENT_TIME, eventTime);
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        return sub.replace(template);
    }

    /**
     * 根据广播流维护状态值
     */
    private void changeRuleState(ReadOnlyBroadcastState<String, RuleCdcPO> broadcastState) throws Exception {
        RuleCdcPO ruleCdcPO = broadcastState.get(KeyConstants.CDC_RULE_MAP_STATE_KEY);
        if (ruleCdcPO == null) {
            ConsoleLogUtil.warning("根据广播流维护状态值-广播流中暂无数据，跳过！");
            return;
        }
//        RulePO ruleCdcPOBefore = ruleCdcPO.getBefore();
        RulePO ruleCdcPOAfter = ruleCdcPO.getAfter();
        switch (ruleCdcPO.getOp()) {
            case "c":
            case "u":
            case "r":
                // 在创建、更新或读取操作时，我们更新规则状态
                ruleMap.put(ruleCdcPOAfter.getScenario(), ruleCdcPOAfter);
                break;
            // 目前不需要删除
//            case "d":
//                // 在删除操作时，我们删除规则状态
//                riskRuleState.remove(ruleCdcPOBefore.getRuleScenario());
//                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + ruleCdcPO.getOp());
        }
    }
}
