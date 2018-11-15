package com.yd.ydsp.common.constants.paypoint;

public enum WareSkuFlagConstants {


    HASSPACPRICE(1 << 0, "商品含有规格定义的价格"),
    ;

    private Integer value;

    private String name;

    WareSkuFlagConstants(Integer value, String name) {
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
