package com.yd.ydsp.web.auth.passport;

/**
 * Created by zengyixun on 17/5/11.
 */

import com.yd.ydsp.common.enums.RoleTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthPassport {
    RoleTypeEnum[] roleType();//申明支持的角色类型
    boolean validate() default true;//为false，表注释上申明了不验证
}
