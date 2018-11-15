package com.yd.ydsp.biz.cp.model;

import com.yd.ydsp.common.redis.SerializeUtils;

/**
 * @author zengyixun
 * @date 17/12/28
 */
public class YdGoodsInfoVO extends SerializeUtils {

    /**
     * 商品id
     */
    String goodId;

    /**
     * 商品名称
     */
    String goodName;

    /**
     * 商品图片地址
     */
    String goodImageUrl;

    /**
     * 商品价格，精确到分
     */
    Integer price;
    /**
     * 价格信息显示，比如：押金：￥490元
     */
    String priceView;

    /**
     * 0: 在线
     * 1: 下线，不使用，不显示，主要用于前端，修改金额啥的，建议下线后新增一个商品
     */
    Integer online=0;

    /**
     * 商品描述
     */
    String desc;

    public String getGoodId(){ return goodId; }
    public void setGoodId(String goodId){ this.goodId = goodId; }

    public String getGoodName(){ return goodName; }
    public void setGoodName(String goodName){ this.goodName = goodName; }

    public String getGoodImageUrl(){ return goodImageUrl; }
    public void setGoodImageUrl(String goodImageUrl){ this.goodImageUrl = goodImageUrl; }

    public String getPriceView(){ return priceView; }
    public void setPriceView(String priceView){ this.priceView = priceView; }

    public Integer getPrice(){ return price; }
    public void setPrice(Integer price){ this.price = price; }

    public Integer getOnline(){ return online; }
    public void setOnline(Integer online){ this.online = online; }

    public String getDesc(){ return desc; }
    public void setDesc(String desc){ this.desc = desc; }


}
