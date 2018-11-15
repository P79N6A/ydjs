package com.yd.ydsp.biz.cp;

import com.yd.ydsp.biz.cp.model.ApplyShopInfoVO;
import com.yd.ydsp.client.domian.ShopInfoDTO;
import com.yd.ydsp.client.domian.ShopInfoVO;
import com.yd.ydsp.client.domian.openshop.YdCpMemberCardVO;
import com.yd.ydsp.client.domian.openshop.YdShopHoursInfoVO;
import com.yd.ydsp.client.domian.paypoint.ShopSimpleInfoVO;
import com.yd.ydsp.common.enums.OrderStatusEnum;
import com.yd.ydsp.common.enums.paypoint.PrintTypeEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/9/12.
 */
public interface ShopInfoService {

    /**
     * 取负责人下的所有店铺id
     * @param openid
     * @return
     */
    List<String> getShopsByOwner(String openid);

    /**
     * 取管理员下的所有店铺id
     * @param openid
     * @return
     */
    List<String> getShopsByManager(String openid);

    /**
     * 取默认的店铺信息
     * @param openid
     * @return
     */
    public ShopSimpleInfoVO getDefaultShop(String openid);

    /**
     * 根据cp用户，取他下面的可操作店铺信息
     * @param openid
     * @return
     */
    List<ShopSimpleInfoVO> getShopsByCpUser(String openid);

    /**
     * 入驻申请的第一步
     * @param openid
     * @param contractid
     * @param shopInfoVO 对应ApplyShopInfoVO
     * @return
     */
    String applyShopJoin(String openid,String contractid,ApplyShopInfoVO shopInfoVO);
//    String applyShopJoin(String openid,ApplyShopInfoVO applyShopJson);

    /**
     * 根据shopid来取申请入驻第一步的申请信息数据
     * @param shopid
     * @return
     */
    ApplyShopInfoVO getApplyShopInfoByShopId(String shopid);

    /**
     * 取一个店铺的信息
     * @param shopid
     * @return
     */
    ShopInfoDTO getShopInfo(String shopid);

//    /**
//     *
//     * 设置店铺信息，只有owner可以操作
//     * @param openid
//     * @param shopInfoVO
//     * @return
//     */
//    boolean setShopInfoByOwner(String openid, ShopInfoVO shopInfoVO);

    /**
     *
     * 设置店铺信息，owner与管理员可以操作
     * @param openid
     * @param shopInfoVO
     * @return
     */
    boolean setShopInfo(String openid, ShopInfoVO shopInfoVO);

    /**
     * 取店铺信信
     * @param openid
     * @param shopid
     * @return
     */
    ShopInfoVO getShopInfoByInfoPage(String openid,String shopid);


    /**
     * 获取店铺套餐信息
     * @return
     */
    List<Map<String,Object>> getSetMealInfo();

    /**
     * 根据选择的套餐及月份计算总金额
     * @param setMealType
     * @param monthNum
     * @return 返回一个原价，一个折后价
     */
    Map<String,Integer> getTotalAmountBySetMeal(String setMealType, Integer monthNum,boolean isUp);

    /**
     * 创建套餐订单
     * @param openid
     * @param shopid
     * @param setMealType
     * @param monthNum
     * @return CpSetMealOrderVO 与 payUrl
     */
    Map<String,Object> createOrderBySetMeal(String openid,String shopid,String setMealType, Integer monthNum, String ip);

    /**
     * 取支付页面url，如果第一次需要向第三方支付系统先发起创建订单，如果orderId中已经存在payUrl则直接返回
     * @param openid
     * @param shopid
     * @param orderId
     * @return
     */
    String getPayUrlBySetMeal(String openid, String shopid, String orderId);

    /**
     * 更新CP订单状态，包括CP订单表及店铺表的状态都要更新
     * @param orderid
     * @param statusEnum
     * @return
     */
    void updateCpOrderStatus(String orderid, OrderStatusEnum statusEnum);

    /**
     * 查询自己管理权限里的店铺的支付订单状态及金额
     * @param openid
     * @param orderid
     * @return
     */
    public Map<String,Object> queryOrderIdByCP(String openid, String orderid) throws Exception;

    /**
     * 计算店铺套餐的剩余天数
     * @param shopid
     * @return
     */
    public Long queryRemanent(String shopid,boolean isDay);
    public Long queryRemanent(ShopInfoDTO shopInfoDTO,boolean isDay);
    /**
     * 计算店铺套餐的剩余多少个月多少天数
     */
    public Map<String,Long> queryRemanent(String shopid);

    /**
     * 取指定套餐类型的信息
     * @param setMealType
     * @return
     */
    public Map<String,Object> getSetMeal(String setMealType);

    /**
     * 取申请中的店铺信息
     * @param openid
     * @return
     */
    public ApplyShopInfoVO getApplyingShopInfo(String openid);

    /**
     * 检查一个deviceid是不是这个店铺的设备
     * @param shopid
     * @return
     */
    public boolean checkDeviceInShop(String shopid, String deviceid, TypeEnum type);

    /**
     * 设置堂食是否立即支付
     * @param openid
     * @param shopid
     * @param isPayNow
     * @return
     */
    public boolean setPayModel(String openid, String shopid, boolean isPayNow);

    /**
     * 取一个店铺的堂食付款模式
     * @param shopid
     * @return
     */
    public boolean isPayNow(String shopid);

    /**
     * 设置购物是否立即支付
     * @param openid
     * @param shopid
     * @param isPayNow
     * @return
     */
    public boolean setUserPayModel(String openid, String shopid, boolean isPayNow);

    /**
     * 取一个店铺的购物付款模式
     * @param shopid
     * @return
     */
    public boolean userPayNow(String shopid);

    /**
     * ----------------------会员卡管理-----------------------
     */

    /**
     * 初始化商家会员卡功能设置的基本信息
     * @param shopid
     * @return
     */
    public boolean initMemberCard(String shopid);

    /**
     * 查询一个店下的所有会员卡基本信息
     * 此后接口不可暴露到控制层，应该是在店铺创建时直接初始化
     * @param shopid
     * @return
     */
    public List<YdCpMemberCardVO> queryMemberCardInfoList(String shopid);

    /**
     * 根据卡id查询此卡的基本设置信息
     * @param cardid
     * @return
     */
    public YdCpMemberCardVO queryMemberCardInfo(String cardid);

    /**
     * 更新卡设置的基本信息
     * @param openid
     * @param cpMemberCardVO
     * @return
     */
    public boolean updateMemberCardInfo(String openid, YdCpMemberCardVO cpMemberCardVO);

    /**
     * 积分奖励
     * @param userid
     * @param userSourceType
     * @param shopid
     * @return
     */
    Integer rewardConsumptionPoint(Long userid, Integer userSourceType,String shopid, BigDecimal amount,boolean writeDB);

    /**
     * 生成订单时，检查消耗积分是否正确，正确时就要将用户的积分进行扣除，如果订单被取消，在进行取消操作时将积分再返还
     * @param point 要消耗积分
     * @param orderMoney 订单金额
     * @return 成功抵扣的金额
     */
    BigDecimal consumptionPoint(Long userid, Integer userSourceType,String shopid, Integer point, BigDecimal orderMoney) throws IllegalAccessException;

    /**
     * 订单取消，退回已经扣掉的积分
     * @param userid
     * @param userSourceType
     * @param shopid
     * @param point
     * @return
     */
    boolean rebackConsumptionPoint(Long userid, Integer userSourceType,String shopid, Integer point);

    /**
     * 取用户可以抵扣的金额
     * @param userid
     * @param userSourceType
     * @param shopid
     * @return
     */
    Map<String,Object> getDeductionMoney(Long userid, Integer userSourceType,String shopid,BigDecimal orderMoney) throws IllegalAccessException;

    /**
     * 查询用户的卡积分可用数量及总积分数量
     * @param userid
     * @param userSourceType
     * @param shopid
     * @return
     */
    Map<String,Long> getCardPointValue(Long userid, Integer userSourceType,String shopid);

    /**
     * 判断店铺是否已经休息
     * @param shopHoursInfoVO
     * @param status 店铺的状态值
     * @return
     */
    boolean shopIsSleep(YdShopHoursInfoVO shopHoursInfoVO,Integer status);

}
