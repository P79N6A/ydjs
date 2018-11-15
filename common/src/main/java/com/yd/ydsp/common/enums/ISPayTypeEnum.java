package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum ISPayTypeEnum {

    WAITPAY("WAITPAY", -1),//未支付
    ONLINE("ONLINE", 0),//线上支付
    CASH("CASH", 1),//线下现金支付
    CONSUMPTION("CONSUMPTION",2),//使用会员卡的充值资金支付
    ;

    public String name;

    public Integer type;

    ISPayTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (ISPayTypeEnum compareEnum : ISPayTypeEnum.values()) {
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


    public static ISPayTypeEnum nameOf(String value){
        for (ISPayTypeEnum compareEnum : ISPayTypeEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static ISPayTypeEnum nameOf(Integer value){
        for (ISPayTypeEnum compareEnum : ISPayTypeEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }


}
