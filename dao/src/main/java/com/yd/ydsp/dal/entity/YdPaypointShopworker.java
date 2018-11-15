package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdPaypointShopworker {
    private Long id;

    private String shopid;

    private String ownerMobile;

    private String managerMobile;

    private String waiterMobile;

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

    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile == null ? null : ownerMobile.trim();
    }

    public String getManagerMobile() {
        return managerMobile;
    }

    public void setManagerMobile(String managerMobile) {
        this.managerMobile = managerMobile == null ? null : managerMobile.trim();
    }

    public String getWaiterMobile() {
        return waiterMobile;
    }

    public void setWaiterMobile(String waiterMobile) {
        this.waiterMobile = waiterMobile == null ? null : waiterMobile.trim();
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