package com.yd.ydsp.biz.openshop;

import com.yd.ydsp.biz.customer.model.order.ShopOrderExt2C;
import com.yd.ydsp.client.domian.UserOrderDeliveryInfoVO;
import com.yd.ydsp.dal.entity.YdShopConsumerOrder;

import java.util.Map;

/**
 * 内部使用
 */
public interface ShopOrderService {

    /**
     * 查订单
     * @param orderid
     * @return
     */
    YdShopConsumerOrder queryUserOrder(String orderid);

//    /**
//     * 根据orderid查询订单详情
//     * @param consumerOrder
//     * @return
//     */
//    ShopOrderExt2C queryUserOrderDetail(YdShopConsumerOrder consumerOrder);

    /**
     * 更新订单数据到openseache及统计表计数
     * @param orderid
     * @return
     */
    boolean updateShopUserOrder(String orderid) throws Exception;

    /**
     * 更新订单物流信息
     * @param orderid
     * @param orderDeliveryInfoVO
     * @return
     */
    boolean updateUserDeliveryInfo(String orderid, UserOrderDeliveryInfoVO orderDeliveryInfoVO);

    /**
     * 支付完成确认接口
     * @param orderid
     * @param totalFee
     * @param responeMap
     * @return
     */
    boolean orderPayFinish(String orderid, Integer totalFee, Map<String,Object> responeMap);

    /**
     * 订单超时关闭
     * @param orderid
     * @return
     */
    boolean timoutCloseOrder(String orderid);

    /**
     * 打印用户订单信息，以提醒商家接单或者收银
     * @param orderid
     */
    void UserOrderByPrinter(String orderid) throws Exception;

    void sendPrintMessage(String shopid, String orderid);

    /**
     * 更新日报表（实时统计表)
     * @param consumerOrder
     * @return
     */
    void updateShoporderStatistics(YdShopConsumerOrder consumerOrder);

    /**
     * 更新收钱宝付款的订单状态
     * @param orderid
     * @param count
     * @return
     */
    void updateMoneyOrder(String orderid,Integer count) throws Exception;

//    /**
//     * 当使用
//     * @param orderid
//     * @return
//     */
//    boolean updateOrderDataToShopBillData(String orderid);
}
