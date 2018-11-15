package com.yd.ydsp.biz.customer.model;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum SearchUserOrderTypeEnum {
    USERALLINSHOP(0),// 用户在一个店的所有订单
    USERWAITEPAYINSHOP( 1),// 用户在一个店的待支付订单
    USERWATIESENDOUTINSHOP(2),//用户在一个店的待发货订单
    USERWATIECONFIRMINSHOP(3),//用户在一个店的待确认收货订单
    SHOPINDATEINDOOR(6),//商家指定日期的所有堂食订单
    SHOPINDATEBYTABLEID(7),//商家指定日期下指定桌位的订单
    SHOPINDATEBYTCHANNEL(8),//商家指定日期下指定渠道的订单
    SHOPINDATEONLINE(9),//商家指定日期下所有线上商城（外送）订单
    ;


    public Integer type;

    SearchUserOrderTypeEnum(Integer type) {
        this.type = type;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static SearchUserOrderTypeEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (SearchUserOrderTypeEnum compareEnum : SearchUserOrderTypeEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }


}
