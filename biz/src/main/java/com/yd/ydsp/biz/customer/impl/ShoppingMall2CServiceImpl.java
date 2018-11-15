package com.yd.ydsp.biz.customer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yd.ydsp.biz.config.DiamondWeiXinInfoConfigHolder;
import com.yd.ydsp.biz.config.DiamondYdPayPointConfigHolder;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.cp.*;
import com.yd.ydsp.biz.customer.ShoppingMall2CService;
import com.yd.ydsp.biz.customer.model.Order2CVO;
import com.yd.ydsp.biz.customer.model.SearchUserOrderTypeEnum;
import com.yd.ydsp.biz.customer.model.ShoppingCartSkuVO;
import com.yd.ydsp.biz.customer.model.ShoppingCartVO;
import com.yd.ydsp.biz.customer.model.order.ShopOrder2C;
import com.yd.ydsp.biz.customer.model.order.ShopOrderExt2C;
import com.yd.ydsp.biz.customer.model.order.ShoppingOrderSkuVO;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.opensearch.OrderSearchService;
import com.yd.ydsp.biz.openshop.ShopCashRechargeService;
import com.yd.ydsp.biz.openshop.ShopOrderService;
import com.yd.ydsp.biz.pay.WeiXinPayService;
import com.yd.ydsp.biz.pay.YdPayService;
import com.yd.ydsp.biz.pay.model.WeiXinPayRequestDO;
import com.yd.ydsp.biz.pay.model.YdPayResponse;
import com.yd.ydsp.biz.pay.model.YeePayRequestDO;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.user.Userinfo2ShopService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.yunprinter.FEPrinterService;
import com.yd.ydsp.client.domian.*;
import com.yd.ydsp.client.domian.openshop.SearchOrderDataResultVO;
import com.yd.ydsp.client.domian.openshop.ShopCashRechargeConfigVO;
import com.yd.ydsp.client.domian.openshop.YdShopHoursInfoVO;
import com.yd.ydsp.client.domian.paypoint.DiningtableVO;
import com.yd.ydsp.client.domian.paypoint.PrinterInfoVO;
import com.yd.ydsp.client.domian.paypoint.ShopUserBillVO;
import com.yd.ydsp.client.domian.paypoint.WareSkuVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.*;
import com.yd.ydsp.common.enums.paypoint.PrintTypeEnum;
import com.yd.ydsp.common.enums.paypoint.ShopStatusEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.AmountUtils;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.metadata.IIOMetadataNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ShoppingMall2CServiceImpl implements ShoppingMall2CService {

    public static final Logger logger = LoggerFactory.getLogger(ShoppingMall2CServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private CpService cpService;
    @Resource
    private ShopInfoService shopInfoService;
    @Resource
    private WareService wareService;
    @Resource
    private RedisManager redisManager;
    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    private YdPaypointShopInfoExtMapper ydPaypointShopInfoExtMapper;
    @Resource
    private YdConsumerOrderMapper ydConsumerOrderMapper;
    @Resource
    private YdShoporderStatisticsMapper ydShoporderStatisticsMapper;
    @Resource
    private YdPaypointCpdeviceInfoMapper ydPaypointCpdeviceInfoMapper;
    @Resource
    private YdPrintOrderLogMapper ydPrintOrderLogMapper;
    @Resource
    private YdPrintCashierOrderLogMapper ydPrintCashierOrderLogMapper;
    @Resource
    private YdShopConsumerOrderMapper ydShopConsumerOrderMapper;
    @Resource
    private YdPaypointConsumerAddressMapper ydPaypointConsumerAddressMapper;
    @Resource
    private YdWeixinServiceConfigMapper ydWeixinServiceConfigMapper;
    @Resource
    private YdWeixinUserInfoMapper ydWeixinUserInfoMapper;
    @Resource
    private YdCpChannelMapper ydCpChannelMapper;
    @Resource
    private Userinfo2ShopService userinfo2ShopService;
    @Resource
    private UserinfoService userinfoService;
    @Resource
    private YdPayService ydPayService;
    @Resource
    private SpecConfigService specConfigService;
    @Resource
    private FEPrinterService fePrinterService;
    @Resource
    private MqMessageService mqMessageService;
    @Resource
    private OrderSearchService orderSearchService;
    @Resource
    private WeiXinPayService weiXinPayService;
    @Resource
    private ShopOrderService shopOrderService;
    @Resource
    private ShopCashRechargeService shopCashRechargeService;


    @Override
    public ShoppingCartVO updateCartInfo(String openid, String qrCode, Map<String, Integer> skuMap) throws IOException, ClassNotFoundException {
        if(StringUtil.isBlank(qrCode)||skuMap==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"参数不完整，请检查！");
        }
        if(skuMap.keySet().isEmpty()){
            this.clearShoppintCart(openid,qrCode);
            return null;
        }
        DiningtableVO diningtable = cpService.queryTableByQrcode(qrCode);
        if(diningtable==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"扫描码未绑定桌台!");
        }
        if(diningtable.getStatus().intValue()!=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"当前桌号处于不可用状态!");
        }

        ShoppingCartVO result = new ShoppingCartVO();
        byte[] codeBytes = redisManager.get(SerializeUtils.serialize(openid+diningtable.getShopid()));
        if(!SerializeUtils.isEmpty(codeBytes)){
            ShoppingCartVO oldCart = (ShoppingCartVO)SerializeUtils.deserialize(codeBytes);
            if(oldCart!=null){
                result.setOrderid(oldCart.getOrderid());
            }else{
                result.setOrderid(RandomUtil.getSNCode(TypeEnum.CONSUMERORDER));
            }
        }else{
            result.setOrderid(RandomUtil.getSNCode(TypeEnum.CONSUMERORDER));
        }

        result.setTableName(diningtable.getName());
        result.setQrCode(qrCode);
        result.setShopid(diningtable.getShopid());
        result.setTotalAmount(new BigDecimal(0.00));
        result.setDisTotalAmount(new BigDecimal(0.00));
        Map<String,ShoppingCartSkuVO> cartSkuVOMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : skuMap.entrySet()) {

            WareSkuVO skuInfo = wareService.getWareSku(entry.getKey());
            if(skuInfo==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),entry.getKey()+"的商品不存在!");
            }

            if(entry.getValue()<=0){
                continue;
            }

            ShoppingCartSkuVO cartSkuVO = new ShoppingCartSkuVO();
            cartSkuVO.setSkuName(skuInfo.getName());
            cartSkuVO.setPrice(skuInfo.getPrice());
            cartSkuVO.setDisprice(skuInfo.getDisprice());
            cartSkuVO.setCount(entry.getValue());
            cartSkuVO.setTotalAmount(AmountUtils.mul(cartSkuVO.getPrice(),cartSkuVO.getCount()));
            cartSkuVO.setDisTotalAmount(AmountUtils.mul(cartSkuVO.getDisprice(),cartSkuVO.getCount()));
            cartSkuVOMap.put(entry.getKey(),cartSkuVO);
            result.setTotalAmount(result.getTotalAmount().add(cartSkuVO.getTotalAmount()));
            result.setDisTotalAmount(result.getDisTotalAmount().add(cartSkuVO.getDisTotalAmount()));

//            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());


        }
        result.setShoppingCartSkuVOMap(cartSkuVOMap);

        /**
         * 设置到缓存，1800秒过期
         */

        redisManager.set(SerializeUtils.serialize(openid+result.getShopid()),SerializeUtils.serialize(result),1800);

        return result;
    }

    @Override
    public Map<String,Object> getCartInfo(String openid, String qrCode) throws IOException, ClassNotFoundException {
        Map<String,Object> result = null;
        if(StringUtil.isBlank(qrCode)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"参数不完整，请检查！");
        }
        DiningtableVO diningtable = cpService.queryTableByQrcode(qrCode);
        if(diningtable==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"扫描码未绑定桌台!");
        }
        if(diningtable.getStatus().intValue()!=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"当前桌号处于不可用状态!");
        }
        byte[] codeBytes = redisManager.get(SerializeUtils.serialize(openid+diningtable.getShopid()));
        if(!SerializeUtils.isEmpty(codeBytes)){
            ShoppingCartVO cartVO = (ShoppingCartVO)SerializeUtils.deserialize(codeBytes);
            if(cartVO==null){
                redisManager.del(SerializeUtils.serialize(openid+diningtable.getShopid()));
                return null;
            }
            if(cartVO.getShoppingCartSkuVOMap()!=null) {
                for (String skuid : cartVO.getShoppingCartSkuVOMap().keySet()) {
                    /**
                     * 查sku,取图片信息
                     */
                    WareSkuVO skuVO = wareService.getWareSku(skuid);
                    if(skuVO==null){
                        continue;
                    }
                    cartVO.getShoppingCartSkuVOMap().get(skuid).setSkuImgUrl(skuVO.getWareimg());

                }
            }
            ShopInfoDTO shopInfoDTO = shopInfoService.getShopInfo(cartVO.getShopid());
            if(shopInfoDTO.isPayNow()){
                cartVO.setPayMode(1);
            }else{
                cartVO.setPayMode(0);
            }
            result = new HashMap<>();
            result.put("tableid",diningtable.getTableid());
            result.put("cartVO",cartVO);
            return result;

        }
        return null;
    }

    @Override
    public boolean clearShoppintCart(String openid, String qrCode) throws IOException {
        if(StringUtil.isBlank(qrCode)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"参数不完整，请检查！");
        }
        DiningtableVO diningtable = cpService.queryTableByQrcode(qrCode);
        if(diningtable==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"扫描码未绑定桌台!");
        }
        if(diningtable.getStatus().intValue()!=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"当前桌号处于不可用状态!");
        }
        byte[] codeBytes = redisManager.get(SerializeUtils.serialize(openid+diningtable.getShopid()));
        if(!SerializeUtils.isEmpty(codeBytes)) {
            redisManager.del(SerializeUtils.serialize(openid + diningtable.getShopid()));
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Order2CVO submitOrder(String openid, String qrCode,String description) throws IOException, ClassNotFoundException {
         Map<String,Object> resultCartInfoMap = this.getCartInfo(openid,qrCode);
        if(resultCartInfoMap==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"购物车信息不存在，请重新选择商品！");
        }

        ShoppingCartVO cartVO = (ShoppingCartVO)resultCartInfoMap.get("cartVO");
        String tableid = (String)resultCartInfoMap.get("tableid");

        /**
         * 查店铺信息
         */
        ShopInfoDTO shopInfoDTO = shopInfoService.getShopInfo(cartVO.getShopid());
        if(shopInfoDTO==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"你是不是走错店铺了！");
        }

        if(ShopStatusEnum.nameOf(shopInfoDTO.getStatus())!=ShopStatusEnum.NORMAL){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺没有营业，请联系店家开通服务！");
        }

        Integer shopOrderNumToday = DiamondYdPayPointConfigHolder.getInstance().getOrderCountToday(shopInfoDTO.getFeature(Constant.SHOP_SETMEAL_TYPE));

        Order2CVO order2CVO;
        /**
         * 保险起见，加一层分布式事务缓存锁,超时时间为1秒
         */
        boolean lockIsTrue = redisManager.lockWithTimeout(cartVO.getShopid()+"order2c",cartVO.getOrderid(),1500,1000);

        boolean isFirstOrderToday = false;
        YdShoporderStatistics shoporderStatistics = ydShoporderStatisticsMapper.selectByShopidRowLock(cartVO.getShopid(), new Date());
        if (shoporderStatistics == null) {
            isFirstOrderToday = true;
            shoporderStatistics = new YdShoporderStatistics();
            shoporderStatistics.setShopid(cartVO.getShopid());
            shoporderStatistics.setOrderCount(0);
            shoporderStatistics.setOrderDate(new Date());
            String bagStr = shopInfoDTO.getFeature(Constant.BAGORDERNUM);
            if(StringUtil.isBlank(bagStr)){
                shoporderStatistics.setBagCount(0);
            }else {
                shoporderStatistics.setBagCount(Integer.valueOf(bagStr));
            }
            shoporderStatistics.setReceiveAmount(new BigDecimal("0.00"));
        }

        String curShopType = shopInfoDTO.getFeature(Constant.SHOP_SETMEAL_TYPE);
        if(StringUtil.isBlank(curShopType)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺没有选择购买套餐！");
        }
        curShopType = curShopType.trim();
        shoporderStatistics.setOrderCount(shoporderStatistics.getOrderCount()+1);
        if(!StringUtil.equals(curShopType,Constant.DIAMOND_VERSION)) {

            if (shoporderStatistics.getOrderCount() > (shopOrderNumToday + shoporderStatistics.getBagCount())) {
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺接单单量已经到达上限，请联系店家增加接单量！");
            }
        }

        /**
         * 提交订单，生成订单信息
         */
        YdConsumerOrder consumerOrder = new YdConsumerOrder();
        consumerOrder.setShopid(cartVO.getShopid());
        consumerOrder.setTableid(tableid);
        consumerOrder.setOpenid(openid);
        consumerOrder.setOrderDate(shoporderStatistics.getOrderDate());
        consumerOrder.setConsumerDesc(description);
        if (AmountUtils.changeY2F(cartVO.getTotalAmount()) > 0) {
            consumerOrder.setStatus(OrderStatusEnum.NEW.getStatus());
        } else {
            consumerOrder.setStatus(OrderStatusEnum.PAYFINISH.getStatus());
        }
        consumerOrder.setOrderType(PayOrderTypeEnum.C2B.getType());
        consumerOrder.setPayMode(cartVO.getPayMode());
        consumerOrder.setTotalAmount(AmountUtils.bigDecimalBy2(cartVO.getTotalAmount()));
        consumerOrder.setDisTotalAmount(AmountUtils.bigDecimalBy2(cartVO.getDisTotalAmount()));
        if (StringUtil.isNotBlank(cartVO.getOrderid())) {
            consumerOrder.setOrderid(cartVO.getOrderid());
        } else {
            consumerOrder.setOrderid(RandomUtil.getSNCode(TypeEnum.CONSUMERORDER));
            cartVO.setOrderid(consumerOrder.getOrderid());
        }
        consumerOrder.setFeature(JSON.toJSONString(cartVO));


        order2CVO = this.doMapOrder(consumerOrder);


        consumerOrder.setPrintCount(shoporderStatistics.getOrderCount());

        /**
         * 取一个商品名称作为订单名称前缀
         */

        Map<String, ShoppingCartSkuVO> cartSkuVOMap = order2CVO.getShoppingCartSkuVOMap();
        Integer wareCount = cartSkuVOMap.keySet().size();
        String wareName = "";


        for(String key:cartSkuVOMap.keySet()){
            ShoppingCartSkuVO cartSkuVO = cartSkuVOMap.get(key);
            wareName = cartSkuVO.getSkuName();
            break;
        }

        wareName = StringUtil.abbreviate(wareName,10)+"等"+wareCount.toString()+"种菜品";
        order2CVO.setOrderName(wareName);
        consumerOrder.setOrderName(wareName);

        /**
         * 写订单到数据库
         */
        if (ydConsumerOrderMapper.insert(consumerOrder) <= 0) {
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "订单创建失败，请稍后再试！");
        }
        if (consumerOrder.getPayMode().intValue() == 0) {

            /**
             * 如果是后付款模式，需要发起打印机厨房接单
             */
            order2CVO.setPrintCount(shoporderStatistics.getOrderCount());
            this.OrderByPrinter(consumerOrder.getOrderid(),PrintTypeEnum.KITCHEN);
            /**
             * 看有没有接单打印机，有的话打个单子，好交给用户
             */
            this.OrderByPrinter(consumerOrder.getOrderid(),PrintTypeEnum.CASHIER);

        }

        /**
         * 写订单统计表
         */
        if(!isFirstOrderToday){
            shoporderStatistics.setLastid(consumerOrder.getId());
            if(ydShoporderStatisticsMapper.updateByPrimaryKeySelective(shoporderStatistics)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "订单创建失败，请稍后再试！");
            }

        }else {
            shoporderStatistics.setFirstid(consumerOrder.getId());
            shoporderStatistics.setLastid(consumerOrder.getId());
            shoporderStatistics.setWithdrawals(0);
            if(ydShoporderStatisticsMapper.insert(shoporderStatistics)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "订单创建失败，请稍后再试！");
            }

        }

        /**
         * 删除购物车
         */
        redisManager.del(SerializeUtils.serialize(openid + cartVO.getShopid()));

        if(lockIsTrue){
            /**
             * 释放锁
             */
            redisManager.releaseLock(cartVO.getShopid()+"order2c",cartVO.getOrderid());
        }
        /**
         * 以下发消息去关闭超8小时没有支付的订单
         */
        if(consumerOrder.getStatus().intValue()==OrderStatusEnum.NEW.getStatus().intValue()) {
            Map<String, String> closeOrderMessage = new HashMap<>();
            closeOrderMessage.put(Constant.ORDERID, cartVO.getOrderid());
            closeOrderMessage.put(Constant.PAYORDERTYPE, PayOrderTypeEnum.C2B.getName());
            closeOrderMessage.put(Constant.ORDERTYPE, OrderStatusEnum.TIMEOUT.getName());
            String msgId = mqMessageService.sendOrderStatusChange(cartVO.getOrderid() + "close2c", JSON.toJSONString(closeOrderMessage), 28800000, PayOrderTypeEnum.C2B);
            logger.info("ShoppingMal2C msgId is :" + msgId);
        }

        return order2CVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> payConsumerOrder(String openid, String orderid, String ip) throws Exception {

        YdConsumerOrder consumerOrder = ydConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单不存在！");
        }
        if(consumerOrder.getStatus()>0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单状态不正确！");
        }
        Integer totalAmount = AmountUtils.changeY2F(consumerOrder.getDisTotalAmount());
        if(totalAmount.intValue()<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单金额不能为0！");
        }
        /**
         * 先查支付订单是不是已经创建过了，如果已经创建过了，直接返回支付收银台的url
         */
        Map<String,Object> orderPayMap = ydPayService.queryPayOrderInfoByLocal(orderid,PayOrderTypeEnum.C2B,null);
        if(orderPayMap!=null){
            if(orderPayMap.keySet().contains(Constant.STATUS)&&orderPayMap.keySet().contains(Constant.ORDERID)){
                if(StringUtil.equals((String)orderPayMap.get(Constant.ORDERID),orderid)){
                    Integer payStatus = (Integer) orderPayMap.get(Constant.STATUS);
                    if(payStatus>0){
                        throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单已经支付！");
                    }else{
                        if(orderPayMap.keySet().contains(Constant.PAYURL)) {
                            return orderPayMap;
                        }
                    }
                }
            }
        }
        Order2CVO order = this.doMapOrder(consumerOrder);

        Map<String,Object> result = new HashMap<>();
        result.put("success", true);
        result.put(Constant.ORDERID,order.getOrderid());
        result.put(Constant.STATUS,order.getStatus());
        YeePayRequestDO yeePayRequestDO = new YeePayRequestDO();
        /**
         * 因为是公众号，所以此参数传7,identityid传openid
         */
        yeePayRequestDO.setOrderid(orderid);
        yeePayRequestDO.setAmount(totalAmount);
        yeePayRequestDO.setProductname(order.getOrderName());
        yeePayRequestDO.setProductdesc("订单编号："+orderid);
        yeePayRequestDO.setIdentitytype(7);
        yeePayRequestDO.setIdentityid(openid);
        yeePayRequestDO.setProductcatalog("7");
        yeePayRequestDO.setAppId(userinfoService.getWeiXinAppIdFor2C());
        yeePayRequestDO.setUserip(ip);
        YdPayResponse payResponse = ydPayService.payMobileRequest(yeePayRequestDO, PayOrderTypeEnum.C2B, DiamondYdSystemConfigHolder.getInstance().yeePay2COrderFCallbackUrl);
        if(payResponse.getSuccess()){
            result.put(Constant.PAYURL,payResponse.getPayurl());
        }else{
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"创建订单失败，请稍候再试！");
        }

        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(String orderid, OrderStatusEnum orderStatusEnum) {

        YdConsumerOrder consumerOrder = ydConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "orderid:" + consumerOrder.getOrderid() + "订单不存在!");
        }

        if(orderStatusEnum==OrderStatusEnum.TIMEOUT){
            /**
             * 说明要进行订单关闭操作，一般是超时订单
             */
            if(consumerOrder.getStatus().intValue()==OrderStatusEnum.NEW.getStatus().intValue()) {
                consumerOrder.setStatus(OrderStatusEnum.OVER.getStatus());
                if (ydConsumerOrderMapper.updateByPrimaryKey(consumerOrder) <= 0) {
                    throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "orderid:" + consumerOrder.getOrderid() + "订单关闭失败!");
                }

            }
            return;

        }

        /**
         * 如果是先付款方式，需要自动接单，能到这里说明是真实支付了的，都要打印接单
         */
        if (consumerOrder.getPayMode().intValue() == 1) {
            this.OrderByPrinter(consumerOrder.getOrderid(),PrintTypeEnum.KITCHEN);
            this.OrderByPrinter(consumerOrder.getOrderid(),PrintTypeEnum.CASHIER);

        }


        if(consumerOrder.getUseCash()==0) {
            /**
             * 计算线上支付的千六的支付手续费
             */
            BigDecimal commissionCharge = AmountUtils.mul(consumerOrder.getDisTotalAmount(), 0.006);
            if (AmountUtils.changeY2F(commissionCharge) > 0) {
                consumerOrder.setCommissionCharge(commissionCharge);
            }
        }

        /**
         * 更新统计表的实收金额
         */
        YdShoporderStatistics shoporderStatistics = ydShoporderStatisticsMapper.selectByShopidRowLock(consumerOrder.getShopid(), consumerOrder.getOrderDate());
        if (shoporderStatistics == null) {
            logger.error("支付成功，但更新订单状态失败，原因是统计表数据没有找到, 订单信息：" + JSON.toJSONString(consumerOrder));
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "支付成功，但更新订单状态失败!");
        }

        BigDecimal receiveAmount = AmountUtils.sub(consumerOrder.getDisTotalAmount(),consumerOrder.getCommissionCharge());
        if(orderStatusEnum==OrderStatusEnum.PAYFINISH) {
            shoporderStatistics.setReceiveAmount(shoporderStatistics.getReceiveAmount().add(receiveAmount));
        }else if(orderStatusEnum==OrderStatusEnum.PAYCASH){
            shoporderStatistics.setReceivecashAmount(shoporderStatistics.getReceivecashAmount().add(receiveAmount));
        }

        if (ydShoporderStatisticsMapper.updateByPrimaryKeySelective(shoporderStatistics) <= 0) {
            logger.error("支付成功，但更新订单状态失败，原因是统计表实收金额更新失败, 订单信息：" + JSON.toJSONString(consumerOrder));
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "支付成功，但更新订单状态失败！");
        }

        if(orderStatusEnum==OrderStatusEnum.PAYCASH){
            orderStatusEnum = OrderStatusEnum.PAYFINISH;
        }
        consumerOrder.setStatus(orderStatusEnum.getStatus());
        if (ydConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder) <= 0) {
            logger.error("支付成功，但更新订单状态失败, 订单信息：" + JSON.toJSONString(consumerOrder));
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "支付成功，但更新订单状态失败!");
        }

    }

    @Override
    public Order2CVO getOrder(String openid, String orderid) {
        YdConsumerOrder order = ydConsumerOrderMapper.selectByOrderid(orderid);
        if(order==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单不存在！");
        }
        if(!StringUtil.equals(openid,order.getOpenid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        return this.doMapOrder(order);
    }

    @Override
    public List<Order2CVO> getOrderListByConsumerId(String openid ,Integer status, Integer pageIndex, Integer count) {
        if(pageIndex==null||pageIndex<=0){
            pageIndex = 1;
        }
        if(count==null||count<=0){
            count = 5;
        }
        Integer indexPoint = (pageIndex-1)*count;
        if(indexPoint<=0){
            indexPoint = 0;
        }

        List<YdConsumerOrder> orderList = null;
        if(status==null||status<-1){
            orderList = ydConsumerOrderMapper.selectByOpenid(openid,indexPoint,count);
        }else if(status.intValue()==0){
            orderList = ydConsumerOrderMapper.selectByOpenidIsNoPay(openid,indexPoint,count);
        }else if(status.intValue()>0){
            orderList = ydConsumerOrderMapper.selectByOpenidIsPay(openid,indexPoint,count);
        }

        List<Order2CVO> order2CVOList = new ArrayList<>();
        if(orderList!=null){
            for(YdConsumerOrder order:orderList){
                Order2CVO order2CVO = this.doMapOrder(order);
                if(order2CVO!=null){
                    order2CVOList.add(order2CVO);
                }
            }
        }

        return order2CVOList;
    }

    /**
     * 打印机自动接单或者打印收银单
     * @param orderid
     * @param printTypeEnum
     */
    @Async
    protected void OrderByPrinter(String orderid,PrintTypeEnum printTypeEnum){
        if(StringUtil.isBlank(orderid)){
            return;
        }
        YdConsumerOrder order = ydConsumerOrderMapper.selectByOrderid(orderid);
        if(order==null){
            return;
        }
        Order2CVO order2c = this.doMapOrder(order);
        if(order2c==null){
            return;
        }

        if(printTypeEnum==PrintTypeEnum.KITCHEN) {
            YdPrintOrderLog printOrderLog = ydPrintOrderLogMapper.selectByOrderid(orderid);
            if(printOrderLog!=null){
                return;
            }
        }

        if(printTypeEnum==PrintTypeEnum.CASHIER) {
            YdPrintCashierOrderLog printCashierOrderLog = ydPrintCashierOrderLogMapper.selectByOrderid(orderid);
            if(printCashierOrderLog!=null){
                return;
            }
        }

        /**
         * 开始取接单打印机的相关信息
         */

        String printerId = null;

        if(StringUtil.isNotBlank(order.getTableid())){
            DiningtableVO diningtableVO = cpService.queryTableByTableId(order.getShopid(),order.getTableid());
            if(diningtableVO==null){
                return;
            }
            logger.info("OrderByPrinter in diningtableVO is : "+JSON.toJSONString(diningtableVO));
            if(printTypeEnum==PrintTypeEnum.KITCHEN) {
                printerId = diningtableVO.getKitchenPrintId();
            }
            if(printTypeEnum==PrintTypeEnum.CASHIER) {
                printerId = diningtableVO.getCashierPrintId();
            }
            if(StringUtil.isBlank(printerId)){
                return;
            }

        }else {
            return;
        }

        Object deviceInfoObj = cpService.queryDeviceInfo(order.getShopid(),printerId,TypeEnum.PRINTER);
        if(deviceInfoObj==null){
            return;
        }
        if(!StringUtil.equals(deviceInfoObj.getClass().getName(),PrinterInfoVO.class.getName())){
            return;
        }
        PrinterInfoVO printerInfoVO = (PrinterInfoVO)deviceInfoObj;

        YdPaypointCpdeviceInfo printerInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(printerId);
        CPDeviceInfoDTO deviceInfoDTO = doMapper.map(printerInfo,CPDeviceInfoDTO.class);
        String times = printerInfoVO.getTimes().toString();
        if(StringUtil.isBlank(deviceInfoDTO.getFeature(Constant.PRINT_TYPE))){
            return;
        }

        /**
         * 取打印机的sn
         */
        String printerSN = printerInfoVO.getSn();

        /**
         * 构造接单的打印内容
         */
        String printMsg = this.constructFeiE(order2c,printTypeEnum);
        if(printMsg==null){
            if(printTypeEnum==PrintTypeEnum.KITCHEN) {
                logger.info("打印机接单失败，原因是构造打印内容失败，消费者订单id:" + order2c.getOrderid());
            }else {
                logger.info("打印收银单失败，原因是构造打印内容失败，消费者订单id:" + order2c.getOrderid());
            }
            return;
        }

        String feiEOrderId = fePrinterService.printMsg(printerSN,printMsg,times);
        if(StringUtil.isBlank(feiEOrderId)){
            if(printTypeEnum==PrintTypeEnum.KITCHEN) {
                logger.info("打印机接单失败，原因是云打印调用结果不正确，消费者订单id:" + order2c.getOrderid());
            }else {
                logger.info("打印收银单失败，原因是云打印调用结果不正确，消费者订单id:" + order2c.getOrderid());
            }
            return;
        }

        if(printTypeEnum==PrintTypeEnum.KITCHEN) {
            logger.info("打印机接单成功，消费者订单id:" + order2c.getOrderid() + " 云打印，外部订单号：" + feiEOrderId);
        }else {
            logger.info("打印收银单成功，消费者订单id:" + order2c.getOrderid() + " 云打印，外部订单号：" + feiEOrderId);
        }

        /**
         * 打印成功调用云平台，则记录到打印日志表中
         */
        if(printTypeEnum==PrintTypeEnum.KITCHEN) {
            YdPrintOrderLog printOrderLog = new YdPrintOrderLog();
            printOrderLog.setOrderid(orderid);
            printOrderLog.setPrintOrderid(feiEOrderId);
            try {
                ydPrintOrderLogMapper.insert(printOrderLog);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
        if(printTypeEnum==PrintTypeEnum.CASHIER) {
            YdPrintCashierOrderLog printOrderLog = new YdPrintCashierOrderLog();
            printOrderLog.setOrderid(orderid);
            printOrderLog.setPrintOrderid(feiEOrderId);
            try {
                ydPrintCashierOrderLogMapper.insert(printOrderLog);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }

    }

    private String constructFeiE(Order2CVO order2c,PrintTypeEnum printTypeEnum){
        String result = null;

        if(printTypeEnum==PrintTypeEnum.KITCHEN){
            /**
             * 厨房订单
             */

            String content="<CB>请厨房接单</CB><BR>";
            content +="<B>桌号:"+order2c.getTableName()+ "序号:"+order2c.getPrintCount().toString()+"</B><BR>";
            content += "--------------------------------<BR>";
            content += "名称　　　　　           数量  <BR>";
            content += "--------------------------------<BR>";
            for (ShoppingCartSkuVO skuVO:order2c.getShoppingCartSkuVOMap().values()){
//                String name = StringUtil.abbreviateComplete(skuVO.getSkuName(),26,true,false);
                String count = "x"+skuVO.getCount().toString();
                String name = "<B>"+skuVO.getSkuName()+"</B>";
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
            String content="<CB>商品订单</CB><BR>";
            content +="<B>桌号:"+order2c.getTableName()+ "序号:"+order2c.getPrintCount().toString()+"</B><BR>";
            logger.info("order2c.getStatus() is : "+order2c.getStatus().intValue());
            if(order2c.getStatus().intValue()==1){
                if(order2c.getUseCash().intValue()==0){
                    content += "--------------------------------<BR>";
                    content += "订单已完成线上支付<BR>";
                }
                if(order2c.getUseCash().intValue()==1){
                    content += "--------------------------------<BR>";
                    content += "订单已使用现金支付<BR>";
                }
            }
            content += "--------------------------------<BR>";
            content += "名称　　　　   数量         金额<BR>";
            content += "--------------------------------<BR>";
            for (ShoppingCartSkuVO skuVO:order2c.getShoppingCartSkuVOMap().values()){
                String name = "<B>"+skuVO.getSkuName()+"</B><BR>";
                String placeholder = StringUtil.abbreviateComplete(" ",15,false,false,false);
                String count = "x"+StringUtil.abbreviateComplete(skuVO.getCount().toString(),8,false,false,false);
                String priceTotal = StringUtil.abbreviateComplete(""+AmountUtils.bigDecimalBy2(skuVO.getDisTotalAmount()),8,false,true,false);
                content = content + name +placeholder  + count + priceTotal + "<BR>";
            }
            content += "--------------------------------<BR>";
            content += "<B>合计："+AmountUtils.bigDecimalBy2(order2c.getDisTotalAmount())+"元</B><<BR>";
            if(StringUtil.isNotBlank(order2c.getDescription())) {
                content += "--------------------------------<BR>";
                content += "客户要求：<BR>" + order2c.getDescription()+"<BR>";
            }
            content += "--------------------------------<BR>";
            content += "时间："+ DateUtils.date2String(new Date())+"<BR><BR><BR><CUT>";
            result = content;

        }

        return result;

    }

    private Order2CVO doMapOrder(YdConsumerOrder consumerOrder){
        if(consumerOrder==null){
            return null;
        }
        ShoppingCartVO cartVO = JSON.parseObject(consumerOrder.getFeature(),ShoppingCartVO.class);
        Order2CVO order2CVO = doMapper.map(cartVO,Order2CVO.class);
        order2CVO.setOrderid(consumerOrder.getOrderid());
        order2CVO.setStatus(consumerOrder.getStatus());
        order2CVO.setOrderType(consumerOrder.getOrderType());
        order2CVO.setPrintCount(consumerOrder.getPrintCount());
        order2CVO.setDescription(consumerOrder.getConsumerDesc());
        order2CVO.setOrderName(consumerOrder.getOrderName());
        order2CVO.setUseCash(consumerOrder.getUseCash());
        if(order2CVO.getPayMode()==1) {
            order2CVO.setOrderDate(consumerOrder.getModifyDate());
        }else{
            order2CVO.setOrderDate(consumerOrder.getCreateDate());
        }
        return order2CVO;
    }

    @Override
    public ShopInfoVO2C getShopInfoByCustomer(String shopid) {
        if(StringUtil.isEmpty(shopid)) {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺id不能为空！");
        }
        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(ydPaypointShopInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺不存在！");
        }
        ShopInfoVO2C shopInfoVO2C = doMapper.map(ydPaypointShopInfo,ShopInfoVO2C.class);
        YdPaypointShopInfoExt paypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopid);
        if(paypointShopInfoExt!=null){
            shopInfoVO2C.setMessage(paypointShopInfoExt.getMessage());
            if(StringUtil.isNotEmpty(paypointShopInfoExt.getShopHours())) {
                YdShopHoursInfoVO shopHoursInfoVO = JSON.parseObject(paypointShopInfoExt.getShopHours(),YdShopHoursInfoVO.class);
                shopInfoVO2C.setShopHoursInfoVO(shopHoursInfoVO);
            }
        }
        return shopInfoVO2C;
    }


    /**
     * ------------------------------------以下为独立的C端商城模式-------------------------------------------
     */

    @Override
    public ShopOrder2C calculateOrderMoney(UserSession userSession, List<ShoppingOrderSkuVO> shoppingOrderSkuVOS) {
        if(shoppingOrderSkuVOS==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品列表不能为空！");
        }
        YdWeixinServiceConfig ydWeixinServiceConfig = ydWeixinServiceConfigMapper.selectByAppid(userSession.getAppid());
        if(ydWeixinServiceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商城信息有错误!");
        }
        if(StringUtil.isEmpty(ydWeixinServiceConfig.getShopid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商城信息有错误!");
        }

        logger.info("shoppingOrderSkuVOS is :"+JSON.toJSONString(shoppingOrderSkuVOS));
        ShopOrder2C shopOrder2C = new ShopOrder2C();
        shopOrder2C.setShopid(ydWeixinServiceConfig.getShopid());
        shopOrder2C.setTotalAmount(new BigDecimal("0.00"));
        shopOrder2C.setDisTotalAmount(new BigDecimal("0.00"));

        for(ShoppingOrderSkuVO orderSkuVO : shoppingOrderSkuVOS){
            WareSkuVO wareSkuVO = wareService.getWareSku(orderSkuVO.getSkuid());
            if(wareSkuVO==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), orderSkuVO.getSkuName()+"-商品信息不存在!");
            }
            if(!StringUtil.equals(wareSkuVO.getShopid(),shopOrder2C.getShopid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), wareSkuVO.getName()+"-不是此店铺的商品!");
            }

            Map<String,Object> skuPrice = specConfigService.getPriceBySku(orderSkuVO.getSkuid(),orderSkuVO.getMainSpecName(),orderSkuVO.getChildSpecName());
            if(skuPrice==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), orderSkuVO.getSkuName()+"-价格查询失败!");
            }

            orderSkuVO.setPrice((BigDecimal) skuPrice.get("price"));
            orderSkuVO.setDisprice((BigDecimal)skuPrice.get("disPrice"));

            /**
             * 计算单一订单总金额
             */
            orderSkuVO.setTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.mul(orderSkuVO.getPrice(),orderSkuVO.getCount())));
            orderSkuVO.setDisTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.mul(orderSkuVO.getDisprice(),orderSkuVO.getCount())));
            /**
             * 计算所有订单总金额
             */
            shopOrder2C.setTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.add(shopOrder2C.getTotalAmount(),orderSkuVO.getTotalAmount())));
            shopOrder2C.setDisTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.add(shopOrder2C.getDisTotalAmount(),orderSkuVO.getDisTotalAmount())));

        }

        return shopOrder2C;
    }

    /**
     * 相当于订单创建的预处理接口，为订单创建页的一些初始化信息显示服务
     * @param unionid
     * @param enterCode
     * @return
     */
    @Override
    public Map<String, Object> querPreCreateOrderInfo(String unionid, String shopid, String enterCode) {
        if(StringUtil.isEmpty(shopid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺id不能为空!");
        }

        if(StringUtil.isEmpty(enterCode)){
            enterCode = shopid;
        }

        enterCode = enterCode.trim();

        TypeEnum enterTypeEnum = TypeEnum.getTypeOfSN(enterCode);
        if(!(enterTypeEnum==TypeEnum.SHOP||enterTypeEnum==TypeEnum.QRCODE||
                enterTypeEnum==TypeEnum.DININGTABLE||enterTypeEnum==TypeEnum.CHANNELQRCODE)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "来源不明，无法创建订单!");
        }

        if(enterTypeEnum==TypeEnum.QRCODE) {
            DiningtableVO diningtable = cpService.queryTableByQrcode(enterCode);
            if (diningtable == null) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "桌位不存在!");
            }
            if (!StringUtil.equals(diningtable.getShopid(), enterCode)) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "非当前店铺的桌位!");
            }
            enterCode = diningtable.getTableid();
            enterTypeEnum = TypeEnum.DININGTABLE;
        }

        YdPaypointShopInfo paypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        YdPaypointShopInfoExt paypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopid);


        Map<String, Object> result = new HashMap<>();

        if(paypointShopInfoExt==null||paypointShopInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺信息不完整，无法创建订单!");
        }

        if(StringUtil.isNotEmpty(paypointShopInfoExt.getShopHours())) {
            YdShopHoursInfoVO shopHoursInfoVO = JSON.parseObject(paypointShopInfoExt.getShopHours(),YdShopHoursInfoVO.class);
            if(!shopInfoService.shopIsSleep(shopHoursInfoVO,paypointShopInfo.getStatus())){
                throw new YdException(ErrorCodeConstants.SHOPISSLEEP.getErrorCode(), ErrorCodeConstants.SHOPISSLEEP.getErrorMessage());
            }
        }

        ShopInfoDTO shopInfoDTO = doMapper.map(paypointShopInfo,ShopInfoDTO.class);

        ShopTradeTypeEnum tradeTypeEnum = ShopTradeTypeEnum.nameOf(shopInfoDTO.getTrade());

        result.put(Constant.DELIVERYTYPE,DeliveryTypeEnum.SHANGJIAPEI.getType());

        if(enterTypeEnum==TypeEnum.DININGTABLE){

            DiningtableVO diningtable = cpService.queryTableByTableId(shopid,enterCode);
            if(diningtable==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "桌位不存在!");
            }
            result.put(Constant.DELIVERYTYPE,DeliveryTypeEnum.ZT.getType());

        }else if(enterTypeEnum==TypeEnum.CHANNELQRCODE){
            YdCpChannel ydCpChannel = ydCpChannelMapper.selectByChannelid(enterCode);
            if(ydCpChannel==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "渠道不明!");
            }
            if(!StringUtil.equals(ydCpChannel.getShopid(),shopid)){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "非当前店铺的渠道!");
            }
            result.put(Constant.DELIVERYTYPE,ydCpChannel.getDeliveryType());

        }else if(enterTypeEnum==TypeEnum.SHOP){
            String dTy = shopInfoDTO.getFeature(Constant.DELIVERYTYPE);
            Integer deliveryType = DeliveryTypeEnum.SHANGJIAPEI.getType();
            if(StringUtil.isNotEmpty(dTy)){
                deliveryType = new Integer(dTy);
            }
            result.put(Constant.DELIVERYTYPE,deliveryType);
        }

        /**
         * 设置是先付款还是后付款-0:后付款；1:立即付款
         */
        if((tradeTypeEnum==ShopTradeTypeEnum.Catering)&&(enterTypeEnum==TypeEnum.DININGTABLE)){
            /**
             * 是餐饮店并且堂食模式的付款方式设置
             */
            if(shopInfoDTO.isPayNow()) {
                result.put(Constant.PayMode,1);
            }else {
                result.put(Constant.PayMode,0);
            }
        }else {
            if(shopInfoDTO.userPayNow()) {
                result.put(Constant.PayMode,1);
            }else {
                result.put(Constant.PayMode,0);
            }
        }

        return result;
    }

    @Override
    public BigDecimal queryUserBillBalance(String unionid, String shopid) {
        YdWeixinUserInfo userInfo = ydWeixinUserInfoMapper.selectByUnionid(unionid);
        if(userInfo == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户不存在!");
        }
        return shopCashRechargeService.getUserBillBalance(userInfo.getId(),shopid);
    }

    @Override
    public List<ShopUserBillVO> queryUserBilling(String unionid, String shopid, String year, String month, Integer pageIndex, Integer count) {
        YdWeixinUserInfo userInfo = ydWeixinUserInfoMapper.selectByUnionid(unionid);
        if(userInfo == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户不存在!");
        }
        if(month.length()==1){
            month = "0"+month;
        }
        return shopCashRechargeService.getUserBilling(userInfo.getId(),shopid,year,month,pageIndex,count);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShopOrder2C createUserOrder(UserSession userSession, String addressid, ShopOrder2C shopOrder2C) throws IllegalAccessException {

        if(shopOrder2C.getShoppingOrderSkuVOList()==null||shopOrder2C.getShoppingOrderSkuVOList().isEmpty()){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单数据不能为空!");
        }

        if(StringUtil.isNotEmpty(shopOrder2C.getDescription())){
            if(shopOrder2C.getDescription().length()>100){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户留言长度不能超过100字符!");
            }
        }

        if(StringUtil.isEmpty(shopOrder2C.getEnterCode())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单参数不完整!");
        }

        /**
         * 看看店铺是否在营业中
         */
        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopOrder2C.getShopid());
        YdPaypointShopInfoExt paypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopOrder2C.getShopid());
        if(paypointShopInfoExt!=null){
            if(StringUtil.isNotEmpty(paypointShopInfoExt.getShopHours())) {
                YdShopHoursInfoVO shopHoursInfoVO = JSON.parseObject(paypointShopInfoExt.getShopHours(),YdShopHoursInfoVO.class);
                if(!shopInfoService.shopIsSleep(shopHoursInfoVO,ydPaypointShopInfo.getStatus())){
                    throw new YdException(ErrorCodeConstants.SHOPISSLEEP.getErrorCode(), ErrorCodeConstants.SHOPISSLEEP.getErrorMessage());
                }
            }
        }

        shopOrder2C.setCpDesc("");

        TypeEnum enterTypeEnum = TypeEnum.getTypeOfSN(shopOrder2C.getEnterCode());
        if(enterTypeEnum==null){
            enterTypeEnum = TypeEnum.SHOP;
            shopOrder2C.setEnterCode(shopOrder2C.getShopid());
        }
        if(enterTypeEnum==TypeEnum.QRCODE){
            DiningtableVO diningtable = cpService.queryTableByQrcode(shopOrder2C.getEnterCode());
            if(diningtable==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "桌位不存在!");
            }
            if(!StringUtil.equals(diningtable.getShopid(),shopOrder2C.getShopid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "非当前店铺的桌位!");
            }
            shopOrder2C.setEnterCode(diningtable.getTableid());
            shopOrder2C.setEnterName(diningtable.getName());
            shopOrder2C.setDeliveryType(DeliveryTypeEnum.ZT.getType());
        }else if(enterTypeEnum==TypeEnum.DININGTABLE){

            DiningtableVO diningtable = cpService.queryTableByTableId(shopOrder2C.getShopid(),shopOrder2C.getEnterCode());
            if(diningtable==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "桌位不存在!");
            }
            shopOrder2C.setEnterCode(diningtable.getTableid());
            shopOrder2C.setEnterName(diningtable.getName());
            shopOrder2C.setDeliveryType(DeliveryTypeEnum.ZT.getType());

        }else if(enterTypeEnum==TypeEnum.CHANNELQRCODE){
            YdCpChannel ydCpChannel = ydCpChannelMapper.selectByChannelid(shopOrder2C.getEnterCode());
            if(ydCpChannel==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "渠道不明!");
            }
            if(!StringUtil.equals(ydCpChannel.getShopid(),shopOrder2C.getShopid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "非当前店铺的渠道!");
            }
            shopOrder2C.setEnterName(ydCpChannel.getChannelName());
            shopOrder2C.setDeliveryType(ydCpChannel.getDeliveryType());
        }else if(enterTypeEnum==TypeEnum.SHOP){
            shopOrder2C.setEnterName("主店");
        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "未知渠道来源!");
        }

        enterTypeEnum = TypeEnum.getTypeOfSN(shopOrder2C.getEnterCode());
        YdPaypointConsumerAddress addressInfo = null;

        if(enterTypeEnum==TypeEnum.CHANNELQRCODE||enterTypeEnum==TypeEnum.SHOP) {
            DeliveryTypeEnum deliveryTypeEnum = DeliveryTypeEnum.nameOf(shopOrder2C.getDeliveryType());
            if(deliveryTypeEnum!=DeliveryTypeEnum.ZT) {
                addressInfo = ydPaypointConsumerAddressMapper.selectByAddressid(addressid);

                if (addressInfo == null) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "地址信息不能为空!");
                }
                if (!StringUtil.equals(userSession.getUnionid(), addressInfo.getUnionid())) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "地址信息错误!");
                }
            }
        }

        List<ShoppingOrderSkuVO> shoppingOrderSkuVOS = shopOrder2C.getShoppingOrderSkuVOList();
        if(shoppingOrderSkuVOS==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品列表不能为空！");
        }
        YdWeixinServiceConfig ydWeixinServiceConfig = ydWeixinServiceConfigMapper.selectByAppid(userSession.getAppid());
        if(ydWeixinServiceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商城信息有错误!");
        }
        if(StringUtil.isEmpty(ydWeixinServiceConfig.getShopid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商城信息有错误!");
        }


        if(ydPaypointShopInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺不存在!");
        }

        ShopInfoDTO shopInfoDTO = doMapper.map(ydPaypointShopInfo,ShopInfoDTO.class);
        ShopTradeTypeEnum tradeTypeEnum = ShopTradeTypeEnum.nameOf(ydPaypointShopInfo.getTrade());
        shopOrder2C.setShopid(ydWeixinServiceConfig.getShopid());
        shopOrder2C.setTotalAmount(new BigDecimal("0.00"));
        shopOrder2C.setDisTotalAmount(new BigDecimal("0.00"));
        shopOrder2C.setOrderType(PayOrderTypeEnum.SINGLESHOP.getType());
        shopOrder2C.setOrderDate(new Date());
        /**
         * 设置是先付款还是后付款-0:后付款；1:立即付款
         */
        if((tradeTypeEnum==ShopTradeTypeEnum.Catering)&&(enterTypeEnum==TypeEnum.DININGTABLE)){
            /**
             * 是餐饮店并且堂食模式的付款方式设置
             */
            if(shopInfoDTO.isPayNow()) {
                shopOrder2C.setPayMode(1);
            }else {
                shopOrder2C.setPayMode(0);
            }
        }else {
            if(shopInfoDTO.userPayNow()) {
                shopOrder2C.setPayMode(1);
            }else {
                shopOrder2C.setPayMode(0);
            }
        }
        /**
         * 设置非渠道方式的配送方式,渠道方式在前面已经设置过了
         */
        if(enterTypeEnum!=TypeEnum.CHANNELQRCODE){
            String dTy = shopInfoDTO.getFeature(Constant.DELIVERYTYPE);
            Integer deliveryType = DeliveryTypeEnum.SHANGJIAPEI.getType();
            if(StringUtil.isNotEmpty(dTy)){
                deliveryType = new Integer(dTy);
            }
            shopOrder2C.setDeliveryType(deliveryType);
        }
        /**
         * 根据payMode设置订单的初始状态
         */
        if(shopOrder2C.getPayMode().intValue()==1){
            shopOrder2C.setStatus(UserOrderStatusEnum.WAITEPAYPRE.getStatus());
        }else {
            shopOrder2C.setStatus(UserOrderStatusEnum.WAITE.getStatus());
        }
        shopOrder2C.setDiscount(1.0);
        shopOrder2C.setIsPay(-1);

        shopOrder2C.setOrderid(RandomUtil.getSNCode(TypeEnum.CONSUMERORDER));

        for(ShoppingOrderSkuVO orderSkuVO : shoppingOrderSkuVOS){
            WareSkuVO wareSkuVO = wareService.getWareSku(orderSkuVO.getSkuid());
            if(wareSkuVO==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), orderSkuVO.getSkuName()+"-商品信息不存在!");
            }
            if(!StringUtil.equals(wareSkuVO.getShopid(),shopOrder2C.getShopid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), wareSkuVO.getName()+"-不是此店铺的商品!");
            }
            orderSkuVO.setSkuImgUrl(wareSkuVO.getWareimg());
            orderSkuVO.setSkuName(wareSkuVO.getName());
            shopOrder2C.setOrderName(orderSkuVO.getSkuName()+"等商品");


            Map<String,Object> skuPrice = specConfigService.getPriceBySku(orderSkuVO.getSkuid(),orderSkuVO.getMainSpecName(),orderSkuVO.getChildSpecName());
            if(skuPrice==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), orderSkuVO.getSkuName()+"-价格查询失败!");
            }

            orderSkuVO.setPrice((BigDecimal) skuPrice.get("price"));
            orderSkuVO.setDisprice((BigDecimal)skuPrice.get("disPrice"));


            /**
             * 计算单一订单总金额
             */
            orderSkuVO.setTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.mul(orderSkuVO.getPrice(),orderSkuVO.getCount())));
            orderSkuVO.setDisTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.mul(orderSkuVO.getDisprice(),orderSkuVO.getCount())));
            /**
             * 计算所有订单总金额
             */
            logger.info("create order : "+JSON.toJSONString(shopOrder2C));
            shopOrder2C.setTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.add(shopOrder2C.getTotalAmount(),orderSkuVO.getTotalAmount())));
            shopOrder2C.setDisTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.add(shopOrder2C.getDisTotalAmount(),orderSkuVO.getDisTotalAmount())));

        }

        if(shopOrder2C.getUsePoint()>0){

            BigDecimal deMoneyOver = this.consumptionPoint(userSession,shopOrder2C.getShopid(),shopOrder2C.getUsePoint(),shopOrder2C.getDisTotalAmount());
            if(deMoneyOver!=null){
                if(AmountUtils.changeY2F(shopOrder2C.getDeMoney())!=AmountUtils.changeY2F(deMoneyOver)){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "该订单不支持使用积分进行抵扣!");
                }
                shopOrder2C.setDeMoney(deMoneyOver);

            }else {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "该订单不支持使用积分进行抵扣!");
            }
        }else {
            shopOrder2C.setUsePoint(0);
            shopOrder2C.setDeMoney(new BigDecimal("0.00"));
        }

        /**
         * 准备保存订单
         */
        YdShopConsumerOrder ydShopConsumerOrder = new YdShopConsumerOrder();
        ydShopConsumerOrder.setChannelId(shopOrder2C.getEnterCode());
        ydShopConsumerOrder.setIsPay(-1);
        ydShopConsumerOrder.setUnionid(userSession.getUnionid());
        ydShopConsumerOrder.setOpenid(userSession.getOpenid());
        ydShopConsumerOrder.setWeixinConfigId(userSession.getWeixinConfigId());
        ydShopConsumerOrder.setPayMode(shopOrder2C.getPayMode());
        ydShopConsumerOrder.setShopid(shopOrder2C.getShopid());
        ydShopConsumerOrder.setStatus(shopOrder2C.getStatus());
        ydShopConsumerOrder.setOrderid(shopOrder2C.getOrderid());
        ydShopConsumerOrder.setOrderName(shopOrder2C.getOrderName());
        ydShopConsumerOrder.setOrderType(shopOrder2C.getOrderType());
        ydShopConsumerOrder.setTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.sub(shopOrder2C.getDisTotalAmount(),shopOrder2C.getDeMoney())));


        if(addressInfo!=null) {
            Map<String,Object> deliverMap = new HashMap<>();
            UserAddressInfoVO userAddressInfo = doMapper.map(addressInfo, UserAddressInfoVO.class);
            deliverMap.put(Constant.USERADDRESSINFO,userAddressInfo);
            ydShopConsumerOrder.setDelivery(JSON.toJSONString(deliverMap));
        }

        if(AmountUtils.changeY2F(shopOrder2C.getDisTotalAmount())<=0){
            shopOrder2C.setIsPay(0);
            ydShopConsumerOrder.setIsPay(0);

        }
        if(shopOrder2C.getPayMode().intValue()==1){
            /**
             * 先付款订单
             */
            if(AmountUtils.changeY2F(shopOrder2C.getDisTotalAmount())<=0){
                shopOrder2C.setStatus(UserOrderStatusEnum.PAYFINISH.getStatus());
                ydShopConsumerOrder.setStatus(shopOrder2C.getStatus());
            }
            /**
             * 发订单超时取消消息（24小时没有支付的先付款类型订单将关闭，8小时商家没有接单的货到付款订单关闭）
             */
            Map<String,Object> msgMap = new HashMap<>();
            msgMap.put(Constant.MQTAG,MqTagEnum.USERORDERTIMEOUT.getTag());
            msgMap.put(Constant.MQBODY,ydShopConsumerOrder.getOrderid());
            mqMessageService.sendMessage(ydShopConsumerOrder.getOrderid()+"timeout",MqTagEnum.USERORDERTIMEOUT,JSON.toJSONString(msgMap),86400000);
        }else if(shopOrder2C.getPayMode().intValue()==0){
            /**
             * 货到付款订单
             */
            /**
             * 发订单超时取消消息（24小时没有支付的先付款类型订单将关闭，8小时商家没有接单的货到付款订单关闭）
             */
            Map<String,Object> msgMap = new HashMap<>();
            msgMap.put(Constant.MQTAG,MqTagEnum.USERORDERTIMEOUT.getTag());
            msgMap.put(Constant.MQBODY,ydShopConsumerOrder.getOrderid());
            mqMessageService.sendMessage(ydShopConsumerOrder.getOrderid()+"timeout",MqTagEnum.USERORDERTIMEOUT,JSON.toJSONString(msgMap),28800000);
        }

        /**
         * 保险起见，加一层分布式事务缓存锁,超时时间为1秒
         */
        boolean lockIsTrue = redisManager.lockWithTimeout(shopOrder2C.getShopid()+"neworder",shopOrder2C.getOrderid(),1500,1000);
        boolean isFirstOrderToday = false;
        YdShoporderStatistics shoporderStatistics = ydShoporderStatisticsMapper.selectByShopidRowLock(shopOrder2C.getShopid(), new Date());
        if (shoporderStatistics == null) {
            isFirstOrderToday = true;
            shoporderStatistics = new YdShoporderStatistics();
            shoporderStatistics.setShopid(shopOrder2C.getShopid());
            shoporderStatistics.setOrderCount(0);
            shoporderStatistics.setOrderDate(new Date());
            String bagStr = shopInfoDTO.getFeature(Constant.BAGORDERNUM);
            if(StringUtil.isBlank(bagStr)){
                shoporderStatistics.setBagCount(0);
            }else {
                shoporderStatistics.setBagCount(Integer.valueOf(bagStr));
            }
            shoporderStatistics.setReceiveAmount(new BigDecimal("0.00"));
        }

        shoporderStatistics.setOrderCount(shoporderStatistics.getOrderCount()+1);

        /**
         * 创建订单，写订单表
         */
        shopOrder2C.setPrintCount(shoporderStatistics.getOrderCount());
        ydShopConsumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
        if(ydShopConsumerOrderMapper.insert(ydShopConsumerOrder)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "订单创建失败!");
        }

        /**
         * 写订单统计表
         */
        if(!isFirstOrderToday){
            shoporderStatistics.setLastid(ydShopConsumerOrder.getId());
            if(ydShoporderStatisticsMapper.updateByPrimaryKeySelective(shoporderStatistics)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "订单创建失败，请稍后再试！");
            }

        }else {
            shoporderStatistics.setFirstid(ydShopConsumerOrder.getId());
            shoporderStatistics.setLastid(ydShopConsumerOrder.getId());
            shoporderStatistics.setWithdrawals(0);
            if(ydShoporderStatisticsMapper.insert(shoporderStatistics)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "订单创建失败，请稍后再试！");
            }

        }

        if(lockIsTrue){
            /**
             * 释放锁
             */
            redisManager.releaseLock(shopOrder2C.getShopid()+"neworder",shopOrder2C.getOrderid());
        }

        /**
         * 发消息同步订单信息到opensearch
         */
        Map<String,Object> msgMap = new HashMap<>();
        msgMap.put(Constant.MQTAG,MqTagEnum.USERORDERNEW.getTag());
        msgMap.put(Constant.MQBODY,ydShopConsumerOrder.getOrderid());
        String msgId = mqMessageService.sendMessage(ydShopConsumerOrder.getOrderid()+"new",ydShopConsumerOrder.getOrderid(),MqTagEnum.USERORDERNEW,JSON.toJSONString(msgMap));

        /**
         * 如果后付款订单或者0元已经支付的订单，需要发打印消息
         */
        if(shopOrder2C.getPayMode().intValue()==0||shopOrder2C.getIsPay().intValue()>=0) {
            shopOrderService.sendPrintMessage(shopOrder2C.getShopid(), shopOrder2C.getOrderid());
        }


        return shopOrder2C;
    }

    protected String updateOrder2OpenSearch(String orderid){
        /**
         * 发消息同步订单信息到opensearch
         */
        Map<String,Object> msgMap = new HashMap<>();
        msgMap.put(Constant.MQTAG,MqTagEnum.USERORDERUPDATE.getTag());
        msgMap.put(Constant.MQBODY,orderid);
        String msgId = mqMessageService.sendMessage(orderid+"update",orderid,MqTagEnum.USERORDERUPDATE,JSON.toJSONString(msgMap));
        return msgId;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelUserOrder(UserSession userSession, String orderid) {
        YdShopConsumerOrder ydShopConsumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(ydShopConsumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单不存在!");
        }
        if(!StringUtil.equals(userSession.getUnionid(),ydShopConsumerOrder.getUnionid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(UserOrderStatusEnum.nameOf(ydShopConsumerOrder.getStatus())==UserOrderStatusEnum.WAITE||
                UserOrderStatusEnum.nameOf(ydShopConsumerOrder.getStatus())==UserOrderStatusEnum.WAITEPAYPRE){
            ShopOrder2C shopOrder2C = JSON.parseObject(ydShopConsumerOrder.getFeature(),ShopOrder2C.class);
            /**
             * 回退积分
             */
            if(shopOrder2C.getUsePoint()>0){
                YdWeixinUserInfo weixinUserInfo = ydWeixinUserInfoMapper.selectByUnionid(ydShopConsumerOrder.getUnionid());
                if(weixinUserInfo!=null) {
                    shopInfoService.rebackConsumptionPoint(weixinUserInfo.getId(), UserSourceEnum.WEIXIN.getType(),ydShopConsumerOrder.getShopid(),shopOrder2C.getUsePoint());
                }
            }
            ydShopConsumerOrder.setTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.add(ydShopConsumerOrder.getTotalAmount(),shopOrder2C.getDeMoney())));
            shopOrder2C.setUsePoint(0);
            shopOrder2C.setDeMoney(new BigDecimal("0.00"));

            shopOrder2C.setStatus(UserOrderStatusEnum.CANCEL.getStatus());
            ydShopConsumerOrder.setStatus(shopOrder2C.getStatus());
            ydShopConsumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
            if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(ydShopConsumerOrder)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "订单取消失败!");
            }

        }
        this.updateOrder2OpenSearch(ydShopConsumerOrder.getOrderid());

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean confirmUserOrder(UserSession userSession, String orderid) {
        orderid = orderid.trim();
        YdShopConsumerOrder ydShopConsumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(ydShopConsumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单不存在!");
        }
        if(!StringUtil.equals(userSession.getUnionid(),ydShopConsumerOrder.getUnionid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        UserOrderStatusEnum userOrderStatusEnum = UserOrderStatusEnum.nameOf(ydShopConsumerOrder.getStatus());

        if(userOrderStatusEnum==UserOrderStatusEnum.SENDOUT){
            ShopOrder2C shopOrder2C = JSON.parseObject(ydShopConsumerOrder.getFeature(),ShopOrder2C.class);
            if(shopOrder2C.getIsPay().intValue()>=0){
                shopOrder2C.setStatus(UserOrderStatusEnum.FINISH.getStatus());
            }else {
                shopOrder2C.setStatus(UserOrderStatusEnum.WAITEPAYPOST.getStatus());
            }
            ydShopConsumerOrder.setStatus(shopOrder2C.getStatus());
            ydShopConsumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
            if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(ydShopConsumerOrder)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "确认收货失败!");
            }
            this.updateOrder2OpenSearch(ydShopConsumerOrder.getOrderid());

        }else {
            this.updateOrder2OpenSearch(ydShopConsumerOrder.getOrderid());
            logger.error(ydShopConsumerOrder.getOrderid()+"状态为："+UserOrderStatusEnum.nameOf(ydShopConsumerOrder.getStatus()).getName());
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "当前状态不能进行确认收货操作!");
        }
        return true;
    }

    @Override
    public List<SearchOrderDataResultVO> queryUserOrderList(String unionid, String shopid, Integer pageIndex, Integer count, Integer searchType) {
        SearchUserOrderTypeEnum sot = SearchUserOrderTypeEnum.nameOf(searchType);
        if(sot==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "查询类型错误!");
        }
        List<SearchOrderDataResultVO> result = null;

        if(sot==SearchUserOrderTypeEnum.USERALLINSHOP){
            result = orderSearchService.queryAllUserOrder(unionid,shopid,pageIndex,count);
        }else if(sot == SearchUserOrderTypeEnum.USERWAITEPAYINSHOP){
            result = orderSearchService.queryUserOrderByNeedPay(unionid,shopid,pageIndex,count);
        }else if(sot == SearchUserOrderTypeEnum.USERWATIESENDOUTINSHOP){
            result = orderSearchService.queryUserOrderByWaitSendOut(unionid,shopid,pageIndex,count);
        }else if(sot == SearchUserOrderTypeEnum.USERWATIECONFIRMINSHOP){
            result = orderSearchService.queryUserOrderBySendOut(unionid,shopid,pageIndex,count);
        }

        return result;
    }

    @Override
    public ShopOrderExt2C queryUserOrderDetail(String unionid, String orderid) throws IllegalAccessException {
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderid(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单不存在!");
        }
        if(!StringUtil.equals(unionid,consumerOrder.getUnionid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
        shopOrder2C.setOrderDate(consumerOrder.getCreateDate());
        shopOrder2C.setStatus(consumerOrder.getStatus());
        shopOrder2C.setCpDesc(consumerOrder.getShopDesc());

        /**
         * 积分计算
         */
        if(consumerOrder.getIsPay()<0&&consumerOrder.getStatus().intValue()>=0&&consumerOrder.getStatus().intValue()<UserOrderStatusEnum.FINISHREFUND.getStatus().intValue()) {
            if (shopOrder2C.getUsePoint() <= 0) {
                Map<String, Object> resultDePointData = this.getDeductionMoney(unionid, consumerOrder.getShopid(), consumerOrder.getTotalAmount());
                if (resultDePointData != null) {
                    shopOrder2C.setUsePoint((Integer) resultDePointData.get(Constant.POINT));
                    shopOrder2C.setDeMoney((BigDecimal) resultDePointData.get(Constant.MONEY));
                }
            }
        }

        /**判断是不是堂食**/
        shopOrder2C.setIsIndoor(0);
        if(StringUtil.isNotEmpty(shopOrder2C.getEnterCode())) {
            TypeEnum typeEnum = TypeEnum.getTypeOfSN(shopOrder2C.getEnterCode());
            if(typeEnum==TypeEnum.DININGTABLE){
                shopOrder2C.setIsIndoor(1);
            }
        }

        UserOrderDeliveryInfoVO deliveryInfoVO = null;
        UserAddressInfoVO userAddressInfoVO = null;
        Map<String,Object> deliveryMap = null;
        if(StringUtil.isNotEmpty(consumerOrder.getDelivery())){
            deliveryMap = JSON.parseObject(consumerOrder.getDelivery(),Map.class);
            if(deliveryMap.containsKey(Constant.DELIVERYINFO)){
                deliveryInfoVO = ((JSONObject)deliveryMap.get(Constant.DELIVERYINFO)).toJavaObject(UserOrderDeliveryInfoVO.class);
            }
            if(deliveryMap.containsKey(Constant.USERADDRESSINFO)){
                userAddressInfoVO = ((JSONObject)deliveryMap.get(Constant.USERADDRESSINFO)).toJavaObject(UserAddressInfoVO.class);
            }

        }
        ShopOrderExt2C shopOrderExt2C = new ShopOrderExt2C();
        shopOrderExt2C.setShopOrder2C(shopOrder2C);
        shopOrderExt2C.setDeliveryInfoVO(deliveryInfoVO);
        shopOrderExt2C.setUserAddressInfoVO(userAddressInfoVO);
        shopOrderExt2C.setActualAmount(consumerOrder.getTotalAmount());

        return shopOrderExt2C;
    }

    @Override
    public ShopOrderExt2C queryUserOrderDetail(UserSession userSession, String orderid) throws IllegalAccessException {
        String unionid = userSession.getUnionid();
        return this.queryUserOrderDetail(unionid,orderid);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> payUserOrder(UserSession userSession, String orderid, Integer payType) throws Exception {
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单不存在!");
        }

        logger.info("consumerOrder is :"+JSON.toJSONString(consumerOrder));


        YdWeixinUserInfo userInfo = ydWeixinUserInfoMapper.selectByUnionid(userSession.getUnionid());
        if(userInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户不存在!");
        }
        BigDecimal balance = shopCashRechargeService.getUserBillBalance(userInfo.getId(),consumerOrder.getShopid());

        if(AmountUtils.changeY2F(balance)>=AmountUtils.changeY2F(consumerOrder.getTotalAmount())){

            /**
             * 优先使用链贝付款
             */
            if(shopCashRechargeService.updateShopUserCashBalance(userInfo.getId(),consumerOrder.getShopid(),
                    consumerOrder.getOrderid(),consumerOrder.getTotalAmount())){
                /**
                 * 说明使用链贝支付成功，直接返回，否则继续使用微信支付
                 */
                Map<String, Object> result = new HashMap<>();
                result.put("success",true);
                result.put(Constant.ISPAY,2);
                return result;
            }

        }


        YdWeixinServiceConfig serviceConfig = ydWeixinServiceConfigMapper.selectByWeixinConfigId(consumerOrder.getWeixinConfigId());
        if(serviceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单对应的APP授权信息不存在!");
        }

        PayTypeEnum payTypeEnum = PayTypeEnum.nameOf(payType);
        if(StringUtil.contains(
                    DiamondWeiXinInfoConfigHolder.getInstance().getPublicMchIds(),
                    serviceConfig.getWeixinpaySubMchId())){
            payTypeEnum = PayTypeEnum.WEIXIN2B;
        }

        WeiXinPayRequestDO weiXinPayRequestDO = new WeiXinPayRequestDO();
        if(payTypeEnum==PayTypeEnum.WEIXINS) {
            weiXinPayRequestDO.setSub_appid(serviceConfig.getAppid());
            weiXinPayRequestDO.setSub_mch_id(serviceConfig.getWeixinpaySubMchId());
            weiXinPayRequestDO.setSub_openid(userSession.getOpenid());
        }else if(payTypeEnum==PayTypeEnum.WEIXIN2B) {
            weiXinPayRequestDO.setAppid(serviceConfig.getAppid());
            weiXinPayRequestDO.setMch_id(serviceConfig.getWeixinpaySubMchId());
            weiXinPayRequestDO.setOpenid(userSession.getOpenid());
        }
        weiXinPayRequestDO.setBody("引灯智能店铺-"+consumerOrder.getOrderName());
        weiXinPayRequestDO.setOut_trade_no(orderid);
        weiXinPayRequestDO.setTotal_fee(AmountUtils.changeY2F(consumerOrder.getTotalAmount()));
        weiXinPayRequestDO.setSpbill_create_ip(userSession.getUserIp());
        weiXinPayRequestDO.setTrade_type("JSAPI");

        Map<String,Object> result = weiXinPayService.callPrePay(weiXinPayRequestDO);
        result.put(Constant.ISPAY,-1);
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> cashRecharge(UserSession userSession,String shopid, Integer cashType) throws Exception {
        YdWeixinServiceConfig serviceConfig = ydWeixinServiceConfigMapper.selectByWeixinConfigId(userSession.getWeixinConfigId());
        if(serviceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单对应的APP授权信息不存在!");
        }

        if(!StringUtil.equals(serviceConfig.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺一致性验证失败!");
        }

        ShopInfoDTO shopInfoDTO = shopInfoService.getShopInfo(shopid);
        if(shopInfoDTO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺信息不存在!");
        }

        YdWeixinUserInfo userInfo = ydWeixinUserInfoMapper.selectByUnionid(userSession.getUnionid());
        if(userInfo == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户信息错误!");
        }

        String billId = RandomUtil.getSNCode(TypeEnum.CASHRECHARGE);

        ShopCashRechargeConfigVO cashRechargeConfigVO = shopCashRechargeService.queryShopCashRechargeConfig2C(shopid);
        if(cashRechargeConfigVO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "该店铺没有开通充值功能!");
        }
        if(cashRechargeConfigVO.getStatus().intValue()==1){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "该店铺充值功能暂停中，请稍候再试!");
        }
        BigDecimal cashRecharge = shopCashRechargeService.queryCashRecharge(cashRechargeConfigVO,cashType);
        BigDecimal cashGive = shopCashRechargeService.queryCashGive(cashRechargeConfigVO,cashType);

        if(!shopCashRechargeService.newPreBill(userInfo.getId(),shopid,billId,cashRecharge,cashGive)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "充值预热失败，请稍候再试!");
        }

        PayTypeEnum payTypeEnum = PayTypeEnum.WEIXINS;
        if(StringUtil.contains(
                DiamondWeiXinInfoConfigHolder.getInstance().getPublicMchIds(),
                serviceConfig.getWeixinpaySubMchId())){
            payTypeEnum = PayTypeEnum.WEIXIN2B;
        }

        WeiXinPayRequestDO weiXinPayRequestDO = new WeiXinPayRequestDO();
        if(payTypeEnum==PayTypeEnum.WEIXINS) {
            weiXinPayRequestDO.setSub_appid(serviceConfig.getAppid());
            weiXinPayRequestDO.setSub_mch_id(serviceConfig.getWeixinpaySubMchId());
            weiXinPayRequestDO.setSub_openid(userSession.getOpenid());
        }else if(payTypeEnum==PayTypeEnum.WEIXIN2B) {
            weiXinPayRequestDO.setAppid(serviceConfig.getAppid());
            weiXinPayRequestDO.setMch_id(serviceConfig.getWeixinpaySubMchId());
            weiXinPayRequestDO.setOpenid(userSession.getOpenid());
        }
        weiXinPayRequestDO.setBody(shopInfoDTO.getName()+"-"+userInfo.getNick()+"-"+"购买链贝!");
        weiXinPayRequestDO.setOut_trade_no(billId);
        weiXinPayRequestDO.setTotal_fee(AmountUtils.changeY2F(cashRecharge));
        weiXinPayRequestDO.setSpbill_create_ip(userSession.getUserIp());
        weiXinPayRequestDO.setTrade_type("JSAPI");

        return weiXinPayService.callPrePay(weiXinPayRequestDO);
    }

    @Override
    public Map<String, Long> queryCardPointValue(UserSession userSession, String shopid) {
        YdWeixinUserInfo weixinUserInfo = userinfo2ShopService.selectWeiXinUserInfo(userSession.getUnionid());
        if(weixinUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户不存在!");
        }
        Map<String,Long> result = shopInfoService.getCardPointValue(weixinUserInfo.getId(),UserSourceEnum.WEIXIN.getType(),shopid);
        return result;
    }

    @Override
    public Integer rewardConsumptionPoint(UserSession userSession, String shopid, BigDecimal amount) {
        YdWeixinUserInfo weixinUserInfo = userinfo2ShopService.selectWeiXinUserInfo(userSession.getUnionid());
        if(weixinUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户不存在!");
        }
        return shopInfoService.rewardConsumptionPoint(weixinUserInfo.getId(),UserSourceEnum.WEIXIN.getType(),shopid,amount,false);
    }

    @Override
    public BigDecimal consumptionPoint(UserSession userSession, String shopid, Integer point, BigDecimal orderMoney) throws IllegalAccessException {
        YdWeixinUserInfo weixinUserInfo = userinfo2ShopService.selectWeiXinUserInfo(userSession.getUnionid());
        if(weixinUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户不存在!");
        }
        return shopInfoService.consumptionPoint(weixinUserInfo.getId(),UserSourceEnum.WEIXIN.getType(),shopid,point,orderMoney);
    }

    @Override
    public Map<String, Object> getDeductionMoney(UserSession userSession, String shopid, BigDecimal orderMoney) throws IllegalAccessException {
        YdWeixinUserInfo weixinUserInfo = userinfo2ShopService.selectWeiXinUserInfo(userSession.getUnionid());
        if(weixinUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户不存在!");
        }
        return shopInfoService.getDeductionMoney(weixinUserInfo.getId(),UserSourceEnum.WEIXIN.getType(),shopid,orderMoney);
    }

    @Override
    public Map<String, Object> getDeductionMoney(String unionid, String shopid, BigDecimal orderMoney) throws IllegalAccessException {
        YdWeixinUserInfo weixinUserInfo = userinfo2ShopService.selectWeiXinUserInfo(unionid);
        if(weixinUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户不存在!");
        }
        return shopInfoService.getDeductionMoney(weixinUserInfo.getId(),UserSourceEnum.WEIXIN.getType(),shopid,orderMoney);
    }

    @Override
    public BigDecimal calculatePrice(String unionid, BigDecimal price, Integer count) {
        YdWeixinUserInfo weixinUserInfo = userinfo2ShopService.selectWeiXinUserInfo(unionid);
        if(weixinUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户不存在!");
        }
        return AmountUtils.bigDecimalBy2(AmountUtils.mul(price,count));
    }

    @Override
    public Map<String, Object> queryShopStauts(String unionid, String shopid) {
        YdWeixinUserInfo weixinUserInfo = userinfo2ShopService.selectWeiXinUserInfo(unionid);
        if(weixinUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户不存在!");
        }
        ShopInfoVO2C shopInfo = this.getShopInfoByCustomer(shopid);
        if(shopInfo ==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺不存在!");
        }

        Map<String,Object> result = new HashMap<>();
        result.put(Constant.ISSLEEP,shopInfoService.shopIsSleep(shopInfo.getShopHoursInfoVO(),shopInfo.getStatus()));

        return result;
    }


    public static void main(String[] args){
//        String jsonStr = "{\"orderid\":\"2928151687386366850649718\",\"payMode\":1,\"qrCode\":\"290815160035594073\",\"shopid\":\"29101515513271935\",\"shoppingCartSkuVOMap\":{\"29161515747786786\":{\"count\":1,\"price\":56.00,\"skuImgUrl\":\"http://paypoint.oss-cn-hangzhou.aliyuncs.com/29101515513271935/5276EC8623553BFB181FACBD48EE2FF0.jpg\",\"skuName\":\"土豆炖排骨\",\"totalAmount\":56.000},\"291615157445531870\":{\"count\":1,\"price\":23.00,\"skuImgUrl\":\"http://paypoint.oss-cn-hangzhou.aliyuncs.com/29101515513271935/5276EC8623553BFB181FACBD48EE2FF0.jpg\",\"skuName\":\"宫爆鸡丁\",\"totalAmount\":23.000}},\"tableName\":\"桌位2号\",\"totalAmount\":79.000}";
//
//        ShoppingCartVO order = JSON.parseObject(jsonStr,ShoppingCartVO.class);
//        for(String key:order.getShoppingCartSkuVOMap().keySet()){
//            ShoppingCartSkuVO cartSkuVO = order.getShoppingCartSkuVOMap().get(key);
//            System.out.println(cartSkuVO.getSkuName());
//        }

        Integer status = 1;

        System.out.println(UserOrderStatusEnum.nameOf(status)==UserOrderStatusEnum.WAITEPAYPRE);

    }


}
