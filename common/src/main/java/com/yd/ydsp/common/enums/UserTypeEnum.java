package com.yd.ydsp.common.enums;

public enum UserTypeEnum {
    GUEST("guest", 0,"无用户类型"),
    SHOPOWNER("shopowner", 1,"店铺负责人"),
    SHOPMANAGER("shopmanager", 2,"店铺管理员"),
    SHOPWAITER("shopwaiter", 3,"店铺服务员"),
    CUSTOMER("customer", 6,"C端消费者"),
    ;

    public String name;

    public Integer type;

    public String desc;

    UserTypeEnum(String name, Integer type,String desc) {
        this.name = name;
        this.type = type;
        this.desc = desc;
    }

    public static Integer getTypeByName(String name) {
        Integer value = null;
        for (UserTypeEnum compareEnum : UserTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                value = compareEnum.getType();
            }
        }
        return value;
    }

    public static String getDesc(String name) {
        String desc = null;
        for (UserTypeEnum compareEnum : UserTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                desc = compareEnum.getDesc();
            }
        }
        return desc;
    }

    public static String getDesc(Integer type) {
        String desc = null;
        for (UserTypeEnum compareEnum : UserTypeEnum.values()) {
            if (compareEnum.getType().intValue()==type.intValue()) {
                desc = compareEnum.getDesc();
            }
        }
        return desc;
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

    public String getDesc(){ return desc; }

    public void setDesc(String desc){ this.desc = desc; }

    public static UserTypeEnum nameOf(String name) {

        for (UserTypeEnum compareEnum : UserTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                return compareEnum;
            }

        }
        return null;
    }

    public static UserTypeEnum nameOf(Integer type){
        if(type==null){
            return null;
        }
        for (UserTypeEnum compareEnum : UserTypeEnum.values()) {
            if (compareEnum.getType().intValue()==type.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }
}
