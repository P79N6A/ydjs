package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum PayTypeEnum {

    WEIXINS("weixins", 0),//微信服务商
    WEIXIN2B("weixin2B", 1),//引灯商家端微信帐号
    YEEPAY("yeepay", 2),//易宝
    ALIPAY("alipay",3),//支付宝
    ;

    public String name;

    public Integer type;

    PayTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (PayTypeEnum compareEnum : PayTypeEnum.values()) {
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


    public static PayTypeEnum nameOf(String value){
        for (PayTypeEnum compareEnum : PayTypeEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static PayTypeEnum nameOf(Integer value){
        for (PayTypeEnum compareEnum : PayTypeEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }


}
