package com.yd.ydsp.biz.customer.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public class ShoppingCartVO implements Serializable {

    /**
     * 扫描码
     */
    String qrCode;
    String orderid;
    /**
     * 支付模式，比如是不是立即付款;0:后付款；1:立即付款'
     */
    Integer payMode;
    /**
     * 桌位名称
     */
    String tableName;
    String shopid;
    BigDecimal totalAmount;
    BigDecimal disTotalAmount;

    /**
     * 折扣
     */
    Double discount;

    /**
     * skuid,VO
     */
    Map<String,ShoppingCartSkuVO> shoppingCartSkuVOMap;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getPayMode() {
        return payMode;
    }

    public void setPayMode(Integer payMode) {
        this.payMode = payMode;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    public Map<String, ShoppingCartSkuVO> getShoppingCartSkuVOMap() {
        return shoppingCartSkuVOMap;
    }

    public void setShoppingCartSkuVOMap(Map<String, ShoppingCartSkuVO> shoppingCartSkuVOMap) {
        this.shoppingCartSkuVOMap = shoppingCartSkuVOMap;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public double getDiscount() {
        if(discount==null){
            return 1.0;
        }
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
