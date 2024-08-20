package com.liboshuai.starlink.slr.engine.exception;

/**
 * author: liboshuai
 * description: Flink 配置信息自定义错误
 * date: 2023
 */
public class FlinkPropertiesException extends BizRuntimeException {

    public FlinkPropertiesException(BizExceptionInfo info) {
        super(info);
    }
}
