package com.liboshuai.starlink.slr.connector.api.constants;


import com.liboshuai.starlink.slr.framework.common.exception.ErrorCode;

/**
 * connector 错误码枚举类
 *
 * connector 系统，使用 10001-20000 段
 */
public interface ErrorCodeConstants {

    ErrorCode UPLOAD_EVENT_EXCEPTION = new ErrorCode(10001, "上送事件数据异常: {}");
    ErrorCode UPLOAD_EVENT_NOT_EMPTY = new ErrorCode(10002, "上送事件数据集合不能为空");
    ErrorCode UPLOAD_EVENT_OVER_MAX = new ErrorCode(10003, "上送事件数据集合超过单次最大元素个数[{}]限制");

}
