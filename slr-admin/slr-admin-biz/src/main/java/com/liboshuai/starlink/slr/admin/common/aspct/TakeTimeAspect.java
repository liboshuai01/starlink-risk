package com.liboshuai.starlink.slr.admin.common.aspct;

import com.liboshuai.starlink.slr.framework.common.util.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class TakeTimeAspect {
    //统计请求的处理时间
    ThreadLocal<Long> startTime = new ThreadLocal<>();
    ThreadLocal<Long> endTime = new ThreadLocal<>();

    /**
     * 带有@TakeTime注解的方法
     */
    @Pointcut("@annotation(com.liboshuai.starlink.slr.admin.common.annotation.TakeTime)")
    public void TakeTime() {

    }

    @Before("TakeTime()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 获取方法的名称
        String methodName = joinPoint.getSignature().getName();
        // 获取方法入参
        Object[] param = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object o : param) {
            sb.append(o).append(";");
        }
        //接收到请求，记录请求内容
        String requestUrl = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            HttpServletRequest httpServletRequest = attributes.getRequest();
            requestUrl = httpServletRequest.getRequestURL().toString();
        }

        log.info("==================================================");
        log.info("进入方法: [{}] 参数: {}", methodName, sb);
        startTime.set(System.currentTimeMillis());
        log.info("请求URL: [{}]", requestUrl);
        log.info("开始时间: [{}] ({})", new Date(startTime.get()), startTime.get());
        log.info("--------------------------------------------------");
    }

    @AfterReturning(returning = "ret", pointcut = "TakeTime()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        //处理完请求后，返回内容
        log.info("--------------------------------------------------");
        log.info("方法 [{}] 返回值: {}", joinPoint.getSignature().getName(), JsonUtils.toJsonString(ret));
        endTime.set(System.currentTimeMillis());
        log.info("结束时间: [{}]", new Date(endTime.get()));
        long duration = endTime.get() - startTime.get();
        log.info("执行耗时: {} ms ({} seconds)", duration, duration / 1000.0);
        log.info("==================================================");
    }
}
