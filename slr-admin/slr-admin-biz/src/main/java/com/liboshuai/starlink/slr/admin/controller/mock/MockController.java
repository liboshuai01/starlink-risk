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

    @GetMapping("/createEventFileSingleMode")
    @Operation(summary = "创建事件数据文件（文件内容为单条上送模式）")
    public CommonResult<String> createEventFile(long startMillis, long durationMillis, int perSecondCount) {
        mockService.createEventFileSingleMode(startMillis, durationMillis, perSecondCount);
        return CommonResult.success("事件日志文件开始生成，请等待......");
    }

    @GetMapping("/createEventFileBatchMode")
    @Operation(summary = "创建事件数据文件（文件内容为批量上送模式）")
    public CommonResult<String> createEventFileBatchMode(long startMillis, long durationMillis, int perSecondCount) {
        mockService.createEventFileBatchMode(startMillis, durationMillis, perSecondCount);
        return CommonResult.success("事件日志文件开始生成，请等待......");
    }
}
