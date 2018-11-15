package com.yd.ydsp.biz.message.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.dal.entity.YdMqerrorlog;
import com.yd.ydsp.dal.mapper.YdMqerrorlogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public class MqMessageServiceImpl implements MqMessageService {

    public static final Logger logger = LoggerFactory.getLogger(MqMessageServiceImpl.class);

    @Resource
    private Producer producerMq;

    @Resource
    private OrderProducer producerOrderMq;
    @Resource
    private YdMqerrorlogMapper ydMqerrorlogMapper;

    /**
     * 接收到易宝支付成功的回调后，发消息进行订单状态的后续处理
     * @param keyid
     * @param body json字串
     * @return
     */
    @Override
    public String sendYdPayMessage(String keyid, String body) {
        try {
            Message message = new Message( //
                    // Message所属的Topic
                    "ydmq",
                    // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                    "ydpay",
                    // Message Body 可以是任何二进制形式的数据， MQ不做任何干预
                    // 需要Producer与Consumer协商好一致的序列化和反序列化方式
                    SerializeUtils.serialize(body));
            // 设置代表消息的业务关键属性，请尽可能全局唯一
            // 以方便您在无法正常收到消息情况下，可通过MQ 控制台查询消息并补发
            // 注意：不设置也不会影响消息正常收发
            message.setKey(keyid);
            SendResult sendResult = producerMq.send(message);
            assert sendResult != null;
            return sendResult.getMessageId();
        }catch (Exception ex){
            if(this.saveSendMessageErrorLog("ydpay",keyid,body)<=0){
                logger.error("sendmsg error tag ydpay keyid is "+keyid+" body is :"+body);
            }
            logger.error("",ex);
        }
        return null;

    }

    /**
     * 订单创建后，发一个延时消息去处理到时没有付款的订单的关闭操作
     * @param keyid
     * @param body
     * @param timeout
     * @param orderTypeEnum 订单的类型
     * @return
     */
    @Override
    public String sendOrderStatusChange(String keyid, String body, long timeout, PayOrderTypeEnum orderTypeEnum) {
        String tag = "ordertimeout2c";
        if(orderTypeEnum==PayOrderTypeEnum.CPORDER){
            tag = "ordertimeoutcp";
        }
        try {

            Message message = new Message( //
                    // Message所属的Topic
                    "ydmq",
                    // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                    tag,
                    // Message Body 可以是任何二进制形式的数据， MQ不做任何干预
                    // 需要Producer与Consumer协商好一致的序列化和反序列化方式
                    SerializeUtils.serialize(body));
            // 设置代表消息的业务关键属性，请尽可能全局唯一
            // 以方便您在无法正常收到消息情况下，可通过MQ 控制台查询消息并补发
            // 注意：不设置也不会影响消息正常收发
            message.setKey(keyid);
            //消息延时时间
            message.setStartDeliverTime(System.currentTimeMillis() + timeout);
            SendResult sendResult = producerMq.send(message);
            assert sendResult != null;
            return sendResult.getMessageId();
        }catch (Exception ex){
            if(this.saveSendMessageErrorLog(tag,keyid,body)<=0){
                logger.error("sendmsg error tag sendOrderStatusChange keyid is "+keyid+" body is :"+body);
            }
            logger.error("",ex);
        }
        return null;
    }

    @Override
    public String sendMessage(String keyid, MqTagEnum tagEnum, String body) {
        try {
            if(StringUtil.isEmpty(tagEnum.getTag()))
            {
                return null;
            }
            logger.info("messsage body is : "+body);
            Message message = new Message( //
                    // Message所属的Topic
                    "ydmq",
                    // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                    tagEnum.getTag(),
                    // Message Body 可以是任何二进制形式的数据， MQ不做任何干预
                    // 需要Producer与Consumer协商好一致的序列化和反序列化方式
                    SerializeUtils.serialize(body));
            // 设置代表消息的业务关键属性，请尽可能全局唯一
            // 以方便您在无法正常收到消息情况下，可通过MQ 控制台查询消息并补发
            // 注意：不设置也不会影响消息正常收发
            message.setKey(keyid);
            SendResult sendResult = producerMq.send(message);
            assert sendResult != null;
            return sendResult.getMessageId();
        }catch (Exception ex){
            if(this.saveSendMessageErrorLog(tagEnum.getTag(),keyid,body)<=0){
                logger.error("sendmsg error tag "+tagEnum.getTag()+" keyid is "+keyid+" body is :"+body);
            }
            logger.error("",ex);
        }
        return null;

    }

    @Override
    public String sendMessage(String keyid, MqTagEnum tagEnum, String body,long deliverMillis) {
        try {
            if(StringUtil.isEmpty(tagEnum.getTag()))
            {
                return null;
            }
            logger.info("messsage body is : "+body);
            Message message = new Message( //
                    // Message所属的Topic
                    "ydmq",
                    // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                    tagEnum.getTag(),
                    // Message Body 可以是任何二进制形式的数据， MQ不做任何干预
                    // 需要Producer与Consumer协商好一致的序列化和反序列化方式
                    SerializeUtils.serialize(body));
            // 设置代表消息的业务关键属性，请尽可能全局唯一
            // 以方便您在无法正常收到消息情况下，可通过MQ 控制台查询消息并补发
            // 注意：不设置也不会影响消息正常收发
            message.setKey(keyid);
            //消息延时时间
            message.setStartDeliverTime(System.currentTimeMillis() + deliverMillis);
            SendResult sendResult = producerMq.send(message);
            assert sendResult != null;
            return sendResult.getMessageId();
        }catch (Exception ex){
            if(this.saveSendMessageErrorLog(tagEnum.getTag(),keyid,body)<=0){
                logger.error("send deliverMillisMsg error tag "+tagEnum.getTag()+" keyid is "+keyid+" body is :"+body);
            }
            logger.error("",ex);
        }
        return null;

    }

    @Override
    public String sendMessage(String keyid, String shardingKey, MqTagEnum tagEnum, String body) {
        try {
            if(StringUtil.isEmpty(tagEnum.getTag()))
            {
                return null;
            }
            logger.info("messsage body is : "+body);
            Message message = new Message( //
                    // Message所属的Topic
                    "ydorder",
                    // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                    tagEnum.getTag(),
                    // Message Body 可以是任何二进制形式的数据， MQ不做任何干预
                    // 需要Producer与Consumer协商好一致的序列化和反序列化方式
                    SerializeUtils.serialize(body));
            // 设置代表消息的业务关键属性，请尽可能全局唯一
            // 以方便您在无法正常收到消息情况下，可通过MQ 控制台查询消息并补发
            // 注意：不设置也不会影响消息正常收发
            message.setKey(keyid);
            SendResult sendResult = producerOrderMq.send(message,shardingKey);
            assert sendResult != null;
            return sendResult.getMessageId();
        }catch (Exception ex){
            logger.error("producerOrderMq sendMessage error :",ex);
        }
        return null;

    }

//    @Override
//    public String sendMessage(String keyid, String shardingKey, MqTagEnum tagEnum, String body,long timeout) {
//        try {
//            if(StringUtil.isEmpty(tagEnum.getTag()))
//            {
//                return null;
//            }
//            logger.info("messsage body is : "+body);
//            Message message = new Message( //
//                    // Message所属的Topic
//                    "ydorder",
//                    // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
//                    tagEnum.getTag(),
//                    // Message Body 可以是任何二进制形式的数据， MQ不做任何干预
//                    // 需要Producer与Consumer协商好一致的序列化和反序列化方式
//                    SerializeUtils.serialize(body));
//            // 设置代表消息的业务关键属性，请尽可能全局唯一
//            // 以方便您在无法正常收到消息情况下，可通过MQ 控制台查询消息并补发
//            // 注意：不设置也不会影响消息正常收发
//            message.setKey(keyid);
//            message.setStartDeliverTime(System.currentTimeMillis() + timeout);
//            SendResult sendResult = producerOrderMq.send(message,shardingKey);
//            assert sendResult != null;
//            return sendResult.getMessageId();
//        }catch (Exception ex){
//            if(this.saveSendMessageErrorLog(tagEnum.getTag(),keyid,body)<=0){
//                logger.error("send producerOrderMq error tag "+tagEnum.getTag()+"shardingKey is "+shardingKey+" keyid is "+keyid+" body is :"+body);
//            }
//            logger.error("",ex);
//        }
//        return null;
//
//    }

    /**
     * 记录发送失败的消息到数据库中，方便定时任务重发消息
     * @param tag
     * @param keyid
     * @param body
     * @return
     */
    protected int saveSendMessageErrorLog(String tag,String keyid,String body){

        try{
            YdMqerrorlog mqerrorlog = new YdMqerrorlog();
            mqerrorlog.setKeyid(keyid);
            mqerrorlog.setTag(tag);
            mqerrorlog.setBody(body);
            return ydMqerrorlogMapper.insert(mqerrorlog);
        }catch (Exception ex){
            logger.error("",ex);
        }
        return 0;
    }
}
