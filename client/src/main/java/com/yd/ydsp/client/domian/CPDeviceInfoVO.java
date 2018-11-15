package com.yd.ydsp.client.domian;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zengyixun
 * @date 18/1/10
 */
public class CPDeviceInfoVO implements Serializable{

    /**
     * 名称
     */
    private String name;
    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 设备id
     */
    private String deviceid;

    /**
     * 硬件设备号，是硬件厂家定义的唯一编号
     */
    private String sn;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     *描述信息
     */
    private String description;

    /**
     * 设备状态
     */
    private Integer status;


    private Date createDate;

    private Date modifyDate;

    public String getName(){ return name; }

    public void setName(String name){ this.name = name == null ? null : name.trim();}

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid == null ? null : deviceid.trim();
    }

    public String getSn(){ return sn;}

    public void setSn(String sn){this.sn = sn == null ? null : sn.trim(); }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType == null ? null : deviceType.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
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
