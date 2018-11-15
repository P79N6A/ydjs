package com.yd.ydsp.biz.pay.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.pay.YeePayService;
import com.yd.ydsp.biz.pay.model.YdPayResponse;
import com.yd.ydsp.biz.pay.model.YeePayConstant;
import com.yd.ydsp.biz.pay.model.YeePayRequestDO;
import com.yd.ydsp.biz.pay.yeepay.utils.AES;
import com.yd.ydsp.biz.pay.yeepay.utils.PaymobileUtils;
import com.yd.ydsp.biz.pay.yeepay.utils.RSA;
import com.yd.ydsp.biz.pay.yeepay.utils.RandomUtil;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.enums.OrderStatusEnum;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;
import com.yd.ydsp.common.utils.CollectionUtil;
import com.yd.ydsp.dal.entity.YdPayYeepayCallback;
import com.yd.ydsp.dal.mapper.YdPayYeepayCallbackMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author zengyixun
 * @date 17/12/12
 */
public class YeePayServiceImpl implements YeePayService {

    public static final Logger logger = LoggerFactory.getLogger(YeePayServiceImpl.class);

    @Resource
    private YdPayYeepayCallbackMapper ydPayYeepayCallbackMapper;
    @Resource
    private MqMessageService mqMessageService;

    @Override
    public YdPayResponse payMobileRequest(YeePayRequestDO yeePayRequestDO,PayOrderTypeEnum payOrderTypeEnum,String fcCallBakUrl) {
        YdPayResponse result = new YdPayResponse();
        try {
            yeePayRequestDO.setCallbackurl(DiamondYdSystemConfigHolder.getInstance().yeePayCallbackUrl.trim()+"?orderType="+payOrderTypeEnum.getType());
//            if(payOrderTypeEnum==PayOrderTypeEnum.C2B){
//                yeePayRequestDO.setFcallbackurl(fcCallBakUrl.trim()+yeePayRequestDO.getOrderid().trim());
//            }else {
//                yeePayRequestDO.setFcallbackurl(fcCallBakUrl.trim() + "?orderid=" + yeePayRequestDO.getOrderid());
//            }
            yeePayRequestDO.setFcallbackurl(fcCallBakUrl.trim() + "?orderid=" + yeePayRequestDO.getOrderid());
            TreeMap<String,Object> dataMap = (TreeMap)CollectionUtil.beanToMap(yeePayRequestDO);
            String sign = PaymobileUtils.buildSign(dataMap, DiamondYdSystemConfigHolder.getInstance().yeepayMerchantPrivateKey);
            dataMap.put("sign", sign);
            String jsonStr = JSON.toJSONString(dataMap);
            logger.info("yeePay Request map is : "+ jsonStr);
            String merchantAESKey = RandomUtil.getRandom(16);
            String data = AES.encryptToBase64(jsonStr, merchantAESKey);
            String encryptkey = RSA.encrypt(merchantAESKey, DiamondYdSystemConfigHolder.getInstance().yeepayPublicKey);
            TreeMap<String,Object> resultMapDec = PaymobileUtils.httpPost(DiamondYdSystemConfigHolder.getInstance().yeepayApi,yeePayRequestDO.merchantaccount,data,encryptkey);
            TreeMap<String,Object> resultMap = this.decrypt((String)resultMapDec.get("data"),(String)resultMapDec.get("encryptkey"));
            if(PaymobileUtils.checkSign(resultMap,DiamondYdSystemConfigHolder.getInstance().yeepayPublicKey)) {
                if (resultMap.containsKey("error_code") || resultMap.containsKey("error_msg")) {
                    result.setErrorCode((String)resultMap.get("error_code"));
                    result.setErrorMsg((String)resultMap.get("error_msg"));
                    result.setSuccess(false);
                } else {
                    result.setSuccess(true);
                    result.setOrderid((String)resultMap.get(YeePayConstant.ORDERID));
                    result.setPayorderid(Long.valueOf((String) resultMap.get(YeePayConstant.YB_ORDERID)));
                    result.setPayurl((String)resultMap.get(YeePayConstant.PAYURL));
                    result.setImghexstr((String) resultMap.get(YeePayConstant.IMGHEXSTR));
                    YdPayYeepayCallback payYeepayCallbackInfo = new YdPayYeepayCallback();
                    payYeepayCallbackInfo.setYborderid(result.getPayorderid());
                    payYeepayCallbackInfo.setOrderid(result.getOrderid());
                    payYeepayCallbackInfo.setAmount(yeePayRequestDO.amount);
                    payYeepayCallbackInfo.setPaystatus(0);
                    payYeepayCallbackInfo.setStatus(0);
                    payYeepayCallbackInfo.setPayurl(result.getPayurl());
                    payYeepayCallbackInfo.setOrdertype(payOrderTypeEnum.getType());
                    payYeepayCallbackInfo.setSign(sign);
                    try {
                        ydPayYeepayCallbackMapper.insert(payYeepayCallbackInfo);
                    }catch (Exception ex){
                        logger.error("YeePayRequest insert db is error: ",ex);
                    }
                }
            }else{
                result.setErrorMsg("签名校验错误！");
                result.setSuccess(false);
                logger.error("YeePayServiceImpl payMobileRequest sign is error :" +JSON.toJSONString(resultMap));
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            result.setSuccess(false);
            result.setErrorMsg(ex.getMessage());
            logger.error("YeePayServiceImpl payMobileRequest:" , ex);
        }
        return result;
    }

    @Override
    public Map<String, Object> queryPayOrderInfo(String orderid, PayOrderTypeEnum payOrderTypeEnum) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("success",false);
        try {
            TreeMap<String, Object> dataMap = new TreeMap<>();
            dataMap.put(YeePayConstant.MERCHANT_ACCOUNT, DiamondYdSystemConfigHolder.getInstance().yeepayMerchantaCcount);
            dataMap.put(YeePayConstant.ORDERID, orderid);
            String sign = PaymobileUtils.buildSign(dataMap, DiamondYdSystemConfigHolder.getInstance().yeepayMerchantPrivateKey);
            dataMap.put("sign", sign);
            String jsonStr = JSON.toJSONString(dataMap);
            logger.info("queryPayOrderInfo map is : " + jsonStr);
            String merchantAESKey = RandomUtil.getRandom(16);
            String data = AES.encryptToBase64(jsonStr, merchantAESKey);
            String encryptkey = RSA.encrypt(merchantAESKey, DiamondYdSystemConfigHolder.getInstance().yeepayPublicKey);
            TreeMap<String,Object> resultMapDec = PaymobileUtils.httpGet(DiamondYdSystemConfigHolder.getInstance().yeepayQueryOrderApi,DiamondYdSystemConfigHolder.getInstance().yeepayMerchantaCcount,data,encryptkey);
            if(resultMapDec==null){
                logger.info("queryPayOrderInfo resultMapDec is null! " );
            }else{
                jsonStr = JSON.toJSONString(resultMapDec);
                logger.info("queryPayOrderInfo resultMapDec is : " + jsonStr);
            }
            TreeMap<String,Object> resultMap = this.decrypt((String)resultMapDec.get("data"),(String)resultMapDec.get("encryptkey"));
            if(PaymobileUtils.checkSign(resultMap,DiamondYdSystemConfigHolder.getInstance().yeepayPublicKey)) {
                if (resultMap.containsKey("error_code") || resultMap.containsKey("error")) {
                    result = resultMap;
                    result.put("error_msg",resultMap.get("error"));
                    result.remove("error");
                    result.put("success",false);
                } else {
                    result = resultMap;
                    result.put("success",true);
                    result.remove("sign");
                }
            }else{
                result.put("error_msg","签名校验错误");
                result.put("success",false);
                logger.error("YeePayServiceImpl payMobileRequest sign is error :" +JSON.toJSONString(resultMap));
            }
        }catch (Exception ex){
            result.put("error_msg",ex.getMessage());
            result.put("success",false);
            logger.error("YeePayServiceImpl queryPayOrderInfo:" , ex);
        }
        return result;
    }

    @Override
    public String payCallback(String data, String encryptkey, PayOrderTypeEnum payOrderTypeEnum) {
        String result = "SUCCESS";

        try {
            TreeMap<String, Object> resultMap = this.decrypt(data, encryptkey);
            if (!DiamondYdSystemConfigHolder.getInstance().yeepayMerchantaCcount.trim().equals((String) resultMap.get(YeePayConstant.MERCHANT_ACCOUNT))) {
                logger.error("YeePayServiceImpl payCallback merchantaccount is error :" + JSON.toJSONString(resultMap));
                result = "merchantaccount is error";
                return result;
            }
            logger.info("YeePayServiceImpl payCallback data is:" + JSON.toJSONString(resultMap));
            String sign = (String) resultMap.get(YeePayConstant.SIGN);
            YdPayYeepayCallback payYeepayCallbackInfo;
            payYeepayCallbackInfo = ydPayYeepayCallbackMapper.selectByOrderId((String) resultMap.get(YeePayConstant.ORDERID));
            if (payYeepayCallbackInfo == null) {
                payYeepayCallbackInfo = new YdPayYeepayCallback();
                payYeepayCallbackInfo.setYborderid(Long.valueOf((String) resultMap.get(YeePayConstant.YB_ORDERID)));
                payYeepayCallbackInfo.setOrderid((String) resultMap.get(YeePayConstant.ORDERID));
            }

            payYeepayCallbackInfo.setBankcode((String) resultMap.get(YeePayConstant.BANKCODE));
            payYeepayCallbackInfo.setBank((String) resultMap.get(YeePayConstant.BANK));
            payYeepayCallbackInfo.setLastno((String) resultMap.get(YeePayConstant.LASTNO));
            if (resultMap.get(YeePayConstant.CARDTYPE) != null) {
                payYeepayCallbackInfo.setCardtype((Integer) resultMap.get(YeePayConstant.CARDTYPE));
            }
            payYeepayCallbackInfo.setAmount((Integer) resultMap.get(YeePayConstant.AMOUNT));
            payYeepayCallbackInfo.setPaystatus((Integer) resultMap.get(YeePayConstant.STATUS));
            payYeepayCallbackInfo.setOrdertype(payOrderTypeEnum.getType());
            payYeepayCallbackInfo.setSign(sign);
            if (!PaymobileUtils.checkSign(resultMap, DiamondYdSystemConfigHolder.getInstance().yeepayPublicKey)) {
                logger.error("YeePayServiceImpl payCallback sign is error :" + JSON.toJSONString(resultMap));
                result = "SIGNERROR";
                return result;
            } else {

                payYeepayCallbackInfo.setStatus(0);

            }
            if (payYeepayCallbackInfo.getId() == null) {
                if (!(ydPayYeepayCallbackMapper.insert(payYeepayCallbackInfo) > 0)) {
                    logger.error("yeepay callback insertdb is error:" + payYeepayCallbackInfo.getOrderid());
                }
            } else {
                if (!(ydPayYeepayCallbackMapper.updateByPrimaryKey(payYeepayCallbackInfo) > 0)) {
                    logger.error("yeepay callback updatedb is error:" + payYeepayCallbackInfo.getOrderid());
                }
            }
            /**
             * 以下发消息去更新订单状态
             */
            Map<String,String> paySuccessMessage = new HashMap<>();
            paySuccessMessage.put(Constant.ORDERID,payYeepayCallbackInfo.getOrderid());
            paySuccessMessage.put(Constant.PAYORDERTYPE,payOrderTypeEnum.getName());
            paySuccessMessage.put(Constant.ORDERTYPE, OrderStatusEnum.PAYFINISH.getName());
            mqMessageService.sendYdPayMessage(payYeepayCallbackInfo.getOrderid(),JSON.toJSONString(paySuccessMessage));

        }catch (YdException yex){
            logger.error("",yex);
        }catch (Exception ex){
            logger.error("yeepay callback is error:",ex);
            result = "Fail";
        }finally {
            logger.info("yeepay callback payOrderTypeEnum is :"+payOrderTypeEnum.getName());
            logger.info("yeepay callback data is :"+data);
            logger.info("yeepay callback encryptkey is :"+encryptkey);
            return result;

        }


    }


    /**
     * 解密码方法
     * @param data
     * @param encryptkey
     * @return
     */
    private TreeMap<String,Object> decrypt(String data,String encryptkey) throws Exception {
        String yeepayAESKey = RSA.decrypt(encryptkey, DiamondYdSystemConfigHolder.getInstance().yeepayMerchantPrivateKey);
        String decryptData = AES.decryptFromBase64(data, yeepayAESKey);
        return JSON.parseObject(decryptData,TreeMap.class);
    }

}
