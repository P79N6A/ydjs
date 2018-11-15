package com.yd.ydsp.biz.weixin.model;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum WeixinPayTradeStatusEnum {

    SUCCESS("SUCCESS", 0),
    REFUND("REFUND", 1),
    NOTPAY("NOTPAY", 2),
    CLOSED("CLOSED",3),
    REVOKED("REVOKED",4),//已撤销(刷卡支付)
    USERPAYING("USERPAYING",5),//用户支付中
    PAYERROR("PAYERROR",6),
    ;

    public String name;

    public Integer type;

    WeixinPayTradeStatusEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (WeixinPayTradeStatusEnum compareEnum : WeixinPayTradeStatusEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                type = compareEnum.getType();
            }
        }
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static WeixinPayTradeStatusEnum nameOf(String name) {

        for (WeixinPayTradeStatusEnum compareEnum : WeixinPayTradeStatusEnum.values()) {
            if (compareEnum.getName().equals(name.trim().toUpperCase())) {
                return compareEnum;
            }

        }
        return null;
    }

    public static WeixinPayTradeStatusEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (WeixinPayTradeStatusEnum compareEnum : WeixinPayTradeStatusEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }

}
