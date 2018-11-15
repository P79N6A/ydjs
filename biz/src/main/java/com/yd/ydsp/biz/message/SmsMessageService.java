package com.yd.ydsp.biz.message;

/**
 * @author zengyixun
 * @date 17/11/29
 */
public interface SmsMessageService {

    /**
     * 使用手机验证码方式登录的验证码短信
     * @param phoneNumber
     * @param code
     */
    boolean loginSmsMessage(String phoneNumber,String code);

    /**
     * 手机身份验证码短信
     * @param phoneNumber
     * @param code
     */
    boolean identitySmsMessage(String phoneNumber, String code);

    /**
     * 设置手机验证码缓存，5分钟有效
     */
    boolean setIdentityCode(String phoneNumber);

    /**
     * 验证手机验证码是否输入正确
     */
    boolean isIdentityCode(String phoneNumber, String code);


    /**
     * 设置手机登录验证码缓存，5分钟有效
     */
    boolean setLoginIdentityCode(String phoneNumber);

    /**
     * 验证手机登录验证码是否输入正确
     */
    boolean isLoginIdentityCode(String phoneNumber, String code);

}
