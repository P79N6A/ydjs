package com.yd.ydsp.client.domian;

import com.yd.ydsp.common.lang.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zengyixun
 * @date 18/1/10
 */
public class UserOrderDeliveryInfoVO implements Serializable {

    private static final long serialVersionUID = 3000000000000000008L;
    /**
     * 外部订单号（物流运单号）
     */
    String clientId;

    /**
     * 快递员
     * @return
     */
    String transporterName;

    /**
     * 快递员联系方式
     */
    String transporterPhone;

    /**
     * 经度
     */
    private String transporterLng;

    /**
     * 维度
     */
    private String transporterLat;

    /**
     * 取消原因
     */
    String cancelReason;

    /**
     * 配送方式
     */

    Integer deliveryType;

    /**
     * 物流金额
     */
    BigDecimal deliveryMoney;

    /**
     * 订单状态
     */
    Integer statusCode;

    /**
     * 订单状态描述
     */
    String statusMsg;

    /**
     * 配送距离
     */
    Double distance;

    /**
     * 实际支付费用
     */
    BigDecimal actualFee;

    /**
     * 发单时间
     */
    Date createTime;

    /**
     * 接单时间（没有接单为空）
     */
    Date acceptTime;

    /**
     * 取货时间(未取货为空）
     */
    Date fetchTime;

    /**
     * 送达时间（未送达为空）
     */
    Date finishTime;

    /**
     * 取消时间（未取消为空）
     */
    Date cancelTime;

    /**
     * 收货码
     */
    String orderFinishCode;


    public BigDecimal getActualFee() {
        return actualFee;
    }

    public void setActualFee(BigDecimal actualFee) {
        this.actualFee = actualFee;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Date getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(Date fetchTime) {
        this.fetchTime = fetchTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getOrderFinishCode() {
        return orderFinishCode;
    }

    public void setOrderFinishCode(String orderFinishCode) {
        this.orderFinishCode = orderFinishCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance){
        this.distance = distance;
    }

    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId){
        this.clientId = clientId;
    }

    public String getTransporterName(){
        return transporterName;
    }
    public void setTransporterName(String transporterName){
        this.transporterName = transporterName;
    }

    public String getTransporterPhone(){
        return transporterPhone;
    }

    public void setTransporterPhone(String transporterPhone){
        this.transporterPhone = transporterPhone;
    }

    public String getTransporterLng() {
        return transporterLng;
    }

    public void setTransporterLng(String transporterLng) {
        this.transporterLng = transporterLng == null ? null : transporterLng.trim();
    }

    public String getTransporterLat() {
        return transporterLat;
    }

    public void setTransporterLat(String transporterLat) {
        this.transporterLat = transporterLat == null ? null : transporterLat.trim();
    }

    public String getCancelReason(){
        return cancelReason;
    }
    public void setCancelReason(String cancelReason){
        this.cancelReason = cancelReason;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public BigDecimal getDeliveryMoney() {
        if(this.deliveryMoney==null){
            return new BigDecimal("0.00");
        }
        return deliveryMoney;
    }

    public void setDeliveryMoney(BigDecimal deliveryMoney){
        this.deliveryMoney = deliveryMoney;
    }
}
