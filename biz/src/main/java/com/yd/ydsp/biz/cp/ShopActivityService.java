package com.yd.ydsp.biz.cp;

import com.yd.ydsp.client.domian.paypoint.YdShopActivityInfoVO;

import java.util.List;

public interface ShopActivityService {

    /**
     * 增加或者修改一个活动，没有活动id就是增加，有就是修改，修改时，如果状态不等于2则不能修改，时间到期自动设置状态为1（活动关闭）
     * @param openid
     * @param shopActivityInfoVO
     * @return
     */

    String updateShopActivity(String openid,YdShopActivityInfoVO shopActivityInfoVO);

    /**
     * 查询活动记录
     * @param openid
     * @param shopid
     * @param year
     * @param month
     * @param status
     * @return
     */
    List<YdShopActivityInfoVO> queryShopActivity(String openid,String shopid,String year,String month,Integer status, Integer pageIndex, Integer count);

    /**
     * 根据活动id查询活动内容
     * @param activityid
     * @return
     */
    YdShopActivityInfoVO queryShopActivity(String activityid);

    /**
     * 活动关闭接口
     * @param activityid
     * @return
     */
    boolean closeShopActivity(String openid,String activityid);

}
