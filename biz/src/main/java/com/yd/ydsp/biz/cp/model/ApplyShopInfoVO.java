package com.yd.ydsp.biz.cp.model;

import com.yd.ydsp.common.redis.SerializeUtils;

/**
 * Created by zengyixun on 17/9/4.
 */
public class ApplyShopInfoVO extends SerializeUtils {

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 店铺名称，如果是同一家公司有多个店，需要区分出分店名称
     */
    private String name;

    /**
     * 公司营业执照代码，或者统一的15位社会信用代码
     */
    private String code;


    /**
     * 联系人姓名
     */
    private String contact;

    /**
     * 店铺地址
     */
    private String address;

    /**
     * 区县
     */
    private String district;

    /**
     * 城市
     */
    private String city;

    /**
     * 省份、自治区
     */
    private String province;

    /**
     * 国家
     */
    private String country;

    /**
     * 法人或者联系人手机
     */
    private String mobile;

    /**
     * 法人或者联系人手机的验证码
     */
    private String mobileCheckCode;

    /**
     * 法人身份证号码
     * @return
     */
    private String identityNumber;

    /**
     * 行业代码，默认0，表示餐饮业
     */
    private Integer trade = 0;

    private Integer shopStatus;

    public Integer getShopStatus(){ return shopStatus; }
    public void setShopStatus(Integer shopStatus){
        this.shopStatus = shopStatus;
    }

    public String getIdentityNumber(){ return identityNumber; }
    public void setIdentityNumber(String identityNumber){ this.identityNumber = identityNumber; }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
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


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getMobileCheckCode() {
        return mobileCheckCode;
    }

    public void setMobileCheckCode(String mobileCheckCode) {
        this.mobileCheckCode = mobileCheckCode == null ? null : mobileCheckCode.trim();
    }

    public Integer getTrade(){ return trade; }
    public void setTrade(Integer trade){ this.trade = trade; }

}
