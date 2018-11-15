package com.yd.ydsp.biz.pay;

import com.yd.ydsp.biz.pay.model.WeiXinPayRequestDO;
import java.util.Map;

public interface WeiXinPayService {

    Map<String,Object> callPrePay(WeiXinPayRequestDO weiXinPayRequestDO) throws Exception;
    Map<String,Object> microPay(WeiXinPayRequestDO weiXinPayRequestDO) throws Exception;


    /**
     * trade_state:
     * SUCCESS—支付成功

     REFUND—转入退款

     NOTPAY—未支付

     CLOSED—已关闭

     REVOKED—已撤销(刷卡支付)

     USERPAYING--用户支付中

     PAYERROR--支付失败(其他原因，如银行返回失败)

     trade_type:JSAPI，NATIVE，APP，MICROPAY

     * @param appid
     * @param mchId
     * @param orderid
     * @return
     * @throws Exception
     */
    Map<String,Object> queryOrderByWeiXin(String appid,String mchId,String orderid) throws Exception;

    /**
     * 微信支付回调通知
     * @param resultXml
     */
    String calbackResultPay(String orderid, String resultXml);

    /**
     * 微信扫码支付回调通知
     * @param resultXml
     * @return
     */
    String bizPayCallBack(String resultXml);

    /**
     * 创建微信扫码支付短链url
     * @param appid
     * @param mchId
     * @param orderid
     * @return
     */
    String createBizPayQrCode(String appid,String mchId, String orderid) throws Exception;

}
