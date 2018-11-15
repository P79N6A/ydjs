package com.yd.ydsp.biz.customer.model.order;

import java.io.Serializable;

/**
 * 用于售卖计算与库存加减
 */
public class ShopOrderSkuVO implements Serializable {

    String skuid;

    /**
     * 选择的主规格内容
     */
    String mainSpecName;

    /**
     * 选择的子规格内容
     */
    String childSpecName;

    /**
     * 商品数量，可以为负数，用来表明取消的情况
     */
    Integer Count;

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid;
    }

    public String getMainSpecName() {
        return mainSpecName;
    }

    public void setMainSpecName(String mainSpecName) {
        this.mainSpecName = mainSpecName;
    }

    public String getChildSpecName() {
        return childSpecName;
    }

    public void setChildSpecName(String childSpecName) {
        this.childSpecName = childSpecName;
    }

    public Integer getCount() {
        return Count;
    }

    public void setCount(Integer count) {
        Count = count;
    }
}
