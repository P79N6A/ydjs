package com.yd.ydsp.biz.openshop;

import com.yd.ydsp.client.domian.openshop.components.PageInfoVO;
import com.yd.ydsp.client.domian.openshop.components.ShopIndexPageVO;

import java.util.List;

public interface ShopPageService {

    /**
     * 新增与更新页面配置信息
     * @param openid
     * @param pageInfoVO
     * @return
     */
    String updatePageInfo(String openid,PageInfoVO pageInfoVO);

    /**
     * 设置为店铺主页
     * @param openid
     * @param shopid
     * @param pageid
     * @return
     */
    boolean setShopIndexPage(String openid,String shopid,String pageid);

    /**
     * 设置为渠道主页
     * @param openid
     * @param channelid
     * @param pageid
     * @return
     */
    boolean setChannelIndexPage(String openid,String channelid,String pageid);

    /**
     * 根据pageid查询页面配置信息
     * @param pageid
     * @return
     */
    PageInfoVO queryPageInfo(String pageid);
    PageInfoVO queryPageInfoRelease(String pageid);

    /**
     * 查询一个店的页面列表
     * @param shopid
     * @return
     */
    List<PageInfoVO> queryPageInfo(String shopid, Integer pageIndex, Integer count);

    List<PageInfoVO> queryPageInfo(String shopid, Integer status, Integer pageIndex, Integer count);

    /**
     * 转换为C端用户直接使用的页面数据
     * @param pageInfoVO
     * @return
     */
    PageInfoVO pageInfo2c(PageInfoVO pageInfoVO);

    /**
     * 用于C端展示的页面信息数据获取
     * @param pageid
     * @return
     */
    PageInfoVO getPageInfo2C(String pageid);

    /**
     * 用于B端预览展示的页面信息数据获取
     * @param pageid
     * @return
     */
    PageInfoVO getPageInfo2BReview(String pageid);

    /**
     * 发布一个页面数据
     * @param openid
     * @param pageid
     * @return
     */
    boolean releasePageInfo(String openid,String pageid);

    /**
     * 生成html片段
     * @param pageInfoVO
     * @return
     */
    String getPageInfoVM(PageInfoVO pageInfoVO);

}
