package com.yd.ydsp.biz.cp.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShopReportPageVO implements Serializable {

    /**
     * 订单计数
     */
    Integer orderCount;
    /**
     * 实收金额计数
     */
    BigDecimal receiveAmount;

    /**
     * 报表类型
     */
    Integer type;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
