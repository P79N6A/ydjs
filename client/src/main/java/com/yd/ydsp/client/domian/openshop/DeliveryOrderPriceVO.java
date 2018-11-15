package com.yd.ydsp.client.domian.openshop;

import java.io.Serializable;
import java.math.BigDecimal;

public class DeliveryOrderPriceVO implements Serializable {

    /**
     *物流平台订单号或者下单的Token
     */
    String clientId;

    /**
     * 实际需要支付的金额
     */
    BigDecimal payMoney;

    /**
     * 总金额（包括优惠券）
     */
    BigDecimal totalMoney;

    /**
     * 配送距离
     */
    BigDecimal distance;


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public BigDecimal getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(BigDecimal payMoney) {
        this.payMoney = payMoney;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }
}
