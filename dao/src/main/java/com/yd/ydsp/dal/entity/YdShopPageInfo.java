package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdShopPageInfo {
    private Long id;

    private String shopid;

    private String pageid;

    private String pageName;

    private Integer pageType=0;

    private String components;

    private Long flag=0L;

    private String feature;

    private Integer status=1;

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

    public String getPageid() {
        return pageid;
    }

    public void setPageid(String pageid) {
        this.pageid = pageid == null ? null : pageid.trim();
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName == null ? null : pageName.trim();
    }

    public Integer getPageType() {
        return pageType;
    }

    public void setPageType(Integer pageType) {
        this.pageType = pageType;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components == null ? null : components.trim();
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