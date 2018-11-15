package com.yd.ydsp.common.enums.paypoint;

/**
 * Created by zengyixun on 17/9/12.
 */
public enum WeiXinTypeEnum {
    OPENPUBLIC(0),//开放平台授权公众号
    OPENSMALL(1),//开放平台授权小程序
    SERVICE(2),//服务号
    SMALL(3),//小程序
    SUBSCRIBE(4),//订阅号
    ;

    public Integer type;

    WeiXinTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static WeiXinTypeEnum nameOf(Integer type) {

        if(type==null){
            return null;
        }
        for (WeiXinTypeEnum compareEnum : WeiXinTypeEnum.values()) {
            if (compareEnum.getType().intValue()==type.intValue()) {
                return compareEnum;
            }

        }
        return null;
    }

}
