package com.yd.ydsp.biz.message;

import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;

public interface MqMessageService {

    /**
     * 当支付成功时，调用此方法发送消息，以进行后续业务处理
     * 发送失败时，统一写入消息失败日志表，由定时任务进行补发
     * @param keyid
     * @param body json字串
     * @return messageId
     */
    String sendYdPayMessage(String keyid,String body);

    /**
     * 改变订单状态的消息，目前用于订单超时关闭
     * @param keyid// 设置代表消息的业务关键属性，请尽可能全局唯一
    // 以方便您在无法正常收到消息情况下，可通过MQ 控制台查询消息并补发
    // 注意：不设置也不会影响消息正常收发
     * @param body
     * @param timeout
     * @param orderTypeEnum 订单的类型
     * @return messageId
     */
    String sendOrderStatusChange(String keyid, String body, long timeout, PayOrderTypeEnum orderTypeEnum);

    /**
     * 通用消息发送，在消费者接收时，根据body中的内容再自行分发业务逻辑与调用
     * @param keyid
     * @param body json字串
     * @return messageId
     */
    String sendMessage(String keyid, MqTagEnum tagEnum, String body);

    /**
     * 通用的延时消息发送
     * @param keyid
     * @param tagEnum
     * @param body
     * @param deliverMillis 延时多少毫秒
     * @return
     */
    String sendMessage(String keyid, MqTagEnum tagEnum, String body,long deliverMillis);

    /**
     * 发送顺序消息，以shardingKey为分区标识，相同shardingKey的消息顺序消费
     * @param keyid
     * @param shardingKey
     * @param tagEnum
     * @param body
     * @return
     */
    String sendMessage(String keyid, String shardingKey, MqTagEnum tagEnum, String body);
//    String sendMessage(String keyid, String shardingKey, MqTagEnum tagEnum, String body,long timeout);

}
