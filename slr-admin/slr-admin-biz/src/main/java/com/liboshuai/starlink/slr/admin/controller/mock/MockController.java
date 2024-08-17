package com.liboshuai.starlink.slr.admin.controller.mock;

import com.liboshuai.starlink.slr.admin.service.mock.MockService;
import com.liboshuai.starlink.slr.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Tag(name = "mock接口")
@RequestMapping("/mock")
public class MockController {

    @Resource
    private MockService mockService;

    @GetMapping("/createEventFile")
    @Operation(summary = "生成事件日志文件")
    public CommonResult<String> createEventFile(long startMillis, long durationMillis, int perSecondCount) {
//        mockService.createEventFile(System.currentTimeMillis(), TimeUnit.MINUTES.toMillis(10), 1000);
        mockService.createEventFile(startMillis, durationMillis, perSecondCount);
        return CommonResult.success("事件日志文件开始生成，请等待......");
    }
}
