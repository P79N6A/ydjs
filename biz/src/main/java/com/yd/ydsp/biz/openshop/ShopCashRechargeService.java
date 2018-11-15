package com.yd.ydsp.biz.openshop;

import com.yd.ydsp.client.domian.openshop.ShopCashRechargeConfigVO;
import com.yd.ydsp.client.domian.paypoint.ShopUserBillVO;
import com.yd.ydsp.dal.entity.YdShopUserBill;
import com.yd.ydsp.dal.entity.YdShopUserCashBalance;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ShopCashRechargeService {

    /**
     * 更新充值配置
     * @param openid
     * @param rechargeConfigVO
     * @return
     */
    boolean updateShopCashRechargeConfig(String openid,ShopCashRechargeConfigVO rechargeConfigVO);

    /**
     * 更新店铺充值功能的统计数据
     * @param billid
     * @param billType
     * @return
     */
    boolean updateShopCashData(String billid, Integer billType);

    /**
     * 查询充值配置信息
     * @param openid
     * @param shopid
     * @return
     */
    ShopCashRechargeConfigVO queryShopCashRechargeConfig(String openid,String shopid);

    /**
     * 查询充值配置信息,C端使用
     * @param shopid
     * @return
     */
    ShopCashRechargeConfigVO queryShopCashRechargeConfig2C(String shopid);

    /**
     * 根据cashType查询对应的充值金额
     * @param cashRechargeConfigVO
     * @param cashType
     * @return
     */
    BigDecimal queryCashRecharge(ShopCashRechargeConfigVO cashRechargeConfigVO, Integer cashType);

    /**
     * 根据cashType查询对应的赠送金额
     * @param cashRechargeConfigVO
     * @param cashType
     * @return
     */
    BigDecimal queryCashGive(ShopCashRechargeConfigVO cashRechargeConfigVO, Integer cashType);

    /**
     * 接下来都是帐单的生成与查询接口
     */

    /**
     * 查询用户在一个店下的上一次充值记录(只查询状态为0的情况的已经付款的充值记录）
     * @param userid
     * @param shopid
     * @return
     */
    YdShopUserBill queryLastCashRecharge(Long userid,String shopid);

    /**
     * 新的预充值记录（包括充值赠送）
     * @param userid
     * @param shopid
     * @param billid
     * @param cashRecharge
     * @param cashGive
     * @return
     */
    boolean newPreBill(Long userid,String shopid,String billid,BigDecimal cashRecharge,BigDecimal cashGive);

    /**
     * 付款成功后，充值与赠送记录通过顺序消息队列正入加入到帐单表中
     * @param billid
     * @return
     */
    boolean newBill(String billid);

    /**
     * 收到创建新帐单消息后，统一调用这个方法，由此方法来路由不同类型的新帐单的创建
     * @param billid
     * @param billType
     * @return
     */
    boolean newBill(String billid,Integer billType);

    /**
     * 交易帐单，消费的金额
     * @param userid
     * @param shopid
     * @param orderid
     * @param tradeMoney
     * @return
     */
    boolean newBillByOrder(Long userid,String shopid,String orderid,BigDecimal tradeMoney,Date orderDate);

    /**
     * @param userid 充值帐单，这里可以直接传为null
     * @param billid 商品交易时，此值其实为orderid
     * @param billType 表示了是充值还是充卡赠送还是商品交易
     * @return
     */
    String sendNewBillMsg(Long userid,String billid,Integer billType);

    /**
     * 通过卡券方式赠送的金额
     * @param userid
     * @param shopid
     * @param code
     * @param billid
     * @param cashGive
     * @return
     */
    boolean newBillByGiveCode(Long userid,String shopid,String code,String billid,BigDecimal cashGive);

    /**
     * 查用户链贝余额
     * @param userid
     * @param shopid
     * @return
     */
    BigDecimal getUserBillBalance(Long userid,String shopid);


    /**
     * 当订单使用链贝支付时，调用此接口传入订单金额，来更新用户的链贝余额
     * @param userid
     * @param shopid
     * @param orderMoney
     * @return
     */
    boolean updateShopUserCashBalance(Long userid,String shopid,String orderid,BigDecimal orderMoney);

    /**
     * 取用户链贝帐单
     * @param userid
     * @param shopid
     * @param year
     * @param month
     * @param pageIndex
     * @param count
     * @return
     */
    List<ShopUserBillVO> getUserBilling(Long userid, String shopid, String year, String month, Integer pageIndex, Integer count);

}
