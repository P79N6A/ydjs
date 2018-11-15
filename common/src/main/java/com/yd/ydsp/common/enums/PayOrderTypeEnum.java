package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum PayOrderTypeEnum {
    CPORDER("cporder", 0),//cp
    C2B("c2b", 1),//消费者向商家付款
    B2B("b2b", 2),//一个cp向另一个cp进行采购
    SINGLESHOP("singleshop", 3),//独立的商户支付，自有支付体系
    RECHARGE("recharge",31),//充值订单
    YDPAY("ydpay", 38),//收钱宝支付订单

    ;

    public String name;

    public Integer type;

    PayOrderTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (PayOrderTypeEnum compareEnum : PayOrderTypeEnum.values()) {
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


    public static PayOrderTypeEnum nameOf(String value){
        for (PayOrderTypeEnum compareEnum : PayOrderTypeEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static PayOrderTypeEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (PayOrderTypeEnum compareEnum : PayOrderTypeEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }


}
