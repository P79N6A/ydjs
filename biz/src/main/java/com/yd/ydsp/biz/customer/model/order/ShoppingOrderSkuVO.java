package com.yd.ydsp.biz.customer.model.order;

import com.yd.ydsp.common.lang.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShoppingOrderSkuVO implements Serializable {

    String skuid;

    String skuName;

    /**
     * 选择的主规格内容
     */
    String mainSpecName;

    /**
     * 选择的子规格内容
     */
    String childSpecName;

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

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid == null ? null : skuid.trim();
    }

    public String getMainSpecName() {
        return mainSpecName;
    }

    public void setMainSpecName(String mainSpecName) {
        this.mainSpecName = mainSpecName == null ? null : mainSpecName.trim();
    }

    public String getChildSpecName() {
        return childSpecName;
    }

    public void setChildSpecName(String childSpecName) {
        if(StringUtil.isNotEmpty(this.mainSpecName)) {
            this.childSpecName = childSpecName == null ? "" : childSpecName.trim();
        }else {
            this.childSpecName = childSpecName == null ? null : childSpecName.trim();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ShoppingOrderSkuVO) {
            ShoppingOrderSkuVO skuVO = (ShoppingOrderSkuVO) obj;
            if(StringUtil.equals(skuVO.getSkuid(),this.skuid)){
                if(StringUtil.isEmpty(skuVO.getMainSpecName())&&StringUtil.isEmpty(this.getMainSpecName())){
                    return true;
                }
                if(StringUtil.equals(skuVO.getMainSpecName(),this.getMainSpecName())){
                    if(StringUtil.isEmpty(skuVO.getChildSpecName())&&StringUtil.isEmpty(this.getChildSpecName())){
                        return true;
                    }
                    if(StringUtil.equals(skuVO.getChildSpecName(),this.getChildSpecName())){
                        return true;
                    }else {
                        return false;
                    }
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

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
