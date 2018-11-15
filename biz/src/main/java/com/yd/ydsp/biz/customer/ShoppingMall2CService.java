package com.yd.ydsp.biz.customer;

import com.yd.ydsp.biz.customer.model.Order2CVO;
import com.yd.ydsp.biz.customer.model.ShoppingCartVO;
import com.yd.ydsp.biz.customer.model.order.ShopOrder2C;
import com.yd.ydsp.biz.customer.model.order.ShopOrderExt2C;
import com.yd.ydsp.biz.customer.model.order.ShoppingOrderSkuVO;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.client.domian.ShopInfoVO2C;
import com.yd.ydsp.client.domian.UserOrderDeliveryInfoVO;
import com.yd.ydsp.client.domian.openshop.SearchOrderDataResultVO;
import com.yd.ydsp.client.domian.paypoint.ShopUserBillVO;
import com.yd.ydsp.common.enums.OrderStatusEnum;
import com.yd.ydsp.common.enums.UserSourceEnum;
import com.yd.ydsp.dal.entity.YdShopConsumerOrder;
import com.yd.ydsp.dal.entity.YdShopUserBill;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ShoppingMall2CService {

    /**
     * 加购物车
     * @param openid
     * @param qrCode
     * @param skuMap
     * @return
     */
    ShoppingCartVO updateCartInfo(String openid,String qrCode,Map<String,Integer> skuMap) throws IOException, ClassNotFoundException;

    /**
     * 取当前用户的购物车信息
     * @param openid
     * @param qrCode
     * @return
     */
    Map<String,Object> getCartInfo(String openid,String qrCode) throws IOException, ClassNotFoundException;

    /**
     * 清空购物车
     * @param openid
     * @param qrCode
     */
    boolean clearShoppintCart(String openid,String qrCode) throws IOException;

    /**
     * 提交订单操作
     * @param openid
     * @param qrCode
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Order2CVO submitOrder(String openid, String qrCode,String description) throws IOException, ClassNotFoundException;

    /**
     * C端订单发起支付
     * @param openid
     * @param orderid
     * @param ip
     * @return
     */
    Map<String,Object> payConsumerOrder(String openid,String orderid,String ip) throws Exception;


    /**
     * 用于内部服务状态变更
     * @param orderid
     * @param orderStatusEnum
     * @retur
     */
    void updateOrderStatus(String orderid,OrderStatusEnum orderStatusEnum);

    /**
     * 查询订单信息
     * @param openid
     * @param orderid
     * @return
     */
    Order2CVO getOrder(String openid, String orderid);

    /**
     *
     * @param openid
     * @param pageIndex 从1开始
     * @param count 每页数量
     * @return
     */
    List<Order2CVO> getOrderListByConsumerId(String openid , Integer status,Integer pageIndex, Integer count);

    /**
     * 店铺公开信息
     * @param shopid
     * @return
     */
    ShopInfoVO2C getShopInfoByCustomer(String shopid);


    /**
     * ------------------------------------独立的C端商城模式-------------------------------------------
     */

    /**
     * 计算订单以及购物车的金额
     * @param shoppingOrderSkuVOS
     * @return
     */
    ShopOrder2C calculateOrderMoney(UserSession userSession, List<ShoppingOrderSkuVO> shoppingOrderSkuVOS);

    /**
     * 创建订单
     * @param userSession
     * @param shopOrder2C
     * @return
     */
    ShopOrder2C createUserOrder(UserSession userSession, String addressid, ShopOrder2C shopOrder2C) throws IllegalAccessException;

    /**
     * 消费者取消订单
     * @param userSession
     * @param orderid
     * @return
     */
    boolean cancelUserOrder(UserSession userSession,String orderid);

    /**
     * 消费者确认订单送达
     * @param userSession
     * @param orderid
     * @return
     */
    boolean confirmUserOrder(UserSession userSession,String orderid);

    /**
     * C端用户订单查询接口
     * @param unionid
     * @param shopid
     * @param pageIndex
     * @param count
     * @param searchType
     * @return
     */
    List<SearchOrderDataResultVO> queryUserOrderList(String unionid, String shopid, Integer pageIndex, Integer count, Integer searchType);

    /**
     * 根据orderid查询订单详情
     * @param unionid
     * @param orderid
     * @return
     */
    ShopOrderExt2C queryUserOrderDetail(String unionid, String orderid) throws IllegalAccessException;
    ShopOrderExt2C queryUserOrderDetail(UserSession userSession, String orderid) throws IllegalAccessException;

    /**
     * 用户订单进行支付
     * @param orderid
     * @param payType 支付方式，默认为微信支付
     * @return
     */
    Map<String,Object> payUserOrder(UserSession userSession,String orderid,Integer payType) throws Exception;

    /**
     * 用户充值功能
     * @param userSession
     * @param shopid
     * @param cashType
     * @return
     */
    Map<String, Object> cashRecharge(UserSession userSession,String shopid, Integer cashType) throws Exception;

    /**
     * 查询一个用户在某家店里的积分值，总累积分值，与可用分值
     * @param userSession
     * @param shopid
     * @return
     */
    Map<String,Long> queryCardPointValue(UserSession userSession,String shopid);


    /**
     * 可获取积分查询
     * @param userSession
     * @param shopid
     * @param amount
     * @return
     */
    public Integer rewardConsumptionPoint(UserSession userSession, String shopid, BigDecimal amount);

    /**
     * 生成订单时，检查消耗积分是否正确，正确时就要将用户的积分进行扣除，如果订单被取消，在进行取消操作时将积分再返还
     * @param point 要消耗积分
     * @param orderMoney 订单金额
     * @return 成功抵扣的金额
     */
    BigDecimal consumptionPoint(UserSession userSession,String shopid, Integer point, BigDecimal orderMoney) throws IllegalAccessException;

    /**
     * 取用户可以抵扣的金额
     * @param shopid
     * @return
     */
    Map<String,Object> getDeductionMoney(UserSession userSession,String shopid,BigDecimal orderMoney) throws IllegalAccessException;
    Map<String, Object> getDeductionMoney(String unionid, String shopid, BigDecimal orderMoney) throws IllegalAccessException;

    /**
     * 给前端一个计算单价*数量的接口
     * @param unionid
     * @param price
     * @param count
     * @return
     */
    BigDecimal calculatePrice(String unionid, BigDecimal price,Integer count);

    /**
     * 查询店铺的一些实时状态，比如是否已经休息
     * @param unionid
     * @param shopid
     * @return
     */
    Map<String,Object> queryShopStauts(String unionid,String shopid);

    /**
     * 取下单前，下单页上需要进行的一些初始化显示
     * @param unionid
     * @param enterCode
     * @return
     */
    Map<String,Object> querPreCreateOrderInfo(String unionid, String shopid,String enterCode);

    /**
     * 取用户在一个店铺的链贝余额
     * @param unionid
     * @param shopid
     * @return
     */
    BigDecimal queryUserBillBalance(String unionid,String shopid);

    /**
     * 根据年月，取用户在一个店的链贝帐单记录
     * @param unionid
     * @param shopid
     * @param year
     * @param month
     * @param pageIndex
     * @param count
     * @return
     */
    List<ShopUserBillVO> queryUserBilling(String unionid, String shopid, String year, String month, Integer pageIndex, Integer count);

}
