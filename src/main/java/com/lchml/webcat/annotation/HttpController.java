package com.lchml.webcat.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a "HttpController"
 *
 * Created by lc on 17/5/11.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface HttpController {
    String path() default "";
}
