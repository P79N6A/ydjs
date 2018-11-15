package com.yd.ydsp.dal.entity;

import java.math.BigDecimal;
import java.util.Date;

public class YdShopUserData {
    private Long id;

    private Integer userSource;

    private Long userid;

    private String shopid;

    private Long pointTotal;

    private Long curPoint;

    private BigDecimal rechargeAmount;

    private Date lastEnterShopTime;

    private BigDecimal amountTotal;

    private Integer status;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserSource() {
        return userSource;
    }

    public void setUserSource(Integer userSource) {
        this.userSource = userSource;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public Long getPointTotal() {
        return pointTotal;
    }

    public void setPointTotal(Long pointTotal) {
        this.pointTotal = pointTotal;
    }

    public Long getCurPoint() {
        return curPoint;
    }

    public void setCurPoint(Long curPoint) {
        this.curPoint = curPoint;
    }

    public BigDecimal getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(BigDecimal rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Date getLastEnterShopTime() {
        return lastEnterShopTime;
    }

    public void setLastEnterShopTime(Date lastEnterShopTime) {
        this.lastEnterShopTime = lastEnterShopTime;
    }

    public BigDecimal getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(BigDecimal amountTotal) {
        this.amountTotal = amountTotal;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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