package com.lchml.webcat.annotation;

import java.lang.annotation.*;

/**
 * Meta annotation that indicates a web mapping annotation.
 *
 * Created by lc on 17/5/11.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRequestMapping {
    String path() default "";

    ReqMethod[] method() default {};

    String[] headers() default {};

    String[] consumes() default {};

    String[] produces() default {};
}
