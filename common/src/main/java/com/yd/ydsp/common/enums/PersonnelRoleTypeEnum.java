package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/5/11.
 */
public enum PersonnelRoleTypeEnum {
    ROOT("root",0),//超级用户
    OWNER("owner", 1),//owner
    MANAGER("manager", 2),//管理员
    NORMAL("normal", 3),//一般用户
    ;

    public String name;

    public Integer type;

    PersonnelRoleTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (PersonnelRoleTypeEnum compareEnum : PersonnelRoleTypeEnum.values()) {
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

    public static PersonnelRoleTypeEnum nameOf(String value){
        for (PersonnelRoleTypeEnum compareEnum : PersonnelRoleTypeEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static PersonnelRoleTypeEnum nameOf(Integer value){
        for (PersonnelRoleTypeEnum compareEnum : PersonnelRoleTypeEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }
}
