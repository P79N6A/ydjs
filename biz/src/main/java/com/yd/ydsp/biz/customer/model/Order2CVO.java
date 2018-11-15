package com.yd.ydsp.biz.customer.model;

import java.io.Serializable;
import java.util.Date;

public class Order2CVO extends ShoppingCartVO implements Serializable {

    /**
     * 订单名称，以其中一个商品名称为名称再加上有多少商品来定名称
     */
    String orderName;
    /**
     * 订单类型
     */
    Integer orderType;

    /**
     * 0:线上支付；1:线下使用现金支付
     */
    Integer useCash;

    Date orderDate;

    /**
     * 订单状态
     */
    Integer status;

    /**
     * 用来表示此订单为今天的第几单（流水号），用于店家接单时判定先后关系
     * 每日重新置为0
     */
    Integer printCount;

    /**
     * 备注
     */
    String description;

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName == null ? null : orderName.trim();
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getUseCash() {
        return useCash;
    }

    public void setUseCash(Integer useCash) {
        this.useCash = useCash;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPrintCount() {
        return printCount;
    }

    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descption) {
        this.description = descption;
    }
}
