package com.yd.ydsp.common.constants.paypoint;

public enum ShopUserBillFlagConstants {


    THEFIRST(1 << 0, "新用户充值"),
    WRITESHOPDATAFINISH(1<<1,"此帐单已经完成店铺数据统计"),
    WRITEBILLFINISH(1<<2,"预处理帐单已经写入正式记帐表中"),
    ;

    private Integer value;

    private String name;

    ShopUserBillFlagConstants(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
