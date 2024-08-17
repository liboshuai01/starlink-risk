package com.liboshuai.starlink.slr.admin.api.annotaion;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StrategyIdentifier {
    String[] value();
}