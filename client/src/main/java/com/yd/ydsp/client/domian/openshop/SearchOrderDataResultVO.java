package com.yd.ydsp.client.domian.openshop;

import java.io.Serializable;


public class SearchOrderDataResultVO implements Serializable {
    Long id;
    /**
     * shopid
     */
    String shopid;
    /**
     * 订单名称
     */
    String orderName;
    /**
     * 如果是堂食，则为桌位id，如果是渠道则为渠道id，如果是主店消费都为shopid
     */
    String channelId;
    /**
     * 订单id
     */
    String orderid;
    /**
     * 订单类型
     */
    Integer orderType;
    /**
     * 支付方式，先付，还是后付
     */
    Integer payMode;
    /**
     * 是否已经付款
     */
    Integer isPay;
    /**
     * 订单金额
     */
    String totalAmount;
    /**
     * 订单状态
     */
    Integer status;
    /**
     * 用户留言
     */
    String userDescription;
    /**
     * 收货人手机
     */
    String reviceUserMobile;
    /**
     * 订单创建时间
     */
    String orderDate;
    /**
     * 是不是店内（堂食)订单   0-不是; 1-是
     */
    Integer isIndoor;

    /**
     * 物流方式
     */
    private String deliveryType;

    /**
     * 商品图片
     */
    private String goodsPicUrl;


    public Long getId() {
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Integer getPayMode() {
        return payMode;
    }

    public void setPayMode(Integer payMode) {
        this.payMode = payMode;
    }

    public Integer getIsPay() {
        return isPay;
    }

    public void setIsPay(Integer isPay) {
        this.isPay = isPay;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getReviceUserMobile() {
        return reviceUserMobile;
    }

    public void setReviceUserMobile(String reviceUserMobile) {
        this.reviceUserMobile = reviceUserMobile;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public Integer getIsIndoor() {
        return isIndoor;
    }

    public void setIsIndoor(Integer isIndoor) {
        this.isIndoor = isIndoor;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getGoodsPicUrl() {
        return goodsPicUrl;
    }

    public void setGoodsPicUrl(String goodsPicUrl) {
        this.goodsPicUrl = goodsPicUrl;
    }
}
