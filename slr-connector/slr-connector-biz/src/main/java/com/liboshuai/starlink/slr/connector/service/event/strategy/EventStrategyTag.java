package com.liboshuai.starlink.slr.connector.service.event.strategy;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventStrategyTag {
    String[] channels();
}