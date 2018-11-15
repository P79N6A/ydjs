package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/10/19.
 */
public enum ModifyDataTypeEnum {
    ADD("add", 0),//增加
    UPDATE("update", 1),//修改
    DELETE("delete", 2),//删除
    ;

    public String name;

    public Integer type;

    ModifyDataTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (ModifyDataTypeEnum compareEnum : ModifyDataTypeEnum.values()) {
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
