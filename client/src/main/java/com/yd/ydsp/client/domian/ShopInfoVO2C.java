package com.yd.ydsp.client.domian;

import com.yd.ydsp.client.domian.openshop.YdShopHoursInfoVO;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.security.util.SecurityUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zengyixun on 17/8/18.
 */
public class ShopInfoVO2C implements Serializable {

    private String shopid;

    /**
     * 店铺图片，用于点餐页最上方的图片显示
     */
    private String shopImg;

    /**
     * 店铺名称，如果是同一家公司有多个店，需要区分出分店名称
     */
    private String name;

    /**
     * 店铺地址
     */
    private String address;

    /**
     * 邮编
     */
    private String zipcode;

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
     * 说明描述性文字
     */
    private String description;

    /**
     * 所属行业代码
     */
    private Integer trade;


    /**
     * 座机
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 商家公告
     */
    private String message;

    /**
     * 营业时间信息对象
     */
    private YdShopHoursInfoVO shopHoursInfoVO;

    /**
     * 主页配置文件url
     */
    private String indexPageId;


    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getShopImg() {
        if(StringUtil.isNotEmpty(this.shopImg)){
            return this.shopImg.replaceFirst("http://","https://");
        }
        return shopImg;
    }

    public void setShopImg(String shopImg) {
        this.shopImg = shopImg == null ? null : shopImg.trim();
        if(StringUtil.isNotEmpty(this.shopImg)){
            this.shopImg = this.shopImg.replaceFirst("http://","https://");
        }
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
        return SecurityUtil.escapeHtml(district);
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


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public YdShopHoursInfoVO getShopHoursInfoVO() {
        return shopHoursInfoVO;
    }

    public void setShopHoursInfoVO(YdShopHoursInfoVO shopHoursInfoVO) {
        this.shopHoursInfoVO = shopHoursInfoVO;
    }

    public String getIndexPageId() {
        return indexPageId;
    }

    public void setIndexPageId(String indexPageId) {
        this.indexPageId = indexPageId == null ? null : indexPageId.trim();
    }
}
