package com.liboshuai.starlink.slr.admin.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.liboshuai.starlink.slr.framework.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private RestTemplate restTemplate;

    private static final String APP_ID = "game";
    private static final String APP_SECRET = "game_secret";

    @GetMapping("/apiSignature")
    public CommonResult<?> apiSignature() {
        log.info("apiSignature");
        String jsonBody = "{\"username\": \"lbs\",\"age\": 18}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appId", APP_ID);
        String uuidString = UUID.randomUUID().toString();
        headers.set("nonce", uuidString);
        long currentTimeMillis = System.currentTimeMillis();
        headers.set("timestamp", currentTimeMillis + "");
        SortedMap<String, String> parameterMap = new TreeMap<>();
        SortedMap<String, String> headerMap = new TreeMap<>();
        headerMap.put("appId", APP_ID);
        headerMap.put("nonce", uuidString);
        headerMap.put("timestamp", currentTimeMillis + "");
        String sign = MapUtil.join(parameterMap, "&", "=")
                + jsonBody
                + MapUtil.join(headerMap, "&", "=")
                + APP_SECRET;
        headers.set("sign", DigestUtil.sha256Hex(sign));

        HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<CommonResult> response = restTemplate.exchange("http://localhost:30000/kafkaTest/hello",
                HttpMethod.POST, httpEntity, CommonResult.class);
        CommonResult body = response.getBody();
        return body;
    }
}
