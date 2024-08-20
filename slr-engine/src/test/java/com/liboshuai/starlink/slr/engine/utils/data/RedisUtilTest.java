package com.liboshuai.starlink.slr.engine.utils.data;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RedisUtilTest {

    @Test
    public void testStringOperations() {
        String key = "testKey";
        String value = "testValue";

        RedisUtil.setString(key, value);
        assertEquals(value, RedisUtil.getString(key));

        RedisUtil.setStringWithExpiry(key, "newValue", 10);
        assertEquals("newValue", RedisUtil.getString(key));
        assertTrue(RedisUtil.ttl(key) > 0);
    }

    @Test
    public void testListOperations() {
        String key = "testList";

        RedisUtil.lpush(key, "value1", "value2");
        assertEquals("value2", RedisUtil.lrange(key, 0, 0).get(0));

        RedisUtil.lpushWithExpiry(key, 10, "value3");
        assertTrue(RedisUtil.ttl(key) > 0);
    }

    @Test
    public void testSetOperations() {
        String key = "testSet";

        RedisUtil.sadd(key, "member1", "member2");
        assertTrue(RedisUtil.smembers(key).contains("member1"));

        RedisUtil.saddWithExpiry(key, 10, "member3");
        assertTrue(RedisUtil.ttl(key) > 0);
    }

    @Test
    public void testHashOperations() {
        String key = "testHash";
        String field = "field1";
        String value = "value1";

        RedisUtil.hset(key, field, value);
        assertEquals(value, RedisUtil.hget(key, field));

        RedisUtil.hsetWithExpiry(key, 10, field, "newValue");
        assertEquals("newValue", RedisUtil.hget(key, field));
        assertTrue(RedisUtil.ttl(key) > 0);
    }

    @Test
    public void testZSetOperations() {
        String key = "testZSet";

        RedisUtil.zadd(key, 1.0, "member1");
        assertTrue(RedisUtil.zrange(key, 0, -1).contains("member1"));

        RedisUtil.zaddWithExpiry(key, 10, 2.0, "member2");
        assertTrue(RedisUtil.ttl(key) > 0);
    }

    @Test
    public void testKeyOperations() {
        String key = "testKeyOp";
        RedisUtil.setString(key, "value");

        assertTrue(RedisUtil.exists(key));
        RedisUtil.del(key);
        assertFalse(RedisUtil.exists(key));
    }

    private static final int THREAD_COUNT = 10; // 线程数
    private static final int OPERATIONS_PER_THREAD = 1000; // 每个线程的操作次数

    @Test
    public void testHighConcurrency() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    String key = "key-" + Thread.currentThread().getId() + "-" + j;
                    String value = "value-" + j;

                    // 执行Redis操作
                    RedisUtil.setString(key, value);
                    String retrievedValue = RedisUtil.getString(key);

                    if (!value.equals(retrievedValue)) {
                        System.err.println("数据不一致: " + key);
                    }
                }
            });
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("压测完成，耗时: " + (endTime - startTime) + " ms");
    }
}
