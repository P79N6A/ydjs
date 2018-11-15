package com.yd.ydsp.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 该注解忽略字段描述,例如输出某个对象的内容不需要某些字段时,可以采用该注解,并在转换时忽略相应字段
 * Created by zengyixun on 16/1/11.
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface LogIgnore {
}
