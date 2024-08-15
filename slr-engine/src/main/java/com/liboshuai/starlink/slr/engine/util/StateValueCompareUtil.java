package com.liboshuai.starlink.slr.engine.util;

public class StateValueCompareUtil {


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
}
