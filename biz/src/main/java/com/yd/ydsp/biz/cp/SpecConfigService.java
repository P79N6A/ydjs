package com.yd.ydsp.biz.cp;

import com.yd.ydsp.client.domian.paypoint.YdCpSpecConfigInfoVO;
import com.yd.ydsp.client.domian.paypoint.YdCpSpecInfoVO;
import com.yd.ydsp.dal.entity.YdPaypointWaresSku;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SpecConfigService {

    /**
     * 新增一个规格信息
     * @param shopid
     * @param cpSpecInfoVO
     * @return
     */
    String addSpecInfo(String shopid, YdCpSpecInfoVO cpSpecInfoVO);

    /**
     * 更新一个规格信息，就包括了增加这个规格的item与删除及修改这个规格的item内容
     * @param openid
     * @param cpSpecInfoVO
     * @return
     */
    boolean updateSpecInfo(String openid,YdCpSpecInfoVO cpSpecInfoVO);

    /**
     * 将整个规格进行删除
     * @param openid
     * @param specid
     * @return
     */
    boolean deleteSpecInfo(String openid,String specid);

    /**
     * 查询一个指定的规格信息
     * @param openid
     * @param specid
     * @return
     */
    YdCpSpecInfoVO querySpecInfo(String openid,String specid);

    /**
     * 查询一个店铺下所有的基本规格名称与id
     * @param openid
     * @param shopid
     * @return   [{"specid":"29898765655","name":"规格一"},......]
     */
    List<Map> querySpecNameList(String openid,String shopid);


    /**
     * --------------------------------以下开始对一个商品进行具体的规格组合及价格设置---------------------------------
     */


    /**
     * 暂时不暴露到外部接口中
     * 合并两个规格内容，如果相同，并用后一个覆盖前一个
     * @param openid
     * @param shopid
     * @param skuid
     * @param cpBaseSpecConfigInfoVOList
     * @param cpAddSpecConfigInfoVOList
     * @return
     */
    List<YdCpSpecConfigInfoVO> mergeSpecConfig(String openid, String shopid,String skuid, Set<YdCpSpecConfigInfoVO> cpBaseSpecConfigInfoVOList,Set<YdCpSpecConfigInfoVO> cpAddSpecConfigInfoVOList);

    /**
     * 新增或者覆盖一个商品的组合规格配置信息(skuid与主规格名称需要全部一致）
     * @param openid
     * @param cpSpecConfigInfoVOList
     * @return
     */
    boolean addSpecConfigInfo(String openid, Set<YdCpSpecConfigInfoVO> cpSpecConfigInfoVOList);

    /**
     * 删除指定的商品的规格配置信息
     * @return
     */
    boolean deleteSpecConfigInfoBySkuId(String openid,String skuid);

    /**
     * 查询一个指定商品的规格配置信息总览
     * @param openid
     * @param skuid
     * @return
     */
    Set<YdCpSpecConfigInfoVO> querySpecConfigInfo(String openid, String skuid);
    List<YdCpSpecConfigInfoVO> querySpecConfigInfo(String skuid);

    /**
     * 取得一个商品下的主规格列表
     * @param skuid
     * @return
     */
    Set<String> getMainSpecNameList(String skuid);

    /**
     *根据选中的主规格，返回此主规格下的子规格列表
     * @param skuid
     * @param mainSpecName
     * @return
     */
    Set<String> getChildSpecNameList(String skuid,String mainSpecName);

    /**
     * 取一个规格配置的价格
     * @param skuid
     * @param mainSpecName
     * @param childSpecName
     * @return
     */
    Map<String,Object> getPriceBySku(String skuid,String mainSpecName, String childSpecName);
    Map<String,Object> getInfoBySku(String skuid,String mainSpecName, String childSpecName);

    /**
     * 取一个商品中规格设置中的最低价格
     * @param skuid
     * @return
     */
    BigDecimal getLowerPriceBySpecConfigInfo(String skuid);
    BigDecimal getLowerPriceBySpecConfigInfo(YdPaypointWaresSku ydPaypointWaresSku);

    /**
     * 取一个商品规格设置中所有规格的库存总和
     * @param skuid
     * @return
     */
    Integer getInventoryBySpecConfigInfo(String skuid);
    Integer getInventoryBySpecConfigInfo(YdPaypointWaresSku ydPaypointWaresSku);

}
