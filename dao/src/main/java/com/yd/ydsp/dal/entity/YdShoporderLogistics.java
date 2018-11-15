package com.yd.ydsp.dal.entity;

import java.math.BigDecimal;
import java.util.Date;

public class YdShoporderLogistics {
    private Long id;

    private String orderid;

    private String lgcode;

    private String driverName;

    private String driverJobnum;

    private String driverMobile;

    private String lgcompany;

    private String lgcompanycode;

    private Integer state;

    private Integer lgtype;

    private BigDecimal amount;

    private String feature;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid == null ? null : orderid.trim();
    }

    public String getLgcode() {
        return lgcode;
    }

    public void setLgcode(String lgcode) {
        this.lgcode = lgcode == null ? null : lgcode.trim();
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName == null ? null : driverName.trim();
    }

    public String getDriverJobnum() {
        return driverJobnum;
    }

    public void setDriverJobnum(String driverJobnum) {
        this.driverJobnum = driverJobnum == null ? null : driverJobnum.trim();
    }

    public String getDriverMobile() {
        return driverMobile;
    }

    public void setDriverMobile(String driverMobile) {
        this.driverMobile = driverMobile == null ? null : driverMobile.trim();
    }

    public String getLgcompany() {
        return lgcompany;
    }

    public void setLgcompany(String lgcompany) {
        this.lgcompany = lgcompany == null ? null : lgcompany.trim();
    }

    public String getLgcompanycode() {
        return lgcompanycode;
    }

    public void setLgcompanycode(String lgcompanycode) {
        this.lgcompanycode = lgcompanycode == null ? null : lgcompanycode.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getLgtype() {
        return lgtype;
    }

    public void setLgtype(Integer lgtype) {
        this.lgtype = lgtype;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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