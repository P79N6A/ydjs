package com.yd.ydsp.biz.pay.model;

public class WeiXinPayRequestDO {
    /**
     * 服务商id
     */
    String appid;

    /**
     * 服务商商户号
     */
    String mch_id;

    /**
     * 商家公众号或者小程序的appid
     */
    String sub_appid;

    /**
     * 商家的支付商户号
     */
    String sub_mch_id;

    /**
     * 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
     * 小程序不传
     */
    String device_info;

    /**
     * 随机字符串，不长于32位
     */
    String nonce_str;

    /**
     * 签名(MD5)
     */
    String sign;

    String sign_type="MD5";

    /**
     * 商品描述-(店名-销售商品类目)128个长度
     */
    String body;

    /**
     * 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     */
    String attach;

    /**
     * 商户订单号，32个长度以内
     */
    String out_trade_no;

    /**
     *总金额	, 单位分
     */
    Integer total_fee;

    String fee_type="CNY";

    /**
     * APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
     */
    String spbill_create_ip;

    /**
     * 通知地址:接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数
     */
    String notify_url;

    /**
     * 交易类型: JSAPI--公众号(小程序)支付、NATIVE--原生扫码支付、APP--app支付，
     MICROPAY--刷卡支付，刷卡支付有单独的支付接口，不调用统一下单接口
     */
    String trade_type;

    /**
     * 用户子标识：trade_type=JSAPI，此参数必传，用户在子商户appid下的唯一标识。openid和sub_openid可以选传其中之一，如果选择传sub_openid,则必须传sub_appid
     */
    String sub_openid;
    String openid;

    /**
     * 扫码支付授权码，设备读取用户微信中的条码或者二维码信息
     （注：用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头）
     */
    String auth_code;


    /**
     * 交易起始时间:订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010
     */
    String time_start;

    /**
     * 交易结束时间:订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010
     * 订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，
     * 所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id
     */
    String time_expire;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(Integer total_fee) {
        this.total_fee = total_fee;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getSub_appid() {
        return sub_appid;
    }

    public void setSub_appid(String sub_appid) {
        this.sub_appid = sub_appid;
    }

    public String getSub_mch_id() {
        return sub_mch_id;
    }

    public void setSub_mch_id(String sub_mch_id) {
        this.sub_mch_id = sub_mch_id;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getSub_openid() {
        return sub_openid;
    }

    public void setSub_openid(String sub_openid) {
        this.sub_openid = sub_openid == null ? null : sub_openid.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(String time_expire) {
        this.time_expire = time_expire;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getAuth_code() {
        return auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }
}
