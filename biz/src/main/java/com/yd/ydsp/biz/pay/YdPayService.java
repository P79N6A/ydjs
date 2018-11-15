package com.yd.ydsp.biz.pay;

import com.yd.ydsp.biz.pay.model.YdPayResponse;
import com.yd.ydsp.biz.pay.model.YeePayRequestDO;
import com.yd.ydsp.common.enums.OrderStatusEnum;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;

import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/21
 */
public interface YdPayService {

    public YdPayResponse payMobileRequest(YeePayRequestDO yeePayRequestDO, PayOrderTypeEnum payOrderTypeEnum,String fcCallBakUrl);

    public Map<String,Object> queryPayOrderInfoByLocal(String orderid, PayOrderTypeEnum payOrderTypeEnum, OrderStatusEnum orderStatusEnum) throws Exception;

}
