package com.liboshuai.starlink.slr.admin.api.constants;


import com.liboshuai.starlink.slr.framework.common.exception.ErrorCode;

/**
 * admin 错误码枚举类
 *
 * admin 系统，使用 20001-30000 段
 */
public interface ErrorCodeConstants {

    // ========== 公众号账号 1-006-000-000 ============
    ErrorCode ACCOUNT_NOT_EXISTS = new ErrorCode(1_006_000_000, "公众号账号不存在");

}
