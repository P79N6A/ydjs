package com.yd.ydsp.biz.message.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.yd.ydsp.biz.cp.ShopInfoService;
import com.yd.ydsp.biz.customer.ShoppingMall2CService;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.opensearch.OrderSearchService;
import com.yd.ydsp.biz.openshop.ShopOrderService;
import com.yd.ydsp.client.domian.UserOrderDeliveryInfoVO;
import com.yd.ydsp.client.domian.openshop.DeliveryOrderPriceVO;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.enums.OrderStatusEnum;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.dal.entity.YdShopConsumerOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

public class OrderMsgListener implements MessageListener {
    public static final Logger logger = LoggerFactory.getLogger(OrderMsgListener.class);

    @Resource
    private ShopInfoService shopInfoService;
    @Resource
    private MqMessageService mqMessageService;

    @Resource
    private ShoppingMall2CService shoppingMall2CService;
    @Resource
    private ShopOrderService shopOrderService;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        logger.info("OrderMsgListener Receive: " + message);
        try {
            String body = (String)SerializeUtils.deserialize(message.getBody());
            logger.info("OrderMsgListener body is :" + body);
            Map<String,Object> msgMap = JSON.parseObject(body,Map.class);
            if(msgMap.containsKey(Constant.MQTAG)){
                String mqTag = (String)msgMap.get(Constant.MQTAG);
                MqTagEnum mqTagEnum = MqTagEnum.tagOf(mqTag);
                if(mqTagEnum==MqTagEnum.USERORDERTIMEOUT) {
                    String orderid = (String) msgMap.get(Constant.MQBODY);
                    String msgid = mqMessageService.sendMessage(orderid+"timeout",orderid,MqTagEnum.USERORDERTIMEOUT,JSON.toJSONString(msgMap));
                    if(StringUtil.isEmpty(msgid)){
                        return Action.ReconsumeLater;
                    }
                }else if(mqTagEnum==MqTagEnum.MicroPay){
                    Map<String,Object> moneyBody = JSON.parseObject((String)msgMap.get(Constant.MQBODY),Map.class);
                    shopOrderService.updateMoneyOrder((String)moneyBody.get(Constant.ORDERID),(Integer)moneyBody.get("count"));
                }
            }else {
                /**
                 * 1.0时期的逻辑
                 */
                Map<String, String> paySuccessMsg = JSON.parseObject(body, Map.class);
                OrderStatusEnum orderStatusEnum = OrderStatusEnum.nameOf(paySuccessMsg.get(Constant.ORDERTYPE));
                PayOrderTypeEnum payOrderTypeEnum = PayOrderTypeEnum.nameOf(paySuccessMsg.get(Constant.PAYORDERTYPE));
                if (payOrderTypeEnum == PayOrderTypeEnum.C2B) {
                    shoppingMall2CService.updateOrderStatus(paySuccessMsg.get(Constant.ORDERID), orderStatusEnum);
                } else if (payOrderTypeEnum == PayOrderTypeEnum.CPORDER) {
                    shopInfoService.updateCpOrderStatus(paySuccessMsg.get(Constant.ORDERID), orderStatusEnum);
                }
            }

            return Action.CommitMessage;
        } catch (Exception e) {
            logger.error("OrderMsgListener consume is error: ",e);
            //消费失败
            return Action.ReconsumeLater;
        }

    }


}
