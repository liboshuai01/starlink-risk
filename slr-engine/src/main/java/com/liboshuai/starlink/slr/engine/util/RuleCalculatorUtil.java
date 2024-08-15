package com.liboshuai.starlink.slr.engine.util;

import com.liboshuai.starlink.slr.engine.calculator.RuleCalculator;
import com.liboshuai.starlink.slr.engine.pojo.RuleMeta;
import groovy.lang.GroovyClassLoader;
import org.apache.flink.api.common.functions.RuntimeContext;

public class RuleCalculatorUtil {
    public static RuleCalculator generateRuleCalculator(GroovyClassLoader groovyClassLoader, RuleMeta ruleMeta , RuntimeContext runtimeContext) throws InstantiationException, IllegalAccessException {
        // 构造规则运算机对象
        RuleCalculator ruleCalculator;
        // 获取规则的运算机模型代码
        String calculatorCode = ruleMeta.getCalculator_code();

        // 用groovy类加载器，动态编译代码，并加载到类路径
        Class aClass = groovyClassLoader.parseClass(calculatorCode);

        // 通过反射，来获得运算机的实例对象
        ruleCalculator = (RuleCalculator) aClass.newInstance();

        // 用规则元数据中的规则参数，来初始化运算机对象
        ruleCalculator.init(runtimeContext, ruleMeta.getRule_param_json(), ruleMeta.getPre_select_users_bitmap());
        return ruleCalculator;
    }
}
