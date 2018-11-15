package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum LinkTypeEnum {

    NULL("null", 0),//不跳转
    URL("url", 1),//普通url
    GOODS("goods", 2),//商品
    ACTIVITY("activity", 3),//活动页
    ;

    public String name;

    public Integer type;

    LinkTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (LinkTypeEnum compareEnum : LinkTypeEnum.values()) {
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

    public static LinkTypeEnum nameOf(String value){
        for (LinkTypeEnum compareEnum : LinkTypeEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static LinkTypeEnum nameOf(Integer value){
        for (LinkTypeEnum compareEnum : LinkTypeEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }


}
