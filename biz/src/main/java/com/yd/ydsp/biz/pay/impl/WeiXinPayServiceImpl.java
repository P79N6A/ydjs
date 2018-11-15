package com.yd.ydsp.biz.pay.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondWeiXinInfoConfigHolder;
import com.yd.ydsp.biz.openshop.ShopCashRechargeService;
import com.yd.ydsp.biz.openshop.ShopOrderService;
import com.yd.ydsp.biz.pay.WeiXinPayService;
import com.yd.ydsp.biz.pay.model.WeiXinBizPayQrcodePequestDO;
import com.yd.ydsp.biz.pay.model.WeiXinOrderQueryPequestDO;
import com.yd.ydsp.biz.pay.model.WeiXinPayRequestDO;
import com.yd.ydsp.biz.utils.XmlUtil;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.BillTypeEnum;
import com.yd.ydsp.common.enums.PayTypeEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.lang.Util;
import com.yd.ydsp.common.utils.*;
import com.yd.ydsp.dal.entity.YdShopConsumerOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.*;

public class WeiXinPayServiceImpl implements WeiXinPayService {

    public static final Logger logger = LoggerFactory.getLogger(WeiXinPayServiceImpl.class);

    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private ShopCashRechargeService shopCashRechargeService;

    /**
     * 引灯作为微信服务商的appid
     */
    private String ydjsAppId;
    /**
     * 引灯作为微信支付服务商的商户号
     */
    private String ydjsMchId;
    /**
     * 引灯商家端公众号支付商户号
     */
    private String yd2bMchId;

    public void setYdjsAppId(String ydjsAppId) {
        this.ydjsAppId = ydjsAppId;
    }

    public void setYdjsMchId(String ydjsMchId) {
        this.ydjsMchId = ydjsMchId;
    }

    public void setYd2bMchId(String yd2bMchId) {
        this.yd2bMchId = yd2bMchId;
    }

    protected  String sign(Map<String,Object> requestMap){
        String result = null;
        if(requestMap==null){
            return result;
        }

        if(requestMap.containsKey("sign")){
            requestMap.remove("sign");
        }

        //valueSet
        Collection<String> keySet = requestMap.keySet();
        List<String> list = new ArrayList<String>(keySet);
        Collections.sort(list);

        //拼凑签名字符串
        StringBuffer signStr = new StringBuffer();
        for(String key: list){
            if(requestMap.get(key)==null){
                continue;
            }
            String tmepStr = ""+requestMap.get(key);
            if(StringUtil.isNotEmpty(tmepStr)) {
                signStr.append(key+"="+requestMap.get(key)+"&");
            }
        }
        signStr.append("key="+DiamondWeiXinInfoConfigHolder.getInstance().getYdjsPaySecretKey());
//        System.out.println(signStr);

        //MD5签名
        result = EncryptionUtil.md5Hex(signStr.toString()).toUpperCase();

        return result;

    }

    protected Map<String,Object> singWithJs(String appid,String prepayId){
        Map<String,Object> result = new HashMap<>();
        String timeStamp = String.valueOf(new Date().getTime()/1000);
        String nonceStr = RandomUtil.getSNCode(TypeEnum.OTHER)+RandomUtil.getNotSimple(10);
        String signStr = "appId="+appid+"&nonceStr="+nonceStr+"&package=prepay_id="+prepayId+"&signType=MD5&timeStamp="+timeStamp+"&key="+DiamondWeiXinInfoConfigHolder.getInstance().getYdjsPaySecretKey();
        String sign = EncryptionUtil.md5Hex(signStr.toString()).toUpperCase();
        result.put("appId",appid);
        result.put("timeStamp",timeStamp);
        result.put("nonceStr",nonceStr);
        result.put("package","prepay_id="+prepayId);
        result.put("signType","MD5");
        result.put("paySign",sign);
        return result;
    }

    private String callPay(WeiXinPayRequestDO weiXinPayRequestDO) throws Exception {
        if(weiXinPayRequestDO==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "支付请求数据不能为空！");
        }
        if(StringUtil.isEmpty(weiXinPayRequestDO.getAppid())) {
            weiXinPayRequestDO.setAppid(this.ydjsAppId);
        }
        if(StringUtil.isEmpty(weiXinPayRequestDO.getMch_id())) {
            weiXinPayRequestDO.setMch_id(this.ydjsMchId);
        }
        weiXinPayRequestDO.setNonce_str(RandomUtil.getSNCode(TypeEnum.OTHER));
        weiXinPayRequestDO.setTime_start(DateUtils.date2String2(new Date()));
        weiXinPayRequestDO.setTime_expire(DateUtils.date2String2(DateUtils.plusDay(new Date(),1)));
        weiXinPayRequestDO.setNotify_url(DiamondWeiXinInfoConfigHolder.getInstance().getWeixinPayCallBackPreUrl().trim()+weiXinPayRequestDO.getOut_trade_no().trim());

        Map<String,Object> requestMap = JSON.parseObject(JSON.toJSONString(weiXinPayRequestDO),Map.class);
        String sign = this.sign(requestMap);
        if(StringUtil.isEmpty(sign)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "签名错误！");
        }
        weiXinPayRequestDO.setSign(sign);
        String requestBody = XmlUtil.getInstance().getWeixinPayRequestData(weiXinPayRequestDO);
//        logger.info("callPrePay requestBody is :"+requestBody);
        requestBody = new String(requestBody.toString().getBytes(), "ISO8859-1");
        String resultStr = HttpUtil.postJson("https://api.mch.weixin.qq.com/pay/unifiedorder",requestBody);
        return resultStr;
    }

    @Override
    public Map<String, Object> callPrePay(WeiXinPayRequestDO weiXinPayRequestDO) throws Exception {

        Map<String,Object> result = new HashMap<>();
        result.put("success",false);
        String resultStr = this.callPay(weiXinPayRequestDO);
//        logger.info("callPrePay result : "+resultStr);
        Map<String,Object> resultMap = XmlUtil.getInstance().xml2map(resultStr);

//        logger.info("resultMap is :"+JSON.toJSONString(resultMap));
        if(resultMap.containsKey("return_code")){
            String returnCode = (String)resultMap.get("return_code");
            if(resultMap.containsKey("result_code")) {
                String resultCode = (String) resultMap.get("result_code");
                if (StringUtil.equals(returnCode.toLowerCase().trim(), "success") && StringUtil.equals(resultCode.trim().toLowerCase(), "success")) {
                    if(StringUtil.isNotEmpty(weiXinPayRequestDO.getSub_appid())) {
                        result = this.singWithJs(weiXinPayRequestDO.getSub_appid(), (String) resultMap.get("prepay_id"));
                    }else {
                        result = this.singWithJs(weiXinPayRequestDO.getAppid(), (String) resultMap.get("prepay_id"));
                    }
                    result.put("success", true);
//                result.put(Constant.PREPAYID,resultMap.get("prepay_id"));
                    if (resultMap.containsKey("code_url")) {
                        result.put(Constant.CODEURL, resultMap.get("code_url"));
                    }
                } else {
                    if(StringUtil.equals(returnCode.toUpperCase(),"FAIL")){
                        if(resultMap.containsKey("err_code_des")){
                            throw new YdException((String) resultMap.get("err_code"), (String) resultMap.get("err_code_des"));
                        }else {
                            throw new YdException((String) resultMap.get("return_code"), (String) resultMap.get("return_msg"));
                        }
                    }
                }
            }
            if(StringUtil.equals(returnCode.toUpperCase(),"FAIL")){
                if(resultMap.containsKey("return_msg")){
                    throw new YdException((String) resultMap.get("return_code"), (String) resultMap.get("return_msg"));
                }else {
                    throw new YdException((String) resultMap.get("err_code"), (String) resultMap.get("err_code_des"));
                }
            }
        }

        return result;
    }

    @Override
    public Map<String, Object> microPay(WeiXinPayRequestDO weiXinPayRequestDO) throws Exception {
        if(weiXinPayRequestDO==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "支付请求数据不能为空！");
        }
        if(StringUtil.isEmpty(weiXinPayRequestDO.getAppid())) {
            weiXinPayRequestDO.setAppid(this.ydjsAppId);
        }
        if(StringUtil.isEmpty(weiXinPayRequestDO.getMch_id())) {
            weiXinPayRequestDO.setMch_id(this.ydjsMchId);
        }
        weiXinPayRequestDO.setNonce_str(RandomUtil.getSNCode(TypeEnum.OTHER));
        weiXinPayRequestDO.setTime_start(DateUtils.date2String2(new Date()));
        weiXinPayRequestDO.setTime_expire(DateUtils.date2String2(DateUtils.plusDay(new Date(),1)));
//        weiXinPayRequestDO.setNotify_url(DiamondWeiXinInfoConfigHolder.getInstance().getWeixinPayCallBackPreUrl().trim()+weiXinPayRequestDO.getOut_trade_no().trim());

        Map<String,Object> requestMap = JSON.parseObject(JSON.toJSONString(weiXinPayRequestDO),Map.class);
        String sign = this.sign(requestMap);
        if(StringUtil.isEmpty(sign)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "签名错误！");
        }
        weiXinPayRequestDO.setSign(sign);
        String requestBody = XmlUtil.getInstance().getWeixinPayRequestData(weiXinPayRequestDO);
        logger.info("microPay requestBody is :"+requestBody);
        requestBody = new String(requestBody.toString().getBytes(), "ISO8859-1");
        String resultStr = HttpUtil.postJson("https://api.mch.weixin.qq.com/pay/micropay",requestBody);
        Map<String,Object> resultMap = XmlUtil.getInstance().xml2map(resultStr);
        logger.info("resultMap is :"+JSON.toJSONString(resultMap));
        if(resultMap.containsKey("return_code")){
            String returnCode = (String)resultMap.get("return_code");
            if(resultMap.containsKey("result_code")) {
                String resultCode = (String) resultMap.get("result_code");
                if (StringUtil.equals(returnCode.toLowerCase().trim(), "success") && StringUtil.equals(resultCode.trim().toLowerCase(), "success")) {
                    /**
                     * 签名校验
                     */
                    String weixinSign = (String) resultMap.get("sign");
                    String signRespone = this.sign(resultMap);
                    if(!StringUtil.equals(weixinSign,signRespone)){
                        logger.error(weiXinPayRequestDO.getOut_trade_no()+":微信刷卡支付返回值签名错误!");
                        resultMap.put("success", false);
                        resultMap.put("trade_state_desc","支付签名异常，请稍后再试！");
                    }else {
                        resultMap.put("success", true);
                        resultMap.put("trade_state_desc","支付成功！");
                    }
                }
            }
            if(StringUtil.equals(returnCode.toUpperCase(),"FAIL")){
                if(resultMap.containsKey("err_code_des")){
                    if(StringUtil.equals((String)resultMap.get("err_code_des"),"201 商户订单号重复")){
                        resultMap.put("trade_state_desc","商户订单号重复,请使用其它方式支付！");
                    }else {
                        resultMap.put("trade_state_desc",(String)resultMap.get("err_code_des"));
                    }
                }else {
                    resultMap.put("trade_state_desc","支付异常，请稍后再试！");
                }
            }
        }

        if(!resultMap.containsKey("success")){
            resultMap.put("success",false);
        }

        return resultMap;
    }

    @Override
    public Map<String, Object> queryOrderByWeiXin(String appid,String mchId,String orderid) throws Exception {
        String subAppid = ""+appid;
        String subMchId = ""+mchId;
        if(StringUtil.contains(
                DiamondWeiXinInfoConfigHolder.getInstance().getPublicMchIds(),
                mchId)){
            subAppid = null;
            subMchId = null;
        }else {
            appid = this.ydjsAppId;
            mchId = this.ydjsMchId;
        }
        WeiXinOrderQueryPequestDO queryPequestDO = new WeiXinOrderQueryPequestDO();
        queryPequestDO.setAppid(appid);
        queryPequestDO.setSub_appid(subAppid);
        queryPequestDO.setMch_id(mchId);
        queryPequestDO.setSub_mch_id(subMchId);
        queryPequestDO.setOut_trade_no(orderid);
        queryPequestDO.setNonce_str(RandomUtil.getSNCode(TypeEnum.OTHER));
        Map<String,Object> requestMap = JSON.parseObject(JSON.toJSONString(queryPequestDO),Map.class);
        String sign = this.sign(requestMap);
        queryPequestDO.setSign(sign);

        String requestBody = XmlUtil.getInstance().getWeiXinOrderQueryPequestData(queryPequestDO);

        String resultStr = HttpUtil.postJson("https://api.mch.weixin.qq.com/pay/orderquery",requestBody);
        logger.info("queryMoneyOrderByWeiXin result is:"+resultStr);
        Map<String,Object> resultMap = XmlUtil.getInstance().xml2map(resultStr);
        if(resultMap.containsKey("return_code")) {
            String returnCode = (String) resultMap.get("return_code");
            if (resultMap.containsKey("result_code")) {
                String resultCode = (String) resultMap.get("result_code");
                if (StringUtil.equals(returnCode.toLowerCase().trim(), "success") && StringUtil.equals(resultCode.trim().toLowerCase(), "success")) {
                    /**
                     * 签名校验
                     */
                    String weixinSign = (String) resultMap.get("sign");
                    String signRespone = this.sign(resultMap);
                    if(!StringUtil.equals(weixinSign,signRespone)){
                        throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), orderid+"微信支付查询签名错误!");
                    }
                    resultMap.put("success",true);

                }
            }
        }
        if(!resultMap.containsKey("success")){
            resultMap.put("success",false);
        }
        return resultMap;
    }

    @Override
    public String calbackResultPay(String orderid, String resultXml) {
        String result = "<xml>" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>" +
                "  <return_msg><![CDATA[OK]]></return_msg>" +
                "</xml>";

        try {
            /**
             * 判断resultXml的合法性与是否支付成功
             */

            Map<String,Object> responeMap = XmlUtil.getInstance().xml2map(resultXml);
            logger.info("calbackResultPay is: "+resultXml);
            if(StringUtil.equals(((String)responeMap.get("return_code")).toUpperCase(),"SUCCESS")){
                if(StringUtil.equals(((String)responeMap.get("result_code")).toUpperCase(),"SUCCESS")){
                    /**
                     * 签名验证
                     */
                    String weixinSign = (String) responeMap.get("sign");
                    String sign = this.sign(responeMap);
                    if(!StringUtil.equals(weixinSign,sign)){
                        throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), orderid+"微信支付签名错误!");
                    }
                    if(responeMap.containsKey("sub_mch_id")){
                        responeMap.put(Constant.PAYTYPE, PayTypeEnum.WEIXINS.getType());
                    }else {
                        responeMap.put(Constant.PAYTYPE, PayTypeEnum.WEIXIN2B.getType());
                    }
                    int payFee = Integer.valueOf((String) responeMap.get("cash_fee"));
                    /**
                     * 判断支付回调的类型
                     */
                    TypeEnum typeEnum = TypeEnum.getTypeOfSN(orderid);
                    if(typeEnum==TypeEnum.CASHRECHARGE){
                        /**
                         * 链贝充值支付成功，发消息处理充值订单
                         */
                        shopCashRechargeService.sendNewBillMsg(null,orderid, BillTypeEnum.IN.getType());
                    }else {
                        /**
                         * 交易订单
                         */
                        boolean isOk = shopOrderService.orderPayFinish((String) responeMap.get("out_trade_no"), payFee,
                                responeMap);
                        if (!isOk) {
                            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), orderid + "订单状态更新失败!");
                        }
                    }

                }
            }

        }catch (YdException yex){
            logger.error("calbackResultPay:",yex);
            result = "<xml>" +
                    "  <return_code><![CDATA[FAIL]]></return_code>" +
                    "  <return_msg><![CDATA["+yex.getMessage()+"]]></return_msg>" +
                    "</xml>";
        }catch (Exception ex){
            logger.error("calbackResultPay:",ex);
            result = "<xml>" +
                    "  <return_code><![CDATA[FAIL]]></return_code>" +
                    "  <return_msg><![CDATA[ERROR]]></return_msg>" +
                    "</xml>";
        }

        return result;

    }

    @Override
    public String bizPayCallBack(String resultXml) {
        String result = "";

        try {
            Map<String, Object> bizCallBackMap = XmlUtil.getInstance().xml2map(resultXml);
            logger.info("bizPayCallBack is: " + resultXml);
            /**
             * 签名验证
             */
            String weixinSign = (String) bizCallBackMap.get("sign");
            String sign = this.sign(bizCallBackMap);
            String orderid = (String)bizCallBackMap.get("product_id");
            if(!StringUtil.equals(weixinSign,sign)){
                logger.error("orderid is :"+orderid);
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), new String("微信扫码支付签名错误!".getBytes(), "ISO8859-1"));
            }

            /**
             * 查订单信息
             */
            YdShopConsumerOrder consumerOrder = shopOrderService.queryUserOrder(orderid);
            if(consumerOrder==null){
                logger.error("orderid is :"+orderid);
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), new String("微信扫码支付订单不存在!".getBytes(), "ISO8859-1"));
            }

            if(consumerOrder.getIsPay().intValue()>=0){
                logger.error("orderid is :"+orderid);
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), new String("订单已经付过款!".getBytes(), "ISO8859-1"));
            }
            if(consumerOrder.getStatus().intValue()<0||consumerOrder.getStatus().intValue()>10){
                logger.error("orderid is :"+orderid);
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), new String("订单当前状态已经不支持付款!".getBytes(), "ISO8859-1"));
            }
            /**
             * 付款金额，单位分
             */
            Integer payMoney = AmountUtils.changeY2F(consumerOrder.getTotalAmount());
            String mchId = ((String)bizCallBackMap.get("mch_id")).trim();
            PayTypeEnum payTypeEnum = PayTypeEnum.WEIXINS;
            if(StringUtil.equals(mchId,"1489419802")) {
                payTypeEnum = PayTypeEnum.WEIXIN2B;
            }
            String appid = (String)bizCallBackMap.get("appid");
            String openid = (String)bizCallBackMap.get("openid");
            WeiXinPayRequestDO weiXinPayRequestDO = new WeiXinPayRequestDO();
            if(payTypeEnum==PayTypeEnum.WEIXINS) {
                weiXinPayRequestDO.setSub_appid(appid);
                weiXinPayRequestDO.setSub_mch_id(mchId);
                weiXinPayRequestDO.setSub_openid(openid);
            }else if(payTypeEnum==PayTypeEnum.WEIXIN2B) {
                weiXinPayRequestDO.setAppid(appid);
                weiXinPayRequestDO.setMch_id(mchId);
                weiXinPayRequestDO.setOpenid(openid);
            }
            weiXinPayRequestDO.setBody("引灯智能店铺-"+consumerOrder.getOrderName());
            weiXinPayRequestDO.setOut_trade_no(orderid);
            weiXinPayRequestDO.setTotal_fee(AmountUtils.changeY2F(consumerOrder.getTotalAmount()));
            InetAddress addr = InetAddress.getLocalHost();
            String ip=addr.getHostAddress().toString(); //获取本机ip
            weiXinPayRequestDO.setSpbill_create_ip(ip);
            weiXinPayRequestDO.setTrade_type("NATIVE");
            result = this.callPay(weiXinPayRequestDO);
        }catch (YdException yex){
            logger.error("calbackResultPay:",yex);
            result = "<xml>" +
                    "  <return_code><![CDATA[FAIL]]></return_code>" +
                    "  <return_msg><![CDATA["+yex.getMessage()+"]]></return_msg>" +
                    "</xml>";
        }catch (Exception ex){
            logger.error("calbackResultPay:",ex);
            result = "<xml>" +
                    "  <return_code><![CDATA[FAIL]]></return_code>" +
                    "  <return_msg><![CDATA[未知错误]]></return_msg>" +
                    "</xml>";
        }
        return result;
    }

    @Override
    public String createBizPayQrCode(String appid,String mchId, String orderid) throws Exception {
        String subAppid = ""+appid;
        String subMchId = ""+mchId;
        if(StringUtil.contains(
                DiamondWeiXinInfoConfigHolder.getInstance().getPublicMchIds(),
                mchId)){
            subAppid = null;
            subMchId = null;
        }else {
            appid = this.ydjsAppId;
            mchId = this.ydjsMchId;
        }

        String nonceStr = RandomUtil.getSNCode(TypeEnum.OTHER);
        Map<String,Object> packageParams = new HashMap<>();
        if(StringUtil.isEmpty(subAppid)){
            packageParams.put("appid",appid);
        }else {
            packageParams.put("appid",subAppid);
        }
        if(StringUtil.isEmpty(subMchId)){
            packageParams.put("mch_id",mchId);
        }else {
            packageParams.put("mch_id",subMchId);
        }
        packageParams.put("product_id",orderid);
        String timeStamp = String.valueOf(new Date().getTime()/1000);
        packageParams.put("time_stamp",timeStamp);
        packageParams.put("nonce_str",nonceStr);
        String packageSign = this.sign(packageParams);

        StringBuffer longQrCode = new StringBuffer();
        for(String key: packageParams.keySet()){
            String tmepStr = ""+packageParams.get(key);
            if(StringUtil.isNotEmpty(tmepStr)) {
                longQrCode.append(key+"="+packageParams.get(key)+"&");
            }
        }
        longQrCode.append("sign="+packageSign);
        String longQrCodeStr = URLEncoder.encode("weixin://wxpay/bizpayurl?"+longQrCode.toString().trim(),"UTF-8");

        logger.info("longQrCodeStr is :"+longQrCodeStr);

        WeiXinBizPayQrcodePequestDO weiXinBizPayQrcodePequestDO = new WeiXinBizPayQrcodePequestDO();
        weiXinBizPayQrcodePequestDO.setAppid(appid);
        weiXinBizPayQrcodePequestDO.setSub_appid(subAppid);
        weiXinBizPayQrcodePequestDO.setMch_id(mchId);
        weiXinBizPayQrcodePequestDO.setSub_mch_id(subMchId);
        weiXinBizPayQrcodePequestDO.setNonce_str(nonceStr);
        weiXinBizPayQrcodePequestDO.setLong_url(longQrCodeStr);
        Map<String,Object> requestMap = JSON.parseObject(JSON.toJSONString(weiXinBizPayQrcodePequestDO),Map.class);
        String sign = this.sign(requestMap);

        weiXinBizPayQrcodePequestDO.setSign(sign);

        String requestBody = XmlUtil.getInstance().getWeiXinBizPayQrcodePequestData(weiXinBizPayQrcodePequestDO);
//        requestBody = new String(requestBody.toString().getBytes(), "ISO8859-1");
        logger.info("requestBody is :"+requestBody);
        String resultStr = HttpUtil.postJson("https://api.mch.weixin.qq.com/tools/shorturl",requestBody);
        logger.info("createBizPayQrCode result is:"+resultStr);
        Map<String,Object> resultMap = XmlUtil.getInstance().xml2map(resultStr);
        if(resultMap.containsKey("return_code")) {
            String returnCode = (String) resultMap.get("return_code");
            if (resultMap.containsKey("result_code")) {
                String resultCode = (String) resultMap.get("result_code");
                if (StringUtil.equals(returnCode.toLowerCase().trim(), "success") && StringUtil.equals(resultCode.trim().toLowerCase(), "success")) {
                    /**
                     * 签名校验
                     */
                    String weixinSign = (String) resultMap.get("sign");
                    String signRespone = this.sign(resultMap);
                    if(!StringUtil.equals(weixinSign,signRespone)){
                        throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), orderid+"微信支付签名错误!");
                    }

                    return (String)resultMap.get("short_url");

                }
            }
        }


        return null;
    }

    public static void main(String[] args) throws Exception {
        WeiXinPayRequestDO weiXinPayRequestDO = new WeiXinPayRequestDO();
        weiXinPayRequestDO.setMch_id("242525235");
        weiXinPayRequestDO.setTrade_type("JSAPI");
        weiXinPayRequestDO.setBody("afafaf");
        weiXinPayRequestDO.setAppid("99999");
        weiXinPayRequestDO.setDevice_info("kkkkkkk");
        weiXinPayRequestDO.setNonce_str(RandomUtil.getSNCode(TypeEnum.OTHER));
        Map<String,Object> requestMap = JSON.parseObject(JSON.toJSONString(weiXinPayRequestDO),Map.class);
        WeiXinPayServiceImpl weiXinPayService = new WeiXinPayServiceImpl();
        System.out.println(weiXinPayService.sign(requestMap));

    }

}
