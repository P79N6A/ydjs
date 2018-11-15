package com.yd.ydsp.biz.cp;

import com.yd.ydsp.biz.customer.model.order.ShopOrderSkuVO;
import com.yd.ydsp.client.domian.paypoint.WareItemVO;
import com.yd.ydsp.client.domian.paypoint.WareSkuBaseVO;
import com.yd.ydsp.client.domian.paypoint.WareSkuVO;
import com.yd.ydsp.dal.entity.YdPaypointWaresSku;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 18/1/8
 */
public interface WareService {

    /**
     * 根据itemids查询商品类目信息
     * @param itemid
     * @return
     */
    WareItemVO getWareItem(String itemid);
    /**
     * 根据shopid取店铺的商品分类(toC)
     * @param shopid
     * @return
     */
    List<WareItemVO> getWareItemByShopId(String shopid);

    /**
     *取shopid下的商品列表(toC)
     * @param shopid
     * @param wareitemid 可以为空，为空时查所有此shopid下的商品
     * @return
     */
    List<WareSkuVO> getWareSku(String shopid, String wareitemid,Boolean is2C);

    /**
     * 根据skuid查询商品信息
     * @param skuid
     * @return
     */
    WareSkuVO getWareSku(String skuid);
    WareSkuVO getWareSku(YdPaypointWaresSku paypointWaresSku);

    WareSkuBaseVO getWareSkuBase(String skuid);
    WareSkuBaseVO getWareSkuBase(YdPaypointWaresSku paypointWaresSku);

    /**
     * 根据shopid取商品信息与分类信息(toB)
     * @param shopid
     * @return
     * "wareItemList":List<WareItemVO>，"wareSkuList":List<WareSkuVO>
     */
    Map<String , Object> getWareInfoByShopId(String shopid);

    /**
     * 根据shopid取商品信息与分类信息(toC，数量为0的SKU不会显示)
     * @param shopid
     * @return
     */
    Map<String, Object> getWareInfo2CByShopId(String shopid);


    /**
     * 增加商品类别（toC)
     * @param openid
     * @param shopid
     * @param name
     * @param seq
     * @param putaway true上架 false下架
     * @return 类目id
     */
    String addWareItem(String openid,String shopid,String name,Integer seq,Boolean putaway);

    /**
     * 修改商品类别（toC)
     * @param openid
     * @param shopid
     * @param wareitemid
     * @param name
     * @param putaway true上架 false下架
     * @return
     */
    boolean modifyWareItem(String openid,String shopid, String wareitemid,String name,Boolean putaway);

    /**
     * 删除商品类别(toC)
     * @param openid
     * @param shopid
     * @param wareitemid
     * @return
     */
    List<WareItemVO> delWareItem(String openid,String shopid, String wareitemid);

    /**
     * 对商品类别进行排序(toC)
     * @param openid
     * @param shopid
     * @param wareItemVOList
     * @return
     */
    boolean sortWareItem(String openid,String shopid, List<WareItemVO> wareItemVOList);

    /**
     * 对商品进行增加操作
     * @param openid
     * @param wareSkuVO
     * @return
     */
    String addWareSku(String openid,WareSkuVO wareSkuVO);

    /**
     * 对商品进行修改操作
     * @param openid
     * @param wareSkuVO
     * @return
     */
    boolean modifyWareSku(String openid,WareSkuVO wareSkuVO);

    /**
     * 删除商品
     * @param openid
     * @param shopid
     * @param skuid
     * @return
     */
    List<WareSkuVO> delWareSku(String openid,String shopid, String skuid);

    /**
     * 商品排序操作
     * @param openid
     * @param shopid
     * @param wareitemid
     * @param wareSkuVOList
     * @return
     */
    boolean sortWareSku(String openid,String shopid, String wareitemid,List<WareSkuVO> wareSkuVOList);

    /**
     * 取上传图片token
     * @param openid
     * @param shopid
     * @param key
     * @return
     */
    Map<String, Object> getOssAuthentication(String openid,String shopid, String key) throws UnsupportedEncodingException;

    /**
     * 取必点商品列表
     * @param shopid
     * @return
     */
    List<WareSkuVO> getIsMustWare(String shopid);
    List<String> getIsMustWareId(String shopid);

    /**
     * 用于更新商品的售卖数量与库存数量
     */
    void updateWareSellInfo(String shopOrderSkuVOSJsonStr);

    /**
     * 发送商品售买及库存数量更新的消息
     * @return
     */
    String sendWareSellInfoMessage(String skuid,List<ShopOrderSkuVO> shopOrderSkuVOS);


    /**
     * 取推荐商品列表
     * @param shopid
     * @return
     */
    List<WareSkuVO> getCommendWare(String shopid);
    List<WareSkuVO> getCommendWare(String shopid,Integer count);

    /**
     * 取前N个新品，不得超过20个
     * @param shopid
     * @return
     */
    List<WareSkuVO> getNewArrival(String shopid,Integer count);

    /**
     * 热卖商品N个，不得超过20个
     * @param shopid
     * @param count
     * @return
     */
    List<WareSkuVO> getHostWare(String shopid,Integer count);

    /**
     * 更多商品，以后逻辑要改为猜你喜欢
     * @param shopid
     * @param count
     * @return
     */
    List<WareSkuVO> getMoreWare(String shopid,Integer count);

}
