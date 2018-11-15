package com.yd.ydsp.common.constants.paypoint;

/**
 * Created by zengyixun on 17/8/18.
 */
public enum ShopSupportFlagConstants {

    EATERY(1 << 0, "餐馆"),
    FRUIT(1 << 1, "水果"),
    MONITOR(1 << 2, "监控"),
    RETAIL(1 << 3, "零售"),
    WEISHANG(1 << 6, "微商"),
    SMALLPRIVATE(1<<5,"个体户"),
    PAYNOW(1 << 8, "堂食立即付款"),
    PAYNOW2C(1 << 9, "用户立即付款"),
    PAYMENTWEIXINSERVICE(1 << 10, "支持微信商户收款"),
    PAYMENTWEIXINPRIVATE(1 << 11, "支持微信个人收款"),
    SHOPISPUBLIC(1<<12,"公有商城模式"),//建立在引灯的公众号小程序上开店
    SHOPISPRIVATE(1<<16,"独立商城模式"),//自己独立拥有的公众号小程序商城
    ;

    private Integer value;

    private String name;

    ShopSupportFlagConstants(Integer value, String name) {
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
