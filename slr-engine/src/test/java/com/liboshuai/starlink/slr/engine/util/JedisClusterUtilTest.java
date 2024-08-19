package com.liboshuai.starlink.slr.engine.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JedisClusterUtilTest {

    @BeforeAll
    public static void setUp() {
        // 初始化工具类
        // JedisClusterUtil 在静态块中已经初始化，无需额外操作
    }

    @Test
    public void testSetAndGetString() {
        String key = "testKey";
        String value = "testValue";

        // 写入数据
        JedisClusterUtil.getJedisCluster().set(key, value);

        // 读取数据
        String result = JedisClusterUtil.getJedisCluster().get(key);

        // 验证结果
        assertEquals(value, result);
    }

    @Test
    public void testListOperations() {
        String key = "testList";
        String value1 = "value1";
        String value2 = "value2";

        // 清空列表
        JedisClusterUtil.getJedisCluster().del(key);

        // 向列表添加元素
        JedisClusterUtil.getJedisCluster().lpush(key, value1, value2);

        // 从列表中弹出元素
        String result1 = JedisClusterUtil.getJedisCluster().rpop(key);
        String result2 = JedisClusterUtil.getJedisCluster().rpop(key);

        // 验证结果
        assertEquals(value1, result2);
        assertEquals(value2, result1);
    }

    @Test
    public void testSetOperations() {
        String key = "testSet";
        String member = "member1";

        // 清空集合
        JedisClusterUtil.getJedisCluster().del(key);

        // 添加元素到集合
        JedisClusterUtil.getJedisCluster().sadd(key, member);

        // 验证元素是否存在
        boolean isMember = JedisClusterUtil.getJedisCluster().sismember(key, member);

        // 验证结果
        assertTrue(isMember);
    }
}

