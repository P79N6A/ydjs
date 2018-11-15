package com.yd.ydsp.client.domian.weixin;

import com.yd.ydsp.client.domian.ShopInfoVO;
import com.yd.ydsp.client.domian.openshop.YdShopHoursInfoVO;

import java.io.Serializable;

public class ShopInfoExtVO extends ShopInfoVO implements Serializable {

    /**
     * 用户公众号或者小程序授权开放平台的配置表id
     */
    private String weixinConfigId;

    /**
     * 服务商（代理商）id
     */
    private String agentid;

    /**
     * 负责人身份证号码
     */
    private String ownerIdentificationCard;

    /**
     * 负责人身份证正面
     */
    private String identificationCardImg1;

    /**
     * 负责人身份证反面
     */
    private String identificationCardImg2;

    /**
     * 营业执照图片
     */
    private String businessLicenseImg;

    private String longitude;

    private String latitude;

    /**
     * 商家公告
     */
    private String message;

    /**
     * 营业时间信息对象
     */
    private YdShopHoursInfoVO shopHoursInfoVO;

    public String getWeixinConfigId() {
        return weixinConfigId;
    }

    public void setWeixinConfigId(String weixinConfigId) {
        this.weixinConfigId = weixinConfigId == null ? null : weixinConfigId.trim();
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid == null ? null : agentid.trim();
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

    public String getBusinessLicenseImg() {
        return businessLicenseImg;
    }

    public void setBusinessLicenseImg(String businessLicenseImg) {
        this.businessLicenseImg = businessLicenseImg == null ? null : businessLicenseImg.trim();
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
}
