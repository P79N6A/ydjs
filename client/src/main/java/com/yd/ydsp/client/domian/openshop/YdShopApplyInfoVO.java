package com.yd.ydsp.client.domian.openshop;

import java.util.Date;

/**
 * 新的V：增加一个商家申请
 */
public class YdShopApplyInfoVO {


    /**
     * 商家店铺类型：0-餐馆; 1-零售; 2-微商
     */
    private Integer tradeType;
    /**
     * 提交类型：0-草稿;1-提交;
     */
    private Integer submitType=0;

    /**
     * 是否是个体户
     */
    private Boolean isSmallPrivate = false;

    /**
     * 是否是直接使用引灯公众号与小程序来开店,true-在引灯小程序上直接开店，false-使用自己的小程序来开店
     */
    private Boolean shopIsPublic = true;

    /**
     * 申请单id
     */
    private String applyid;

    /**
     * 公众号appid
     */
    private String publicAppid;

    /**
     * 小程序appid
     */
    private String smallAppid;

    /**
     * 加盟商id
     */
    private String agentid;

    /**
     * 商家logo
     */
    private String shopImg;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 法人
     */
    private String corporation;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 法人手机（需要关注了引灯公众号并绑定过此手机）
     */
    private String mobile;

    /**
     * 商家电话
     */
    private String telephone;

    /**
     * 法人身份证号码
     */
    private String ownerIdentificationCard;

    /**
     * 法人身份证正面
     */
    private String identificationCardImg1;

    /**
     * 法人身份证反面
     */
    private String identificationCardImg2;

    /**
     * 商家营业执照号或者三合一统一社会信息码
     */
    private String businessCode;

    /**
     * 商家营业执照号或者三合一统一社会信息码，冗余一下
     */
    private String businessLicense;

    /**
     * 商家营业执照号或者三合一统一社会信息证件图
     */
    private String businessLicenseImg;

    /**
     * 微信申请时需要的公司盖章的公函
     */
    private String weixinAuthFile;


    private String address;

    private String zipcode;

    private String district;

    private String city;

    private String province;

    private String country;

    private String longitude;

    private String latitude;

    private String description;

    private String email;

    private Integer status;

    private Date contractTimeBegin;

    private Date contractTimeEnd;

    public Integer getSubmitType() {
        return submitType;
    }

    public void setSubmitType(Integer submitType) {
        this.submitType = submitType;
    }

    public String getApplyid() {
        return applyid;
    }

    public void setApplyid(String applyid) {
        this.applyid = applyid == null ? null : applyid.trim();
    }

    public String getPublicAppid() {
        return publicAppid;
    }

    public void setPublicAppid(String publicAppid) {
        this.publicAppid = publicAppid == null ? null : publicAppid.trim();
    }

    public String getSmallAppid() {
        return smallAppid;
    }

    public void setSmallAppid(String smallAppid) {
        this.smallAppid = smallAppid == null ? null : smallAppid.trim();
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid == null ? null : agentid.trim();
    }

    public Boolean getSmallPrivate() {
        return isSmallPrivate;
    }

    public void setSmallPrivate(Boolean smallPrivate) {
        isSmallPrivate = smallPrivate;
    }

    public Boolean getShopIsPublic() {
        return shopIsPublic;
    }

    public void setShopIsPublic(Boolean shopIsPublic) {
        this.shopIsPublic = shopIsPublic;
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

    public String getWeixinAuthFile() {
        return weixinAuthFile;
    }

    public void setWeixinAuthFile(String weixinAuthFile) {
        this.weixinAuthFile = weixinAuthFile == null ? null : weixinAuthFile.trim();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}

