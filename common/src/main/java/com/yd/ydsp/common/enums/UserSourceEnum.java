package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/4/4.
 */
public enum UserSourceEnum {
    WEIXIN("WEIXIN", 0),
    ALIPAY("ALIPAY",1),
    DINGTALK("DINGTALK",2),
    YDJSH5("YDJSH5", 7),
    YDANDORID("YDANDORID", 8),
    YDIOS("YDIOS", 9),
    ;

    public String name;

    public Integer type;

    UserSourceEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (UserSourceEnum compareEnum : UserSourceEnum.values()) {
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


    public static UserSourceEnum nameOf(String name) {

        for (UserSourceEnum compareEnum : UserSourceEnum.values()) {
            if (compareEnum.getName().equals(name.trim().toUpperCase())) {
                return compareEnum;
            }

        }
        return null;
    }

    public static UserSourceEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (UserSourceEnum compareEnum : UserSourceEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }

}
