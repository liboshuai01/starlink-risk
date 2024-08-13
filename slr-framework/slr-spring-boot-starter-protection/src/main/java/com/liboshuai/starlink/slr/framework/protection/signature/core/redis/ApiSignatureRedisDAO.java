package com.liboshuai.starlink.slr.framework.protection.signature.core.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * HTTP API 签名 Redis DAO
 *
 * @author Zhougang
 */
@AllArgsConstructor
public class ApiSignatureRedisDAO {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 验签随机数
     *
     * KEY 格式：slr:signature_nonce:%s // 参数为 随机数
     * VALUE 格式：String
     * 过期时间：不固定
     */
    private static final String SIGNATURE_NONCE = "slr:api_signature_nonce:%s";

    /**
     * 签名密钥
     *
     * HASH 结构
     * KEY 格式：%s // 参数为 appid
     * VALUE 格式：String
     * 过期时间：永不过期（预加载到 Redis）
     */
    private static final String SIGNATURE_APPID = "slr:api_signature_app";

    // ========== 验签随机数 ==========

    public String getNonce(String nonce) {
        return stringRedisTemplate.opsForValue().get(formatNonceKey(nonce));
    }

    public void setNonce(String nonce, int time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(formatNonceKey(nonce), "", time, timeUnit);
    }

    private static String formatNonceKey(String key) {
        return String.format(SIGNATURE_NONCE, key);
    }

    // ========== 签名密钥 ==========

    public String getAppSecret(String appId) {
        return (String) stringRedisTemplate.opsForHash().get(SIGNATURE_APPID, appId);
    }

}
