package com.yd.ydsp.biz.openshop;

public interface ShopGoodsService {

    /**
     * 更新商品信息到opensearch
     * @param skuid
     * @return
     */
    boolean updateOpenSearchData(String skuid);

}
