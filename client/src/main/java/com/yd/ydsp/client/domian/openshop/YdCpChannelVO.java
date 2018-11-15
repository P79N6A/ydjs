package com.yd.ydsp.client.domian.openshop;

import java.util.Date;

public class YdCpChannelVO {

    /**
     * 主店ID
     */
    private String shopid;

    /**
     * 渠道id
     */
    private String channelid;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 渠道类型 0-分店; 1-主店；2-分销
     */
    private Integer channelType;

    /**
     * 渠道负责人姓名
     */
    private String ownerName;

    /**
     * 渠道负责人手机
     */
    private String ownerMobile;

    /**
     * 渠道的物流方式 0-商家自己配送；1-自提；2-UU跑腿；3-达达配送
     */
    private Integer deliveryType;

    /**
     * 接单打印机id
     */
    private String printerNum;

    /**
     * 打印机名称
     */
    private String printerName;

    /**
     * 地址
     */
    private String address;

    /**
     * 区
     */
    private String district;

    /**
     * 城市
     */
    private String city;

    /**
     * 省
     */
    private String province;

    /**
     * 国家
     */
    private String country;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;

    /**
     * 渠道联系电话
     */
    private String telephone;

    private String description;

    /**
     * 0-正常；1-停止使用；
     */
    private Integer status;

    private String qrCodeUrl;

    private Date createDate;

    private Date modifyDate;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid == null ? null : channelid.trim();
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName == null ? null : channelName.trim();
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName == null ? null : ownerName.trim();
    }

    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile == null ? null : ownerMobile.trim();
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getPrinterNum() {
        return printerNum;
    }

    public void setPrinterNum(String printerNum) {
        this.printerNum = printerNum == null ? null : printerNum.trim();
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district == null ? null : district.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? null : longitude.trim();
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? null : latitude.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
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

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
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
