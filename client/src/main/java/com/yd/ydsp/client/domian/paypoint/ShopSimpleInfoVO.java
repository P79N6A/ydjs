package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;

public class ShopSimpleInfoVO implements Serializable {

    String shopid;
    String name;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
