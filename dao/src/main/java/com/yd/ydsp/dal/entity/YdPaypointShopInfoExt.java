package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdPaypointShopInfoExt {
    private Long id;

    private String shopid;

//    private String weixinConfigId;

    private String agentid;

    private String ownerIdentificationCard;

    private String identificationCardImg1;

    private String identificationCardImg2;

    private String businessCode;

    private String businessLicense;

    private String businessLicenseImg;

    private String longitude;

    private String latitude;

    private String commend;

    private String mustWare;

    private String message;

    private String shopHours;

    private String featureext;

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

//    public String getWeixinConfigId() {
//        return weixinConfigId;
//    }
//
//    public void setWeixinConfigId(String weixinConfigId) {
//        this.weixinConfigId = weixinConfigId == null ? null : weixinConfigId.trim();
//    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid == null ? null : agentid.trim();
    }

    public String getOwnerIdentificationCard() {
        return ownerIdentificationCard;
    }

    public void setOwnerIdentificationCard(String ownerIdentificationCard) {
        this.ownerIdentificationCard = ownerIdentificationCard == null ? null : ownerIdentificationCard.trim();
    }

    public String getIdentificationCardImg1() {
        return identificationCardImg1;
    }

    public void setIdentificationCardImg1(String identificationCardImg1) {
        this.identificationCardImg1 = identificationCardImg1 == null ? null : identificationCardImg1.trim();
    }

    public String getIdentificationCardImg2() {
        return identificationCardImg2;
    }

    public void setIdentificationCardImg2(String identificationCardImg2) {
        this.identificationCardImg2 = identificationCardImg2 == null ? null : identificationCardImg2.trim();
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode == null ? null : businessCode.trim();
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense == null ? null : businessLicense.trim();
    }

    public String getBusinessLicenseImg() {
        return businessLicenseImg;
    }

    public void setBusinessLicenseImg(String businessLicenseImg) {
        this.businessLicenseImg = businessLicenseImg == null ? null : businessLicenseImg.trim();
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCommend() {
        return commend;
    }

    public void setCommend(String commend) {
        this.commend = commend == null ? null : commend.trim();
    }

    public String getMustWare() {
        return mustWare;
    }

    public void setMustWare(String mustWare) {
        this.mustWare = mustWare == null ? null : mustWare.trim();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public String getShopHours() {
        return shopHours;
    }

    public void setShopHours(String shopHours) {
        this.shopHours = shopHours == null ? null : shopHours.trim();
    }

    public String getFeatureext() {
        return featureext;
    }

    public void setFeatureext(String featureext) {
        this.featureext = featureext == null ? null : featureext.trim();
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}