package com.yd.ydsp.dal.entity;

import java.math.BigDecimal;
import java.util.Date;

public class YdShopCashRechargeConfig {
    private Long id;

    private String shopid;

    private BigDecimal amountTotal;

    private BigDecimal currenAmount;

    private BigDecimal giveAmount;

    private Integer customerCount;

    private BigDecimal amountConfOne;

    private BigDecimal amountConfTwo;

    private BigDecimal amountConfThr;

    private BigDecimal amountConfFour;

    private BigDecimal amountConfFive;

    private BigDecimal amountConfSix;

    private BigDecimal oneAmountGive;

    private BigDecimal twoAmountGive;

    private BigDecimal thrAmountGive;

    private BigDecimal fourAmountGive;

    private BigDecimal fiveAmountGive;

    private BigDecimal sixAmountGive;

    private Integer status;

    private Long flag;

    private String feature;

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

    public BigDecimal getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(BigDecimal amountTotal) {
        this.amountTotal = amountTotal;
    }

    public BigDecimal getCurrenAmount() {
        return currenAmount;
    }

    public void setCurrenAmount(BigDecimal currenAmount) {
        this.currenAmount = currenAmount;
    }

    public BigDecimal getGiveAmount() {
        return giveAmount;
    }

    public void setGiveAmount(BigDecimal giveAmount) {
        this.giveAmount = giveAmount;
    }

    public Integer getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(Integer customerCount) {
        this.customerCount = customerCount;
    }

    public BigDecimal getAmountConfOne() {
        return amountConfOne;
    }

    public void setAmountConfOne(BigDecimal amountConfOne) {
        this.amountConfOne = amountConfOne;
    }

    public BigDecimal getAmountConfTwo() {
        return amountConfTwo;
    }

    public void setAmountConfTwo(BigDecimal amountConfTwo) {
        this.amountConfTwo = amountConfTwo;
    }

    public BigDecimal getAmountConfThr() {
        return amountConfThr;
    }

    public void setAmountConfThr(BigDecimal amountConfThr) {
        this.amountConfThr = amountConfThr;
    }

    public BigDecimal getAmountConfFour() {
        return amountConfFour;
    }

    public void setAmountConfFour(BigDecimal amountConfFour) {
        this.amountConfFour = amountConfFour;
    }

    public BigDecimal getAmountConfFive() {
        return amountConfFive;
    }

    public void setAmountConfFive(BigDecimal amountConfFive) {
        this.amountConfFive = amountConfFive;
    }

    public BigDecimal getAmountConfSix() {
        return amountConfSix;
    }

    public void setAmountConfSix(BigDecimal amountConfSix) {
        this.amountConfSix = amountConfSix;
    }

    public BigDecimal getOneAmountGive() {
        return oneAmountGive;
    }

    public void setOneAmountGive(BigDecimal oneAmountGive) {
        this.oneAmountGive = oneAmountGive;
    }

    public BigDecimal getTwoAmountGive() {
        return twoAmountGive;
    }

    public void setTwoAmountGive(BigDecimal twoAmountGive) {
        this.twoAmountGive = twoAmountGive;
    }

    public BigDecimal getThrAmountGive() {
        return thrAmountGive;
    }

    public void setThrAmountGive(BigDecimal thrAmountGive) {
        this.thrAmountGive = thrAmountGive;
    }

    public BigDecimal getFourAmountGive() {
        return fourAmountGive;
    }

    public void setFourAmountGive(BigDecimal fourAmountGive) {
        this.fourAmountGive = fourAmountGive;
    }

    public BigDecimal getFiveAmountGive() {
        return fiveAmountGive;
    }

    public void setFiveAmountGive(BigDecimal fiveAmountGive) {
        this.fiveAmountGive = fiveAmountGive;
    }

    public BigDecimal getSixAmountGive() {
        return sixAmountGive;
    }

    public void setSixAmountGive(BigDecimal sixAmountGive) {
        this.sixAmountGive = sixAmountGive;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getFlag() {
        return flag;
    }

    public void setFlag(Long flag) {
        this.flag = flag;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
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