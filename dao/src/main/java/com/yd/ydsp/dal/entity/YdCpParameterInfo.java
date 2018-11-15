package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdCpParameterInfo {
    private Long id;

    private String shopid;

    private String skuid;

    private String parameterid;

    private String parameterKey;

    private String parameterValue;

    private String parameterGroup;

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

    public String getParameterid() {
        return parameterid;
    }

    public void setParameterid(String parameterid) {
        this.parameterid = parameterid == null ? null : parameterid.trim();
    }

    public String getParameterKey() {
        return parameterKey;
    }

    public void setParameterKey(String parameterKey) {
        this.parameterKey = parameterKey == null ? null : parameterKey.trim();
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue == null ? null : parameterValue.trim();
    }

    public String getParameterGroup() {
        return parameterGroup;
    }

    public void setParameterGroup(String parameterGroup) {
        this.parameterGroup = parameterGroup == null ? null : parameterGroup.trim();
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