package com.liboshuai.starlink.slr.connector.api.constants;


import com.liboshuai.starlink.slr.framework.common.exception.ErrorCode;

/**
 * connector 错误码枚举类
 *
 * connector 系统，使用 10001-20000 段
 */
public interface ErrorCodeConstants {

    ErrorCode UPLOAD_EVENT_ERROR= new ErrorCode(10001, "上送事件数据错误");

}
