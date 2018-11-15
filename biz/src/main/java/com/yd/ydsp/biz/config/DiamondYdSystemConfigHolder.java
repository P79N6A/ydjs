package com.yd.ydsp.biz.config;

/**
 * @author zengyixun
 * @date 17/11/25
 */
public class DiamondYdSystemConfigHolder {
    static final DiamondYdSystemConfigHolder instance = new DiamondYdSystemConfigHolder();
    public static DiamondYdSystemConfigHolder getInstance(){ return instance; }

    /**
     * 是否记录前端日志的开关
     */
    public Boolean logDebug = true;
    /**
     * 会话过期时间
     */
    public Integer sessionExpire = 600;
    /**
     * 消费者打码购物的url前缀，在这个前缀上加上qrcode的值，就是完整的扫码入口
     */
    public String b2cPreUrl = "http://www.ydjs360.com/b2c?qrcode=";

    public String cpChannelPreUrl = "http://www.ydjs360.com/channel?qrcode=";

    /**
     * 测试联调token,testToken如果为off表明关闭测试通道
     */

    public String testToken="XYUHJKK9876549678LKJHYTR5434543";

    /**
     * 相关的动态码，比如要密码的地方，要手机验证码的地方等等，如果testToken不等off时，都可以直接使用这个万能码饶开验证
     */
    public String testCode="888888";

    /**
     * 超级管理员用户的手机白名单
     */
    public String rootUserMobiles;

    public String ossAccessKeyId;
    public String ossAccessKeySecret;
    public String ossEndpoint;

    public String qjCpBucketKey="qjcp";
    public String payPointBucketKey="paypoint";
    /**
     * 指的是当前上传策略失效时间，单位秒
     */
    public long ossExpire=30;

    /**
     * 萤石开放平台的应用秘钥
     */
    public String ysAppKey;
    public String ysSecret;
    /**
     * 萤石accessToken失效时间，单位秒，默认3天
     */
    public int ysTokenExpire=259200;

    /**
     * 易宝支付相关配置参数
     */
    /**
     * 我们在易宝的商户编号
     */
    public String yeepayMerchantaCcount;
    /**
     * 我们的易宝私钥
     */
    public String yeepayMerchantPrivateKey;

    /**
     * 易宝公钥
     */
    public String yeepayPublicKey;

    /**
     * 易宝订单支付接口
     */
    public String yeepayApi="https://ok.yeepay.com/paymobile/payapi/request";

    /**
     * 易宝订单查询接口
     */
    public String yeepayQueryOrderApi="https://ok.yeepay.com/merchant/query_server/pay_single";

    /**
     * 易宝单笔退款接口
     */
    public String yeepayRefundApi="https://ok.yeepay.com/merchant/query_server/direct_refund";

    /**
     * 易宝退款查询接口
     */
    public String yeepayQueryRefundApi="https://ok.yeepay.com/merchant/query_server/refund_single";

    /**
     * 易宝支付入驻套餐成功回调接口地址
     */
    public String yeePayCallbackUrl;

    /**
     * cp商城购买支付成功后，易宝支付回调地址
     */
    public String yeePayCpMallFCallbackUrl;

    /**
     * 易宝支付完成返回商户页面时回调的页面地址
     */
    public String yeePayFCallbackUrl;

    /**
     * C端用户支付完成后的回跳url
     */
    public String yeePay2COrderFCallbackUrl;

    /**
     * 微信用户认证授权后，服务端带着cookieId去到的前端页面地址
     */
    public String ydB2BLoginRedirectUrl;

    public String ydB2CLoginRedirectUrl;

    public String dadaCallbackPreUtl="http://www.ydjs360.com/paypoint/cp/public/dada/callback/";

    public Integer getSessionExpire() {
        return sessionExpire;
    }

    public void setSessionExpire(Integer sessionExpire) {
        this.sessionExpire = sessionExpire;
    }

    public String getYdB2BLoginRedirectUrl() {
        return ydB2BLoginRedirectUrl;
    }

    public void setYdB2BLoginRedirectUrl(String ydB2BLoginRedirectUrl) {
        this.ydB2BLoginRedirectUrl = ydB2BLoginRedirectUrl;
    }

    public String getYdB2CLoginRedirectUrl() {
        return ydB2CLoginRedirectUrl;
    }

    public void setYdB2CLoginRedirectUrl(String ydB2CLoginRedirectUrl) {
        this.ydB2CLoginRedirectUrl = ydB2CLoginRedirectUrl;
    }

    public void setB2cPreUrl(String b2cPreUrl) {
        if(b2cPreUrl!=null){
            b2cPreUrl = b2cPreUrl.trim();
        }
        this.b2cPreUrl = b2cPreUrl;
    }

    public String getB2cPreUrl() {
        return b2cPreUrl;
    }

    public String getCpChannelPreUrl() {
        return cpChannelPreUrl;
    }

    public void setCpChannelPreUrl(String cpChannelPreUrl) {
        if(cpChannelPreUrl!=null){
            cpChannelPreUrl = cpChannelPreUrl.trim();
        }
        this.cpChannelPreUrl = cpChannelPreUrl;
    }

    public void setTestToken(String testToken){ this.testToken = testToken; }
    public String getTestToken(){
        if(this.testToken!=null){
            if (!(this.testToken.trim().toLowerCase().equals("off"))){
                return testToken;
            }
        }
        return null;
    }

    public void setRootUserMobiles(String rootUserMobiles){ this.rootUserMobiles = rootUserMobiles; }
    public String getRootUserMobiles(){ return rootUserMobiles; }

    public void setOssAccessKeyId(String ossAccessKeyId){ this.ossAccessKeyId = ossAccessKeyId; }
    public String getOssAccessKeyId(){ return ossAccessKeyId; }

    public void setOssAccessKeySecret(String ossAccessKeySecret){ this.ossAccessKeySecret = ossAccessKeySecret; }
    public String getOssAccessKeySecret(){ return ossAccessKeySecret; }

    public void setOssEndpoint(String ossEndpoint){ this.ossEndpoint = ossEndpoint; }
    public String getOssEndpoint(){ return ossEndpoint; }

    public void setQjCpBucketKey(String qjCpBucketKey){ this.qjCpBucketKey = qjCpBucketKey; }
    public String getQjCpBucketKey(){ return  qjCpBucketKey; }
    public void setPayPointBucketKey(String payPointBucketKey){ this.payPointBucketKey = payPointBucketKey; }
    public String getPayPointBucketKey(){ return payPointBucketKey; }

    public void setOssExpire(long ossExpire){ this.ossExpire = ossExpire; }
    public long getOssExpire(){ return ossExpire; }

    public void setYsAppKey(String ysAppKey){ this.ysAppKey = ysAppKey; }
    public String getYsAppKey(){ return ysAppKey; }
    public void setYsSecret(String ysSecret){ this.ysSecret = ysSecret; }
    public String getYsSecret(){ return ysSecret; }

    public void setYsTokenExpire(int ysTokenExpire){ this.ysTokenExpire = ysTokenExpire; }
    public int getYsTokenExpire(){ return ysTokenExpire; }

    public void setYeepayMerchantaCcount(String yeepayMerchantaCcount){ this.yeepayMerchantaCcount = yeepayMerchantaCcount; }
    public String getYeepayMerchantaCcount(){ return yeepayMerchantaCcount; }

    public void setYeepayMerchantPrivateKey(String yeepayMerchantPrivateKey){ this.yeepayMerchantPrivateKey = yeepayMerchantPrivateKey;}
    public String getYeepayMerchantPrivateKey(){ return yeepayMerchantPrivateKey; }

    public void setYeepayPublicKey(String yeepayPublicKey){ this.yeepayPublicKey = yeepayPublicKey; }
    public String getYeepayPublicKey(){ return yeepayPublicKey; }

    public void setYeepayApi(String yeepayApi){ this.yeepayApi = yeepayApi; }
    public String getYeepayApi(){ return yeepayApi; }

    public void setYeepayQueryOrderApi(String yeepayQueryOrderApi){ this.yeepayQueryOrderApi = yeepayQueryOrderApi; }
    public String getYeepayQueryOrderApi(){ return yeepayQueryOrderApi; }

    public void setYeepayRefundApi(String yeepayRefundApi){ this.yeepayRefundApi = yeepayRefundApi; }
    public String getYeepayRefundApi(){ return yeepayRefundApi; }

    public void setYeepayQueryRefundApi(String yeepayQueryRefundApi){ this.yeepayQueryRefundApi = yeepayQueryRefundApi; }
    public String getYeepayQueryRefundApi(){ return yeepayQueryRefundApi; }

    public void setYeePayCallbackUrl(String yeePayCallbackUrl) { this.yeePayCallbackUrl = yeePayCallbackUrl; }
    public String getYeePayCallbackUrl(){ return yeePayCallbackUrl; }

    public void setYeePayFCallbackUrl(String yeePayFCallbackUrl){ this.yeePayFCallbackUrl = yeePayFCallbackUrl; }
    public String getYeePayFCallbackUrl(){ return yeePayFCallbackUrl; }

    public String getYeePayCpMallFCallbackUrl() {
        return yeePayCpMallFCallbackUrl;
    }

    public void setYeePayCpMallFCallbackUrl(String yeePayCpMallFCallbackUrl) {
        this.yeePayCpMallFCallbackUrl = yeePayCpMallFCallbackUrl;
    }

    public String getTestCode(){
        if(this.getTestSwitch()){
            return testCode;
        }
        return null;
    }

    public void setTestCode(String testCode){ this.testCode = testCode; }

    public boolean getTestSwitch(){
        if(this.getTestToken()==null){
            return false;
        }
        return true;
    }

    public String getYeePay2COrderFCallbackUrl() {
        return yeePay2COrderFCallbackUrl;
    }

    public void setYeePay2COrderFCallbackUrl(String yeePay2COrderFCallbackUrl) {
        this.yeePay2COrderFCallbackUrl = yeePay2COrderFCallbackUrl;
    }

    public String getDadaCallbackPreUtl() {
        return dadaCallbackPreUtl;
    }

    public void setDadaCallbackPreUtl(String dadaCallbackPreUtl) {
        this.dadaCallbackPreUtl = dadaCallbackPreUtl;
    }

    public Boolean getLogDebug() {
        return logDebug;
    }

    public void setLogDebug(Boolean logDebug) {
        this.logDebug = logDebug;
    }
}
