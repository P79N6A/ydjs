package com.yd.ydsp.biz.pay.model;

/**
 * @author zengyixun
 * @date 17/12/21
 */
public class YeePayConstant {

    /**
     * 商户编号
     */
    public static final String MERCHANT_ACCOUNT = "merchantaccount";
    /**
     * 易宝流水号,易宝中唯一
     */
    public static final String YB_ORDERID = "yborderid";
    /**
     * 商户订单号，原值返回
     */
    public static final String ORDERID = "orderid";
    /**
     * 1、移动收银台:返回的链接为需要进行支 付的易宝支付收银台地址,商户需要将浏览 器跳转到此地址,以完成后续支付流程;
     2、PC 扫码返回的是二维码链接。
     */
    public static final String PAYURL = "payurl";
    /**
     * 二维码二进制字符串,请在扫码支付时使用
     */
    public static final String IMGHEXSTR = "imghexstr";
    /**
     * 商户使用自己生成的 RSA 私钥对除参数” sign”以外的其他参数进行字母排序后串 成的字符串进行签名
     */
    public static final String SIGN = "sign";

    /**
     * 订单金额
     */
    public static final String AMOUNT = "amount";

    /**
     * 银行编码
     */
    public static final String BANKCODE = "bankcode";

    /**
     * 银行名称
     */
    public static final String BANK = "bank";

    /**
     * 卡号后 4 位
     */
    public static final String LASTNO = "lastno";

    /**
     * 卡类型
     */
    public static final String CARDTYPE = "cardtype";

    /**
     * 订单状态
     */
    public static final String STATUS = "status";

    /**
     * 给易宝回调返回接收成功
     */
    public static final String SUCCESS = "SUCCESS";
}
