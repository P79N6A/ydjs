package com.yd.ydsp.common.enums.paypoint;

/**
 * Created by zengyixun on 17/9/17.
 *
 */
public enum PrintTypeEnum {
    KITCHEN("kitchen", 1,"后厨打印机"),
    CASHIER("cashier", 2,"接单打印机"),
//    TAKEOUT("takeOutPrint", 3,"店铺接单打印机"),
    ;

    public String name;

    public Integer type;

    public String desc;

    PrintTypeEnum(String name, Integer type,String desc) {
        this.name = name;
        this.type = type;
        this.desc = desc;
    }

    public static Integer getTypeByName(String name) {
        Integer value = null;
        for (PrintTypeEnum compareEnum : PrintTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                value = compareEnum.getType();
            }
        }
        return value;
    }

    public static String getDesc(String name) {
        String desc = null;
        for (PrintTypeEnum compareEnum : PrintTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                desc = compareEnum.getDesc();
            }
        }
        return desc;
    }

    public static String getDesc(Integer type) {
        String desc = null;
        for (PrintTypeEnum compareEnum : PrintTypeEnum.values()) {
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

    public static PrintTypeEnum nameOf(String name) {

        for (PrintTypeEnum compareEnum : PrintTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                return compareEnum;
            }

        }
        return null;
    }

    public static PrintTypeEnum nameOf(Integer type){
        if(type==null){
            return null;
        }
        for (PrintTypeEnum compareEnum : PrintTypeEnum.values()) {
            if (compareEnum.getType().intValue()==type.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }
}
