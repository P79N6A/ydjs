package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum BillTypeEnum {

    IN("in", 0),//充值
    GIVE("give", 1),//赠送
    GIVECODE("giveCode", 2),//通过卡卷赠送
    TRADE("trade",3),//商品交易
    ;

    public String name;

    public Integer type;

    BillTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (BillTypeEnum compareEnum : BillTypeEnum.values()) {
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


    public static BillTypeEnum nameOf(String value){
        for (BillTypeEnum compareEnum : BillTypeEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static BillTypeEnum nameOf(Integer value){
        for (BillTypeEnum compareEnum : BillTypeEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }


}
