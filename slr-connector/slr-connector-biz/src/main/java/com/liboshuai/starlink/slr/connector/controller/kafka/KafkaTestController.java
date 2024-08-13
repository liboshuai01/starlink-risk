package com.liboshuai.starlink.slr.connector.controller.kafka;

import com.liboshuai.starlink.slr.connector.pojo.vo.KafkaInfoVO;
import com.liboshuai.starlink.slr.connector.service.kafka.KafkaTestService;
import com.liboshuai.starlink.slr.framework.common.pojo.CommonResult;
import com.liboshuai.starlink.slr.framework.protection.signature.core.annotation.ApiSignature;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.liboshuai.starlink.slr.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "kafka测试接口")
@RestController
@RequestMapping("/kafkaTest")
public class KafkaTestController {

    @Resource
    private KafkaTestService kafkaTestService;

    @ApiSignature
    @PostMapping("/hello")
    @Operation(summary = "你好")
    public CommonResult<String> createMailAccount() {
        log.info("hello");
        return success("hello spring!");
    }

    /**
     * 获取Kafka信息，包含是否可连接，并获取broker列表、topic列表、消费组列表等
     */
    @GetMapping("/getKafkaInfo")
    public CommonResult<KafkaInfoVO> getKafkaInfo() {
        KafkaInfoVO kafkaInfoVO = kafkaTestService.getKafkaInfo();
        return success(kafkaInfoVO);
    }
}
