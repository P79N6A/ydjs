package com.yd.ydsp.biz.config;

import java.util.List;

public class DiamondWeiXinInfoConfigHolder {
    static final DiamondWeiXinInfoConfigHolder instance = new DiamondWeiXinInfoConfigHolder();
    public static DiamondWeiXinInfoConfigHolder getInstance(){ return instance; }

    public String shopApplyWeiXinPreAuthUrl = "http://www.ydjs360.com/wex/preAuth/";

    // b2b关注时发送的图文标提
    public String b2bSubscribeTitle="扫码点餐功能扬帆起航";

    // b2b关注时发送的图文消息描述
    public String b2bSubscribeDesc="开启智能化点餐服务，马上入驻创建一个线上餐馆";

    // b2b关注时发送的图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80
    public String b2bSubscribePicUrl="http://paypoint.oss-cn-hangzhou.aliyuncs.com/weixin/yangfan.jpg";

    // b2b关注时发送的点击图文消息跳转链接
    public String b2bSubscribePageUrl="http://www.ydjs360.com/b2b";

    //微信用户授权登录成功后，要去的主页面

    public String mainPageUrl="";

    //授权失败应该去的统一的错误提示页面
    public String errorPageUrl="";

    /**
     * 微信商城小程序主模板id
     */
    public Integer weixinSamllShopMainTemplateId = 0;

    public String weixinSamllShopMainTemplateVersion = "V1.0";
    public String weixinSamllShopMainTemplateDesc = "Test";

    /**
     * 商家管理员预授权码有效时间（默认8小时）
     */
    public Integer preAuthLinkCodeExpin=28800;

    /**
     * 引灯服务商户签名密钥
     */
    public String ydjsPaySecretKey="1234567890098765432112";

    public String weixinPayCallBackPreUrl = "https://www.ydjs360.com/sys/paycallback/weixin/";

    public String defaultOnlinMainPage = "pages/index/index";

    public String publicMchIds="1489419802";

    public String getB2bSubscribeTitle() {
        return b2bSubscribeTitle;
    }

    public void setB2bSubscribeTitle(String b2bSubscribeTitle) {
        this.b2bSubscribeTitle = b2bSubscribeTitle;
    }

    public String getB2bSubscribeDesc() {
        return b2bSubscribeDesc;
    }

    public void setB2bSubscribeDesc(String b2bSubscribeDesc) {
        this.b2bSubscribeDesc = b2bSubscribeDesc;
    }

    public String getB2bSubscribePicUrl() {
        return b2bSubscribePicUrl;
    }

    public void setB2bSubscribePicUrl(String b2bSubscribePicUrl) {
        this.b2bSubscribePicUrl = b2bSubscribePicUrl;
    }

    public String getB2bSubscribePageUrl() {
        return b2bSubscribePageUrl;
    }

    public void setB2bSubscribePageUrl(String b2bSubscribePageUrl) {
        this.b2bSubscribePageUrl = b2bSubscribePageUrl;
    }

    public String getMainPageUrl() {
        return mainPageUrl;
    }

    public void setMainPageUrl(String mainPageUrl) {
        this.mainPageUrl = mainPageUrl;
    }

    public String getErrorPageUrl() {
        return errorPageUrl;
    }

    public void setErrorPageUrl(String errorPageUrl) {
        this.errorPageUrl = errorPageUrl;
    }

    public Integer getWeixinSamllShopMainTemplateId() {
        return weixinSamllShopMainTemplateId;
    }

    public void setWeixinSamllShopMainTemplateId(Integer weixinSamllShopMainTemplateId) {
        this.weixinSamllShopMainTemplateId = weixinSamllShopMainTemplateId;
    }

    public String getWeixinSamllShopMainTemplateVersion() {
        return weixinSamllShopMainTemplateVersion;
    }

    public void setWeixinSamllShopMainTemplateVersion(String weixinSamllShopMainTemplateVersion) {
        this.weixinSamllShopMainTemplateVersion = weixinSamllShopMainTemplateVersion;
    }

    public String getWeixinSamllShopMainTemplateDesc() {
        return weixinSamllShopMainTemplateDesc;
    }

    public void setWeixinSamllShopMainTemplateDesc(String weixinSamllShopMainTemplateDesc) {
        this.weixinSamllShopMainTemplateDesc = weixinSamllShopMainTemplateDesc;
    }

    public String getYdjsPaySecretKey() {
        return ydjsPaySecretKey;
    }

    public void setYdjsPaySecretKey(String ydjsPaySecretKey) {
        this.ydjsPaySecretKey = ydjsPaySecretKey;
    }

    public String getShopApplyWeiXinPreAuthUrl() {
        return shopApplyWeiXinPreAuthUrl;
    }

    public void setShopApplyWeiXinPreAuthUrl(String shopApplyWeiXinPreAuthUrl) {
        this.shopApplyWeiXinPreAuthUrl = shopApplyWeiXinPreAuthUrl;
    }

    public String getWeixinPayCallBackPreUrl() {
        return weixinPayCallBackPreUrl;
    }

    public void setWeixinPayCallBackPreUrl(String weixinPayCallBackPreUrl) {
        this.weixinPayCallBackPreUrl = weixinPayCallBackPreUrl;
    }

    public Integer getPreAuthLinkCodeExpin() {
        return preAuthLinkCodeExpin;
    }

    public void setPreAuthLinkCodeExpin(Integer preAuthLinkCodeExpin) {
        this.preAuthLinkCodeExpin = preAuthLinkCodeExpin;
    }

    public String getDefaultOnlinMainPage() {
        return defaultOnlinMainPage;
    }

    public void setDefaultOnlinMainPage(String defaultOnlinMainPage) {
        this.defaultOnlinMainPage = defaultOnlinMainPage;
    }

    public String getPublicMchIds() {
        return publicMchIds;
    }

    public void setPublicMchIds(String publicMchIds) {
        this.publicMchIds = publicMchIds;
    }
    public String[] getPublicMchIdList(){
        return publicMchIds.split(",");
    }
}
