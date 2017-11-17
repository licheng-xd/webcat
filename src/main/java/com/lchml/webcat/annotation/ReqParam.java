package com.lchml.webcat.annotation;

import java.lang.annotation.*;

/**
 * Created by lc on 17/5/11.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReqParam {

    String name() default "";

    boolean required() default true;

    String defaultValue() default "";
}
