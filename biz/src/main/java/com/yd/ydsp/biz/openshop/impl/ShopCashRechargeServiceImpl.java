package com.yd.ydsp.biz.openshop.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.openshop.ShopCashRechargeService;
import com.yd.ydsp.biz.openshop.ShopOrderService;
import com.yd.ydsp.biz.pay.WeiXinPayService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.client.domian.openshop.ShopCashRechargeConfigVO;
import com.yd.ydsp.client.domian.paypoint.ShopUserBillVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.constants.paypoint.ShopUserBillFlagConstants;
import com.yd.ydsp.common.enums.BillTypeEnum;
import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.utils.AmountUtils;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.YdShopCashRechargeConfigMapper;
import com.yd.ydsp.dal.mapper.YdShopUserBillMapper;
import com.yd.ydsp.dal.mapper.YdShopUserCashBalanceMapper;
import com.yd.ydsp.dal.mapper.YdShopUserCashRechargeMapper;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

public class ShopCashRechargeServiceImpl implements ShopCashRechargeService {
    public static final Logger logger = LoggerFactory.getLogger(ShopCashRechargeServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private YdShopCashRechargeConfigMapper ydShopCashRechargeConfigMapper;
    @Resource
    private YdShopUserCashRechargeMapper ydShopUserCashRechargeMapper;
    @Resource
    private YdShopUserCashBalanceMapper ydShopUserCashBalanceMapper;
    @Resource
    private YdShopUserBillMapper ydShopUserBillMapper;
    @Resource
    private UserinfoService userinfoService;
    @Resource
    private MqMessageService mqMessageService;
    @Resource
    private ShopOrderService shopOrderService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateShopCashRechargeConfig(String openid, ShopCashRechargeConfigVO rechargeConfigVO) {
        if(!(userinfoService.checkIsManager(openid,rechargeConfigVO.getShopid())||
                userinfoService.checkIsOwner(openid,rechargeConfigVO.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(rechargeConfigVO.getAmountConfOne()==null||rechargeConfigVO.getAmountConfTwo()==null||rechargeConfigVO.getAmountConfThr()==null||
                rechargeConfigVO.getAmountConfFour()==null||rechargeConfigVO.getAmountConfFive()==null||
                rechargeConfigVO.getAmountConfSix()==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"信息不完整，请检查！");
        }

        AmountUtils.bigDecimalBy2(rechargeConfigVO.getAmountConfOne());
        AmountUtils.bigDecimalBy2(rechargeConfigVO.getAmountConfTwo());
        AmountUtils.bigDecimalBy2(rechargeConfigVO.getAmountConfThr());
        AmountUtils.bigDecimalBy2(rechargeConfigVO.getAmountConfFour());
        AmountUtils.bigDecimalBy2(rechargeConfigVO.getAmountConfFive());
        AmountUtils.bigDecimalBy2(rechargeConfigVO.getAmountConfSix());

        if(AmountUtils.changeY2F(rechargeConfigVO.getAmountConfOne())<=0||AmountUtils.changeY2F(rechargeConfigVO.getAmountConfTwo())<=0||
                AmountUtils.changeY2F(rechargeConfigVO.getAmountConfThr())<=0||AmountUtils.changeY2F(rechargeConfigVO.getAmountConfFour())<=0||
                AmountUtils.changeY2F(rechargeConfigVO.getAmountConfFive())<=0||AmountUtils.changeY2F(rechargeConfigVO.getAmountConfSix())<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"金额不能小于或者等于0，请检查！");
        }

        rechargeConfigVO.setStatus(0);

        if(rechargeConfigVO.getOneAmountGive()==null){
            rechargeConfigVO.setOneAmountGive(new BigDecimal("0.00"));
        }else {
            if(AmountUtils.changeY2F(rechargeConfigVO.getOneAmountGive())<0){
                rechargeConfigVO.setOneAmountGive(new BigDecimal("0.00"));
            }else {
                rechargeConfigVO.setOneAmountGive(AmountUtils.bigDecimalBy2(rechargeConfigVO.getOneAmountGive()));
            }
        }
        if(rechargeConfigVO.getTwoAmountGive()==null){
            rechargeConfigVO.setTwoAmountGive(new BigDecimal("0.00"));
        }else {
            if(AmountUtils.changeY2F(rechargeConfigVO.getTwoAmountGive())<0){
                rechargeConfigVO.setTwoAmountGive(new BigDecimal("0.00"));
            }else {
                rechargeConfigVO.setTwoAmountGive(AmountUtils.bigDecimalBy2(rechargeConfigVO.getTwoAmountGive()));
            }
        }
        if(rechargeConfigVO.getThrAmountGive()==null){
            rechargeConfigVO.setThrAmountGive(new BigDecimal("0.00"));
        }else {
            if(AmountUtils.changeY2F(rechargeConfigVO.getThrAmountGive())<0){
                rechargeConfigVO.setThrAmountGive(new BigDecimal("0.00"));
            }else {
                rechargeConfigVO.setThrAmountGive(AmountUtils.bigDecimalBy2(rechargeConfigVO.getThrAmountGive()));
            }
        }
        if(rechargeConfigVO.getFourAmountGive()==null){
            rechargeConfigVO.setFourAmountGive(new BigDecimal("0.00"));
        }else {
            if(AmountUtils.changeY2F(rechargeConfigVO.getFourAmountGive())<0){
                rechargeConfigVO.setFourAmountGive(new BigDecimal("0.00"));
            }else {
                rechargeConfigVO.setFourAmountGive(AmountUtils.bigDecimalBy2(rechargeConfigVO.getFourAmountGive()));
            }
        }
        if(rechargeConfigVO.getFiveAmountGive()==null){
            rechargeConfigVO.setFiveAmountGive(new BigDecimal("0.00"));
        }else {
            if(AmountUtils.changeY2F(rechargeConfigVO.getFiveAmountGive())<0){
                rechargeConfigVO.setFiveAmountGive(new BigDecimal("0.00"));
            }else {
                rechargeConfigVO.setFiveAmountGive(AmountUtils.bigDecimalBy2(rechargeConfigVO.getFiveAmountGive()));
            }
        }
        if(rechargeConfigVO.getSixAmountGive()==null){
            rechargeConfigVO.setSixAmountGive(new BigDecimal("0.00"));
        }else {
            if(AmountUtils.changeY2F(rechargeConfigVO.getSixAmountGive())<0){
                rechargeConfigVO.setSixAmountGive(new BigDecimal("0.00"));
            }else {
                rechargeConfigVO.setSixAmountGive(AmountUtils.bigDecimalBy2(rechargeConfigVO.getSixAmountGive()));
            }
        }

        YdShopCashRechargeConfig rechargeConfigDB = ydShopCashRechargeConfigMapper.selectByShopidRowLock(rechargeConfigVO.getShopid());
        YdShopCashRechargeConfig rechargeConfig = doMapper.map(rechargeConfigVO,YdShopCashRechargeConfig.class);

        if(rechargeConfigDB==null){
            rechargeConfig.setAmountTotal(new BigDecimal("0.00"));
            rechargeConfig.setCurrenAmount(new BigDecimal("0.00"));
            rechargeConfig.setGiveAmount(new BigDecimal("0.00"));
            rechargeConfig.setCustomerCount(0);
            if(ydShopCashRechargeConfigMapper.insert(rechargeConfig)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"设置失败，请稍后重试！");
            }
        }else {
            rechargeConfig.setAmountTotal(null);
            rechargeConfig.setAmountTotal(null);
            rechargeConfig.setGiveAmount(null);
            rechargeConfig.setCustomerCount(null);
            rechargeConfig.setId(rechargeConfigDB.getId());
            if(ydShopCashRechargeConfigMapper.updateByPrimaryKeySelective(rechargeConfig)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"修改失败，请稍后重试！");
            }
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateShopCashData(String billid, Integer billType) {
        BillTypeEnum billTypeEnum = BillTypeEnum.nameOf(billType);
        if(billTypeEnum==null){
            logger.error(billid+":"+billType+"是不存在的帐单类型");
            return true;
        }

        if(billTypeEnum==BillTypeEnum.IN){
            YdShopUserBill userBill = ydShopUserBillMapper.selectByBillId(billid,billType);
            if(userBill==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),billid+":"+billTypeEnum.getName()+"-订单没有找到！");
            }

            if(userBill.isExistFlag(ShopUserBillFlagConstants.WRITESHOPDATAFINISH)){
                return true;
            }
            YdShopCashRechargeConfig cashRechargeConfig = ydShopCashRechargeConfigMapper.selectByShopidRowLock(userBill.getShopid());
            cashRechargeConfig.setAmountTotal(AmountUtils.bigDecimalBy2(AmountUtils.add(cashRechargeConfig.getAmountTotal(),userBill.getCashAmount())));
            cashRechargeConfig.setCurrenAmount(AmountUtils.bigDecimalBy2(AmountUtils.add(cashRechargeConfig.getCurrenAmount(),userBill.getCashAmount())));
            if(userBill.isExistFlag(ShopUserBillFlagConstants.THEFIRST)){
                cashRechargeConfig.setCustomerCount(cashRechargeConfig.getCustomerCount()+1);
            }

            userBill.addFlag(ShopUserBillFlagConstants.WRITESHOPDATAFINISH);

            if(ydShopCashRechargeConfigMapper.updateByPrimaryKeySelective(cashRechargeConfig)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"充值统计数据修改失败，请稍后重试！");
            }

            if(ydShopUserBillMapper.updateByPrimaryKeySelective(userBill)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"充值帐单修改失败，请稍后重试！");
            }

        }else if(billTypeEnum==BillTypeEnum.GIVE){
            YdShopUserBill userBill = ydShopUserBillMapper.selectByBillId(billid,billType);
            if(userBill==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),billid+":"+billTypeEnum.getName()+"-订单没有找到！");
            }

            if(userBill.isExistFlag(ShopUserBillFlagConstants.WRITESHOPDATAFINISH)){
                return true;
            }

            YdShopCashRechargeConfig cashRechargeConfig = ydShopCashRechargeConfigMapper.selectByShopidRowLock(userBill.getShopid());
            cashRechargeConfig.setGiveAmount(AmountUtils.bigDecimalBy2(AmountUtils.add(cashRechargeConfig.getGiveAmount(),userBill.getCashAmount())));
            cashRechargeConfig.setCurrenAmount(AmountUtils.bigDecimalBy2(AmountUtils.add(cashRechargeConfig.getCurrenAmount(),userBill.getCashAmount())));
            userBill.addFlag(ShopUserBillFlagConstants.WRITESHOPDATAFINISH);
            if(ydShopCashRechargeConfigMapper.updateByPrimaryKeySelective(cashRechargeConfig)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"充值奖励统计数据修改失败，请稍后重试！");
            }

            if(ydShopUserBillMapper.updateByPrimaryKeySelective(userBill)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"充值奖励帐单修改失败，请稍后重试！");
            }

        }else if(billTypeEnum==BillTypeEnum.TRADE){
            YdShopUserBill userBill = ydShopUserBillMapper.selectByBillId(billid,billType);
            if(userBill==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),billid+":"+billTypeEnum.getName()+"-订单没有找到！");
            }

            if(userBill.isExistFlag(ShopUserBillFlagConstants.WRITESHOPDATAFINISH)){
                return true;
            }

            YdShopCashRechargeConfig cashRechargeConfig = ydShopCashRechargeConfigMapper.selectByShopidRowLock(userBill.getShopid());
            cashRechargeConfig.setCurrenAmount(AmountUtils.bigDecimalBy2(AmountUtils.sub(cashRechargeConfig.getCurrenAmount(),userBill.getCashAmount())));

            userBill.addFlag(ShopUserBillFlagConstants.WRITESHOPDATAFINISH);
            if(ydShopCashRechargeConfigMapper.updateByPrimaryKeySelective(cashRechargeConfig)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"交易订单统计数据修改失败，请稍后重试！");
            }

            if(ydShopUserBillMapper.updateByPrimaryKeySelective(userBill)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"交易帐单修改失败，请稍后重试！");
            }
        }

        return true;
    }

    @Override
    public ShopCashRechargeConfigVO queryShopCashRechargeConfig(String openid, String shopid) {
        if(!(userinfoService.checkIsManager(openid,shopid)||
                userinfoService.checkIsOwner(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdShopCashRechargeConfig rechargeConfig = ydShopCashRechargeConfigMapper.selectByShopid(shopid);
        ShopCashRechargeConfigVO rechargeConfigVO = null;
        if(rechargeConfig!=null){
            rechargeConfigVO = doMapper.map(rechargeConfig,ShopCashRechargeConfigVO.class);
        }
        return rechargeConfigVO;
    }

    @Override
    public ShopCashRechargeConfigVO queryShopCashRechargeConfig2C(String shopid) {
        YdShopCashRechargeConfig rechargeConfig = ydShopCashRechargeConfigMapper.selectByShopid(shopid);
        ShopCashRechargeConfigVO rechargeConfigVO = null;
        if(rechargeConfig!=null){
            rechargeConfigVO = doMapper.map(rechargeConfig,ShopCashRechargeConfigVO.class);
            rechargeConfigVO.setAmountTotal(null);
            rechargeConfigVO.setCurrenAmount(null);
            rechargeConfigVO.setGiveAmount(null);
            rechargeConfigVO.setCustomerCount(null);
        }
        return rechargeConfigVO;
    }

    @Override
    public BigDecimal queryCashRecharge(ShopCashRechargeConfigVO cashRechargeConfigVO, Integer cashType) {
        if(cashRechargeConfigVO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"链贝配置信息不存在！");
        }
        int rechargeType = cashType.intValue();
        if(rechargeType<=0||rechargeType>6){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"配置信息类型范围超出！");
        }

        if(rechargeType==1){
            return cashRechargeConfigVO.getAmountConfOne();
        }else if(rechargeType==2){
            return cashRechargeConfigVO.getAmountConfTwo();
        }else if(rechargeType==3){
            return cashRechargeConfigVO.getAmountConfThr();
        }else if(rechargeType==4){
            return cashRechargeConfigVO.getAmountConfFour();
        }else if(rechargeType==5){
            return cashRechargeConfigVO.getAmountConfFive();
        }else if(rechargeType==6){
            return cashRechargeConfigVO.getAmountConfSix();
        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"配置信息类型范围超出！");
        }
    }

    @Override
    public BigDecimal queryCashGive(ShopCashRechargeConfigVO cashRechargeConfigVO, Integer cashType) {
        if(cashRechargeConfigVO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"链贝配置信息不存在！");
        }
        int rechargeType = cashType.intValue();
        if(rechargeType<=0||rechargeType>6){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"配置信息类型范围超出！");
        }
        if(rechargeType==1){
            return cashRechargeConfigVO.getOneAmountGive();
        }else if(rechargeType==2){
            return cashRechargeConfigVO.getTwoAmountGive();
        }else if(rechargeType==3){
            return cashRechargeConfigVO.getThrAmountGive();
        }else if(rechargeType==4){
            return cashRechargeConfigVO.getFourAmountGive();
        }else if(rechargeType==5){
            return cashRechargeConfigVO.getFiveAmountGive();
        }else if(rechargeType==6){
            return cashRechargeConfigVO.getSixAmountGive();
        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"配置信息类型范围超出！");
        }
    }

    @Override
    public YdShopUserBill queryLastCashRecharge(Long userid, String shopid) {
        YdShopUserBill userBill = ydShopUserBillMapper.selectByLastCashRechargeBill(userid,shopid);
        return userBill;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean newPreBill(Long userid, String shopid, String billid, BigDecimal cashRecharge, BigDecimal cashGive) {
        Date date = new Date();
        String[] dateStr = DateUtils.date2StrWithYearAndMonthAndDayArray(date);
        YdShopUserCashRecharge shopUserBillIn = new YdShopUserCashRecharge();
        shopUserBillIn.setStatus(0);
        shopUserBillIn.setUserid(userid);
        shopUserBillIn.setShopid(shopid);
        shopUserBillIn.setBillid(billid);
        shopUserBillIn.setBilltype(BillTypeEnum.IN.getType());
        shopUserBillIn.setInYear(dateStr[0]);
        shopUserBillIn.setInMonth(dateStr[1]);
        shopUserBillIn.setInDay(dateStr[2]);
        shopUserBillIn.setCashAmount(AmountUtils.bigDecimalBy2(cashRecharge));

        if(ydShopUserCashRechargeMapper.insert(shopUserBillIn)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"充值失败，请稍后再试！");
        }

        if(cashGive !=null){
            cashGive = AmountUtils.bigDecimalBy2(cashGive);
            if(AmountUtils.changeY2F(cashGive)>0){
                YdShopUserCashRecharge shopUserBillGive = new YdShopUserCashRecharge();
                shopUserBillGive.setStatus(0);
                shopUserBillGive.setUserid(userid);
                shopUserBillGive.setShopid(shopid);
                shopUserBillGive.setBillid(billid);
                shopUserBillGive.setBilltype(BillTypeEnum.GIVE.getType());
                shopUserBillGive.setInYear(dateStr[0]);
                shopUserBillGive.setInMonth(dateStr[1]);
                shopUserBillGive.setInDay(dateStr[2]);
                shopUserBillGive.setCashAmount(cashGive);
                if(ydShopUserCashRechargeMapper.insert(shopUserBillGive)<=0){
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"充值失败，请稍后再试！");
                }
            }
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean newBill(String billid) {
        YdShopUserCashRecharge shopUserBillIn = ydShopUserCashRechargeMapper.selectByBillId(billid,BillTypeEnum.IN.getType());
        if(shopUserBillIn==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),billid+":充值记录没有找到！");
        }

        if(shopUserBillIn.isExistFlag(ShopUserBillFlagConstants.WRITEBILLFINISH)){
            return true;
        }

        shopUserBillIn.addFlag(ShopUserBillFlagConstants.WRITEBILLFINISH);

        YdShopUserBill shopUserNewBillIn = doMapper.map(shopUserBillIn,YdShopUserBill.class);
        shopUserNewBillIn.setBillName("充值");

        YdShopUserCashBalance userCashBalance = ydShopUserCashBalanceMapper.selectByUserIdAndShopIdRowLock(shopUserBillIn.getUserid(),shopUserBillIn.getShopid());

        boolean cashBalanceIsNew = false;
        if(userCashBalance==null){
            cashBalanceIsNew = true;
            shopUserNewBillIn.addFlag(ShopUserBillFlagConstants.THEFIRST);
            userCashBalance = new YdShopUserCashBalance();
            userCashBalance.setUserid(shopUserBillIn.getUserid());
            userCashBalance.setShopid(shopUserBillIn.getShopid());
            userCashBalance.setCashBalance(AmountUtils.bigDecimalBy2(shopUserBillIn.getCashAmount()));

        }else{
            userCashBalance.setCashBalance(AmountUtils.bigDecimalBy2(AmountUtils.add(userCashBalance.getCashBalance(),shopUserBillIn.getCashAmount())));

        }

        shopUserNewBillIn.setCashAmountTotal(AmountUtils.bigDecimalBy2(userCashBalance.getCashBalance()));
        shopUserNewBillIn.setCashAmount(AmountUtils.bigDecimalBy2(shopUserBillIn.getCashAmount()));
        if(ydShopUserBillMapper.insert(shopUserNewBillIn)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),billid+":写入充值失败！");
        }

        YdShopUserCashRecharge shopUserBillGive = ydShopUserCashRechargeMapper.selectByBillId(billid,BillTypeEnum.GIVE.getType());
        YdShopUserBill shopUserNewBillGive = null;
        if(shopUserBillGive!=null) {
            shopUserBillGive.addFlag(ShopUserBillFlagConstants.WRITEBILLFINISH);
            shopUserNewBillGive = doMapper.map(shopUserBillGive, YdShopUserBill.class);
            shopUserNewBillGive.setBillName("充值赠送");

            shopUserNewBillGive.setCashAmountTotal(AmountUtils.bigDecimalBy2(AmountUtils.add(userCashBalance.getCashBalance(), shopUserBillGive.getCashAmount())));
            shopUserNewBillGive.setCashAmount(AmountUtils.bigDecimalBy2(shopUserBillGive.getCashAmount()));

            userCashBalance.setCashBalance(AmountUtils.bigDecimalBy2(AmountUtils.add(userCashBalance.getCashBalance(),shopUserNewBillGive.getCashAmount())));

            if (ydShopUserBillMapper.insert(shopUserNewBillGive) <= 0) {
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), billid + ":写入充值奖励数据失败！");
            }
            if(ydShopUserCashRechargeMapper.updateByPrimaryKeySelective(shopUserBillGive)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),billid+":预处理表打标失败！");
            }
        }

        if(ydShopUserCashRechargeMapper.updateByPrimaryKeySelective(shopUserBillIn)<0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),billid+":预处理表打标失败！");
        }
        if(cashBalanceIsNew){
            if(ydShopUserCashBalanceMapper.insert(userCashBalance)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),billid+":写入充值余额表失败！");
            }
        }else {
            if(ydShopUserCashBalanceMapper.updateByPrimaryKeySelective(userCashBalance)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),billid+":更新充值余额表失败！");
            }
        }

        /**
         * 发个消息去更新店铺链贝充值配置表中的店铺总的链贝，当前还没有被消费的链贝，总的奖励的链贝，和第一次充值用户计数
         *
         */
        this.sendUpdateShopCashRechargeDataMsg(shopUserNewBillIn.getShopid(),shopUserNewBillIn.getBillid(),shopUserNewBillIn.getBilltype());
        if(shopUserBillGive!=null) {
            this.sendUpdateShopCashRechargeDataMsg(shopUserNewBillGive.getShopid(), shopUserNewBillGive.getBillid(), shopUserNewBillGive.getBilltype());
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean newBillByOrder(Long userid, String shopid, String orderid, BigDecimal tradeMoney,Date orderDate) {
        String[] dateStr = DateUtils.date2StrWithYearAndMonthAndDayArray(orderDate);
        YdShopUserCashBalance userCashBalance = ydShopUserCashBalanceMapper.selectByUserIdAndShopIdRowLock(userid,shopid);

        if(userCashBalance==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),shopid+"_"+userid+":没有充值就开始消费了？");
        }

        YdShopUserBill userBill = ydShopUserBillMapper.selectByBillId(orderid,BillTypeEnum.TRADE.getType());
        if(userBill!=null){
            return true;
        }

        userBill = new YdShopUserBill();
        userBill.setUserid(userid);
        userBill.setStatus(0);
        userBill.setShopid(shopid);
        userBill.setBillid(orderid);
        userBill.setBilltype(BillTypeEnum.TRADE.getType());
        userBill.setCashAmountTotal(AmountUtils.bigDecimalBy2(AmountUtils.sub(userCashBalance.getCashBalance(),tradeMoney)));
        userBill.setCashAmount(AmountUtils.bigDecimalBy2(tradeMoney));
        userBill.setInYear(dateStr[0]);
        userBill.setInMonth(dateStr[1]);
        userBill.setInDay(dateStr[2]);

        Map<String,Object> payRespone = JSON.parseObject(JSON.toJSONString(userBill),Map.class);
        payRespone.put(Constant.ISPAY,2);

        if(!shopOrderService.orderPayFinish(orderid,AmountUtils.changeY2F(tradeMoney),payRespone)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),orderid+":更新订单失败");
        }

        YdShopConsumerOrder consumerOrder = shopOrderService.queryUserOrder(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),orderid+":订单没有找到！");
        }

        userBill.setBillName(consumerOrder.getOrderName());

        if(ydShopUserBillMapper.insert(userBill)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),orderid+":写入交易帐单数据失败！");
        }
        /**
         * 发个消息去更新店铺链贝充值配置表中的当前还没有被消费的链贝
         *
         */
        this.sendUpdateShopCashRechargeDataMsg(shopid,orderid,BillTypeEnum.TRADE.getType());

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean newBill(String billid, Integer billType) {
        BillTypeEnum billTypeEnum = BillTypeEnum.nameOf(billType);
        if(billTypeEnum==null){
            return true;
        }

        if(billTypeEnum==BillTypeEnum.IN){
            return this.newBill(billid);
        }else if(billTypeEnum==BillTypeEnum.TRADE){
            YdShopConsumerOrder consumerOrder = shopOrderService.queryUserOrder(billid);
            if(consumerOrder==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),billid+":消费订单没有找到！");
            }
            YdWeixinUserInfo weixinUserInfo = userinfoService.queryUserInfo2C(consumerOrder.getUnionid());
            if(weixinUserInfo==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),billid+":消费订单的用户信息没找到！User unioinid is:"+consumerOrder.getUnionid());
            }

            return this.newBillByOrder(weixinUserInfo.getId(),consumerOrder.getShopid(),consumerOrder.getOrderid(),consumerOrder.getTotalAmount(),consumerOrder.getCreateDate());

        }

        return true;
    }

    private String sendUpdateShopCashRechargeDataMsg(String shopid,String billid,Integer billType){

        Map<String,Object> msg = new HashMap<>();
        msg.put(Constant.MQTAG, MqTagEnum.UPDATESHOPCASHRECHARGE.getTag());
        msg.put(Constant.BILLID,billid);
        msg.put(Constant.BILLTYPE,billType);
        String msgId = mqMessageService.sendMessage(shopid+"bill",shopid+"bill",MqTagEnum.UPDATESHOPCASHRECHARGE, JSON.toJSONString(msg));
        return msgId;

    }

    @Override
    public String sendNewBillMsg(Long userid,String billid, Integer billType) {
        String msgId = null;
        BillTypeEnum billTypeEnum = BillTypeEnum.nameOf(billType);
        if(billTypeEnum == null||billTypeEnum==BillTypeEnum.GIVE){
            /**
             * 创建帐单时，GIVE与在IN的这个类型里一起创建的，所以这里不需要单独处理
             */
            return null;
        }
        if(billTypeEnum==BillTypeEnum.IN){
            YdShopUserCashRecharge shopUserBill = ydShopUserCashRechargeMapper.selectByBillId(billid,billType);
            if(shopUserBill==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),billid+":预处理帐单数据没找到！");
            }
            userid = shopUserBill.getUserid();
            Map<String,Object> msg = new HashMap<>();
            msg.put(Constant.MQTAG, MqTagEnum.SHOPUSERBILL.getTag());
            msg.put(Constant.BILLID,billid);
            msg.put(Constant.BILLTYPE,billType);
            msgId = mqMessageService.sendMessage(billid,userid+"bill",MqTagEnum.SHOPUSERBILL, JSON.toJSONString(msg));
        }else if(billTypeEnum==BillTypeEnum.TRADE){
            Map<String,Object> msg = new HashMap<>();
            msg.put(Constant.MQTAG, MqTagEnum.SHOPUSERBILL.getTag());
            msg.put(Constant.BILLID,billid);
            msg.put(Constant.BILLTYPE,billType);
            msgId = mqMessageService.sendMessage(billid,userid+"bill",MqTagEnum.SHOPUSERBILL, JSON.toJSONString(msg));
        }


        return msgId;
    }


    @Override
    public boolean newBillByGiveCode(Long userid, String shopid, String code, String billid, BigDecimal cashGive) {
        return false;
    }

    @Override
    public BigDecimal getUserBillBalance(Long userid, String shopid) {
        YdShopUserCashBalance cashBalance = ydShopUserCashBalanceMapper.selectByUserIdAndShopId(userid,shopid);
        if(cashBalance==null){
            return new BigDecimal("0.00");
        }
        return cashBalance.getCashBalance();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateShopUserCashBalance(Long userid, String shopid,String orderid, BigDecimal orderMoney) {
        if(AmountUtils.changeY2F(orderMoney)<=0){
            return false;
        }

        YdShopUserCashBalance cashBalance = ydShopUserCashBalanceMapper.selectByUserIdAndShopIdRowLock(userid,shopid);
        if(AmountUtils.changeY2F(cashBalance.getCashBalance())>=AmountUtils.changeY2F(orderMoney)){

            cashBalance.setCashBalance(AmountUtils.bigDecimalBy2(AmountUtils.sub(cashBalance.getCashBalance(),orderMoney)));
            if(ydShopUserCashBalanceMapper.updateByPrimaryKeySelective(cashBalance)<=0){
                logger.error(orderid+":更新链贝失败");
                return false;
            }else {
                this.sendNewBillMsg(userid,orderid,BillTypeEnum.TRADE.getType());
                return true;
            }
        }

        return false;
    }

    @Override
    public List<ShopUserBillVO> getUserBilling(Long userid, String shopid, String year, String month, Integer pageIndex, Integer count) {
        Integer indexPoint = (pageIndex-1)*count;
        if(indexPoint.intValue()<=0){
            indexPoint = 0;
        }

        if(count.intValue()<0||count.intValue()>20){
            count = 20;
        }

        List<YdShopUserBill> userBills = ydShopUserBillMapper.selectByUserId(userid,shopid,year,month,indexPoint,count);
        List<ShopUserBillVO> shopUserBillVOS = new ArrayList<>();
        for(YdShopUserBill shopUserBill : userBills){
            ShopUserBillVO shopUserBillVO = doMapper.map(shopUserBill,ShopUserBillVO.class);
            shopUserBillVO.setCreateDate(DateUtils.date2String(shopUserBill.getCreateDate()));
            shopUserBillVOS.add(shopUserBillVO);
        }
        return shopUserBillVOS;
    }

}
