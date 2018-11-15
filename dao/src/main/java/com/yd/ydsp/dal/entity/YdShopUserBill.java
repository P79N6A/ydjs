package com.yd.ydsp.dal.entity;

import com.yd.ydsp.common.constants.paypoint.ShopUserBillFlagConstants;

import java.math.BigDecimal;
import java.util.Date;

public class YdShopUserBill {
    private Long id;

    private String shopid;

    private Long userid;

    private String billid;

    private Integer billtype;

    private BigDecimal cashAmount;

    private BigDecimal cashAmountTotal;

    private String billName;

    private String inYear;

    private String inMonth;

    private String inDay;

    private Integer status=0;

    private Long flag=0L;

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

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
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

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName == null ? null : billName.trim();
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

    /**
     * 添加flag标记
     */
    public void addFlag(ShopUserBillFlagConstants f) {
        if (null == f) {
            return;
        }
        if (null == flag) {
            flag = Long.valueOf(0);
        }
        flag = flag | f.getValue();
    }

    /**
     * 删除flag标记
     */
    public void removeFlag(ShopUserBillFlagConstants f) {
        if (null == f) {
            return;
        }
        if (null == flag) {
            flag = Long.valueOf(0);
        }
        flag = flag & ~f.getValue();
    }

    /**
     *判断是否存在某个标志
     */
    public boolean isExistFlag(ShopUserBillFlagConstants f){

        if(flag == null || flag ==0){
            return false;
        }
        long support = f.getValue() & flag;
        return support > 0;
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