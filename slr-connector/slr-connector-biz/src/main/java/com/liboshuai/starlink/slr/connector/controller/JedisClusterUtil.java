package com.liboshuai.starlink.slr.connector.controller;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class JedisClusterUtil {

    @Getter
    private static final JedisCluster jedisCluster;

    static {
        // 加载配置文件
        Properties properties = new Properties();
        try (InputStream input = JedisClusterUtil.class.getClassLoader().getResourceAsStream("flink.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find flink.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file", e);
        }

        // 解析配置
        String clusterNodes = properties.getProperty("redis.cluster.nodes");
        String[] nodes = clusterNodes.split(",");
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        for (String node : nodes) {
            String[] hostPort = node.split(":");
            jedisClusterNodes.add(new HostAndPort(hostPort[0].trim(), Integer.parseInt(hostPort[1].trim())));
        }

        // 配置连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxWait(Duration.ofMillis(Long.parseLong(properties.getProperty("redis.pool.maxWait", "3000"))));
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(Long.parseLong(properties.getProperty("redis.pool.timeBetweenEvictionRuns", "30000"))));
        poolConfig.setNumTestsPerEvictionRun(Integer.parseInt(properties.getProperty("redis.pool.numTestsPerEvictionRun", "-1")));
        poolConfig.setMaxTotal(Integer.parseInt(properties.getProperty("redis.pool.maxTotal", "100")));
        poolConfig.setMaxIdle(Integer.parseInt(properties.getProperty("redis.pool.maxIdle", "50")));
        poolConfig.setMinIdle(Integer.parseInt(properties.getProperty("redis.pool.minIdle", "20")));
        poolConfig.setTestWhileIdle(true);

        int connectionTimeout = Integer.parseInt(properties.getProperty("redis.connectionTimeout", "3000"));
        int soTimeout = Integer.parseInt(properties.getProperty("redis.soTimeout", "3000"));
        int maxAttempts = Integer.parseInt(properties.getProperty("redis.maxAttempts", "3"));
        String password = properties.getProperty("redis.password");

        if (!StringUtils.isEmpty(password)) {
            jedisCluster = new JedisCluster(jedisClusterNodes, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);

        } else {
            jedisCluster = new JedisCluster(jedisClusterNodes, connectionTimeout, soTimeout, maxAttempts, poolConfig);
        }
    }

}
