package com.liboshuai.starlink.slr.admin.controller.user;

import com.liboshuai.starlink.slr.admin.dal.dataobject.user.UserDO;
import com.liboshuai.starlink.slr.admin.service.user.UserService;
import com.liboshuai.starlink.slr.framework.common.pojo.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/hello")
    public CommonResult<UserDO> hello() {
        UserDO userDO = userService.hello();
        return CommonResult.success(userDO);
    }
}
