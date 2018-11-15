package com.yd.ydsp.common.enums.paypoint;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum ShopReportTypeEnum {

    DAY("day", 0),
    WEEK("week", 1),
    MONTH("month", 2),
    YEAR("year", 3),
    ;

    public String name;

    public Integer type;

    ShopReportTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (ShopReportTypeEnum compareEnum : ShopReportTypeEnum.values()) {
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


    public static ShopReportTypeEnum nameOf(String value){
        for (ShopReportTypeEnum compareEnum : ShopReportTypeEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static ShopReportTypeEnum nameOf(Integer value){
        for (ShopReportTypeEnum compareEnum : ShopReportTypeEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }


}
