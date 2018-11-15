package com.yd.ydsp.biz.cp.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondYDGoodsConfig;
import com.yd.ydsp.biz.config.DiamondYdPayPointConfigHolder;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.cp.CpMallServie;
import com.yd.ydsp.biz.cp.ShopInfoService;
import com.yd.ydsp.biz.cp.model.GoodsInfoVO;
import com.yd.ydsp.biz.cp.model.YdGoodsInfoVO;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.pay.YdPayService;
import com.yd.ydsp.biz.pay.model.YdPayResponse;
import com.yd.ydsp.biz.pay.model.YeePayRequestDO;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.client.domian.ShopInfoDTO;
import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.client.domian.paypoint.CPOrderDTO;
import com.yd.ydsp.client.domian.paypoint.CpMallOrderVO;
import com.yd.ydsp.client.domian.paypoint.CpMallSubOrderVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.OrderStatusEnum;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;
import com.yd.ydsp.common.enums.StatusEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.AmountUtils;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.YdPaypointCpOrder;
import com.yd.ydsp.dal.entity.YdPaypointCpSubOrder;
import com.yd.ydsp.dal.entity.YdPaypointShopInfo;
import com.yd.ydsp.dal.mapper.YdPaypointCpOrderMapper;
import com.yd.ydsp.dal.mapper.YdPaypointCpSubOrderMapper;
import com.yd.ydsp.dal.mapper.YdPaypointShopInfoMapper;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * @author zengyixun
 * @date 18/1/15
 */
public class CpMallServiceImpl implements CpMallServie {
    public static final Logger logger = LoggerFactory.getLogger(CpMallServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;

    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    private YdPaypointCpSubOrderMapper ydPaypointCpSubOrderMapper;

    @Resource
    private YdPaypointCpOrderMapper ydPaypointCpOrderMapper;

    @Resource
    private UserinfoService userinfoService;

    @Resource
    private YdPayService ydPayService;
    @Resource
    private ShopInfoService shopInfoService;
    @Resource
    private MqMessageService mqMessageService;

    @Override
    public Map<String, Object> getGoodsInfo(String openid, String shopid) {
        /**
         * 先构建通用的商品信息
         */
        Map<String, Object> result = new HashMap<>();
        List<YdGoodsInfoVO> goodsInfoVOS = DiamondYDGoodsConfig.config;
        if(StringUtil.isBlank(shopid)){
            result.put(Constant.YDGOODS,goodsInfoVOS);
            return result;
        }
        if(!userinfoService.checkIsManager(openid,shopid)){
            /**
             * 不是这个店铺权限的人，或者shopid为空，只显示基本商品
             */
            result.put(Constant.YDGOODS,goodsInfoVOS);
            return result;
        }

        /**
         * 再根据shopid来构建立套餐升级信息与加油包商品信息
         */

        Map<String,Object> setMealObj = this.getSetMeal(shopid);
        Integer remanent = shopInfoService.queryRemanent(shopid,true).intValue();
        logger.info("remanent is :"+remanent.toString());
        if(setMealObj!=null&&remanent>0){
            String typeName = (String)setMealObj.get(Constant.SETMEALTYPE);
            Integer orderNum = (Integer) setMealObj.get(Constant.BAGORDERNUM);
            Integer id = (Integer) setMealObj.get("id");
            String name = (String) setMealObj.get("name");
            logger.info("id is :"+id.toString());
            logger.info("orderNum is :"+orderNum.toString());
            logger.info("typeName is :"+typeName);
            logger.info("name is :"+name);
            if(!StringUtil.equals(typeName.trim().toLowerCase(),Constant.DIAMOND_VERSION.toLowerCase())){
                /**
                 * 说明需要进行套餐升级，以及有加油包，注意配置时，只能是最后一个套餐才能没有订单量的限制
                 */
                YdGoodsInfoVO bagOrderInfo = new YdGoodsInfoVO();
                bagOrderInfo.setPrice((Integer) setMealObj.get("bagPrice"));
                bagOrderInfo.setPriceView(name+"："+ AmountUtils.changeF2Y(bagOrderInfo.getPrice())+"/"+orderNum.toString()+"单");
                bagOrderInfo.setGoodName(name+"加油包");
                bagOrderInfo.setGoodId(typeName);
                result.put("bagInfo",bagOrderInfo);
                /**
                 * 设置升级信息
                 */

                Map<String,String> upgradeType = new LinkedHashMap<>();

                Map<String,Object> upgradeObj = new LinkedHashMap<>();

                List<Map<String,Object>> shopSetMeals = shopInfoService.getSetMealInfo();
                if(shopSetMeals!=null){
                    for(Map<String,Object> setMealInfo : shopSetMeals){
                        if((Integer)setMealInfo.get("id")>id){
                            upgradeType.put((String)setMealInfo.get(Constant.SETMEALTYPE),(String)setMealInfo.get("name"));
                        }
                    }
                    upgradeObj.put("upgradeType",upgradeType);
                    /**
                     * 设置当套餐的剩余天数
                     */
                    upgradeObj.put("remanent",remanent);
                    result.put("upgrade",upgradeObj);
                }

            }

        }
        result.put(Constant.YDGOODS,goodsInfoVOS);

        return result;
    }

    @Override
    public GoodsInfoVO getTotalAmountByGoods(GoodsInfoVO goodsInfoVO) throws IllegalAccessException {
        try {
            logger.info("goodsInfoStr is :"+JSON.toJSONString(goodsInfoVO));
//            goodsInfoVO = JSON.parseObject(goodsInfoStr,GoodsInfoVO.class);
            logger.info("goodsInfoVO is :"+JSON.toJSONString(goodsInfoVO));
            if(goodsInfoVO==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),ErrorCodeConstants.PARAM_ERROR.getErrorMessage());
            }
        }catch (Exception ex){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),ErrorCodeConstants.PARAM_ERROR.getErrorMessage());
        }

        Map<String ,Integer> amountResult = this.getTotalAmountByUpgrade(goodsInfoVO.getShopid(),goodsInfoVO.getUpgradeType());
        Integer totalUpgrade = amountResult.get("totalAmount");
//        /**
//         * 如果计算出来升级套餐为0，则保底为1毛钱
//         */
//        if(totalUpgrade<=0) {
//            totalUpgrade = 10;
//        }
        goodsInfoVO.setUpgradeTotalAmount(AmountUtils.changeF2YWithBigDecimal(totalUpgrade));

        goodsInfoVO.setRemanent(amountResult.get("remanent"));

        Integer totalBag = this.getTotalAmountByBagOrder(goodsInfoVO.getShopid(),goodsInfoVO.getBagNumber());
        if(totalBag>0){
            goodsInfoVO.setBagTotalAmount(AmountUtils.changeF2YWithBigDecimal(totalBag));
        }else {
            goodsInfoVO.setBagTotalAmount(AmountUtils.changeF2YWithBigDecimal(0));
        }
        Integer total=0;
        total = total + totalUpgrade + totalBag;
        goodsInfoVO.setTotalAmount(AmountUtils.changeF2YWithBigDecimal(total));
        if(goodsInfoVO.getGoods()==null){
            return goodsInfoVO;
        }
        List<YdGoodsInfoVO> ydGoodsInfoVOList = DiamondYDGoodsConfig.config;
        if(ydGoodsInfoVOList==null||ydGoodsInfoVOList.size()<=0){
            return goodsInfoVO;
        }

        Map<String,BigDecimal> totalGoodsAmount = new HashMap<>();
        for(String goodsId : goodsInfoVO.getGoods().keySet()){
            for(YdGoodsInfoVO ydGoodsInfoVO:ydGoodsInfoVOList){
                if(goodsId.equals(ydGoodsInfoVO.getGoodId())){
                    /**
                     * 找到了对应的商品，开始计算
                     */
                    Integer goodsAmount = AmountUtils.mul(ydGoodsInfoVO.getPrice(),goodsInfoVO.getGoods().get(goodsId)).toBigInteger().intValue();
                    if(goodsAmount>0){
                        total = total + goodsAmount;
                        totalGoodsAmount.put(goodsId,AmountUtils.changeF2YWithBigDecimal(goodsAmount));
                    }
                    break;
                }
            }
        }
        goodsInfoVO.setTotalAmount(AmountUtils.changeF2YWithBigDecimal(total));

        goodsInfoVO.setGoodsAmount(totalGoodsAmount);

        return goodsInfoVO;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CpMallOrderVO submitOrder(String openid,GoodsInfoVO goodsInfo) throws IllegalAccessException {


        if(goodsInfo == null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"创建订单失败！，请重试！");
        }


        if(!(userinfoService.checkIsOwner(openid,goodsInfo.getShopid())||userinfoService.checkIsManager(openid,goodsInfo.getShopid())||(userinfoService.checkIsWaiter(openid,goodsInfo.getShopid())))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        goodsInfo = this.getTotalAmountByGoods(goodsInfo);
        CPOrderDTO cpOrderDTO = new CPOrderDTO();
        cpOrderDTO.setShopid(goodsInfo.getShopid());
        cpOrderDTO.setBuyerMessage(goodsInfo.getBuyerMessage());
        Map<String,Object> setMealObj = this.getSetMeal(goodsInfo.getShopid());
        String typeName = (String)setMealObj.get(Constant.SETMEALTYPE);
        cpOrderDTO.addFeature(Constant.CP_ORDER_TYPE,Constant.YDGOODS);
        cpOrderDTO.addFeature(Constant.SETMEALTYPE,typeName);
        if(StringUtil.isNotBlank(goodsInfo.getUpgradeType())) {
            if(goodsInfo.getUpgradeTotalAmount()==null){
                goodsInfo.setUpgradeTotalAmount(new BigDecimal("0.00"));
            }
            if (AmountUtils.changeY2F(goodsInfo.getUpgradeTotalAmount()) >= 0) {
                cpOrderDTO.addFeature(Constant.UPSETMEAL, goodsInfo.getUpgradeType());
                cpOrderDTO.addFeature(Constant.UPSETMEAL_REMANENT, goodsInfo.getRemanent().toString());
                cpOrderDTO.addFeature(Constant.UPSETMEAL_AMOUNT, AmountUtils.bigDecimal2Str(goodsInfo.getUpgradeTotalAmount()));
            }
        }
        if(goodsInfo.getBagTotalAmount()!=null) {
            if (AmountUtils.changeY2F(goodsInfo.getBagTotalAmount()) > 0) {
                Integer orderNum = (Integer)setMealObj.get(Constant.BAGORDERNUM);
                cpOrderDTO.addFeature(Constant.BAGORDERNUM, String.valueOf(goodsInfo.getBagNumber()*orderNum));
                cpOrderDTO.addFeature(Constant.BAGORDERAMOUNT, AmountUtils.bigDecimal2Str(goodsInfo.getBagTotalAmount()));

            }
        }
        cpOrderDTO.setOrderid(RandomUtil.getSNCode(TypeEnum.CPORDER));
        cpOrderDTO.setTotalAmount(goodsInfo.getTotalAmount());
        cpOrderDTO.setStatus(0);

        /**
         * 判断是否需要增加子订单
         */
        if(goodsInfo.getGoods()!=null) {
            logger.info("CPMall submitOrder goodsInfo:"+JSON.toJSONString(goodsInfo));
            for (String goodsId : goodsInfo.getGoods().keySet()) {
                YdPaypointCpSubOrder subOrder = new YdPaypointCpSubOrder();
                subOrder.setOrderid(cpOrderDTO.getOrderid());
                subOrder.setSubOrderid(RandomUtil.getSNCode(TypeEnum.CPSUBORDER));
                subOrder.setGoodsid(goodsId);
                subOrder.setGoodsCount(goodsInfo.getGoods().get(goodsId));
                logger.info("goodsId:"+goodsId);
                subOrder.setAmount(goodsInfo.getGoodsAmount().get(goodsId));
                YdGoodsInfoVO ydGoodsInfoVO = DiamondYDGoodsConfig.getGoodsInfoVoById(goodsId);
                if (ydGoodsInfoVO == null) {
                    throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "商品不存在！");
                }
                subOrder.setName(ydGoodsInfoVO.getGoodName());
                subOrder.setPrice(AmountUtils.changeF2YWithBigDecimal(ydGoodsInfoVO.getPrice().intValue()));
                if (ydPaypointCpSubOrderMapper.insert(subOrder) > 0) {
                    cpOrderDTO.addListFeature(Constant.CPSUBORDER, subOrder.getSubOrderid());
                } else {
                    throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "子订单创建失败！");
                }
            }
        }

        YdPaypointCpOrder ydPaypointCpOrder = doMapper.map(cpOrderDTO,YdPaypointCpOrder.class);
        ydPaypointCpOrder.setOpenid(openid);
        /**
         * 设置为商城类型的订单
         */
        ydPaypointCpOrder.setOrderType(1);
        if(ydPaypointCpOrderMapper.insert(ydPaypointCpOrder)<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"主订单创建失败！");
        }
        /**
         * 以下发消息去关闭超1小时没有支付的订单
         */
        Map<String,String> closeOrderMessage = new HashMap<>();
        closeOrderMessage.put(Constant.ORDERID,ydPaypointCpOrder.getOrderid());
        closeOrderMessage.put(Constant.PAYORDERTYPE,PayOrderTypeEnum.CPORDER.getName());
        closeOrderMessage.put(Constant.ORDERTYPE, OrderStatusEnum.OVER.getName());
        String msgId = mqMessageService.sendOrderStatusChange(ydPaypointCpOrder.getOrderid()+"cpmallclose",JSON.toJSONString(closeOrderMessage),3600000,PayOrderTypeEnum.CPORDER);
        logger.info("CpMall msgId is :"+msgId);
        CpMallOrderVO cpMallOrderVO = this.constructOrderVO(ydPaypointCpOrder);
        return cpMallOrderVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setDeliveryAddress(String openid, String orderid, String deliveryAddress) {
        if(StringUtil.isBlank(deliveryAddress)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"地址信息不能为空！");
        }
        UserAddressInfoVO addressInfo = JSON.parseObject(deliveryAddress,UserAddressInfoVO.class);
        if(StringUtil.isBlank(addressInfo.getName())||StringUtil.isBlank(addressInfo.getMobile())||
                StringUtil.isBlank(addressInfo.getAddress())||StringUtil.isBlank(addressInfo.getCity())||
                StringUtil.isBlank(addressInfo.getDistrict())||StringUtil.isBlank(addressInfo.getProvince())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"请完整的填写地址信息！");
        }
        YdPaypointCpOrder ydPaypointCpOrder = ydPaypointCpOrderMapper.selectByOrderIdRowLock(orderid);
        if(ydPaypointCpOrder==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单不存在！");
        }
        if(ydPaypointCpOrder.getStatus().intValue()!=OrderStatusEnum.NEW.getStatus().intValue()){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单状态不正确！");
        }
        if(!StringUtil.equals(openid,ydPaypointCpOrder.getOpenid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        ydPaypointCpOrder.setDeliveryAddress(deliveryAddress);
        if(ydPaypointCpOrderMapper.updateByPrimaryKeySelective(ydPaypointCpOrder)>0){
            return true;
        }else{
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"地址信息设置失败，请重试！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelOrder(String openid, String orderid) {
        YdPaypointCpOrder ydPaypointCpOrder = ydPaypointCpOrderMapper.selectByOrderIdRowLock(orderid);
        if(ydPaypointCpOrder==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单不存在！");
        }
        if(!StringUtil.equals(openid,ydPaypointCpOrder.getOpenid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        ydPaypointCpOrder.setStatus(OrderStatusEnum.OVER.getStatus());
        if(ydPaypointCpOrderMapper.updateByPrimaryKeySelective(ydPaypointCpOrder)>0){
            return true;
        }else{
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"取消订单失败，请重试！");
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String,Object> payMall(String openid, String orderid,String ip) {
        CpMallOrderVO cpMallOrderVO = this.getOrder(openid,orderid);
        if(cpMallOrderVO==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单不存在");
        }

        YdPaypointCpOrder paypointCpOrder = ydPaypointCpOrderMapper.selectByOrderIdRowLock(orderid);
        if(paypointCpOrder.getStatus()>0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单状态不正确！");
        }
        Integer totalAmount = AmountUtils.changeY2F(cpMallOrderVO.getTotalAmount());
        if(totalAmount.intValue()<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"金额不正确！");
        }

        List<CpMallSubOrderVO> subOrderVOS = cpMallOrderVO.getSubOrders();
        String name = "商家服务类";
        String productcatalog = "1";
        if(!(subOrderVOS==null||subOrderVOS.size()<=0)){
            if(cpMallOrderVO.getAddressInfoVO()==null){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"收货地址不完整，无法支付！");
            }
            productcatalog = "7";
            name = subOrderVOS.get(0).getName()+"等";
        }


        Map<String,Object> result = new HashMap<>();

        result.put(Constant.ORDERID,orderid);
        result.put(Constant.STATUS,cpMallOrderVO.getStatus());
        //需要创建支付订单
        YeePayRequestDO yeePayRequestDO = new YeePayRequestDO();
        /**
         * 因为是公众号，所以此参数传7,identityid传openid
         */
        yeePayRequestDO.setOrderid(orderid);
        yeePayRequestDO.setAmount(totalAmount);
        yeePayRequestDO.setProductname(Constant.APPNAMECP+"-"+name+"商品");
        yeePayRequestDO.setProductdesc("订单编号："+orderid);
        yeePayRequestDO.setIdentitytype(7);
        yeePayRequestDO.setIdentityid(openid);
        yeePayRequestDO.setProductcatalog(productcatalog);
        yeePayRequestDO.setAppId(userinfoService.getWeiXinAppIdForCP());
        yeePayRequestDO.setUserip(ip);
        YdPayResponse payResponse = ydPayService.payMobileRequest(yeePayRequestDO, PayOrderTypeEnum.CPORDER, DiamondYdSystemConfigHolder.getInstance().yeePayCpMallFCallbackUrl);
        if(payResponse.getSuccess()){
            result.put(Constant.PAYURL,payResponse.getPayurl());
        }else{
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"创建订单失败，请稍候再试！");
        }

        return result;

    }

    @Override
    public CpMallOrderVO getOrder(String openid, String orderid) {
        YdPaypointCpOrder cpOrder = ydPaypointCpOrderMapper.selectByOrderId(orderid);
        if(cpOrder==null){
            return null;
        }
        if(cpOrder.getOrderType().intValue()<1){
            return null;
        }
        if(!StringUtil.equals(openid,cpOrder.getOpenid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        CpMallOrderVO cpMallOrderVO = this.constructOrderVO(cpOrder);
        return cpMallOrderVO;
    }

    @Override
    public List<CpMallOrderVO> getOrderList(String openid,Integer pageIndex, Integer count) {
        List<CpMallOrderVO> result = new ArrayList<>();
        if(pageIndex==null){
            pageIndex = 1;
        }else{
            if(pageIndex<=0){
                pageIndex = 1;
            }
        }
        if(count==null){
            count = 5;
        }else{
            if(count<5){
                count = 5;
            }
        }
        Integer indexPoint = (pageIndex-1)*count;
        if(indexPoint<=0){
            indexPoint = 0;
        }
        List<YdPaypointCpOrder> cpOrderList = ydPaypointCpOrderMapper.selectByOpenid(openid,indexPoint,count);
        if(cpOrderList==null||cpOrderList.size()<=0){
            return result;
        }
        for(YdPaypointCpOrder cpOrder:cpOrderList){

            CpMallOrderVO cpMallOrderVO = this.constructOrderVO(cpOrder);
            result.add(cpMallOrderVO);

        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBuyerMessage(String openid, String orderid, String buyerMessage) {
        YdPaypointCpOrder ydPaypointCpOrder = ydPaypointCpOrderMapper.selectByOrderIdRowLock(orderid);
        if(ydPaypointCpOrder==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单不存在！");
        }
        if(!StringUtil.equals(openid,ydPaypointCpOrder.getOpenid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        ydPaypointCpOrder.setBuyerMessage(buyerMessage);
        if(ydPaypointCpOrderMapper.updateByPrimaryKeySelective(ydPaypointCpOrder)>0){
            return true;
        }else{
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"留言保存失败，请重试！");
        }
    }

    /**
     * 必须保证传入的YdPaypointCpOrder不为空且orderType=1
     * @param cpOrder
     * @return
     */
    private CpMallOrderVO constructOrderVO(YdPaypointCpOrder cpOrder){
        CpMallOrderVO cpMallOrderVO = new CpMallOrderVO();
        cpMallOrderVO.setShopid(cpOrder.getShopid());
        cpMallOrderVO.setOrderid(cpOrder.getOrderid());
        cpMallOrderVO.setStatus(cpOrder.getStatus());
        cpMallOrderVO.setCreateDate(cpOrder.getCreateDate());
        cpMallOrderVO.setTotalAmount(cpOrder.getTotalAmount());
        cpMallOrderVO.setBuyerMessage(cpOrder.getBuyerMessage());
        CPOrderDTO cpOrderDTO = doMapper.map(cpOrder,CPOrderDTO.class);
        String upSetMealType = cpOrderDTO.getFeature(Constant.UPSETMEAL);
        /**
         * 看一下有没有地址信息
         */

        if(StringUtil.isNotBlank(cpOrder.getDeliveryAddress())){
            UserAddressInfoVO userAddressInfoVO = JSON.parseObject(cpOrder.getDeliveryAddress(),UserAddressInfoVO.class);
            cpMallOrderVO.setAddressInfoVO(userAddressInfoVO);
        }

        /**
         * 看一下有没有套餐升级
         */
        if(StringUtil.isNotBlank(upSetMealType)){
            cpMallOrderVO.setUpgradeType(upSetMealType);
            cpMallOrderVO.setUpgradeTotalAmount(new BigDecimal(cpOrderDTO.getFeature(Constant.UPSETMEAL_AMOUNT)));
            cpMallOrderVO.setRemanent(Integer.valueOf(cpOrderDTO.getFeature(Constant.UPSETMEAL_REMANENT)));
            cpMallOrderVO.setOrderCount(cpMallOrderVO.getOrderCount()+1);
        }else{
            cpMallOrderVO.setUpgradeTotalAmount(new BigDecimal("0.00"));
        }

        /**
         * 看一下有没有加油包
         */
        String bagNum = cpOrderDTO.getFeature(Constant.BAGORDERNUM);
        if(StringUtil.isNotBlank(bagNum)){
            cpMallOrderVO.setBagNumber(Integer.valueOf(bagNum));
            cpMallOrderVO.setBagTotalAmount(new BigDecimal(cpOrderDTO.getFeature(Constant.BAGORDERAMOUNT)));
            cpMallOrderVO.setOrderCount(cpMallOrderVO.getOrderCount()+1);
        }else{
            cpMallOrderVO.setBagNumber(0);
            cpMallOrderVO.setBagTotalAmount(new BigDecimal("0.00"));
        }

        /**
         * 看一下有没有子订单（也是实体商品）
         */
        List<String> subOrderIds = cpOrderDTO.getListFeature(Constant.CPSUBORDER);
        List<CpMallSubOrderVO> subOrderList = this.getMallSubOrders(subOrderIds);
        cpMallOrderVO.setSubOrders(subOrderList);
        if(subOrderIds!=null){
            cpMallOrderVO.setOrderCount(cpMallOrderVO.getOrderCount()+subOrderIds.size());
        }
        return cpMallOrderVO;
    }


    private List<CpMallSubOrderVO> getMallSubOrders(List<String> subOrderIds){
        if(subOrderIds==null||subOrderIds.size()<=0){
            return null;
        }


        List<CpMallSubOrderVO> result = new ArrayList<>();
        for(String subOrderId:subOrderIds){
            YdPaypointCpSubOrder cpSubOrder = ydPaypointCpSubOrderMapper.selectBySubOrderId(subOrderId);
            if(cpSubOrder==null){
                continue;
            }
            CpMallSubOrderVO subOrderVO = doMapper.map(cpSubOrder,CpMallSubOrderVO.class);
            YdGoodsInfoVO goodsInfoVO = DiamondYDGoodsConfig.getGoodsInfoVoById(subOrderVO.getGoodsid());
            subOrderVO.setGoodsImageUrl(goodsInfoVO.getGoodImageUrl());
            result.add(subOrderVO);

        }

        return result;

    }


    private Map getSetMeal(String shopid){

        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(shopInfo!=null){
            if(shopInfo.getStatus()>2){
                ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
                String setMealVersion = shopInfoDTO.getFeature(Constant.SHOP_SETMEAL_TYPE);
                if(StringUtil.isNotBlank(setMealVersion)){
                    Map<String ,Object> result = DiamondYdPayPointConfigHolder.getInstance().getSetMealObject(setMealVersion);
                    logger.info("result setMealVersion map is :"+JSON.toJSONString(result));
                    return result;
                }
            }
        }
        logger.info("result setMealVersion map is :null");
        return null;
    }

    /**
     *
     * @param shopid
     * @param upgradeType
     * @return 返回：totalAmount:总金额， remanent:剩余天数
     * @throws IllegalAccessException
     */
    private Map<String,Integer> getTotalAmountByUpgrade(String shopid , String upgradeType) throws IllegalAccessException {

        Map<String,Integer> result = new HashMap<>();
        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(shopInfo==null){
            result.put("totalAmount",0);
            return result;
        }
        if(shopInfo.getStatus()<=2){
            result.put("totalAmount",0);
            return result;
        }

        LocalDate beginDate = DateUtils.date2LocalDate(shopInfo.getContractTimeBegin());
        LocalDate endDate = DateUtils.date2LocalDate(shopInfo.getContractTimeEnd());

        Integer remanent = DateUtils.subDate(LocalDate.now(),endDate).intValue();
        if(StringUtil.isBlank(upgradeType)){

            result.put("remanent",remanent);
            result.put("totalAmount",0);
            return result;
        }
        Map<String , Long> monthAndDay = DateUtils.subMonthAndDay(LocalDate.now(),endDate);
        /**
         * 得到还剩几月几日
         */
        Integer remanentMonth = monthAndDay.get("month").intValue();
        Integer remanentDay = monthAndDay.get("day").intValue();
        if(remanentMonth<=0&&remanent<=0){
            result.put("remanent",remanent);
            result.put("totalAmount",0);
            return result;
        }

        Map<String,Object> targetSetMealObj = DiamondYdPayPointConfigHolder.getInstance().getSetMealObject(upgradeType);
        if(targetSetMealObj==null){
            result.put("remanent",remanent);
            result.put("totalAmount",0);
            return result;
        }
        /**
         * 取本月总共有多少天
         */
        LocalDate today = LocalDate.now();
        //本月的第一天
        LocalDate firstday = LocalDate.of(today.getYear(),today.getMonth(),1);
        //本月的最后一天
        LocalDate lastDay =today.with(TemporalAdjusters.lastDayOfMonth());
        Integer howDayWithMonth = DateUtils.subDate(firstday,lastDay).intValue();
        if(howDayWithMonth<=0){
            howDayWithMonth = 0;
        }
        /**
         * 计算新的套餐本月还要给多少钱
         */
        BigDecimal dayAmount;
        if(howDayWithMonth>0){
            dayAmount = AmountUtils.div((Integer)targetSetMealObj.get("price"),howDayWithMonth,6);
        }else {
            dayAmount = new BigDecimal("0.00");
        }
        BigDecimal upDayAmount = AmountUtils.mul(dayAmount,remanentDay);
        /**
         * 得到新套餐的剩余月份的价格
         */
        Integer upAmountFen = 0;
        BigDecimal upAmount;
        if(remanentMonth>0) {
            Map<String, Integer> targetAmount = shopInfoService.getTotalAmountBySetMeal(upgradeType, remanentMonth, true);

            if (targetAmount.containsKey(Constant.DISCOUNT)) {
                upAmountFen = targetAmount.get(Constant.DISCOUNT);
            } else {
                upAmountFen = targetAmount.get(Constant.ORIG);
            }
            upAmount = AmountUtils.changeF2YWithBigDecimal(upAmountFen);
        }else {
            upAmount = new BigDecimal("0.00");
        }
        /**
         * 得到新套餐应该付多少钱
         */
        upAmount.add(upDayAmount);


        Map<String,Object> setMealObj = this.getSetMeal(shopid);
        Integer price = (Integer)setMealObj.get("price");
        if(setMealObj==null) {
            result.put("remanent", remanent);
            result.put("totalAmount", 0);
        }
        /**
         * 如果老套餐是免费版，那么新套餐金额就是要支付的升级金额
         */
        result.put("remanent", remanent);
        if(price<=0){

            result.put("totalAmount", AmountUtils.changeY2F(upAmount));
            return result;
        }
        /**
         * 取老套餐本月还没有消费的金额
         */
        BigDecimal dayOldAmount;
        if(howDayWithMonth>0){
            dayOldAmount = AmountUtils.div(price,howDayWithMonth,6);
        }else {
            dayOldAmount = new BigDecimal("0.00");
        }
        BigDecimal oldDayAmount = AmountUtils.mul(dayOldAmount,remanentDay);
        /**
         * 取老套餐其他没有消费的月份的总金额
         */
        Integer oldAmountFen = 0;
        BigDecimal oldAmount;
        if(remanentMonth>0) {
            Map<String, Integer> oldMonthAmount = shopInfoService.getTotalAmountBySetMeal((String) setMealObj.get(Constant.SETMEALTYPE), remanentMonth, true);

            if (oldMonthAmount.containsKey(Constant.DISCOUNT)) {
                oldAmountFen = oldMonthAmount.get(Constant.DISCOUNT);
            } else {
                oldAmountFen = oldMonthAmount.get(Constant.ORIG);
            }
            oldAmount = AmountUtils.changeF2YWithBigDecimal(oldAmountFen);
        }else{
            oldAmount = new BigDecimal("0.00");
        }
        oldAmount.add(oldDayAmount);

        /**
         * 新的金额减老的没消费金额，得到应该支付的套餐升级金额
         */
        BigDecimal payAmount = AmountUtils.sub(upAmount,oldAmount);
        if(AmountUtils.changeY2F(payAmount)<=0){
            result.put("totalAmount", 0);
        }else{
            result.put("totalAmount", AmountUtils.changeY2F(payAmount));
        }

        return result;


//        logger.info("shopid is : "+shopid);
//        Map<String,Integer> result = new HashMap<>();
//        Integer totalAmount = 0;
//        if(StringUtil.isBlank(upgradeType)){
//            result.put("totalAmount",totalAmount);
//            return result;
//        }
//        Map<String,Object> targetSetMealObj = DiamondYdPayPointConfigHolder.getInstance().getSetMealObject(upgradeType);
//        if(targetSetMealObj==null){
//            result.put("totalAmount",totalAmount);
//            return result;
//        }
//        Map<String,Object> setMealObj = this.getSetMeal(shopid);
//        Integer remanent = shopInfoService.queryRemanent(shopid,true).intValue();
//        /**
//         * 剩余多少月多少天
//         */
//        Map<String,Long> monthAndDay = shopInfoService.queryRemanent(shopid);
//        if(monthAndDay == null){
//            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"计算剩余时间出错！");
//        }
//
//        result.put("remanent",remanent);
//        if(!(setMealObj!=null&&remanent>0)){
//            result.put("totalAmount",totalAmount);
//            return result;
//        }
//
//        if(((Integer)setMealObj.get("id"))>=((Integer)targetSetMealObj.get("id"))){
//            result.put("totalAmount",totalAmount);
//            return result;
//        }
//
//        /**
//         * 计算一下新的套餐总共要多少钱，从原来的开始时间到结束时间整个都要计算，然后用这个钱减去原来套餐支付过的钱
//         */
//
//        Map<String, Integer> totalAmountYearObj = shopInfoService.getTotalAmountBySetMeal((String)setMealObj.get("setMealType"),12,true);
//        Map<String, Integer> totalAmountMonthObj = shopInfoService.getTotalAmountBySetMeal((String)setMealObj.get("setMealType"),12,true);
//
//        Integer totalAmountYear = 0;
//        Integer totalAmountDay = 0;
//        if(totalAmountYearObj.containsKey(Constant.DISCOUNT)){
//            totalAmountYear = totalAmountYearObj.get(Constant.DISCOUNT);
//        }else{
//            totalAmountYear = totalAmountYearObj.get(Constant.ORIG);
//        }
//
//        Integer totalAmountMonthTarget = 0;
//        Integer totalAmountDayTarget = 0;
//        Map<String, Integer> totalAmountMonthTargetObj = shopInfoService.getTotalAmountBySetMeal((String)targetSetMealObj.get("setMealType"),1,false);
//        if(totalAmountMonthTargetObj.containsKey(Constant.DISCOUNT)){
//            totalAmountMonthTarget = totalAmountMonthTargetObj.get(Constant.DISCOUNT);
//        }else{
//            totalAmountMonthTarget = totalAmountMonthTargetObj.get(Constant.ORIG);
//        }
//        if(totalAmountYear>0){
//            totalAmountDay = AmountUtils.div(totalAmountYear,31,2).toBigInteger().intValue();
//        }
//        if(totalAmountMonthTarget>0){
//            totalAmountDayTarget = AmountUtils.div(totalAmountMonthTarget,31,2).toBigInteger().intValue();
//        }
//        if(totalAmountDayTarget>totalAmountDay){
//            totalAmount = AmountUtils.mul(totalAmountDayTarget-totalAmountDay,remanent).toBigInteger().intValue();
//        }
//
//        result.put("totalAmount",totalAmount);

    }

    private Integer getTotalAmountByBagOrder(String shopid, Integer num){
        Integer result = 0;
        if(num<=0){
            return result;
        }
        Map<String,Object> setMealObj = this.getSetMeal(shopid);
        if(setMealObj==null){
            return result;
        }
        if(setMealObj!=null){
            result = AmountUtils.mul((Integer) setMealObj.get("bagPrice"),num).toBigInteger().intValue();
        }

        return result;
    }

}
