package com.yd.ydsp.biz.pay;

import com.yd.ydsp.biz.pay.model.YdPayResponse;
import com.yd.ydsp.biz.pay.model.YeePayRequestDO;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;

import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/12
 */
public interface YeePayService {

    /**
     * 易宝支付订单创建接口
     * @param yeePayRequestDO
     * @param payOrderTypeEnum
     * @param fcCallBakUrl 支付后回调的页面
     * @return
     * 状态：
     *  0:未支付
        1:支付成功
        2 : 已撤销【表示订单已过有效期】 3:阻断交易 - 订单因为高风险而被阻断 4:失败 5:处理中
    refundtotal:累计退款金额
    targetamount:收款方实收金额
     */
    public YdPayResponse payMobileRequest(YeePayRequestDO yeePayRequestDO,PayOrderTypeEnum payOrderTypeEnum,String fcCallBakUrl);


    public Map<String,Object> queryPayOrderInfo(String orderid,PayOrderTypeEnum payOrderTypeEnum) throws Exception;

    /**
     * 直接入yd_pay_yeepay_callback
     * 如果sign不正确 yd_pay_yeepay_callback表中的status字段设置为-1
     * @param data
     * @param encryptkey
     * @param payOrderTypeEnum
     */
    public String payCallback(String data, String encryptkey,PayOrderTypeEnum payOrderTypeEnum);

}
