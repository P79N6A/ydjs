package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdPaypointBankInfo {
    private Long id;

    private String openid;

    private String shopid;

    private Integer cartType;

    private String yeepayCityCode;

    private String yeepayBankCode;

    private String city;

    private String province;

    private String country;

    private String mobile;

    private String bankName;

    private String subbranchName;

    private String bankAccountName;

    private String bankAccountCode;

    private String identityCard;

    private String identityFrontImg;

    private String identityBackImg;

    private String businessLicenceImg;

    private String bankAccountLicenceImg;

    private Integer status;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public Integer getCartType() {
        return cartType;
    }

    public void setCartType(Integer cartType) {
        this.cartType = cartType;
    }

    public String getYeepayCityCode() {
        return yeepayCityCode;
    }

    public void setYeepayCityCode(String yeepayCityCode) {
        this.yeepayCityCode = yeepayCityCode == null ? null : yeepayCityCode.trim();
    }

    public String getYeepayBankCode() {
        return yeepayBankCode;
    }

    public void setYeepayBankCode(String yeepayBankCode) {
        this.yeepayBankCode = yeepayBankCode == null ? null : yeepayBankCode.trim();
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getSubbranchName() {
        return subbranchName;
    }

    public void setSubbranchName(String subbranchName) {
        this.subbranchName = subbranchName == null ? null : subbranchName.trim();
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName == null ? null : bankAccountName.trim();
    }

    public String getBankAccountCode() {
        return bankAccountCode;
    }

    public void setBankAccountCode(String bankAccountCode) {
        this.bankAccountCode = bankAccountCode == null ? null : bankAccountCode.trim();
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard == null ? null : identityCard.trim();
    }

    public String getIdentityFrontImg() {
        return identityFrontImg;
    }

    public void setIdentityFrontImg(String identityFrontImg) {
        this.identityFrontImg = identityFrontImg == null ? null : identityFrontImg.trim();
    }

    public String getIdentityBackImg() {
        return identityBackImg;
    }

    public void setIdentityBackImg(String identityBackImg) {
        this.identityBackImg = identityBackImg == null ? null : identityBackImg.trim();
    }

    public String getBusinessLicenceImg() {
        return businessLicenceImg;
    }

    public void setBusinessLicenceImg(String businessLicenceImg) {
        this.businessLicenceImg = businessLicenceImg == null ? null : businessLicenceImg.trim();
    }

    public String getBankAccountLicenceImg() {
        return bankAccountLicenceImg;
    }

    public void setBankAccountLicenceImg(String bankAccountLicenceImg) {
        this.bankAccountLicenceImg = bankAccountLicenceImg == null ? null : bankAccountLicenceImg.trim();
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