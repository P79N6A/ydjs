package com.yd.ydsp.biz.message.impl;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import com.yd.ydsp.biz.message.MnsService;
import com.yd.ydsp.common.constants.GlobalConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/1
 */
public class MnsServiceImpl implements MnsService {

    /**
     * MNS AK
     */
    String accessKeyId;
    String accessKeySecret;
    String endpoint;
    CloudAccount account;
    MNSClient client;
    CloudQueue queueSMS;

    public void setAccessKeyId(String accessKeyId){ this.accessKeyId = accessKeyId; }
    public void setAccessKeySecret(String accessKeySecret){ this.accessKeySecret = accessKeySecret; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public void init(){
        this.account = new CloudAccount(this.accessKeyId,accessKeySecret,endpoint);
        this.client = account.getMNSClient();
        this.queueSMS = this.client.getQueueRef("sms");
    }

    public void destroy(){
        this.client.close();
    }

    @Override
    public Map<String,String> receiveMsg(String queueType) {

        Message popMsg = null;
        if(GlobalConstant.SMS_LOGIN.equals(queueType)) {
            popMsg = queueSMS.popMessage();
        }
        if(popMsg ==null){
            return null;
        }
        Map<String,String> result = new HashMap<>();
        if(popMsg != null){
            result.put("id",popMsg.getMessageId());
            result.put("handle",popMsg.getReceiptHandle());
            result.put("body",popMsg.getMessageBody());
            return result;
        }
        return null;

    }

    @Override
    public void deleteMsg(String queueType, String handle) {
        if(GlobalConstant.SMS_LOGIN.equals(queueType)){
            queueSMS.deleteMessage(handle);
        }
    }

    @Override
    public String sendMsg(String queueType,String body) {
        if(GlobalConstant.SMS_LOGIN.equals(queueType)){
            Message message = new Message();
            message.setMessageBody(body);
            Message putMsg = queueSMS.putMessage(message);
            return putMsg.getMessageId();
        }
        return null;
    }
}
