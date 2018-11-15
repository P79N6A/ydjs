package com.yd.ydsp.biz.customer.model.order;

import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.client.domian.UserOrderDeliveryInfoVO;
import com.yd.ydsp.client.domian.paypoint.BankInfoVO;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShopOrderExt2C implements Serializable {


    ShopOrder2C shopOrder2C;
    UserOrderDeliveryInfoVO deliveryInfoVO;
    UserAddressInfoVO userAddressInfoVO;
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

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }
}
