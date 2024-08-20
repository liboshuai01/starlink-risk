package com.liboshuai.starlink.slr.connector.controller;

public class JedisClusterUtilTest {

    public static void main(String[] args) {
        setUp();

        testSetAndGetString();
        testListOperations();
        testSetOperations();
    }

    public static void setUp() {
        // 初始化工具类
        // JedisClusterUtil 在静态块中已经初始化，无需额外操作
    }

    public static void testSetAndGetString() {
        String key = "testKey";
        String value = "testValue";

        // 写入数据
        JedisClusterUtil.getJedisCluster().set(key, value);

        // 读取数据
        String result = JedisClusterUtil.getJedisCluster().get(key);

        // 验证结果
        if (value.equals(result)) {
            System.out.println("testSetAndGetString passed");
        } else {
            System.out.println("testSetAndGetString failed: expected " + value + ", got " + result);
        }
    }

    public static void testListOperations() {
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
        if (value1.equals(result2) && value2.equals(result1)) {
            System.out.println("testListOperations passed");
        } else {
            System.out.println("testListOperations failed: expected [" + value1 + ", " + value2 + "], got [" + result2 + ", " + result1 + "]");
        }
    }

    public static void testSetOperations() {
        String key = "testSet";
        String member = "member1";

        // 清空集合
        JedisClusterUtil.getJedisCluster().del(key);

        // 添加元素到集合
        JedisClusterUtil.getJedisCluster().sadd(key, member);

        // 验证元素是否存在
        boolean isMember = JedisClusterUtil.getJedisCluster().sismember(key, member);

        // 验证结果
        if (isMember) {
            System.out.println("testSetOperations passed");
        } else {
            System.out.println("testSetOperations failed: " + member + " should be a member of the set");
        }
    }
}
