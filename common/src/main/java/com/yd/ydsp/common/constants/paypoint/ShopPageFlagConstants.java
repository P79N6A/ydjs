package com.yd.ydsp.common.constants.paypoint;

/**
 * Created by zengyixun on 17/8/18.
 */
public enum ShopPageFlagConstants {

    NewVersion(1 << 0, "有新的版本没有发布"),
//    Nothing(1 << 0, "是否付款"),
    ;

    private Integer value;

    private String name;

    ShopPageFlagConstants(Integer value, String name) {
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
