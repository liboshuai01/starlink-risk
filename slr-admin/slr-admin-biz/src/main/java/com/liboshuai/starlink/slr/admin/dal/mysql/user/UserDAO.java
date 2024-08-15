package com.liboshuai.starlink.slr.admin.dal.mysql.user;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liboshuai.starlink.slr.admin.dal.dataobject.user.UserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO extends BaseMapper<UserDO> {
}