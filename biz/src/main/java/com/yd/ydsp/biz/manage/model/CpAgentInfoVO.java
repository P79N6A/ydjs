package com.yd.ydsp.biz.manage.model;

import com.yd.ydsp.common.redis.SerializeUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by zengyixun on 17/9/4.
 */
public class CpAgentInfoVO extends SerializeUtils {

    /**
     * 提交类型：0-草稿方式;1-第一步提交;2-提交申请;
     */
    private Integer submitType=0;
    /**
     * 加盟商id
     */
    private String agentid;

    /**
     * 代理商类型 0-个人；1公司
     */
    private Integer agentType=0;

    /**
     * 代理城市名称列表，比如：杭州市
     */
    private List<String> agentMode;

    /**
     * 个人代理商姓名
     */
    private String name;
    private Integer sex;

    /**
     * 法人或者个人身份证
     */
    private String ownerIdentificationCard;
    private String identificationCardImg1;
    private String identificationCardImg2;

    /**
     * 公司代理商名称
     */
    private String companyname;

    /**
     * 公司营业执照代码，或者统一的15位社会信用代码
     */
    private String businessLicense;
    private String businessLicenseImg;


    /**
     * 法人或者联系人姓名
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
     * 邮编
     */
    private String zipcode;

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
     * 座机
     */
    private String telephone;

    private String email;

    /**
     * 法人或者联系人手机的验证码
     */
    private String mobileCheckCode;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 以下内容为回传时有，引入时没有的
     *
     */

    private Date contractTimeBegin;

    private Date contractTimeEnd;

    private Date jointime;

    /**
     * 签约码，只有小二才能查看到
     */
    private String sigCode;

    private Integer status;

    /**
     * 此代理商名下有多少店铺
     */
    private Integer cpNum;


    public Integer getSubmitType() {
        return submitType;
    }

    public void setSubmitType(Integer submitType) {
        this.submitType = submitType;
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid == null ? null : agentid.trim();
    }

    public Integer getAgentType() {
        return agentType;
    }

    public void setAgentType(Integer agentType) {
        this.agentType = agentType;
    }

    public List<String> getAgentMode() {
        return agentMode;
    }

    public void setAgentMode(List<String> agentMode) {
        this.agentMode = agentMode;
    }

    public String getOwnerIdentificationCard(){ return ownerIdentificationCard; }
    public void setOwnerIdentificationCard(String ownerIdentificationCard){ this.ownerIdentificationCard = ownerIdentificationCard; }


    public String getIdentificationCardImg1() {
        return identificationCardImg1;
    }

    public void setIdentificationCardImg1(String identificationCardImg1) {
        this.identificationCardImg1 = identificationCardImg1;
    }

    public String getIdentificationCardImg2() {
        return identificationCardImg2;
    }

    public void setIdentificationCardImg2(String identificationCardImg2) {
        this.identificationCardImg2 = identificationCardImg2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname == null ? null : companyname.trim();
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
        this.businessLicenseImg = businessLicenseImg;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }


    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileCheckCode() {
        return mobileCheckCode;
    }

    public void setMobileCheckCode(String mobileCheckCode) {
        this.mobileCheckCode = mobileCheckCode == null ? null : mobileCheckCode.trim();
    }

    public String getSigCode() {
        return sigCode;
    }

    public void setSigCode(String sigCode) {
        this.sigCode = sigCode;
    }

    public Integer getCpNum() {
        return cpNum;
    }

    public void setCpNum(Integer cpNum) {
        this.cpNum = cpNum;
    }
}
