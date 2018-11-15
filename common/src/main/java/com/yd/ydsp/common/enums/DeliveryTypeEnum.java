package com.yd.ydsp.common.enums;

public enum DeliveryTypeEnum {

    ZT("自提", 0),
    SHANGJIAPEI("商家配送", 1),
    UU("UU跑腿", 2),
    DADA("达达配送", 3),
    ;

    public String name;

    public Integer type;

    DeliveryTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (DeliveryTypeEnum compareEnum : DeliveryTypeEnum.values()) {
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

    public static DeliveryTypeEnum nameOf(Integer type){
        if(type==null){
            return SHANGJIAPEI;
        }
        for (DeliveryTypeEnum compareEnum : DeliveryTypeEnum.values()) {
            if (compareEnum.getType().intValue()==type.intValue()) {
                return compareEnum;
            }

        }
        return SHANGJIAPEI;

    }
    
}
