package com.yd.ydsp.biz.deliver.model;

import java.io.Serializable;

public class UUCallbackData implements Serializable {

    private static final long serialVersionUID = 6000000000000000006L;

    /**
     * 内部订单号
     */
    String originId;
    /**
     * UU订单号
     */
    String orderCode;
    /**
     * 跑男名
     */
    String driverName;
    /**
     * 工号
     */
    String driverJobnum;
    /**
     * 手机
     */
    String driverMobile;

    /**
     * 维度
     */
    String lat;

    /**
     * 经度
     */
    String lng;
    /**
     * 状态：当前状态1下单成功 3跑男抢单 4已到达 5已取件 6到达目的地 10收件人已收货 -1订单取消
     */
    String state;
    /**
     * 当前状态说明
     */
    String stateText;
    /**
     * 随机字符串，不长于32位
     */
    String nonceStr;
    /**
     * 加密签名
     */
    String sign;
    /**
     * 第三方用户唯一凭证
     */
    String appid;
    String returnMsg;
    String returnCode;

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverJobnum() {
        return driverJobnum;
    }

    public void setDriverJobnum(String driverJobnum) {
        this.driverJobnum = driverJobnum;
    }

    public String getDriverMobile() {
        return driverMobile;
    }

    public void setDriverMobile(String driverMobile) {
        this.driverMobile = driverMobile;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateText() {
        return stateText;
    }

    public void setStateText(String stateText) {
        this.stateText = stateText;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }
}