package com.yd.ydsp.client.domian;

import com.yd.ydsp.client.domian.openshop.YdShopHoursInfoVO;
import com.yd.ydsp.common.constants.paypoint.ShopSupportFlagConstants;
import com.yd.ydsp.common.security.util.SecurityUtil;
import com.yd.ydsp.common.utils.FeatureUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/8/18.
 */
public class ShopInfoVO implements Serializable {

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
     * 公司营业执照代码，或者统一的15位社会信用代码
     */
    private String code;

    /**
     * 法人名字
     */
    private String corporation;

    /**
     * 联系人姓名
     */
    private String contact;

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
     * 法人或者联系人手机
     */
    private String mobile;

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
     * 合约开始时间，每年续约用
     */
    private Date contractTimeBegin;

    /**
     * 合约结束时间，每年续约用，以判断服务是否应该继续提供
     */
    private Date contractTimeEnd;

    /**
     * 商家公告
     */
    private String message;

    /**
     * 营业时间信息对象
     */
    private YdShopHoursInfoVO shopHoursInfoVO;

    /**
     * 店铺默认配送方式
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
     * 主页配置页面id
     */
    private String indexPageId;


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
        return SecurityUtil.escapeHtml(description);
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

    public String getIndexPageId() {
        return indexPageId;
    }

    public void setIndexPageId(String indexPageId) {
        this.indexPageId = indexPageId == null ? null : indexPageId.trim();
    }
}
