package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;
import java.math.BigDecimal;

public class BillReportVO implements Serializable {

    /**
     * 一个店的当日线上支付实收金额
     */
    private BigDecimal receiveAmount;

    /**
     * 一个店的当日现金实收金额
     */
    private BigDecimal receivecashAmount;
    /**
     * 订单数量
     */
    Integer orderCount;

    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public BigDecimal getReceivecashAmount() {
        return receivecashAmount;
    }

    public void setReceivecashAmount(BigDecimal receivecashAmount) {
        this.receivecashAmount = receivecashAmount;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }
}
