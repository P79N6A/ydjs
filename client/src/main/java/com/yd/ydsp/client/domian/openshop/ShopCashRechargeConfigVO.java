package com.yd.ydsp.client.domian.openshop;

import java.math.BigDecimal;
import java.util.Date;

public class ShopCashRechargeConfigVO {

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 当前累计充值金额
     */
    private BigDecimal amountTotal;

    /**
     * 店铺向用户进行的总的奖励金额
     */
    private BigDecimal giveAmount;

    /**
     * 当前余额
     */
    private BigDecimal currenAmount;

    /**
     * 充值用户总数
     */
    private Integer customerCount;

    /**
     * 充值金额1
     */
    private BigDecimal amountConfOne;

    /**
     * 充值金额2
     */
    private BigDecimal amountConfTwo;

    /**
     * 充值金额3
     */
    private BigDecimal amountConfThr;

    /**
     * 充值金额4
     */
    private BigDecimal amountConfFour;

    /**
     * 充值金额5
     */
    private BigDecimal amountConfFive;

    /**
     * 充值金额6
     */
    private BigDecimal amountConfSix;

    /**
     * 充值金额1时，送多少
     */
    private BigDecimal oneAmountGive;

    /**
     * 充值金额2时，送多少
     */
    private BigDecimal twoAmountGive;

    /**
     * 充值金额3时，送多少
     */
    private BigDecimal thrAmountGive;

    /**
     * 充值金额4时，送多少
     */
    private BigDecimal fourAmountGive;

    /**
     * 充值金额5时，送多少
     */
    private BigDecimal fiveAmountGive;

    /**
     * 充值金额6时，送多少
     */
    private BigDecimal sixAmountGive;

    private Integer status;


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

}