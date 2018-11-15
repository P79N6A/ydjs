package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdCpSpecConfigInfo {
    private Long id;

    private String shopid;

    private String skuid;

    private String mainSpecName;

    private String feature;

    private Integer isDelete;

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

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid == null ? null : skuid.trim();
    }

    public String getMainSpecName() {
        return mainSpecName;
    }

    public void setMainSpecName(String mainSpecName) {
        this.mainSpecName = mainSpecName == null ? null : mainSpecName.trim();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
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