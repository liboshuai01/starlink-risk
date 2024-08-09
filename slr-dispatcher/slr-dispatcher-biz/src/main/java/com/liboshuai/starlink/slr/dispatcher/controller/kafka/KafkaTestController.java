package com.liboshuai.starlink.slr.dispatcher.controller.kafka;

import com.liboshuai.starlink.slr.dispatcher.pojo.vo.KafkaInfoVO;
import com.liboshuai.starlink.slr.dispatcher.service.kafka.KafkaTestService;
import com.liboshuai.starlink.slr.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.liboshuai.starlink.slr.framework.common.pojo.CommonResult.success;

@Tag(name = "kafka测试接口")
@RestController
@RequestMapping("/kafkaTest")
public class KafkaTestController {

    @Resource
    private KafkaTestService kafkaTestService;

    @PostMapping("/hello")
    @Operation(summary = "你好")
    public CommonResult<String> createMailAccount() {
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
