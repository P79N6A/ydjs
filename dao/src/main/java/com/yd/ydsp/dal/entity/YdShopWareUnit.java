package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdShopWareUnit {
    private Long id;

    private String shopid;

    private String unitNames;

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

    public String getUnitNames() {
        return unitNames;
    }

    public void setUnitNames(String unitNames) {
        this.unitNames = unitNames == null ? null : unitNames.trim();
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