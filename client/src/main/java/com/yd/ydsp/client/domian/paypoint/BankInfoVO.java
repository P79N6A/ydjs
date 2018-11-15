package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;

public class BankInfoVO implements Serializable {

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 对公还是对私 0-对私，1-对公
     */
    private Integer cartType;

    /**
     * 城市编码
     */
    private String yeepayCityCode;

    /**
     * 银行编码
     */
    private String yeepayBankCode;

    /**
     * 城市
     */
    private String city;

    /**
     * 省
     */
    private String province;

    private String country;

    /**
     * 持卡人手机号
     */
    private String mobile;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 支行名称
     */
    private String subbranchName;

    /**
     * 银行帐户名
     */
    private String bankAccountName;

    /**
     * 银行帐号
     */
    private String bankAccountCode;

    /**
     * 持卡人身份证号
     */
    private String identityCard;

    /**
     * 持卡人身份证正面图
     */
    private String identityFrontImg;

    /**
     * 持卡人身份证反面图
     */
    private String identityBackImg;

    /**
     * 营业执照图
     */
    private String businessLicenceImg;

    /**
     * 对公银行帐号开户许可证图
     */
    private String bankAccountLicenceImg;

    /**
     * 状态：1-信息提交审核中，2-审核通过，0-审核不通过需要重新填写或者只是填写了草稿还没有正式提交审核
     */
    private Integer status;

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

}
