package com.yd.ydsp.biz.customer.model.order;

import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.client.domian.UserOrderDeliveryInfoVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ShopOrderExt2CP implements Serializable {


    ShopOrder2C shopOrder2C;
    UserOrderDeliveryInfoVO deliveryInfoVO;
    UserAddressInfoVO userAddressInfoVO;
    List<String> cpOptionHistory;
    /**
     * 内有微信扫码与支付宝扫码支持url
     */
    Map<String,Object> bizPay;
    /**
     * 实际支付金额
     */
    BigDecimal actualAmount;


    public ShopOrder2C getShopOrder2C() {
        return shopOrder2C;
    }

    public void setShopOrder2C(ShopOrder2C shopOrder2C) {
        this.shopOrder2C = shopOrder2C;
    }

    public UserOrderDeliveryInfoVO getDeliveryInfoVO() {
        return deliveryInfoVO;
    }

    public void setDeliveryInfoVO(UserOrderDeliveryInfoVO deliveryInfoVO) {
        this.deliveryInfoVO = deliveryInfoVO;
    }

    public UserAddressInfoVO getUserAddressInfoVO() {
        return userAddressInfoVO;
    }

    public void setUserAddressInfoVO(UserAddressInfoVO userAddressInfoVO) {
        this.userAddressInfoVO = userAddressInfoVO;
    }

    public List<String> getCpOptionHistory() {
        return cpOptionHistory;
    }

    public void setCpOptionHistory(List<String> cpOptionHistory) {
        this.cpOptionHistory = cpOptionHistory;
    }

    public Map<String, Object> getBizPay() {
        return bizPay;
    }

    public void setBizPay(Map<String, Object> bizPay) {
        this.bizPay = bizPay;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }
}
