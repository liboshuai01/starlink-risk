package com.liboshuai.starlink.slr.engine.function;

import com.liboshuai.starlink.slr.engine.api.dto.*;
import com.liboshuai.starlink.slr.engine.calculator.Calculator;
import com.liboshuai.starlink.slr.engine.common.ParameterConstants;
import com.liboshuai.starlink.slr.engine.dto.RuleCdcDTO;
import com.liboshuai.starlink.slr.engine.utils.jdbc.JdbcUtil;
import com.liboshuai.starlink.slr.engine.utils.parameter.ParameterUtil;
import com.liboshuai.starlink.slr.engine.utils.string.JsonUtil;
import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.CollectionUtil;
import org.apache.flink.util.Collector;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 计算引擎核心
 */
@Slf4j
public class CoreFunction extends KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String> {

    private static final long serialVersionUID = -5913085790319815064L;

    /**
     * cdc规则map：key-规则编号，value-运算机
     */
    private Map<String, Calculator> calculatorByRuleCodeMap;

    /**
     * groovy加载器
     */
    private GroovyClassLoader groovyClassLoader;

    /**
     * 在线规则数量
     */
    private long onlineRuleCount;

    /**
     * 银行数据
     */
    private Map<String, String> bankMap;


    @Override
    public void open(Configuration parameters) throws Exception {
        calculatorByRuleCodeMap = new HashMap<>();
        groovyClassLoader = new GroovyClassLoader();
//        // 查询在线规则数量
//        onlineRuleCount = queryOnlineRuleCount();
//        // 查询银行数据
//        bankMap = queryBank();
    }

    @Override
    public void processElement(EventKafkaDTO eventKafkaDTO, KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.ReadOnlyContext ctx, Collector<String> out) throws Exception {
        // 判断所有运算机是否初始化完毕
        long currentOnlineRuleCount = getCurrentOnlineRuleCount();
        while (true) {
            if (onlineRuleCount == currentOnlineRuleCount) {
                break;
            }
            TimeUnit.SECONDS.sleep(1);
            onlineRuleCount = queryOnlineRuleCount();
            currentOnlineRuleCount = getCurrentOnlineRuleCount();
        }
        // 开始进行计算

    }

    private long getCurrentOnlineRuleCount(){
        Set<String> ruleCodeCount = calculatorByRuleCodeMap.keySet();
        return ruleCodeCount.size();
    }

    @Override
    public void processBroadcastElement(RuleCdcDTO ruleCdcDTO, KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.Context ctx, Collector<String> out) throws Exception {
        log.error("processBroadcastElement 数据: {}", ruleCdcDTO);
//        if (ruleCdcDTO == null) {
//            log.warn("ruleCdcDTO must not be null");
//            return;
//        }
//        // cdc数据更新
//        String op = ruleCdcDTO.getOp();
//        RuleJsonDTO ruleCdcDTOAfter = ruleCdcDTO.getAfter();
//        String ruleCode = ruleCdcDTOAfter.getRuleCode();
//        String ruleJson = ruleCdcDTOAfter.getRuleJson();
//        RuleInfoDTO ruleInfoDTO = JsonUtils.parseObject(ruleJson, RuleInfoDTO.class);
//        if ((Objects.equals(op, Envelope.Operation.READ.code()) || Objects.equals(op, Envelope.Operation.CREATE.code())
//                || Objects.equals(op, Envelope.Operation.UPDATE.code()))
//                && Objects.equals(Objects.requireNonNull(ruleInfoDTO).getStatus(), RuleStatus.ENABLE.getCode())) {
//            // 在读取、创建、更新，且状态为上线时，则上线一个运算机
//            Calculator calculator = buildCalculator(ruleInfoDTO, ctx);
//            calculatorByRuleCodeMap.put(ruleCode, calculator);
//            log.info("上线或更新一个运算机，规则编号为:{}", ruleCode);
//        } else if (Objects.equals(op, Envelope.Operation.UPDATE.code())
//                && Objects.equals(Objects.requireNonNull(ruleInfoDTO).getStatus(), RuleStatus.DISABLE.getCode())) {
//            // 在更新，且状态为下线时，则下线一个运算机
//            calculatorByRuleCodeMap.remove(ruleCode);
//            log.info("下线一个运算机，规则编号为:{}", ruleCode);
//        }
    }

    /**
     * 构造运算机对象
     */
    private Calculator buildCalculator(RuleInfoDTO ruleInfoDTO, KeyedBroadcastProcessFunction<String, EventKafkaDTO, RuleCdcDTO, String>.Context ctx) throws InstantiationException {
        RuleModelDTO ruleModelDTO = Objects.requireNonNull(ruleInfoDTO).getRuleModel();
        String ruleModel = Objects.requireNonNull(ruleModelDTO).getRuleModel();
        Class aClass = groovyClassLoader.parseClass(ruleModel);
        Calculator calculator;
        try {
            calculator = (Calculator) aClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        calculator.init(ctx, ruleInfoDTO);
        return calculator;
    }

    /**
     * 查询上线的规则数量
     */
    private Long queryOnlineRuleCount() {
        // 获取规则表名
        String tableName = ParameterUtil.getParameters().get(ParameterConstants.MYSQL_TABLE_RULE_COUNT);
        // 查询规则数据
        String sql = "select rule_count from " + tableName + " where deleted = 0";
        Optional<RuleCountDTO> optionalRuleCountDTO = JdbcUtil.queryOne(sql, new JdbcUtil.BeanPropertyRowMapper<>(RuleCountDTO.class));
        if (!optionalRuleCountDTO.isPresent()) {
//            log.warn("Mysql Jdbc 查询上线的规则数量为空！");
            throw new RuntimeException("Mysql Jdbc 查询上线的规则数量为空！");
        }
        RuleCountDTO ruleCountDTO = optionalRuleCountDTO.get();
        log.info("Mysql Jdbc 查询上线的规则数量: {}", ruleCountDTO.getRuleCount());
        return ruleCountDTO.getRuleCount();
    }

    /**
     * 查询银行数据银
     */
    private Map<String, String> queryBank() {
        // 获取规则表名
        String tableName = ParameterUtil.getParameters().get(ParameterConstants.MYSQL_TABLE_BANK);
        // 查询规则数据
        String sql = "select bank, name from " + tableName + " where is_deleted = 0";
        List<BankDTO> bankDTOList = JdbcUtil.queryList(sql, new JdbcUtil.BeanPropertyRowMapper<>(BankDTO.class));
        if (CollectionUtil.isNullOrEmpty(bankDTOList)) {
            log.warn("Mysql Jdbc 预加载的银行对象集合bankDTOList为空！");
            return new HashMap<>();
        }
        log.info("Mysql Jdbc 预加载的银行对象集合bankDTOList: {}", JsonUtil.obj2JsonStr(bankDTOList));
        return bankDTOList.stream().collect(Collectors.toMap(BankDTO::getBank, BankDTO::getName));
    }
}