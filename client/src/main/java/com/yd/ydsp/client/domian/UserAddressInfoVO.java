package com.yd.ydsp.client.domian;

import java.io.Serializable;

/**
 * @author zengyixun
 * @date 18/1/10
 */
public class UserAddressInfoVO implements Serializable {

//    private static final long serialVersionUID = 3000000000000000008L;

    /**
     * 地址id
     */
    private String addressid;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 收件人姓名
     */
    private String name;

    /**
     * 人工输入的剩下的地址详情信息
     */
    private String address;

    /**
     * 邮编
     */
    private String zipcode;

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
     * 国家，不填为"中国"
     */
    private String country;

    /**
     * 是否为默认地址
     */
    private Integer isDefault;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;


    public String getAddressid() {
        return addressid;
    }

    public void setAddressid(String addressid) {
        this.addressid = addressid == null ? null : addressid.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
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
}
