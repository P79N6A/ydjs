package com.yd.ydsp.common.enums.paypoint;

/**
 * Created by zengyixun on 17/4/4.
 */
public enum WeiXinUserStatusEnum {
    SUBSCRIBE("subscribe", 0),
    UNSUBSCRIBE("unsubscribe", 1),
    AUTHORIZATION("authorization",2),
    ;

    public String name;

    public Integer type;

    WeiXinUserStatusEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (WeiXinUserStatusEnum compareEnum : WeiXinUserStatusEnum.values()) {
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
