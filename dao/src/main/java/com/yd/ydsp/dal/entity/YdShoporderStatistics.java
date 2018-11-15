package com.yd.ydsp.dal.entity;

import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.security.util.SecurityUtil;

import java.math.BigDecimal;
import java.util.Date;

public class YdShoporderStatistics {
    private Long id;

    private String shopid;

    private Date orderDate;

    private Long firstid;

    private Long lastid;

    /**
     * 0-未发起提现；1-已经发起提现; 2-提现成功；-1:提现异常，需要人工介入
     */
    private Integer withdrawals;

    /**
     * 0-无对帐请求; 1-用户要求对帐; 8-对帐请求已经得到解决
     */
    private Integer checkRequest;

    private Integer orderCount;

    private Integer bagCount;

    /**
     * 一个店的当日线上支付实收金额
     */
    private BigDecimal receiveAmount;

    /**
     * 一个店的当日现金实收金额
     */
    private BigDecimal receivecashAmount;

    private String feature;

    private String description;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Long getFirstid() {
        return firstid;
    }

    public void setFirstid(Long firstid) {
        this.firstid = firstid;
    }

    public Long getLastid() {
        return lastid;
    }

    public void setLastid(Long lastid) {
        this.lastid = lastid;
    }

    public Integer getWithdrawals() {
        if(withdrawals==null){
            return 0;
        }
        return withdrawals;
    }

    public void setWithdrawals(Integer withdrawals) {
        this.withdrawals = withdrawals;
    }

    public Integer getCheckRequest() {
        if(checkRequest==null){
            return 0;
        }
        return checkRequest;
    }

    public void setCheckRequest(Integer checkRequest) {
        this.checkRequest = checkRequest;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Integer getBagCount() {
        if(bagCount == null){
            return 0;
        }
        return bagCount;
    }

    public void setBagCount(Integer bagCount) {
        this.bagCount = bagCount;
    }

    public BigDecimal getReceiveAmount() {
        if(receiveAmount==null){
            return new BigDecimal("0.00");
        }
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public BigDecimal getReceivecashAmount() {
        if(receivecashAmount==null){
            return new BigDecimal("0.00");
        }
        return receivecashAmount;
    }

    public void setReceivecashAmount(BigDecimal receivecashAmount) {
        this.receivecashAmount = receivecashAmount;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public String getDescription() {
        if(description==null){
            return null;
        }
        return SecurityUtil.escapeHtml(description);
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}