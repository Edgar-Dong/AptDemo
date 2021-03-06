package com.example.android.apt_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author:無忌
 * @date:2020/8/7
 * @description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface ClickViewCompiler {
    int value() default -1;
}
