package com.yd.ydsp.biz.deliver;

import com.yd.ydsp.biz.deliver.model.UUCallbackData;
import com.yd.ydsp.client.domian.openshop.DeliveryOrderPriceVO;

import java.util.Map;

public interface UUDeliveryService {

    /**
     * 计算价格
     * @param originId  订单id
     * @param city  订单所在城市名 称(如郑州市就填”郑州市“，必须带上“市”)
     * @param county 订单所在县级地名称(如金水区就填“金水区”)
     * @param fromAddress 起始地址
     * @param fromUsernote 起始地址具体门牌号，可以不填
     * @param fromLat 起始地坐标纬度，如果无，传0(坐标系为百度地图坐标系)
     * @param fromLng 起始地坐标经度，如果无，传0(坐标系为百度地图坐标系)
     * @param toAddress 目的地址
     * @param toUsernote 目的地址具体门牌号，可以不填
     * @param toLat 目的地坐标纬度，如果无，传0(坐标系为百度地图坐标系)
     * @param toLng 目的地坐标经度，如果无，传0(坐标系为百度地图坐标系)
     * @return
     */
    DeliveryOrderPriceVO queryDeliverFee(String originId, String city, String county,
                                         String fromAddress, String fromUsernote, String fromLat, String fromLng,
                                         String toAddress, String toUsernote, String toLat, String toLng);

    /**
     * 发布订单
     * @param priceToken 金额令牌，计算订单价格接口返回的price_token
     * @param orderPrice 订单金额，计算订单价格接口返回的total_money
     * @param balancePaymoney 实际余额支付金额计算订单价格接口返回的need_paymoney
     * @param receiver 收件人
     * @param receiverPhone 收件人电话
     * @param note 订单备注 最长140个汉字 可为空
     * @param callbackUrl 订单提交成功后及状态变化的回调地址 可为空
     * @param specialType 特殊处理类型，是否需要保温箱 1需要 0不需要
     * @param callmeWithtake 取件是否给我打电话 1需要 0不需要
     * @param pubusermobile 发件人电话，（如果为空则是UU注册的手机号）
     * @return
     */
    Map<String,Object> createOrder(String priceToken,String orderPrice,String balancePaymoney,
                                   String receiver,String receiverPhone,String note,String callbackUrl,
                                   String specialType,String callmeWithtake,String pubusermobile);

    /**
     * 取消订单
     * @param originId 第三方对接平台订单id，order_code和origin_id必须二选其一，如果都传，则只根据order_code返回
     * @param orderCode UU跑腿订单编号，order_code和origin_id必须二选其一，如果都传，则只根据order_code返回
     * @param reason 取消原因
     * @return
     */
    Map<String,Object> cancelOrder(String originId,String orderCode,String reason);

    /**
     * 获取订单详情
     * @param originId 第三方对接平台订单id，order_code和origin_id必须二选其一，如果都传，则只根据order_code返回
     * @param orderCode UU跑腿订单编号，order_code和origin_id必须二选其一，如果都传，则只根据order_code返回
     * @return
     */
    Map<String,Object> getOrderDetail(String originId,String orderCode);

    /**
     * 订单提交后及状态变化回调
     * @param UUCallbackData
     */
    void uuCallback(UUCallbackData UUCallbackData);

}
