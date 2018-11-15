package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdPaypointShopInfo {
    private Long id;

    private String shopid;

    private String shopImg;

    private String name;

    private String code;

    private String corporation;

    private String contact;

    private String address;

    private String zipcode;

    private String district;

    private String city;

    private String province;

    private String country;

    private String description;

    private Integer trade;

    private String mobile;

    private String telephone;

    private String email;

    private String alipayAccount;

    private String weixinpaySubMchId;

    private String payeeRealName;

    private Integer status;

    private Long flag;

    private Date jointime;

    private Date contractTimeBegin;

    private Date contractTimeEnd;

    private String deviceInfo;

    private String indexPageId;

    private String feature;

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

    public String getShopImg() {
        return shopImg;
    }

    public void setShopImg(String shopImg) {
        this.shopImg = shopImg == null ? null : shopImg.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getCorporation() {
        return corporation;
    }

    public void setCorporation(String corporation) {
        this.corporation = corporation == null ? null : corporation.trim();
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact == null ? null : contact.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode == null ? null : zipcode.trim();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getTrade() {
        return trade;
    }

    public void setTrade(Integer trade) {
        this.trade = trade;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAlipayAccount() {
        return alipayAccount;
    }

    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount == null ? null : alipayAccount.trim();
    }

    public String getWeixinpaySubMchId() {
        return weixinpaySubMchId;
    }

    public void setWeixinpaySubMchId(String weixinpaySubMchId) {
        this.weixinpaySubMchId = weixinpaySubMchId == null ? null : weixinpaySubMchId.trim();
    }

    public String getPayeeRealName() {
        return payeeRealName;
    }

    public void setPayeeRealName(String payeeRealName) {
        this.payeeRealName = payeeRealName == null ? null : payeeRealName.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getFlag() {
        return flag;
    }

    public void setFlag(Long flag) {
        this.flag = flag;
    }

    public Date getJointime() {
        return jointime;
    }

    public void setJointime(Date jointime) {
        this.jointime = jointime;
    }

    public Date getContractTimeBegin() {
        return contractTimeBegin;
    }

    public void setContractTimeBegin(Date contractTimeBegin) {
        this.contractTimeBegin = contractTimeBegin;
    }

    public Date getContractTimeEnd() {
        return contractTimeEnd;
    }

    public void setContractTimeEnd(Date contractTimeEnd) {
        this.contractTimeEnd = contractTimeEnd;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo == null ? null : deviceInfo.trim();
    }

    public String getIndexPageId() {
        return indexPageId;
    }

    public void setIndexPageId(String indexPageId) {
        this.indexPageId = indexPageId == null ? null : indexPageId.trim();
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
}