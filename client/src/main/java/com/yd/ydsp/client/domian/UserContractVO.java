package com.yd.ydsp.client.domian;

import java.io.Serializable;
import java.util.Date;

public class UserContractVO extends ContractInfoVO implements Serializable {

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 签约人姓名
     */
    private String userName;

    /**
     * 证件号码
     */
    private String identityNumber;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 签约时间
     */
    private Date contractDate;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }
}
