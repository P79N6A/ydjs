package com.yd.ydsp.web.auth.cookie;

/**
 * Created by zengyixun on 17/5/28.
 */
public class CookieConstantTable {
    // cookie的有效期默认为1天
    public final static int COOKIE_MAX_AGE = -1;
    //cookie加密时的额外的salt
    public final static String salt = "ydjs360.com";

    public final static String yidb = "yidb";
    public final static String yidc = "yidc";
    public final static String yidm = "yidm";//小二后台
    public final static String yida = "yida";//服务商后台
    public final static String yidt = "yidt";//测试cookie
}
