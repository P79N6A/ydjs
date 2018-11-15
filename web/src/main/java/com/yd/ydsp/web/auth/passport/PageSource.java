package com.yd.ydsp.web.auth.passport;

/**
 * Created by zengyixun on 17/5/11.
 */

import com.yd.ydsp.common.enums.SourceEnum;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageSource {
    SourceEnum[] sourceType();//申明支持的来源类型
}
