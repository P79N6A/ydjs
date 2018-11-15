package com.yd.ydsp.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by zengyixun on 16/1/11.
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface LogShow {
}
