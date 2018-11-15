package com.yd.ydsp.biz.yunprinter;

import java.io.IOException;

/**
 * Created by zengyixun on 17/9/25.
 */
public interface YLPrinterService {

    /**
     * 设置访问Token与refreshToken，它们被设置到缓存
     * @throws IOException
     */
    void setAccessToken() throws IOException;

    /**
     * 定时更新token，并更新缓存中的token
     * @throws IOException
     */
    void refreshToken() throws IOException, ClassNotFoundException;

    /**
     * 从缓存中取访问token
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    String getAccessToken() throws IOException, ClassNotFoundException;

    /**
     * 从缓存中取刷新token
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    String getRefreshToken()  throws IOException, ClassNotFoundException;

    /**
     * 使用此接口，开发者将获得此台打印机的接口权限，注册绑定打印机
     * @param machineCode 易联云打印机终端号
     * @param msign 易联云终端密钥
     * @return
     */
    boolean addprinter(String machineCode,String msign) throws IOException, ClassNotFoundException;

}
