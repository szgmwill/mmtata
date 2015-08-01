package com.tata.imta.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Will Zhang on 2015/6/12.
 * 安卓IOC
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentView {

    /**
     * 主布局资源文件ID
     */
    int value();
}
