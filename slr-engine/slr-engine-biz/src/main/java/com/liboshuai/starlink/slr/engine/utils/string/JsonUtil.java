package com.liboshuai.starlink.slr.engine.utils.string;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.TypeReference;

import java.util.List;
import java.util.Map;


/**
 * author: liboshuai
 * description: 基于fastjson2的json工具类
 * date: 2023
 */
public class JsonUtil {

    /**
     * author: liboshuai
     * description: Java对象转换为Json字符串
     *
     * @param obj:
     * @return java.lang.String
     */
    public static String obj2JsonStr(Object obj) {
        return JSON.toJSONString(obj);
    }

    /**
     * author: liboshuai
     * description: Json字符串转换为Java对象
     *
     * @param json:
     * @return java.lang.Object
     */
    public static <T> T jsonStr2Obj(String json, Class<T> cls) {
        return JSON.parseObject(json, cls);
    }

    /**
     * author: liboshuai
     * description: Json字符串转换为Java对象（下划线转驼峰）
     */
    public static <T> T jsonStr2ObjSnakeCase(String json, Class<T> cls) {
        return JSON.parseObject(json, cls, JSONReader.Feature.SupportSmartMatch);
    }

    /**
     * author: liboshuai
     * description: Json字符串转换为指定类型的Map
     */
    public static <K, V> Map<K, List<V>> jsonStr2Map(String json, Class<K> key, Class<V> value) {
        return JSON.parseObject(json, new TypeReference<Map<K, List<V>>>(key, value) {
        });
    }

    /**
     * author: liboshuai
     * description: json字符串转换为指定类型的List
     */
    public static <T> List<T> jsonStr2List(String json, Class<T> cls) {

        return JSON.parseArray(json, cls);

    }
}
