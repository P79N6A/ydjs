package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdMonitorInfo {
    private Long id;

    private String shopid;

    private String name;

    private String ossPath;

    private String initImageUrl;

    private String tel;

    private String deviceSerials;

    private String deviceSerialnames;

    private String shopDesc;

    private Integer status;

    private String feature;

    private Date createDate;

    private Date modifyDate;

    private Long flag;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getOssPath() {
        return ossPath;
    }

    public void setOssPath(String ossPath) {
        this.ossPath = ossPath == null ? null : ossPath.trim();
    }

    public String getInitImageUrl() {
        return initImageUrl;
    }

    public void setInitImageUrl(String initImageUrl) {
        this.initImageUrl = initImageUrl == null ? null : initImageUrl.trim();
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }

    public String getDeviceSerials() {
        return deviceSerials;
    }

    public void setDeviceSerials(String deviceSerials) {
        this.deviceSerials = deviceSerials == null ? null : deviceSerials.trim();
    }

    public String getDeviceSerialnames() {
        return deviceSerialnames;
    }

    public void setDeviceSerialnames(String deviceSerialnames) {
        this.deviceSerialnames = deviceSerialnames == null ? null : deviceSerialnames.trim();
    }

    public String getShopDesc() {
        return shopDesc;
    }

    public void setShopDesc(String shopDesc) {
        this.shopDesc = shopDesc == null ? null : shopDesc.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Long getFlag() {
        return flag;
    }

    public void setFlag(Long flag) {
        this.flag = flag;
    }
}