package com.lchml.webcat.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lc on 11/17/17.
 */
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface WsController {
    String path() default "";
}
