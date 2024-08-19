package com.liboshuai.starlink.slr.connector.controller.event;

import com.liboshuai.starlink.slr.admin.api.dto.event.EventErrorDTO;
import com.liboshuai.starlink.slr.admin.api.dto.event.EventUploadDTO;
import com.liboshuai.starlink.slr.connector.api.constants.ErrorCodeConstants;
import com.liboshuai.starlink.slr.connector.pojo.vo.event.KafkaInfoVO;
import com.liboshuai.starlink.slr.connector.service.event.EventService;
import com.liboshuai.starlink.slr.framework.common.pojo.CommonResult;
import com.liboshuai.starlink.slr.framework.protection.ratelimiter.core.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.liboshuai.starlink.slr.framework.common.pojo.CommonResult.error;
import static com.liboshuai.starlink.slr.framework.common.pojo.CommonResult.success;

@Slf4j
@RestController
@Tag(name = "事件接口")
@RequestMapping("/event")
public class EventController {

    @Resource
    private EventService eventService;

    /**
     * 获取Kafka信息，包含是否可连接，并获取broker列表、topic列表、消费组列表等
     */
    @GetMapping("/kafka-info")
    @Operation(summary = "获取Kafka信息")
    public CommonResult<KafkaInfoVO> getKafkaInfo() {
        KafkaInfoVO kafkaInfoVO = eventService.kafkaInfo();
        return success(kafkaInfoVO);
    }

    @RateLimiter(count = 10000)
    @PostMapping("/upload-kafka")
    @Operation(summary = "上送事件数据到kafka")
    public CommonResult<?> uploadKafka(@RequestBody EventUploadDTO eventUploadDTO) {
        eventService.uploadKafka(eventUploadDTO);
        return success();
    }
}
