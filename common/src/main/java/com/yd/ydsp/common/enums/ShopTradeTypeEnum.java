package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/8/13.
 */
public enum ShopTradeTypeEnum {
    /**商家店铺类型：0-餐馆; 1-零售; 2-微商**/
    Catering("Catering",0),
    Retail("Retail", 1),
    MICBusiness("MICBusiness", 2),
    ;

    public String name;

    public Integer type;

    ShopTradeTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (ShopTradeTypeEnum compareEnum : ShopTradeTypeEnum.values()) {
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
    public static ShopTradeTypeEnum nameOf(String value){
        for (ShopTradeTypeEnum compareEnum : ShopTradeTypeEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static ShopTradeTypeEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (ShopTradeTypeEnum compareEnum : ShopTradeTypeEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }
}
