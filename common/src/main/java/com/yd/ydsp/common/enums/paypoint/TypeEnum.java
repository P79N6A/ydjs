package com.yd.ydsp.common.enums.paypoint;

import com.yd.ydsp.common.lang.StringUtil;

/**
 * Created by zengyixun on 17/9/12.
 */
public enum TypeEnum {
    DELETE("00", 0),
    OTHER("01",1),
    PAGE("06",6),
    QRCODE("08", 8),
    PRINTER("09", 9),
    SHOP("10", 10),
    MONITOR("11", 11),
    ADDRESS("12",12),
    DININGTABLE("13", 13),
    WAREITEM("15",15),
    WARESKU("16",16),
    SPEC("17",17),
    POS("18", 18),
    MEMBERCARD("19", 19),
    CONSUMERORDER("28",28),
    MONEYORDER("58",58),
    CASHRECHARGE("85",85),
    CHANNELQRCODE("86", 86),
    CPORDER("88", 88),
    CPSUBORDER("89", 89),
    AGENTID("95",95),
    PRELINKCODE("96",96),
    WEIXINCONFIG("98",98),
    SESSIONID("99",99),
    ;

    public String name;

    public Integer type;

    TypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (TypeEnum compareEnum : TypeEnum.values()) {
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

    public static TypeEnum nameOf(String name) {

        for (TypeEnum compareEnum : TypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                return compareEnum;
            }

        }
        return null;
    }

    public static TypeEnum nameOf(Integer type) {

        if(type==null){
            return null;
        }
        for (TypeEnum compareEnum : TypeEnum.values()) {
            if (compareEnum.getType().intValue()==type.intValue()) {
                return compareEnum;
            }

        }
        return null;
    }

    public static TypeEnum getTypeOfSN(String sn){
        if(StringUtil.isEmpty(sn)){
            return null;
        }
        if(sn.length()<4){
            return null;
        }

        return nameOf(sn.substring(2,4));

    }

}
