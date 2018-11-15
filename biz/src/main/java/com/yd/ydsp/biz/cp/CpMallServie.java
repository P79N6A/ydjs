package com.yd.ydsp.biz.cp;

import com.yd.ydsp.biz.cp.model.GoodsInfoVO;
import com.yd.ydsp.client.domian.paypoint.CpMallOrderVO;
import com.yd.ydsp.dal.entity.YdPaypointCpOrder;

import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 18/1/15
 */
public interface CpMallServie {
    /**
     *
     * @param openid
     * @param shopid 可以为空，如果为空，表明不需要显示套餐升级与加油包信息
     * @return
     */
    Map<String,Object> getGoodsInfo(String openid, String shopid);

    /**
     * 计算商品(toB)金额
     * goodsInfoVO 是前端传来的GoodsInfoVO
     * @param goodsInfoVO
     * @return
     */
    GoodsInfoVO getTotalAmountByGoods(GoodsInfoVO goodsInfoVO) throws IllegalAccessException;


    /**
     * 提交订单
     * @param openid
     * @param goodsInfo
     * @return
     * @throws IllegalAccessException
     */
    CpMallOrderVO submitOrder(String openid,GoodsInfoVO goodsInfo) throws IllegalAccessException;

    /**
     * 设置订单的收货地址
     * @param openid
     * @param orderid
     * @param deliveryAddress
     * @return
     */
    boolean setDeliveryAddress(String openid,String orderid, String deliveryAddress);

    /**
     * 取消订单
     * @param openid
     * @param orderid
     * @return
     */
    boolean cancelOrder(String openid,String orderid);

    /**
     * 创建支付订单开始支付
     * @param openid
     * @param orderid
     * @return
     */
    Map<String,Object> payMall(String openid,String orderid,String ip);

    CpMallOrderVO getOrder(String openid,String orderid);

    /**
     *
     * @param openid
     * @param pageIndex 从1开始
     * @param count 每页数量
     * @return
     */
    List<CpMallOrderVO> getOrderList(String openid,Integer pageIndex, Integer count);

    /**
     * 保存买家留言接口
     * @param openid
     * @param orderid
     * @param buyerMessage
     * @return
     */
    boolean saveBuyerMessage(String openid,String orderid,String buyerMessage);
}
