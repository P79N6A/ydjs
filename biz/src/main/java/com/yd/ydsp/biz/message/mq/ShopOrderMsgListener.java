package com.yd.ydsp.biz.message.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.yd.ydsp.biz.cp.WareService;
import com.yd.ydsp.biz.openshop.ShopCashRechargeService;
import com.yd.ydsp.biz.openshop.ShopGoodsService;
import com.yd.ydsp.biz.openshop.ShopOrderService;
import com.yd.ydsp.client.domian.UserOrderDeliveryInfoVO;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.redis.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 订单与商品信息处理
 */
public class ShopOrderMsgListener implements MessageOrderListener {
    public static final Logger logger = LoggerFactory.getLogger(ShopOrderMsgListener.class);

    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private WareService wareService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopCashRechargeService shopCashRechargeService;

    @Override
    public OrderAction consume(Message message, ConsumeOrderContext consumeOrderContext) {
        logger.info("OrderMsgListener Receive: " + message);
        try {
            String body = (String)SerializeUtils.deserialize(message.getBody());
            logger.info("OrderMsgListener body is :" + body);
            Map<String,Object> msgMap = JSON.parseObject(body,Map.class);
            if(msgMap.containsKey(Constant.MQTAG)){
                MqTagEnum mqTagEnum = MqTagEnum.tagOf((String)msgMap.get(Constant.MQTAG));
                if(mqTagEnum==MqTagEnum.USERORDERNEW||mqTagEnum == MqTagEnum.USERORDERUPDATE){

                    boolean isok = shopOrderService.updateShopUserOrder((String)msgMap.get(Constant.MQBODY));
                    if(!isok){
                        return OrderAction.Suspend;
                    }

                }else if(mqTagEnum==MqTagEnum.ORDERDELIVERY){
                    /**
                     * 物流订单更新消息
                     */
                    UserOrderDeliveryInfoVO userOrderDeliveryInfoVO = JSON.parseObject((String)msgMap.get(Constant.MQBODY),UserOrderDeliveryInfoVO.class);
                    boolean isok = shopOrderService.updateUserDeliveryInfo((String)msgMap.get("orderid"),userOrderDeliveryInfoVO);
                    if(!isok){
                        return OrderAction.Suspend;
                    }
                }else if(mqTagEnum==MqTagEnum.USERORDERTIMEOUT) {
                    String orderid = ((String)msgMap.get(Constant.MQBODY)).trim();
                    boolean isTimeoutOK = shopOrderService.timoutCloseOrder(orderid);
                    if(!isTimeoutOK){
                        return OrderAction.Suspend;
                    }

                }else if(mqTagEnum==MqTagEnum.PRINTMSG) {

                    shopOrderService.UserOrderByPrinter((String)msgMap.get(Constant.MQBODY));
                }else if(mqTagEnum==MqTagEnum.WAREUPDATE) {//更新商品
                    boolean isok = shopGoodsService.updateOpenSearchData((String)msgMap.get(Constant.MQBODY));
                    if(!isok){
                        return OrderAction.Suspend;
                    }
                }else if(mqTagEnum==MqTagEnum.WARESELL) {//接收售买计数与库存调整消息
                    String shopOrderSkuVOSJsonStr = (String)msgMap.get(Constant.MQBODY);
                    logger.info("shopOrderSkuVOSJsonStr is :"+shopOrderSkuVOSJsonStr);
                    wareService.updateWareSellInfo(shopOrderSkuVOSJsonStr);
                }else if(mqTagEnum==MqTagEnum.SHOPUSERBILL||mqTagEnum==MqTagEnum.UPDATESHOPCASHRECHARGE) {
                    boolean isok = this.userBilling(mqTagEnum,msgMap);
                    if(!isok){
                        return OrderAction.Suspend;
                    }
                } else {
                    logger.error("此消息对应的MQTA不存在，是不是发错了？");
                }

            } else {
                logger.error("此消息没有对应的MQTA？");
            }
            return OrderAction.Success;
        } catch (Exception e) {
            logger.error("ShopOrderMsgListener consume is error: ",e);
            //消费失败
            return OrderAction.Suspend;
        }
    }


    protected boolean userBilling(MqTagEnum mqTagEnum,Map<String,Object> msgMap){
        if(mqTagEnum==MqTagEnum.SHOPUSERBILL){

            String billid = (String) msgMap.get(Constant.BILLID);
            Integer billType = (Integer) msgMap.get(Constant.BILLTYPE);
            return shopCashRechargeService.newBill(billid,billType);

        }else if(mqTagEnum==MqTagEnum.UPDATESHOPCASHRECHARGE){
            String billid = (String) msgMap.get(Constant.BILLID);
            Integer billType = (Integer) msgMap.get(Constant.BILLTYPE);
            return shopCashRechargeService.updateShopCashData(billid,billType);
        }
        return true;
    }

}
