package com.yd.ydsp.biz.cp.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShopMainPageVO implements Serializable {

    /**
     * 剩余日单量
     */
    Integer remainder;
    /**
     * 日订单计数
     */
    Integer orderCount;
    /**
     * 日线上支付实收金额
     */
    BigDecimal receiveAmount;

    /**
     * 店铺状态
     */
    Integer status;

    public Integer getRemainder() {
        return remainder;
    }

    public void setRemainder(Integer remainder) {
        this.remainder = remainder;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
