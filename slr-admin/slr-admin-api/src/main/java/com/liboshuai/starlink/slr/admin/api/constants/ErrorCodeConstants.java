package com.liboshuai.starlink.slr.admin.api.constants;


import com.liboshuai.starlink.slr.framework.common.exception.ErrorCode;

/**
 * admin 错误码枚举类
 *
 * admin 系统，使用 20001-30000 段
 */
public interface ErrorCodeConstants {

    ErrorCode RULE_CONDITION_NOT_EXISTS = new ErrorCode(20001, "规则条件不存在");
    ErrorCode EVENT_INFO_NOT_EXISTS = new ErrorCode(20002, "事件信息不存在");

}
