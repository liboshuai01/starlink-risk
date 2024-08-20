package com.liboshuai.starlink.slr.engine.utils.rule;

import com.liboshuai.starlink.slr.engine.function.RuleCalculator;
import com.liboshuai.starlink.slr.engine.pojo.RuleMeta;
import groovy.lang.GroovyClassLoader;

/**
 * 规则工具类
 */
public class RuleUtil {

    /**
     * 规则比较
     * @param stateValue 状态值
     * @param ruleValue 规则值
     * @param compareType 操作符
     * @return 比较结果
     */
    public static boolean compare(Integer  stateValue, Double ruleValue, String compareType) {

        if(stateValue == null) return false;

        return compare(stateValue.doubleValue(),ruleValue,compareType);

    }

    public static boolean compare(Double  stateValue, Double ruleValue, String compareType) {
        if(stateValue == null) return false;
        switch (compareType) {
            case "<":
                return stateValue < ruleValue;
            case "<=":
                return stateValue <= ruleValue;
            case ">":
                return stateValue > ruleValue;
            case ">=":
                return stateValue >= ruleValue;
            case "=":
                return stateValue == ruleValue;
            case "!=":
                return stateValue != ruleValue;
            default:
                return false;
        }
    }

    /**
     * 生成运算机对象
     */
    public static RuleCalculator generateRuleCalculator(GroovyClassLoader groovyClassLoader, RuleMeta ruleMeta)
            throws InstantiationException, IllegalAccessException {
        // 构造规则运算机对象
        RuleCalculator ruleCalculator;
        // 获取规则的运算机模型代码
        String calculatorCode = ruleMeta.getCalculator_code();

        // 用groovy类加载器，动态编译代码，并加载到类路径
        Class aClass = groovyClassLoader.parseClass(calculatorCode);

        // 通过反射，来获得运算机的实例对象
        ruleCalculator = (RuleCalculator) aClass.newInstance();

        return ruleCalculator;
    }
}