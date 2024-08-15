package com.liboshuai.starlink.slr.admin.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liboshuai.starlink.slr.admin.dal.dataobject.user.UserDO;
import com.liboshuai.starlink.slr.admin.dal.mysql.user.UserDAO;
import com.liboshuai.starlink.slr.admin.service.user.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDAO userDAO;

    @Override
    public UserDO hello() {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, "lbs");
        return userDAO.selectOne(queryWrapper);

    }
}
