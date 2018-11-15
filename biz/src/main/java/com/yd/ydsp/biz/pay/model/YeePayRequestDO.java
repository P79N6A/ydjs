package com.yd.ydsp.biz.pay.model;

import java.io.Serializable;

/**
 * @author zengyixun
 * @date 17/12/21
 */
public class YeePayRequestDO implements Serializable {

    /**必填
     * 商户编号
     */
    public String merchantaccount;

    /**必填
     * 商户生成的唯一订单号,最长 50 位
     */
    public String orderid;

    /**必填
     * 时间戳,例如:1361324896,精确到秒
     */
    public Integer transtime;

    /**必填
     * 以「分」为单位的整型,必须大于零
     */
    public Integer amount;

    /**
     * 默认值:156;表示币种为人民币
     */
    public Integer currency;

    /**必填
     *商品类别码
     * 1
     虚拟产品
     3
     公共事业缴费
     4
     手机充值
     6
     公益事业
     7
     实物电商
     8
     彩票业务
     10
     行政教育
     11
     线下服务业
     13
     微信实物电商
     14
     微信虚拟电商
     15
     保险行业
     16
     基金行业
     17
     电子票务
     18
     金融投资
     19
     大额支付
     20
     其他
     21
     旅游机票
     22
     畅付 D
     */
    public String productcatalog;

    /**必填
     *
     * 商品名称
     * 最长 50 位,出于风控考虑,请按下面的 格式传递值:应用-商品名称,如“诛仙-3 阶成品天琊”,此商品名在发送短信校验 的时候会发给用户,所以描述内容不要加 在此参数中,以提高用户的体验度。 说明:超过长度部分系统会进行截取
     */
    public String productname;

    /**
     *商品描述
     * 最长 200 位, 说明:超过长度部分系统会进行截取, 注意:直联模式下此参数必传;
     */
    public String productdesc;

    /**必填
     *用户标识类型
     * 如果 是公众号支付此参数必须传 7.对应的 identityid 传 openId.
     */
    public Integer identitytype;

    /**必填
     *最长 50 位,用户的唯一标识, 如果是公众 号支付此参数传 openId.
     */
    public String identityid;

    /**
     *微信公众号
     */
    public String appId;

    /**必填
     *终端标识类型
     * 0、IMEI;1、MAC;2、UUID;3、 OTHER
     */
    public Integer terminaltype;

    /**必填
     *终端标识 ID
     * terminalid 如果有值则 terminaltype 也 必须有值
     */
    public String terminalid;

    /**必填
     *用户 ip 地址
     * 用户支付时使用的网络终端 IP
     */
    public String userip;

    /**
     * 支付工具
     * 默认值 2
     * 1:扫码支付 2、其它支付方式即非扫码支付时此参数 不要传。 3、当支付工具为扫码支付时,直连编码 directpaytype 参数必填,且只能以直连 方式对接,例:当为微信扫码支付时 directpaytype 必须传 1。 说明:支付工具传 2 时,收银台版本参数 version 需指定为 1(即 PC 端)
     */
    public String paytool;

    /**
     * 直连编码
     * 0:默认
     1:微信支付 2:支付宝支付 3:一键支付 实现支付工具的直连跳转;
     */
    public Integer directpaytype;

    /**
     * 用户终端设备 UA
     */
    public String userua;

    /**
     * 用来通知商户支付结果,前后台回调地址
     的回调内容相同。用户在网页支付成功页
     面,点击“返回商户”时的回调地址;
     说明:扫码支付无页面回调。
     */
    public String fcallbackurl;

    /**
     * 用来通知商户支付结果(页面回调以及后
     台通知均使用该接口)
     */
    public String callbackurl;

    /**
     * 支付方式
     * 格式:1|2|3|4
     1- 借记卡支付;
     2- 信用卡支付;
     3- 手机充值卡支付;
     4- 游戏点卡支付 注:该参数若不传此参数,则默认选择运 营后台为该商户开通的支付方式。
     */
    public String paytypes;

    /**
     * 订单有效期
     * 单位:分钟,范围:5 – 100*24*60 如果不填写,则默认为 24 小时
     */
    public Integer orderexpdate;

    /**
     * 银行卡号
     * 在网页支付请求的时候,如果传此参数会
     把银行卡号直接在银行信息界面显示卡号
     */
    public String cardno;

    /**
     * 证件类型
     * 01:身份证,当前只支持身份证
     */
    public String idcardtype;

    /**
     * 证件号
     */
    public String idcard;

    /**
     * 持卡人姓名
     */
    public String owner;

    /**
     * 收银台版本
     * 商户可以使用此参数定制调用的网页收银 台版本:
     0:wap
     1:pc
     */
    public Integer version;


    public String getMerchantaccount(){ return merchantaccount; }
    public void setMerchantaccount(String merchantaccount){ this.merchantaccount = merchantaccount; }

    public String getOrderid(){ return orderid; }
    public void setOrderid(String orderid){ this.orderid = orderid; }

    public Integer getTranstime(){ return transtime;}
    public void setTranstime(Integer transtime){ this.transtime = transtime; }

    public Integer getAmount(){ return amount; }
    public void setAmount(Integer amount){ this.amount = amount; }

    public Integer getCurrency(){ return  currency; }
    public void setCurrency(Integer currency){ this.currency = currency; }

    public String getProductcatalog(){ return productcatalog; }
    public void setProductcatalog(String productcatalog){ this.productcatalog = productcatalog; }

    public String getProductname(){ return productname; }
    public void setProductname(String productname){ this.productname = productname; }

    public String getProductdesc(){ return productdesc; }
    public void setProductdesc(String productdesc){ this.productdesc = productdesc; }

    public Integer getIdentitytype(){ return identitytype; }
    public void setIdentitytype(Integer identitytype){ this.identitytype = identitytype; }

    public String getIdentityid(){return identityid; }
    public void setIdentityid(String identityid){ this.identityid = identityid; }

    public String getAppId(){ return appId; }
    public void setAppId(String appId){ this.appId = appId; }

    public Integer getTerminaltype(){ return terminaltype; }
    public void setTerminaltype(Integer terminaltype){ this.terminaltype = terminaltype; }

    public String getTerminalid(){ return terminalid; }
    public void setTerminalid(String terminalid){ this.terminalid = terminalid; }

    public String getUserip(){ return userip; }
    public void setUserip(String userip){ this.userip = userip; }

    public String getPaytool(){ return paytool; }
    public void setPaytool(String paytool){ this.paytool = paytool; }

    public Integer getDirectpaytype(){ return directpaytype; }
    public void setDirectpaytype(Integer directpaytype){ this.directpaytype = directpaytype; }

    public String getUserua(){ return userua;}
    public void setUserua(String userua){ this.userua = userua; }

    public String getFcallbackurl(){ return fcallbackurl; }
    public void setFcallbackurl(String fcallbackurl){ this.fcallbackurl = fcallbackurl; }

    public String getCallbackurl(){ return callbackurl; }
    public void setCallbackurl(String callbackurl){ this.callbackurl = callbackurl; }

    public String getPaytypes(){ return  paytypes; }
    public void setPaytypes(String paytypes){ this.paytypes = paytypes; }

    public Integer getOrderexpdate(){ return orderexpdate; }
    public void setOrderexpdate(Integer orderexpdate){ this.orderexpdate = orderexpdate; }

    public String getCardno(){ return cardno; }
    public void setCardno(String cardno){ this.cardno = cardno; }

    public String getIdcardtype(){ return idcardtype; }
    public void setIdcardtype(String idcardtype){ this.idcardtype = idcardtype; }

    public String getIdcard(){ return idcard; }
    public void setIdcard(String idcard) { this.idcard = idcard; }

    public String getOwner(){ return owner; }
    public void setOwner(String owner){ this.owner = owner; }

    public Integer getVersion(){ return version; }
    public void setVersion(Integer version){ this.version = version; }

}
