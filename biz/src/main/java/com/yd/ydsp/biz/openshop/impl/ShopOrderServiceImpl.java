package com.yd.ydsp.biz.openshop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yd.ydsp.biz.cp.CpChannelService;
import com.yd.ydsp.biz.cp.CpService;
import com.yd.ydsp.biz.cp.ShopInfoService;
import com.yd.ydsp.biz.customer.model.order.ShopOrder2C;
import com.yd.ydsp.biz.customer.model.order.ShoppingOrderSkuVO;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.opensearch.OrderSearchService;
import com.yd.ydsp.biz.openshop.ShopOrderService;
import com.yd.ydsp.biz.pay.WeiXinPayService;
import com.yd.ydsp.biz.weixin.model.WeixinPayTradeStatusEnum;
import com.yd.ydsp.biz.yunprinter.FEPrinterService;
import com.yd.ydsp.client.domian.CPDeviceInfoDTO;
import com.yd.ydsp.client.domian.ShopInfoDTO;
import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.client.domian.UserOrderDeliveryInfoVO;
import com.yd.ydsp.client.domian.openshop.YdCpChannelVO;
import com.yd.ydsp.client.domian.paypoint.DiningtableVO;
import com.yd.ydsp.client.domian.paypoint.PrinterInfoVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.constants.paypoint.ShopUserOrderFlagConstants;
import com.yd.ydsp.common.enums.*;
import com.yd.ydsp.common.enums.paypoint.PrintTypeEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.AmountUtils;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShopOrderServiceImpl implements ShopOrderService {

    public static final Logger logger = LoggerFactory.getLogger(ShopOrderServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private YdShopConsumerOrderMapper ydShopConsumerOrderMapper;
    @Resource
    private ShopInfoService shopInfoService;
    @Resource
    private YdWeixinUserInfoMapper ydWeixinUserInfoMapper;
    @Resource
    private YdWeixinServiceConfigMapper ydWeixinServiceConfigMapper;
    @Resource
    private YdPaypointCpdeviceInfoMapper ydPaypointCpdeviceInfoMapper;
    @Resource
    private YdShoporderStatisticsMapper ydShoporderStatisticsMapper;

    @Resource
    private CpService cpService;
    @Resource
    private CpChannelService cpChannelService;
    @Resource
    private FEPrinterService fePrinterService;
    @Resource
    private MqMessageService mqMessageService;
    @Resource
    private OrderSearchService orderSearchService;
    @Resource
    private WeiXinPayService weiXinPayService;

    @Override
    public YdShopConsumerOrder queryUserOrder(String orderid) {
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderid(orderid.trim());
        return consumerOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateShopUserOrder(String orderid) throws Exception {
        orderid = orderid.trim();
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder==null) {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), orderid+"订单id不存在!");
        }

            /**
             * 更新opensearche
             */
        boolean isok = orderSearchService.commitOrderData(consumerOrder);
        if(!isok){
            return false;
        }

        /**
         * 实时统计表（日报表）数据更新
         */

        this.updateShoporderStatistics(consumerOrder);

        /**
         * 生成扫码支付二维护码URL
         */
        if(consumerOrder.getPayMode().intValue()==0&&
                consumerOrder.getIsPay().intValue()<0){
            Map<String,String> bizPayMap = null;
            if(StringUtil.isEmpty(consumerOrder.getPayqrcode())){
                bizPayMap = new HashMap<>();
            }else {
                bizPayMap = JSON.parseObject(consumerOrder.getPayqrcode(),Map.class);
            }
            if(!bizPayMap.containsKey(Constant.BIZPAYWEIXINURL)){
                YdWeixinServiceConfig weixinServiceConfig = ydWeixinServiceConfigMapper.selectByWeixinConfigId(consumerOrder.getWeixinConfigId());
                if(StringUtil.isNotEmpty(weixinServiceConfig.getWeixinpaySubMchId())) {
                    String weiBizPayUrl = weiXinPayService.createBizPayQrCode(weixinServiceConfig.getAppid(),
                            weixinServiceConfig.getWeixinpaySubMchId(), orderid);
                    if(StringUtil.isNotEmpty(weiBizPayUrl)) {
                        bizPayMap.put(Constant.BIZPAYWEIXINURL, weiBizPayUrl);
                        consumerOrder.setPayqrcode(JSON.toJSONString(bizPayMap));
                        if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
                            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), orderid+"更新扫码支付URL失败!");
                        }
                    }
                }
            }
        }

        return true;
    }

//    @Override
//    public ShopOrderExt2C queryUserOrderDetail(YdShopConsumerOrder consumerOrder) {
//        ShopOrderExt2C shopOrderExt2C = null;
//        if(consumerOrder!=null) {
//            ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(), ShopOrder2C.class);
//            shopOrder2C.setOrderDate(consumerOrder.getCreateDate());
//            UserOrderDeliveryInfoVO deliveryInfoVO = null;
//            UserAddressInfoVO userAddressInfoVO = null;
//            Map<String, Object> deliveryMap = null;
//            if (StringUtil.isNotEmpty(consumerOrder.getDelivery())) {
//                deliveryMap = JSON.parseObject(consumerOrder.getDelivery(), Map.class);
//                if (deliveryMap.containsKey(Constant.DELIVERYINFO)) {
//                    deliveryInfoVO = ((JSONObject) deliveryMap.get(Constant.DELIVERYINFO)).toJavaObject(UserOrderDeliveryInfoVO.class);
//                }
//                if (deliveryMap.containsKey(Constant.USERADDRESSINFO)) {
//                    userAddressInfoVO = ((JSONObject) deliveryMap.get(Constant.USERADDRESSINFO)).toJavaObject(UserAddressInfoVO.class);
//                }
//
//            }
//            shopOrderExt2C = new ShopOrderExt2C();
//            shopOrderExt2C.setShopOrder2C(shopOrder2C);
//            shopOrderExt2C.setDeliveryInfoVO(deliveryInfoVO);
//            shopOrderExt2C.setUserAddressInfoVO(userAddressInfoVO);
//        }
//
//        return shopOrderExt2C;
//    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUserDeliveryInfo(String orderid, UserOrderDeliveryInfoVO orderDeliveryInfoVO) {
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), orderid+"订单id不存在!");
        }

        DeliveryTypeEnum deliveryTypeEnum = DeliveryTypeEnum.nameOf(orderDeliveryInfoVO.getDeliveryType());
        UserOrderStatusEnum userOrderStatusEnum = UserOrderStatusEnum.nameOf(consumerOrder.getStatus());
        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
        if(deliveryTypeEnum==DeliveryTypeEnum.DADA||deliveryTypeEnum==DeliveryTypeEnum.UU) {

            DeliveryOrderStatusEnum deliveryOrderStatusEnum = DeliveryOrderStatusEnum.nameOf(orderDeliveryInfoVO.getStatusCode());
            Map<String, Object> orderDeliveryMap = JSON.parseObject(consumerOrder.getDelivery(), Map.class);
            orderDeliveryMap.put(Constant.DELIVERYINFO, orderDeliveryInfoVO);
            consumerOrder.setDelivery(JSON.toJSONString(orderDeliveryMap));

            if(deliveryOrderStatusEnum == DeliveryOrderStatusEnum.Delivery||deliveryOrderStatusEnum == DeliveryOrderStatusEnum.Finish){
                if(consumerOrder.getPayMode().intValue()==0&&(userOrderStatusEnum.status.intValue()>=0&&userOrderStatusEnum.status.intValue()<5)){
                    shopOrder2C.setStatus(UserOrderStatusEnum.SENDOUT.getStatus());
                }else if(consumerOrder.getPayMode().intValue()==1&&consumerOrder.getIsPay().intValue()>=0&&
                        (userOrderStatusEnum.status.intValue()>=2&&userOrderStatusEnum.status.intValue()<5)){
                    shopOrder2C.setStatus(UserOrderStatusEnum.SENDOUT.getStatus());
                }
            }

        }else {
            logger.error(orderid+":不支持的物流单类型!");
            return true;
        }

        consumerOrder.setStatus(shopOrder2C.getStatus());
        consumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
        if (ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder) <= 0) {
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "物流订单信息更新失败!");
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean orderPayFinish(String orderid, Integer totalFee, Map<String, Object> responeMap) {
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), orderid+"订单id不存在!");
        }
        YdWeixinUserInfo weixinUserInfo = ydWeixinUserInfoMapper.selectByUnionid(consumerOrder.getUnionid());
        if(weixinUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), consumerOrder.getUnionid()+"用户不存在!");
        }
        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);

        if(shopOrder2C.getIsPay()>=0){
            return true;
        }

        if(!(AmountUtils.changeY2F(consumerOrder.getTotalAmount())==totalFee.intValue())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), orderid+"订单金额不一致!");
        }
        if(responeMap!=null){
            if(responeMap.containsKey(Constant.ISPAY)){
                Integer ispay = (Integer)responeMap.get(Constant.ISPAY);
                shopOrder2C.setIsPay(ispay);
            }else {
                shopOrder2C.setIsPay(0);
            }

        }else {
            shopOrder2C.setIsPay(0);
        }
        if(shopOrder2C.getPayMode().intValue()==1){
            shopOrder2C.setStatus(UserOrderStatusEnum.PAYFINISH.getStatus());
        }else {
            /**
             * 后付款方式
             */
            shopOrder2C.setStatus(UserOrderStatusEnum.FINISH.getStatus());
        }
        consumerOrder.setStatus(shopOrder2C.getStatus());
        consumerOrder.setIsPay(shopOrder2C.getIsPay());
        Map<String,Object> resultPayInfo = null;
        if(StringUtil.isEmpty(consumerOrder.getResultPayInfo())){
            resultPayInfo = new HashMap<>();
        }else {
            resultPayInfo = JSON.parseObject(consumerOrder.getResultPayInfo(),Map.class);
        }
        resultPayInfo.put(Constant.RESULTPAYINFO,responeMap);
        consumerOrder.setResultPayInfo(JSON.toJSONString(resultPayInfo));

        Integer accPoint = 0;

        try{
            /**
             * 积分奖励
             */
            accPoint = shopInfoService.rewardConsumptionPoint(weixinUserInfo.getId(), UserSourceEnum.WEIXIN.getType(),consumerOrder.getShopid(),consumerOrder.getTotalAmount(),true);
            shopOrder2C.setAccPoint(accPoint);
            consumerOrder.setFeature(JSON.toJSONString(shopOrder2C));


        }catch (YdException yex){
            logger.error("积分奖励出错：",yex);

        }catch (Exception ex){
            logger.error("积分奖励出错：",ex);
        }
        if (ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder) <= 0) {
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "订单支付完成状态更新失败!");
        }

        Map<String,Object> msgMap = new HashMap<>();
        msgMap.put(Constant.MQTAG,MqTagEnum.USERORDERUPDATE.getTag());
        msgMap.put(Constant.MQBODY,orderid);
        String msgId = mqMessageService.sendMessage(orderid+"update",orderid,MqTagEnum.USERORDERUPDATE,JSON.toJSONString(msgMap));

        /**
         * 创建订单时已经发送了后付款订单的打印，此时付款成功直接打印，已经打印了的，不会打印，没有打印的就说明先付款订单，所以这里直接发消息也没啥问题
         */
        this.sendPrintMessage(shopOrder2C.getShopid(), shopOrder2C.getOrderid());

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean timoutCloseOrder(String orderid) {
        logger.info("订单超时被调用了:"+orderid);
        YdShopConsumerOrder ydShopConsumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(ydShopConsumerOrder==null){
            return true;
        }

        UserOrderStatusEnum orderStatusEnum = UserOrderStatusEnum.nameOf(ydShopConsumerOrder.getStatus());

        if(ydShopConsumerOrder.getIsPay().intValue()<0) {
            ShopOrder2C shopOrder2C = JSON.parseObject(ydShopConsumerOrder.getFeature(), ShopOrder2C.class);
            TypeEnum typeEnum = TypeEnum.getTypeOfSN(shopOrder2C.getEnterCode());
            if (orderStatusEnum == UserOrderStatusEnum.WAITEPAYPRE || orderStatusEnum == UserOrderStatusEnum.WAITE || typeEnum==TypeEnum.DININGTABLE) {

                PayOrderTypeEnum payOrderTypeEnum = PayOrderTypeEnum.nameOf(ydShopConsumerOrder.getOrderType());
                if(payOrderTypeEnum!=PayOrderTypeEnum.YDPAY) {
                    /**
                     * 回退积分
                     */
                    if (shopOrder2C.getUsePoint() > 0) {
                        YdWeixinUserInfo weixinUserInfo = ydWeixinUserInfoMapper.selectByUnionid(ydShopConsumerOrder.getUnionid());
                        if (weixinUserInfo != null) {
                            shopInfoService.rebackConsumptionPoint(weixinUserInfo.getId(), UserSourceEnum.WEIXIN.getType(), ydShopConsumerOrder.getShopid(), shopOrder2C.getUsePoint());
                        }
                    }
                    ydShopConsumerOrder.setTotalAmount(AmountUtils.add(ydShopConsumerOrder.getTotalAmount(), shopOrder2C.getDeMoney()));
                    shopOrder2C.setUsePoint(0);
                    shopOrder2C.setDeMoney(new BigDecimal("0.00"));
                    shopOrder2C.setStatus(UserOrderStatusEnum.TIMEOUT.getStatus());
                    ydShopConsumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
                    ydShopConsumerOrder.setStatus(shopOrder2C.getStatus());
                    if (ydShopConsumerOrderMapper.updateByPrimaryKeySelective(ydShopConsumerOrder) <= 0) {
                        throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "设置订单超时失败!");
                    }
                }else {
                    shopOrder2C.setStatus(UserOrderStatusEnum.TIMEOUT.getStatus());
                    ydShopConsumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
                    ydShopConsumerOrder.setStatus(shopOrder2C.getStatus());
                    if (ydShopConsumerOrderMapper.updateByPrimaryKeySelective(ydShopConsumerOrder) <= 0) {
                        throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "设置订单超时失败!");
                    }
                }

                /**
                 * 发消息同步订单信息到opensearch
                 */
                Map<String,Object> msgMap = new HashMap<>();
                msgMap.put(Constant.MQTAG,MqTagEnum.USERORDERUPDATE.getTag());
                msgMap.put(Constant.MQBODY,orderid);
                String msgId = mqMessageService.sendMessage(orderid+"update",orderid,MqTagEnum.USERORDERUPDATE,JSON.toJSONString(msgMap));
            }
        }
        return true;
    }


    /**
     * 打印机自动接单或者打印收银单
     * @param orderid
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void UserOrderByPrinter(String orderid) throws Exception {

        if(StringUtil.isEmpty(orderid)){
            logger.info("print orderid is null");
            return;
        }
        YdShopConsumerOrder order = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid.trim());
        if(order==null){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "订单查询失败");
        }
        ShopOrder2C order2c = JSON.parseObject(order.getFeature(),ShopOrder2C.class);
        if(order2c==null){
            logger.info("print order2c is null");
            logger.info(order.getFeature());
            return;
        }

        /**
         * 生成扫码支付二维护码URL
         */
        String weiBizPayUrl = null;
        Map<String,String> bizPayMap = null;
        if(StringUtil.isNotEmpty(order.getPayqrcode())){
            bizPayMap = JSON.parseObject(order.getPayqrcode(),Map.class);
            if(bizPayMap.containsKey(Constant.BIZPAYWEIXINURL)){
                weiBizPayUrl = bizPayMap.get(Constant.BIZPAYWEIXINURL);
            }
        }

        if(StringUtil.isEmpty(weiBizPayUrl)&&order.getPayMode().intValue()==0&&
                order.getIsPay().intValue()<0){

            if(StringUtil.isEmpty(order.getPayqrcode())){
                bizPayMap = new HashMap<>();
            }else {
                bizPayMap = JSON.parseObject(order.getPayqrcode(),Map.class);
            }
            if(!bizPayMap.containsKey(Constant.BIZPAYWEIXINURL)){
                YdWeixinServiceConfig weixinServiceConfig = ydWeixinServiceConfigMapper.selectByWeixinConfigId(order.getWeixinConfigId());
                if(StringUtil.isNotEmpty(weixinServiceConfig.getWeixinpaySubMchId())) {
                    weiBizPayUrl = weiXinPayService.createBizPayQrCode(weixinServiceConfig.getAppid(),
                            weixinServiceConfig.getWeixinpaySubMchId(), orderid);
                    if(StringUtil.isNotEmpty(weiBizPayUrl)) {
                        bizPayMap.put(Constant.BIZPAYWEIXINURL, weiBizPayUrl);
                        order.setPayqrcode(JSON.toJSONString(bizPayMap));
                    }
                }
            }
        }

        logger.info("print info is:"+JSON.toJSONString(order2c));

        if(order2c.isExistFlag(ShopUserOrderFlagConstants.printIsOver)){
            logger.info("已经打印过了所以不打印");
            return;
        }
        order2c.setOrderDate(order.getCreateDate());

        /**
         * 开始取接单打印机的相关信息
         */

        String printerId = null;
        PrintTypeEnum printTypeEnum = PrintTypeEnum.CASHIER;
        TypeEnum enterTypeEnum =null;
        if(StringUtil.isNotBlank(order2c.getEnterCode())){
            enterTypeEnum = TypeEnum.getTypeOfSN(order2c.getEnterCode());

            if(enterTypeEnum==TypeEnum.DININGTABLE) {
                DiningtableVO diningtableVO = cpService.queryTableByTableId(order.getShopid(), order2c.getEnterCode());
                if (diningtableVO == null) {
                    logger.error("OrderByPrinter error in diningtableVO is null :"+ order2c.getEnterCode());
                    return;
                }
                printerId = diningtableVO.getKitchenPrintId();
                if(StringUtil.isEmpty(printerId)){
                    printerId = diningtableVO.getCashierPrintId();
                }else {
                    printTypeEnum = PrintTypeEnum.KITCHEN;
                }

            }else if(enterTypeEnum==TypeEnum.SHOP) {
                ShopInfoDTO shopInfoDTO = shopInfoService.getShopInfo(order2c.getShopid());
                if(shopInfoDTO==null){
                    logger.info("shop is null , so not print:"+order2c.getShopid());
                    return;
                }

                printerId = shopInfoDTO.getFeature(Constant.SHOP_PRINT);

            }else if(enterTypeEnum==TypeEnum.CHANNELQRCODE){
                YdCpChannelVO ydCpChannelVO = cpChannelService.queryChannelInfo(order2c.getEnterCode());
                if(ydCpChannelVO==null){
                    return;
                }
                printerId = ydCpChannelVO.getPrinterNum();

            }else {
                return;
            }

        }else {
            logger.error("OrderByPrinter error, EnterCode is null :"+ order2c.getOrderid());
            return;
        }

        if (StringUtil.isBlank(printerId)) {
            logger.info("printerId is null");
            return;
        }

        Object deviceInfoObj = cpService.queryDeviceInfo(order.getShopid(),printerId,TypeEnum.PRINTER);
        if(deviceInfoObj==null){
            logger.info("print info not found");
            return;
        }
        if(!(deviceInfoObj instanceof PrinterInfoVO)){
            logger.info("not print class");
            return;
        }
        PrinterInfoVO printerInfoVO = (PrinterInfoVO)deviceInfoObj;

        YdPaypointCpdeviceInfo printerInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(printerId);
        CPDeviceInfoDTO deviceInfoDTO = doMapper.map(printerInfo,CPDeviceInfoDTO.class);
        String times = printerInfoVO.getTimes().toString();
        if(StringUtil.isBlank(deviceInfoDTO.getFeature(Constant.PRINT_TYPE))){
            logger.info("print type is null");
            return;
        }

        /**
         * 取打印机的sn
         */
        String printerSN = printerInfoVO.getSn();

        /**
         * 构造接单的打印内容
         */
        String printMsg = this.constructFeiE(order2c,order.getDelivery(),printTypeEnum,weiBizPayUrl);
        if(printMsg==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "打印收银单失败，原因是构造打印内容失败，消费者订单id:" + order2c.getOrderid());
        }

        String feiEOrderId = fePrinterService.printMsg(printerSN,printMsg,times);
        if(StringUtil.isBlank(feiEOrderId)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"打印收银单失败，原因是云打印调用结果不正确，消费者订单id:" + order2c.getOrderid());
        }

        logger.info("打印收银单成功，消费者订单id:" + order2c.getOrderid() + " 云打印，外部订单号：" + feiEOrderId);

        /**
         * 更新已经打印标志
         */
        order2c.addFlag(ShopUserOrderFlagConstants.printIsOver);

        if(order2c.getPayMode().intValue()==0&&
                (UserOrderStatusEnum.nameOf(order2c.getStatus())==UserOrderStatusEnum.WAITE)){
            order2c.setStatus(UserOrderStatusEnum.READY.getStatus());
        }
        if(enterTypeEnum==TypeEnum.DININGTABLE){
            order2c.setStatus(UserOrderStatusEnum.SENDOUT.getStatus());
        }
        order.setFeature(JSON.toJSONString(order2c));
        order.setStatus(order2c.getStatus());

        try{
            ydShopConsumerOrderMapper.updateByPrimaryKeySelective(order);
        }catch (Exception ex){
            logger.error("",ex);
        }

        /**
         * 发消息同步订单信息到opensearch
         */
        Map<String,Object> msgMap = new HashMap<>();
        msgMap.put(Constant.MQTAG, MqTagEnum.USERORDERUPDATE.getTag());
        msgMap.put(Constant.MQBODY,orderid);
        mqMessageService.sendMessage(orderid+"updatebyprint",orderid,MqTagEnum.USERORDERUPDATE,JSON.toJSONString(msgMap));

    }

    @Override
    public void sendPrintMessage(String shopid, String orderid) {
        Map<String,Object> msgMap = new HashMap<>();
        msgMap.put(Constant.MQTAG, MqTagEnum.PRINTMSG.getTag());
        msgMap.put(Constant.MQBODY,orderid);
        mqMessageService.sendMessage(shopid+"print",shopid,MqTagEnum.PRINTMSG,JSON.toJSONString(msgMap));
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShoporderStatistics(YdShopConsumerOrder consumerOrder) {
        if(consumerOrder==null){
            return;
        }
        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
        if((!shopOrder2C.isExistFlag(ShopUserOrderFlagConstants.accIsOver))&&shopOrder2C.getIsPay().intValue()>=0) {
            YdShoporderStatistics shoporderStatistics = ydShoporderStatisticsMapper.selectByShopidRowLock(consumerOrder.getShopid(), consumerOrder.getCreateDate());
            if (shoporderStatistics == null) {
                logger.error("支付成功，但更新订单状态失败，原因是统计表数据没有找到, 订单信息：" + JSON.toJSONString(shopOrder2C));
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "支付成功，但更新订单状态失败!");
            }

            ISPayTypeEnum isPayTypeEnum = ISPayTypeEnum.nameOf(shopOrder2C.getIsPay());
            if (isPayTypeEnum == ISPayTypeEnum.ONLINE || isPayTypeEnum == ISPayTypeEnum.CONSUMPTION) {
                shoporderStatistics.setReceiveAmount(AmountUtils.bigDecimalBy2(shoporderStatistics.getReceiveAmount().add(consumerOrder.getTotalAmount())));
            } else if (isPayTypeEnum == ISPayTypeEnum.CASH) {
                shoporderStatistics.setReceivecashAmount(shoporderStatistics.getReceivecashAmount().add(consumerOrder.getTotalAmount()));
            }

            if (ydShoporderStatisticsMapper.updateByPrimaryKeySelective(shoporderStatistics) <= 0) {
                logger.error("支付成功，但更新订单状态失败，原因是统计表实收金额更新失败, 订单信息：" + JSON.toJSONString(consumerOrder));
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "支付成功，但更新订单状态失败！");
            }
            shopOrder2C.addFlag(ShopUserOrderFlagConstants.accIsOver);
            consumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
            if (ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder) <= 0) {
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新订单统计标志失败!");
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMoneyOrder(String orderid, Integer count) throws Exception {
        boolean result = true;
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid.trim());
        if(consumerOrder==null){
            result = false;

        }

        if(consumerOrder.getIsPay().intValue()>=0){
            return;
        }

        YdWeixinServiceConfig serviceConfig = ydWeixinServiceConfigMapper.selectByWeixinConfigId(consumerOrder.getWeixinConfigId());
        if(serviceConfig==null){
            result = false;
        }

        /**
         * 先在微信那里查订单状态
         */
        Map<String,Object> weixinOrderMap = weiXinPayService.queryOrderByWeiXin(serviceConfig.getAppid(),serviceConfig.getWeixinpaySubMchId(),orderid);
        result = (Boolean) weixinOrderMap.get("success");
        String tradeStatus = null;
        if(weixinOrderMap.containsKey("trade_state")) {
            tradeStatus = (String) weixinOrderMap.get("trade_state");
        }
        if(StringUtil.isEmpty(tradeStatus)){
            result = false;
        }else {
            WeixinPayTradeStatusEnum tradeStatusEnum = WeixinPayTradeStatusEnum.nameOf(tradeStatus);
            if(tradeStatusEnum!=WeixinPayTradeStatusEnum.SUCCESS){
                result = false;
            }else {
                result = true;
            }
        }

        if(result){
            Map<String,Object> resultPayInfo = new HashMap<>();
            resultPayInfo.put(Constant.RESULTPAYINFO,weixinOrderMap);
            String openid2c = (String)weixinOrderMap.get("openid");
            ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
            shopOrder2C.setIsPay(0);
            shopOrder2C.setStatus(UserOrderStatusEnum.FINISH.getStatus());
            consumerOrder.setIsPay(shopOrder2C.getIsPay());
            consumerOrder.setStatus(shopOrder2C.getStatus());
            consumerOrder.setOpenid(openid2c);
            consumerOrder.setUnionid(openid2c+serviceConfig.getWeixinConfigId());
            consumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
            consumerOrder.setResultPayInfo(JSON.toJSONString(resultPayInfo));
            if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
                logger.error("microPay 支付成功 但是更新订单支付状态失败！");
                result = false;
            }else {
                result = true;
                /**
                 * 发同步订单数据到opensearche消息
                 */
                Map<String,Object> msgMap = new HashMap<>();
                msgMap.put(Constant.MQTAG, MqTagEnum.USERORDERUPDATE.getTag());
                msgMap.put(Constant.MQBODY,orderid);
                String msgId = mqMessageService.sendMessage(orderid+"update",orderid,MqTagEnum.USERORDERUPDATE,JSON.toJSONString(msgMap));
            }
        }

        count = count + 1;
        if(!result){
            if(count>3){
                return;
            }
            Map<String,Object> msgMap = new HashMap<>();
            Map<String,Object> payMap = new HashMap<>();
            msgMap.put(Constant.MQTAG, MqTagEnum.MicroPay.getTag());
            payMap.put(Constant.ORDERID,orderid);
            payMap.put("count",count);
            msgMap.put(Constant.MQBODY,payMap);
            String msgId = mqMessageService.sendMessage(orderid+"MicroPay",MqTagEnum.MicroPay,JSON.toJSONString(msgMap),12000);

        }

    }

//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean updateOrderDataToShopBillData(String orderid) {
//        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid.trim());
//        if(consumerOrder==null){
//            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), orderid+":订单未找到!");
//        }
//        if(consumerOrder.getIsPay().intValue()<0){
//            logger.error(orderid+":未支付订单不应该进行统计!");
//            return true;
//        }
//
//        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
//        if(shopOrder2C.isExistFlag(ShopUserOrderFlagConstants.WRITESHOPDATAFINISH)){
//            /**
//             * 已经统计过了，返回false，调用处得到false把其做为已经统计过的订单来处理
//             */
//            return false;
//        }
//
//        shopOrder2C.addFlag(ShopUserOrderFlagConstants.WRITESHOPDATAFINISH);
//        consumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
//        if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
//            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), orderid+":更新已统计状态失败!");
//        }
//
//        /**
//         * 发同步订单数据到opensearche消息
//         */
//        Map<String,Object> msgMap = new HashMap<>();
//        msgMap.put(Constant.MQTAG, MqTagEnum.USERORDERUPDATE.getTag());
//        msgMap.put(Constant.MQBODY,orderid);
//        mqMessageService.sendMessage(orderid+"update",orderid,MqTagEnum.USERORDERUPDATE,JSON.toJSONString(msgMap));
//
//        return true;
//    }


    private String constructFeiE(ShopOrder2C order2c,String deliveryInfo,PrintTypeEnum printTypeEnum,String bizPayUrl){
        String result = null;

        if(printTypeEnum==PrintTypeEnum.KITCHEN){
            /**
             * 厨房订单
             */

            String content="<CB>请厨房接单</CB><BR>";
            content +="<B>桌号:"+order2c.getEnterName()+ "序号:"+order2c.getPrintCount().toString()+"</B><BR>";
            content += "--------------------------------<BR>";
            content += "名称　　　　　           数量  <BR>";
            content += "--------------------------------<BR>";
            String skuName = "";
            for (ShoppingOrderSkuVO skuVO:order2c.getShoppingOrderSkuVOList()){
                skuName = skuVO.getSkuName();
//                String name = StringUtil.abbreviateComplete(skuVO.getSkuName(),26,true,false);
                String count = "x"+skuVO.getCount().toString();
                if(StringUtil.isNotEmpty(skuVO.getMainSpecName())){
                    skuName += "-"+skuVO.getMainSpecName();
                    if(StringUtil.isNotEmpty(skuVO.getChildSpecName())){
                        skuName += "-"+skuVO.getChildSpecName();
                    }
                }
                String name = "<B>"+skuName+"</B>";
                String countSpac = StringUtil.abbreviate2BComplete(skuVO.getSkuName(),true,count);

                content = content + name  + countSpac +"<B>"+count  + "</B><BR>";
            }
            if(StringUtil.isNotBlank(order2c.getDescription())) {
                content += "--------------------------------<BR>";
                content += "客户要求：<BR>" + order2c.getDescription()+"<BR>";
            }
            content += "--------------------------------<BR>";
            content += "下单时间："+ DateUtils.date2String(new Date())+"<BR><BR><BR><CUT>";
            result = content;

        }

        if(printTypeEnum==PrintTypeEnum.CASHIER){

            /**
             * 收银订单
             */

            UserOrderDeliveryInfoVO deliveryInfoVO = null;
            UserAddressInfoVO userAddressInfoVO = null;
            Map<String,Object> deliveryMap = null;
            if(StringUtil.isNotEmpty(deliveryInfo)){
                deliveryMap = JSON.parseObject(deliveryInfo,Map.class);
                if(deliveryMap.containsKey(Constant.DELIVERYINFO)){
                    deliveryInfoVO = ((JSONObject)deliveryMap.get(Constant.DELIVERYINFO)).toJavaObject(UserOrderDeliveryInfoVO.class);
                }
                if(deliveryMap.containsKey(Constant.USERADDRESSINFO)){
                    userAddressInfoVO = ((JSONObject)deliveryMap.get(Constant.USERADDRESSINFO)).toJavaObject(UserAddressInfoVO.class);
                }

            }

            String content = "<CB>#"+order2c.getPrintCount().toString()+"  引灯科技</CB><BR>";
            content += "<C><BOLD>*"+shopInfoService.getShopInfo(order2c.getShopid()).getName()+"*</BOLD></C><BR>";
            if(order2c.getIsPay().intValue()==0) {
                content += "<CB>---已在线支付---</CB><BR>";
            }else if(order2c.getIsPay().intValue()==1) {
                content += "<CB>---已现金支付---</CB><BR>";

            }else if(order2c.getIsPay().intValue()==-1) {
                content += "<CB>---未付款订单---</CB><BR>";

            }
            content += "--------------------------------<BR>";
            content += "下单时间："+DateUtils.date2String(order2c.getOrderDate())+"<BR>";
            content += "订单号:"+order2c.getOrderid()+"<BR>";
            content += "--------------------------------<BR>";
            content += "名称　　　　   数量         金额<BR>";
            content += "--------------------------------<BR>";
            String skuName = "";
            for (ShoppingOrderSkuVO skuVO:order2c.getShoppingOrderSkuVOList()){
                skuName = skuVO.getSkuName();
                if(StringUtil.isNotEmpty(skuVO.getMainSpecName())){
                    skuName += "-"+skuVO.getMainSpecName();
                    if(StringUtil.isNotEmpty(skuVO.getChildSpecName())){
                        skuName += "-"+skuVO.getChildSpecName();
                    }
                }
                String name = "<L>"+skuName+"</L><BR>";
                String placeholder = StringUtil.abbreviateComplete(" ",15,false,false,false);
                String count = "x"+StringUtil.abbreviateComplete(skuVO.getCount().toString(),8,false,false,false);
                String priceTotal = StringUtil.abbreviateComplete(""+AmountUtils.bigDecimalBy2(skuVO.getDisTotalAmount()),8,false,true,false);
                content = content + name + "<L>"+placeholder  + count + priceTotal + "</L><BR>";
            }
            content += "--------------------------------<BR>";
            content += "<B>合计："+AmountUtils.bigDecimalBy2(order2c.getDisTotalAmount())+"元</B><<BR>";
            content += "--------------------------------<BR>";
            if(StringUtil.isNotBlank(order2c.getDescription())) {
                content += "<BOLD>客户要求：<BR>" + order2c.getDescription()+"</BOLD><BR>";
                content += "--------------------------------<BR>";
            }

            if(userAddressInfoVO!=null){

                content += "<L>地址："+userAddressInfoVO.getProvince()+userAddressInfoVO.getCity()+
                        userAddressInfoVO.getDistrict()+userAddressInfoVO.getAddress()+"</L><BR>";
                if(StringUtil.isNotEmpty(userAddressInfoVO.getZipcode())) {
                    content += "<L>邮编：" +userAddressInfoVO.getZipcode()+"</L><BR>";
                }
                content += "<L>收件人："+userAddressInfoVO.getName()+"</L><BR>";
                content += "<L>手机："+userAddressInfoVO.getMobile()+"</L><BR><BR>";

            }

            if(StringUtil.isNotEmpty(bizPayUrl)){
                content += "<C><BOLD>#请使用微信扫二维码结帐#</BOLD></C><BR>";
                content += "<QR>"+bizPayUrl+"</QR>";
            }

            content += "<C><L>***********#"+order2c.getPrintCount().toString()+"完***********</L></C><BR>";
            content += "<BR><BR><BR><BR><BR><BR><CUT>";
            result = content;

        }

        return result;

    }

}
