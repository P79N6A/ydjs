package com.yd.ydsp.biz.opensearch;

import com.aliyun.opensearch.sdk.generated.search.Order;
import com.yd.ydsp.client.domian.paypoint.WareSkuSearcheVO;

import java.util.List;

public interface GoodsSearchService {

    boolean commitOrderData(String wareItemName, WareSkuSearcheVO wareSkuVO);

    List<WareSkuSearcheVO> queryWareByItemId(String shopid, String wareItemid, List<Integer> status, Integer pageNum, Integer pageSize);
    List<WareSkuSearcheVO> queryWareByItemId(String shopid, String wareItemid, Integer pageNum, Integer pageSize);

    /**
     *
     * @param shopid
     * @param sortName
     * @param sortType 0-DECREASE;1-INCREASE
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<WareSkuSearcheVO> queryWareByShopId(String shopid, String sortName, Integer sortType, List<Integer> status, Integer pageNum, Integer pageSize);
    List<WareSkuSearcheVO> queryWareByShopId(String shopid, Integer sortType, List<Integer> status, Integer pageNum, Integer pageSize);
    List<WareSkuSearcheVO> queryWareByShopId(String shopid, Integer sortType, Integer pageNum, Integer pageSize);

    /**
     * 不能超过20个
     * @param shopid
     * @param count
     * @return
     */
    List<WareSkuSearcheVO> queryWareByNewSku(String shopid, Integer count);
    List<WareSkuSearcheVO> queryWareByHotSku(String shopid, Integer count);
    List<WareSkuSearcheVO> queryWareByMustSku(String shopid, List<Integer> status);
    List<WareSkuSearcheVO> queryWareByMustSku(String shopid);

    /**
     * 暂时用在主页最后的更多商品当中
     * @param shopid
     * @return
     */
    List<WareSkuSearcheVO> queryWareByMoreSku(String shopid,Integer count);

}
