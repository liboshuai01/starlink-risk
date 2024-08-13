package com.liboshuai.starlink.slr.framework.protection.signature.config;

import com.liboshuai.starlink.slr.framework.protection.signature.core.aop.ApiSignatureAspect;
import com.liboshuai.starlink.slr.framework.protection.signature.core.redis.ApiSignatureRedisDAO;
import com.liboshuai.starlink.slr.framework.redis.config.SlrRedisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * HTTP API 签名的自动配置类
 *
 * @author Zhougang
 */
@AutoConfiguration(after = SlrRedisAutoConfiguration.class)
public class SlrApiSignatureAutoConfiguration {

    @Bean
    public ApiSignatureAspect signatureAspect(ApiSignatureRedisDAO signatureRedisDAO) {
        return new ApiSignatureAspect(signatureRedisDAO);
    }

    @Bean
    public ApiSignatureRedisDAO signatureRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new ApiSignatureRedisDAO(stringRedisTemplate);
    }

}
