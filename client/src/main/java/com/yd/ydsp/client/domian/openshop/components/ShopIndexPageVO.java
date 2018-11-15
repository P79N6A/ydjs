package com.yd.ydsp.client.domian.openshop.components;

import com.yd.ydsp.client.domian.paypoint.WareSkuVO;
import com.yd.ydsp.client.domian.paypoint.YdShopActivityInfoVO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ShopIndexPageVO implements Serializable {

    String shopid;
    /**
     * 背景色，可以为空，为空则系统使用自己的默认值
     */
    String backColor;
    /**
     * banner资源列表
     */
    List<ResourceInfoVO> bannerInfoList;

    /**
     * 用户自定义商品展示区
     */
    List<CustomShowItemVO> homeCustomShowItemList;

    /**
     * 商品上新
     */
    CustomShowItemVO homeNewSku;

    /**
     * 商品推荐
     */
    CustomShowItemVO homeWareCommed;

    /**
     * 最火商品
     */
    CustomShowItemVO homeWareHot;

    /**
     * 更多商品，以后要在这里显示为"猜你喜欢"
     */
    CustomShowItemVO homeWareMore;

    /**
     * 主页面相关的商品集合<wareSkuid,详情>
     */
    Map<String,WareSkuVO> wareSkuInfos;

    /**
     * 主页面相关的活动集合
     */
    Map<String,YdShopActivityInfoVO> activityInfos;

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public List<ResourceInfoVO> getBannerInfoList() {
        return bannerInfoList;
    }

    public void setBannerInfoList(List<ResourceInfoVO> bannerInfoList) {
        this.bannerInfoList = bannerInfoList;
    }

    public List<CustomShowItemVO> getHomeCustomShowItemList() {
        return homeCustomShowItemList;
    }

    public void setHomeCustomShowItemList(List<CustomShowItemVO> homeCustomShowItemList) {
        this.homeCustomShowItemList = homeCustomShowItemList;
    }

    public CustomShowItemVO getHomeNewSku() {
        return homeNewSku;
    }

    public void setHomeNewSku(CustomShowItemVO homeNewSku) {
        this.homeNewSku = homeNewSku;
    }

    public CustomShowItemVO getHomeWareCommed() {
        return homeWareCommed;
    }

    public void setHomeWareCommed(CustomShowItemVO homeWareCommed) {
        this.homeWareCommed = homeWareCommed;
    }

    public CustomShowItemVO getHomeWareHot() {
        return homeWareHot;
    }

    public void setHomeWareHot(CustomShowItemVO homeWareHot) {
        this.homeWareHot = homeWareHot;
    }

    public CustomShowItemVO getHomeWareMore() {
        return homeWareMore;
    }

    public void setHomeWareMore(CustomShowItemVO homeWareMore) {
        this.homeWareMore = homeWareMore;
    }

    public Map<String, WareSkuVO> getWareSkuInfos() {
        return wareSkuInfos;
    }

    public void setWareSkuInfos(Map<String, WareSkuVO> wareSkuInfos) {
        this.wareSkuInfos = wareSkuInfos;
    }

    public Map<String, YdShopActivityInfoVO> getActivityInfos() {
        return activityInfos;
    }

    public void setActivityInfos(Map<String, YdShopActivityInfoVO> activityInfos) {
        this.activityInfos = activityInfos;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }
}
