package com.liboshuai.starlink.slr.admin.controller.mock;

import com.liboshuai.starlink.slr.admin.service.mock.MockService;
import com.liboshuai.starlink.slr.framework.common.pojo.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/mock")
public class MockController {

    @Resource
    private MockService mockService;

    @GetMapping("/createEventFile")
    public CommonResult<Boolean> createEventFile() {
        mockService.generatorDataToFile(System.currentTimeMillis(), TimeUnit.DAYS.toMillis(10), 1000);
        return CommonResult.success(true);
    }
}
