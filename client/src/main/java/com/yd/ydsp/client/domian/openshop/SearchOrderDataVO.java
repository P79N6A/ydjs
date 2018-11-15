package com.yd.ydsp.client.domian.openshop;

import java.io.Serializable;
import java.util.Map;


public class SearchOrderDataVO extends SearchOrderDataResultVO implements Serializable {

    String openid;
    String weixinConfigId;
    String unionid;
    String orderDay;
    String orderWeek;
    String orderMonth;


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getWeixinConfigId() {
        return weixinConfigId;
    }

    public void setWeixinConfigId(String weixinConfigId) {
        this.weixinConfigId = weixinConfigId;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getOrderDay() {
        return orderDay;
    }

    public void setOrderDay(String orderDay) {
        this.orderDay = orderDay;
    }

    public String getOrderWeek() {
        return orderWeek;
    }

    public void setOrderWeek(String orderWeek) {
        this.orderWeek = orderWeek;
    }

    public String getOrderMonth() {
        return orderMonth;
    }

    public void setOrderMonth(String orderMonth) {
        this.orderMonth = orderMonth;
    }

}
