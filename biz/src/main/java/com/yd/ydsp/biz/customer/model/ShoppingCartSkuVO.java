package com.yd.ydsp.biz.customer.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShoppingCartSkuVO implements Serializable {

    String skuName;
    /**
     * 单价，售卖价(单品折扣后的价格），而非原价
     */
    BigDecimal disprice;
    /**
     * 原价
     */
    BigDecimal price;
    /**
     * 选择的商品数量
     */
    Integer count;
    /**
     * 单价*数量
     */
    BigDecimal totalAmount;
    BigDecimal disTotalAmount;

    /**
     * 商品图片url
     * @return
     */
    String skuImgUrl;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDisprice() {
        return disprice;
    }

    public void setDisprice(BigDecimal disprice) {
        this.disprice = disprice;
    }

    public String getSkuName(){ return skuName; }

    public void setSkuName(String skuName){
        this.skuName = skuName == null ? null : skuName.trim();
    }


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDisTotalAmount() {
        return disTotalAmount;
    }

    public void setDisTotalAmount(BigDecimal disTotalAmount) {
        this.disTotalAmount = disTotalAmount;
    }

    public String getSkuImgUrl() {
        return skuImgUrl;
    }

    public void setSkuImgUrl(String skuImgUrl) {
        this.skuImgUrl = skuImgUrl;
    }
}
