package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum SexTypeEnum {

    NOTHING("nothing", 0),//未知
    MALE("male", 1),//男
    FEMALE("female", 2),//女
    ;


    public String name;

    public Integer type;

    SexTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (SexTypeEnum compareEnum : SexTypeEnum.values()) {
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
