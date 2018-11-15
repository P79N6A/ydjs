package com.yd.ydsp.biz.openshop;


import java.io.IOException;

public interface ShopApplyService {

    /**
     * 取预授权链接前缀
     * @return
     */
    String getPreAuthLink(String openid,String shopid,Integer weixinType,String appid) throws IOException, ClassNotFoundException;

    /**
     * 申请单完结最后一步，创建出线上(店铺)商城来
     * @param applyid
     * @return
     */
    boolean applyToCreateShop(String applyid);

}
