package com.yd.ydsp.client.domian.paypoint;

import java.math.BigDecimal;
import java.util.Date;

public class ShopUserBillVO {

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 帐单id
     */
    private String billid;

    /**
     * 帐单类型
     * IN("in", 0),//充值
     GIVE("give", 1),//赠送
     GIVECODE("giveCode", 2),//通过卡卷赠送
     TRADE("trade",3),//商品交易
     */
    private Integer billtype;

    /**
     * 该笔帐单的交易金额
     */
    private BigDecimal cashAmount;

    /**
     * 余额
     */
    private BigDecimal cashAmountTotal;

    /**
     * 交易年
     */
    private String inYear;

    /**
     * 交易月
     */
    private String inMonth;

    /**
     * 交易日
     */
    private String inDay;

    /**
     * 帐单创建时间
     */
    private String createDate;

    /**
     * 帐单名称
     */
    private String billName;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid == null ? null : billid.trim();
    }

    public Integer getBilltype() {
        return billtype;
    }

    public void setBilltype(Integer billtype) {
        this.billtype = billtype;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public BigDecimal getCashAmountTotal() {
        return cashAmountTotal;
    }

    public void setCashAmountTotal(BigDecimal cashAmountTotal) {
        this.cashAmountTotal = cashAmountTotal;
    }

    public String getInYear() {
        return inYear;
    }

    public void setInYear(String inYear) {
        this.inYear = inYear == null ? null : inYear.trim();
    }

    public String getInMonth() {
        return inMonth;
    }

    public void setInMonth(String inMonth) {
        this.inMonth = inMonth == null ? null : inMonth.trim();
    }

    public String getInDay() {
        return inDay;
    }

    public void setInDay(String inDay) {
        this.inDay = inDay == null ? null : inDay.trim();
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }
}