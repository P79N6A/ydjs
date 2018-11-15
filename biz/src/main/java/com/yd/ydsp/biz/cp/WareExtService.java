package com.yd.ydsp.biz.cp;

import com.yd.ydsp.client.domian.paypoint.WareSkuPicVO;
import com.yd.ydsp.client.domian.paypoint.YdCpParameterInfoVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品扩展服务
 */
public interface WareExtService {

    /**
     * --------------------------商品参数设置-------------------------------
     */

//    /**
//     * 增加一条参数信息
//     * @param shopid
//     * @param cpParameterInfoVO
//     * @return
//     */
//    String addWareParamInfo(String shopid, YdCpParameterInfoVO cpParameterInfoVO);

    /**
     * 更新一个商品的参数列表，
     * @param shopid
     * @param cpParameterInfoVOS
     * @return
     */
    boolean updateWareParamInfo(String shopid, String skuid, List<YdCpParameterInfoVO> cpParameterInfoVOS);

    /**
     * 批量增加参数信息
     * @param shopid
     * @param ydCpParameterInfoVOList
     * @return
     */
    Set<String> addWareParamInfos(String shopid, Set<YdCpParameterInfoVO> ydCpParameterInfoVOList);

//    /**
//     * 删除一条参数信息
//     * @param openid
//     * @param parameterid
//     * @return
//     */
//    boolean deleteWareParamInfo(String openid, String parameterid);
//
//    boolean deleteWareParamInfoList(String openid, Set<String> parameteridList);

    /**
     * 根据参数ID查询参数信息
     * @param parameterid
     * @return
     */
    YdCpParameterInfoVO queryWareParamInfo(String parameterid);

    /**
     * 根据skuid查询参数信息
     * @param skuid
     * @return
     */
    List<YdCpParameterInfoVO> queryWareParamInfoList(String skuid);


/**
 * --------------------------一个店铺对商品单位的信息维护-------------------------------
 */

    boolean addWareUnitName(String openid , String shopid, String unitName);

    Map queryUnitName(String openid, String shopid);

    boolean deleteUnitName(String openid , String shopid, String unitName);

/**
 * --------------------------一个店铺对商品图文详情的（图片）的操作-------------------------------
 */

    /**
     * 更新图文详情列表（在列表不为空的情况下，全体更新，包括增加/删除/修改，全部重建图文列表）
     * @param openid
     * @param wareSkuPicVOS
     * @return
     */
    boolean updatePicDetails(String openid, List<WareSkuPicVO> wareSkuPicVOS);

    /**
     * 删除所有图文详情(如果列表为空，前端应该直接调用此接口）
     * @param openid
     * @param skuid
     * @return
     */
    boolean deleteAllPicDetails(String openid,String skuid);

    /**
     * 查询一个商品当前的图文详情列表
     * @param skuid
     * @return
     */
    List<WareSkuPicVO> queryPicDetails(String skuid);


}
