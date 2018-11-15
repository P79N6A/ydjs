package com.yd.ydsp.biz.message.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yd.ydsp.biz.message.SmsMessageService;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * @author zengyixun
 * @date 17/11/29
 */
public class SmsMessageServiceImpl implements SmsMessageService {

    private static final Logger logger = LoggerFactory.getLogger(SmsMessageServiceImpl.class);

    @Resource
    RedisManager redisManager;

    SendSmsRequest sendSmsRequest;

    IAcsClient acsClient;

    /**
     * 短信AK
     */
    String accessKeyId;
    String accessKeySecret;

    final static String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
    final static String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）

    public void setAccessKeyId(String accessKeyId){ this.accessKeyId = accessKeyId; }
    public void setAccessKeySecret(String accessKeySecret){ this.accessKeySecret = accessKeySecret; }


    public void init() throws ClientException {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        this.acsClient = new DefaultAcsClient(profile);
        this.sendSmsRequest = new SendSmsRequest();
    }

//    @Async
    @Override
    public boolean loginSmsMessage(String phoneNumber, String code) {

        boolean result = true;
        try {
            sendSmsRequest.setMethod(MethodType.POST);
            sendSmsRequest.setPhoneNumbers(phoneNumber);
            //必填:短信签名-可在短信控制台中找到
            sendSmsRequest.setSignName("引灯科技");
            //必填:短信模板-可在短信控制台中找到
            sendSmsRequest.setTemplateCode("SMS_89850068");
            sendSmsRequest.setTemplateParam("{\"code\":\"" + code + "\"}");
            SendSmsResponse sendSmsResponse = this.acsClient.getAcsResponse(sendSmsRequest);
            if (!(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK"))) {
            /* 请求失败 */
                logger.error("sms-login is error,phonenuber is " + phoneNumber + " , error code is : " + sendSmsResponse.getCode());
            }
        }catch (ClientException ex){
            logger.error("sms-login is error: ",ex);
            result = false;
        }
        return result;

    }

//    @Async
    @Override
    public boolean identitySmsMessage(String phoneNumber, String code) {

        boolean result = true;
        try {
            sendSmsRequest.setMethod(MethodType.POST);
            sendSmsRequest.setPhoneNumbers(phoneNumber);
            //必填:短信签名-可在短信控制台中找到
            sendSmsRequest.setSignName("引灯科技");
            //必填:短信模板-可在短信控制台中找到
            sendSmsRequest.setTemplateCode("SMS_89850070");
            sendSmsRequest.setTemplateParam("{\"code\":\"" + code + "\"}");
            SendSmsResponse sendSmsResponse = this.acsClient.getAcsResponse(sendSmsRequest);
            if (!(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK"))) {
            /* 请求失败 */
                logger.error("sms-identity is error,phonenuber is " + phoneNumber + " , error code is : " + sendSmsResponse.getCode());
                result = false;
            }
        }catch (ClientException ex){
            logger.error("sms-identity is error: ",ex);
            result = false;
        }
        return result;

    }

    @Override
    public boolean setIdentityCode(String phoneNumber) {
        boolean result = true;
        try{
            byte[] bytes = redisManager.get(SerializeUtils.serialize(phoneNumber + "identitysms"));
            if(SerializeUtils.isEmpty(bytes)){
                Integer code = RandomUtil.getNotSimple(6);
                if(this.identitySmsMessage(phoneNumber,code.toString())){
                    redisManager.set(SerializeUtils.serialize(phoneNumber+"identitysms"),SerializeUtils.serialize(code.toString()),300);
                }else {
                    result = false;
                }
            }

        }catch (Exception ex){
            logger.error("",ex);
            result = false;
        }
        return result;
    }

    @Override
    public boolean isIdentityCode(String phoneNumber, String code) {
        boolean result = true;
        try {
            byte[] bytes = redisManager.get(SerializeUtils.serialize(phoneNumber + "identitysms"));
            if(SerializeUtils.isEmpty(bytes)){
                return false;
            }
            String targetCode = (String)SerializeUtils.deserialize(bytes);
            if(code.equals(targetCode)){
                redisManager.del(SerializeUtils.serialize(phoneNumber + "identitysms"));
                result = true;
            }else{
                result = false;
            }
        }catch (Exception ex){

            logger.error("",ex);
            result = false;
        }
        return result;
    }

    @Override
    public boolean setLoginIdentityCode(String phoneNumber) {
        boolean result = true;
        try{
            byte[] bytes = redisManager.get(SerializeUtils.serialize(phoneNumber + "loginsms"));
            if(SerializeUtils.isEmpty(bytes)){
                Integer code = RandomUtil.getNotSimple(6);
                if(this.loginSmsMessage(phoneNumber,code.toString())){
                    redisManager.set(SerializeUtils.serialize(phoneNumber+"loginsms"),SerializeUtils.serialize(code.toString()),300);
                }else {
                    result = false;
                }
            }

        }catch (Exception ex){
            logger.error("",ex);
            result = false;
        }
        return result;
    }

    @Override
    public boolean isLoginIdentityCode(String phoneNumber, String code) {
        boolean result = true;
        try {
            byte[] bytes = redisManager.get(SerializeUtils.serialize(phoneNumber + "loginsms"));
            if(SerializeUtils.isEmpty(bytes)){
                return false;
            }
            String targetCode = (String)SerializeUtils.deserialize(bytes);
            if(code.equals(targetCode)){
                redisManager.del(SerializeUtils.serialize(phoneNumber + "loginsms"));
                result = true;
            }else{
                result = false;
            }
        }catch (Exception ex){

            logger.error("",ex);
            result = false;
        }
        return result;
    }

}
