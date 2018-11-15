package com.yd.ydsp.biz.weixin.model;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum WeixinPayTradeTypeEnum {

    JSAPI("JSAPI", 0),
    NATIVE("NATIVE", 1),
    APP("APP", 2),//
    MICROPAY("MICROPAY",3),
    ;

    public String name;

    public Integer type;

    WeixinPayTradeTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (WeixinPayTradeTypeEnum compareEnum : WeixinPayTradeTypeEnum.values()) {
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


}
