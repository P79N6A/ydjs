package com.yd.ydsp.biz.opensearch;

import com.aliyun.opensearch.sdk.generated.commons.OpenSearchClientException;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchException;
import com.yd.ydsp.client.domian.openshop.SearchOrderDataResultVO;
import com.yd.ydsp.dal.entity.YdShopConsumerOrder;

import java.util.Date;
import java.util.List;

public interface OrderSearchService {

    /**
     * 订单创建，订单修改时，都要将订单数据同步到opensearch上去
     * @param consumerOrder
     * @return
     * @throws OpenSearchClientException
     * @throws OpenSearchException
     */
    boolean commitOrderData(YdShopConsumerOrder consumerOrder);


    /**
     * --------------------------------------------------C端用户查询接口-------------------------------------------------
     */

    /**
     * 用户在某店的所有订单
     * @param unionid
     * @param shopid
     * @param pageNum 查第几页
     * @param pageSize 每页多少数据
     * @return
     */
    List<SearchOrderDataResultVO> queryAllUserOrder(String unionid, String shopid, Integer pageNum, Integer pageSize);

    /**
     * 查待付款订单
     * @param unionid
     * @param shopid
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<SearchOrderDataResultVO> queryUserOrderByNeedPay(String unionid, String shopid, Integer pageNum, Integer pageSize);

    /**
     * 查待发货订单
     * @param unionid
     * @param shopid
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<SearchOrderDataResultVO> queryUserOrderByWaitSendOut(String unionid, String shopid, Integer pageNum, Integer pageSize);

    /**
     * 查待收货订单
     * @param unionid
     * @param shopid
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<SearchOrderDataResultVO> queryUserOrderBySendOut(String unionid, String shopid, Integer pageNum, Integer pageSize);


    /**
     *----------------------------------------------店铺订单查询接口-----------------------------------------
     */

    /**
     * 按日期查询店铺堂食订单
     * @param shopid
     * @param date
     * @return
     */

    List<SearchOrderDataResultVO> queryShopAllOrderIndoor(String shopid, Date date, List<Integer> status, Integer pageNum, Integer pageSize);

    /**
     *指定日期下指定桌位的订单
     * @param shopid
     * @param tableId
     * @param date
     * @return
     */

    public List<SearchOrderDataResultVO> queryShopOrderIndoorByTableId(String shopid, String tableId, Date date, List<Integer> status, Integer pageNum, Integer pageSize);

    /**
     * 查询线上商城指定日期的订单
     * @param shopid
     * @param date
     * @return
     */

    List<SearchOrderDataResultVO> queryShopOnlineOrder(String shopid, Date date, List<Integer> status, Integer pageNum, Integer pageSize);

    /**
     * 查询线上商城指定日期指定渠道的订单
     * @param shopid
     * @param channelId
     * @param date
     * @return
     */

    List<SearchOrderDataResultVO> queryShopOnlineOrderByChannelId(String shopid, String channelId, Date date, List<Integer> status, Integer pageNum, Integer pageSize);

    /**
     * 查询商城指定日期的所有订单
     * @param shopid
     * @param date
     * @return
     */

    List<SearchOrderDataResultVO> queryShopAllOrder(String shopid,String enterCode, Date date, List<Integer> status, Integer pageNum, Integer pageSize);

    /**
     * 按日期查询收钱宝订单
     * @param shopid
     * @param date
     * @return
     */

    List<SearchOrderDataResultVO> queryShopAllMoneyOrder(String shopid, Date date, List<Integer> status, Integer pageNum, Integer pageSize);

}
