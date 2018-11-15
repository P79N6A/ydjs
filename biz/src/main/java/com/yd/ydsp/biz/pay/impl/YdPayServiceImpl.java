package com.yd.ydsp.biz.pay.impl;

import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.pay.YdPayService;
import com.yd.ydsp.biz.pay.YeePayService;
import com.yd.ydsp.biz.pay.model.YdPayResponse;
import com.yd.ydsp.biz.pay.model.YeePayRequestDO;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.enums.OrderStatusEnum;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.dal.entity.YdPayYeepayCallback;
import com.yd.ydsp.dal.mapper.YdPayYeepayCallbackMapper;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/21
 */
public class YdPayServiceImpl implements YdPayService {

    @Resource
    YeePayService yeePayService;
    @Resource
    private YdPayYeepayCallbackMapper ydPayYeepayCallbackMapper;

    @Override
    public YdPayResponse payMobileRequest(YeePayRequestDO yeePayRequestDO, PayOrderTypeEnum payOrderTypeEnum,String fcCallBakUrl) {
        yeePayRequestDO.setMerchantaccount(DiamondYdSystemConfigHolder.getInstance().yeepayMerchantaCcount);
        yeePayRequestDO.setTranstime((int)(Instant.now().getEpochSecond()));
        if(StringUtil.isEmpty(yeePayRequestDO.getProductcatalog())) {
            yeePayRequestDO.setProductcatalog("1");
        }
        if(StringUtil.isEmpty(yeePayRequestDO.getTerminalid())) {
            yeePayRequestDO.setTerminalid("88-88-88-88-88-88");
            yeePayRequestDO.setTerminaltype(3);
        }

        if(yeePayRequestDO.getOrderexpdate()==null) {
            yeePayRequestDO.setOrderexpdate(1440);
        }

     return yeePayService.payMobileRequest(yeePayRequestDO,payOrderTypeEnum,fcCallBakUrl);
    }

    @Override
    public Map<String, Object> queryPayOrderInfoByLocal(String orderid, PayOrderTypeEnum payOrderTypeEnum, OrderStatusEnum orderStatusEnum){
            Map<String, Object> result = new HashMap<>();
            result.put("success",false);
            YdPayYeepayCallback payYeepayCallbackInfo = payYeepayCallbackInfo = ydPayYeepayCallbackMapper.selectByOrderId(orderid);
            if (payYeepayCallbackInfo != null) {
                if (OrderStatusEnum.nameOf(payYeepayCallbackInfo.getPaystatus()) == orderStatusEnum) {
                    result.put("success", true);
                    result.put(Constant.STATUS, payYeepayCallbackInfo.getPaystatus());
                    result.put("amount", payYeepayCallbackInfo.getAmount());
                    result.put(Constant.ORDERID, orderid);
                    result.put(Constant.PAYURL,payYeepayCallbackInfo.getPayurl());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    result.put("ordertime", sdf.format(payYeepayCallbackInfo.getCreateDate()));
                    return result;
                }else{
                    result.put("success", true);
                    result.put(Constant.STATUS, payYeepayCallbackInfo.getPaystatus());
                    result.put(Constant.ORDERID, orderid);
                    result.put(Constant.PAYURL,payYeepayCallbackInfo.getPayurl());
                }
            }
            return result;
    }
}
