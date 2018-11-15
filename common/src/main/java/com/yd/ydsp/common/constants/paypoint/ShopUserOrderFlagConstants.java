package com.yd.ydsp.common.constants.paypoint;

/**
 * Created by zengyixun on 18/8/16.
 */
public enum ShopUserOrderFlagConstants {

    accIsOver(1 << 0, "此订单已经完成统计"),
    printIsOver(1 << 1, "此订单已经完成打印"),
    ;

    private Integer value;

    private String name;

    ShopUserOrderFlagConstants(Integer value, String name) {
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
