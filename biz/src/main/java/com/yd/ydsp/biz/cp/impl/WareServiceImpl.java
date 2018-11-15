package com.yd.ydsp.biz.cp.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.config.DiamondYdWareConfigHolder;
import com.yd.ydsp.biz.cp.SpecConfigService;
import com.yd.ydsp.biz.cp.WareExtService;
import com.yd.ydsp.biz.cp.WareService;
import com.yd.ydsp.biz.customer.model.order.ShopOrderSkuVO;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.oss.OSSFileService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.client.domian.paypoint.*;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.constants.paypoint.WareSkuFlagConstants;
import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.AmountUtils;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.common.utils.FeatureUtil;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.*;
import org.apache.commons.collections.IteratorUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zengyixun
 * @date 18/1/8
 */
public class WareServiceImpl implements WareService {

    public static final Logger logger = LoggerFactory.getLogger(WareServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private YdPaypointWareitemMapper ydPaypointWareitemMapper;
    @Resource
    private YdPaypointWaresSkuMapper ydPaypointWaresSkuMapper;
    @Resource
    private YdPaypointWaresSkuExtMapper ydPaypointWaresSkuExtMapper;
    @Resource
    private YdPaypointShopInfoExtMapper ydPaypointShopInfoExtMapper;
    @Resource
    private YdCpSpecConfigInfoMapper ydCpSpecConfigInfoMapper;

    @Resource
    private OSSFileService ossFileService;

    @Resource
    private WareExtService wareExtService;

    @Resource
    private SpecConfigService specConfigService;

    @Resource
    private UserinfoService userinfoService;
    @Resource
    private MqMessageService mqMessageService;



    @Override
    public WareItemVO getWareItem(String itemid) {
        YdPaypointWareitem paypointWareitem = ydPaypointWareitemMapper.selectByItemId(itemid);
        if(paypointWareitem==null){
            return null;
        }
        WareItemVO wareItemVO = doMapper.map(paypointWareitem,WareItemVO.class);
        return wareItemVO;
    }

    @Override
    public List<WareItemVO> getWareItemByShopId(String shopid) {
        List<WareItemVO> wareItemVOS = new ArrayList<>();
        List<YdPaypointWareitem> ydPaypointWareitems = ydPaypointWareitemMapper.selectByShopid(shopid);
        if(ydPaypointWareitems==null||ydPaypointWareitems.size()<=0){
            return wareItemVOS;
        }
        for(YdPaypointWareitem wareitem:ydPaypointWareitems){
            WareItemVO wareItemVO = doMapper.map(wareitem,WareItemVO.class);
            wareItemVOS.add(wareItemVO);
        }
        return wareItemVOS;
    }

    @Override
    public List<WareSkuVO> getWareSku(String shopid, String wareitemid,Boolean is2C) {
        List<WareSkuVO> wareSkuVOS = new ArrayList<>();
        List<YdPaypointWaresSku> ydPaypointWaresSkus = null;
        if(is2C){
            ydPaypointWaresSkus = ydPaypointWaresSkuMapper.selectByShopidAndItem2C(shopid, wareitemid);
        }else {
            ydPaypointWaresSkus = ydPaypointWaresSkuMapper.selectByShopidAndItem(shopid, wareitemid);
        }
        if(ydPaypointWaresSkus==null||ydPaypointWaresSkus.size()<=0){
            return wareSkuVOS;
        }
        for(YdPaypointWaresSku ydPaypointWaresSku:ydPaypointWaresSkus){
            WareSkuVO wareSkuVO = doMapper.map(ydPaypointWaresSku,WareSkuVO.class);
            wareSkuVO.setModifyDateInt(DateUtils.getSecondTimestamp(ydPaypointWaresSku.getModifyDate()));
            wareSkuVO.setCreateDateInt(DateUtils.getSecondTimestamp(ydPaypointWaresSku.getCreateDate()));
            WareSkuDTO wareSkuDTO = doMapper.map(ydPaypointWaresSku,WareSkuDTO.class);
            if(wareSkuDTO.isExistFlag(WareSkuFlagConstants.HASSPACPRICE)){
                wareSkuVO.setHasSpacPrice(true);
            }else {
                wareSkuVO.setHasSpacPrice(false);
            }

            if(StringUtil.StrIsJson(ydPaypointWaresSku.getWareimg())){
                wareSkuVO.setWareimgList(JSON.parseArray(ydPaypointWaresSku.getWareimg(),WareSkuPicVO.class));
                wareSkuVO.setWareimg(DiamondYdWareConfigHolder.getInstance().getDefaultGoodsPicUrl());
                for(WareSkuPicVO wareSkuPicVO : wareSkuVO.getWareimgList()){
                    if(wareSkuPicVO.getSn().intValue()==1){
                        wareSkuVO.setWareimg(wareSkuPicVO.getPicUrl());
                        break;
                    }
                }
            }

            /**
             * 取规格
             */
            List<YdCpSpecConfigInfoVO> specConfigInfoVOS = specConfigService.querySpecConfigInfo(wareSkuVO.getSkuid());
            wareSkuVO.setSpecConfigInfoVOList(specConfigInfoVOS);

            /**
             * 取参数
             */
            List<YdCpParameterInfoVO> parameterInfoVOS = wareExtService.queryWareParamInfoList(wareSkuVO.getSkuid());
            wareSkuVO.setParameterInfoVOList(parameterInfoVOS);

            /**
             * 图文数据
             */
            List<WareSkuPicVO> picVOS = wareExtService.queryPicDetails(wareSkuVO.getSkuid());
            wareSkuVO.setSkuPicVOList(picVOS);

            YdPaypointWaresSkuExt ydPaypointWaresSkuExt = ydPaypointWaresSkuExtMapper.selectBySkuId(wareSkuVO.getSkuid());
            YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(wareSkuVO.getShopid());
            if(ydPaypointWaresSkuExt!=null){
                wareSkuVO.setWeight(ydPaypointWaresSkuExt.getWeight());
                if(StringUtil.isNotEmpty(ydPaypointWaresSkuExt.getVolume())){
                    wareSkuVO.setVolume(JSON.parseObject(ydPaypointWaresSkuExt.getVolume(),Map.class));
                }
                wareSkuVO.setDiscount(ydPaypointWaresSkuExt.getDiscount());

                if(ydPaypointShopInfoExt!=null) {
                    /**
                     * 是否推荐
                     */
                    if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getCommend())) {
                        List<String> commendList = FeatureUtil.strToList(ydPaypointShopInfoExt.getCommend());
                        if (commendList != null) {
                            if (commendList.contains(wareSkuVO.getSkuid())) {
                                wareSkuVO.setIsCommend(true);
                            } else {
                                wareSkuVO.setIsCommend(false);
                            }
                        } else {
                            wareSkuVO.setIsCommend(false);
                        }
                    }
                    /**
                     * 是否必选商品
                     */
                    if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getMustWare())) {
                        List<String> mustList = FeatureUtil.strToList(ydPaypointShopInfoExt.getMustWare());
                        if (mustList != null) {
                            if (mustList.contains(wareSkuVO.getSkuid())) {
                                wareSkuVO.setIsMustWare(true);
                            } else {
                                wareSkuVO.setIsMustWare(false);
                            }
                        } else {
                            wareSkuVO.setIsMustWare(false);
                        }
                    }
                }else {
                    wareSkuVO.setIsCommend(false);
                    wareSkuVO.setIsMustWare(false);
                }
            }

            wareSkuVOS.add(wareSkuVO);
        }
        return wareSkuVOS;
    }

    @Override
    public WareSkuVO getWareSku(String skuid) {
        YdPaypointWaresSku paypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(paypointWaresSku==null){
            return null;
        }
        return this.getWareSku(paypointWaresSku);

    }

    @Override
    public WareSkuVO getWareSku(YdPaypointWaresSku paypointWaresSku) {
        String skuid = paypointWaresSku.getSkuid();
        WareSkuVO wareSkuVO = doMapper.map(paypointWaresSku,WareSkuVO.class);
        wareSkuVO.setModifyDateInt(DateUtils.getSecondTimestamp(paypointWaresSku.getModifyDate()));
        wareSkuVO.setCreateDateInt(DateUtils.getSecondTimestamp(paypointWaresSku.getCreateDate()));
        WareSkuDTO wareSkuDTO = doMapper.map(paypointWaresSku,WareSkuDTO.class);
        if(wareSkuDTO.isExistFlag(WareSkuFlagConstants.HASSPACPRICE)){
            wareSkuVO.setHasSpacPrice(true);
        }else {
            wareSkuVO.setHasSpacPrice(false);
        }

        if(StringUtil.StrIsJson(paypointWaresSku.getWareimg())){
            wareSkuVO.setWareimgList(JSON.parseArray(paypointWaresSku.getWareimg(),WareSkuPicVO.class));
            wareSkuVO.setWareimg(DiamondYdWareConfigHolder.getInstance().getDefaultGoodsPicUrl());
            for(WareSkuPicVO wareSkuPicVO : wareSkuVO.getWareimgList()){
                if(wareSkuPicVO.getSn().intValue()==1){
                    wareSkuVO.setWareimg(wareSkuPicVO.getPicUrl());
                    break;
                }
            }
        }

        /**
         * 取规格
         */
        List<YdCpSpecConfigInfoVO> specConfigInfoVOS = specConfigService.querySpecConfigInfo(wareSkuVO.getSkuid());
        wareSkuVO.setSpecConfigInfoVOList(specConfigInfoVOS);

        /**
         * 取参数
         */
        List<YdCpParameterInfoVO> parameterInfoVOS = wareExtService.queryWareParamInfoList(wareSkuVO.getSkuid());
        wareSkuVO.setParameterInfoVOList(parameterInfoVOS);

        /**
         * 图文数据
         */
        List<WareSkuPicVO> picVOS = wareExtService.queryPicDetails(wareSkuVO.getSkuid());
        wareSkuVO.setSkuPicVOList(picVOS);

        YdPaypointWaresSkuExt ydPaypointWaresSkuExt = ydPaypointWaresSkuExtMapper.selectBySkuId(wareSkuVO.getSkuid());
        YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(wareSkuVO.getShopid());
        if(ydPaypointWaresSkuExt!=null){
            wareSkuVO.setWeight(ydPaypointWaresSkuExt.getWeight());
            if(StringUtil.isNotEmpty(ydPaypointWaresSkuExt.getVolume())){
                wareSkuVO.setVolume(JSON.parseObject(ydPaypointWaresSkuExt.getVolume(),Map.class));
            }
            wareSkuVO.setDiscount(ydPaypointWaresSkuExt.getDiscount());

            if(ydPaypointShopInfoExt!=null) {
                /**
                 * 是否推荐
                 */
                if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getCommend())) {
                    List<String> commendList = FeatureUtil.strToList(ydPaypointShopInfoExt.getCommend());
                    if (commendList != null) {
                        if (commendList.contains(skuid)) {
                            wareSkuVO.setIsCommend(true);
                        } else {
                            wareSkuVO.setIsCommend(false);
                        }
                    } else {
                        wareSkuVO.setIsCommend(false);
                    }
                }

                /**
                 * 是否必选商品
                 */
                if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getMustWare())) {
                    List<String> mustList = FeatureUtil.strToList(ydPaypointShopInfoExt.getMustWare());
                    if (mustList != null) {
                        if (mustList.contains(skuid)) {
                            wareSkuVO.setIsMustWare(true);
                        } else {
                            wareSkuVO.setIsMustWare(false);
                        }
                    } else {
                        wareSkuVO.setIsMustWare(false);
                    }
                }
            }else {
                wareSkuVO.setIsCommend(false);
                wareSkuVO.setIsMustWare(false);
            }
        }else {
            wareSkuVO.setDiscount(new BigDecimal("1.0"));
        }

        return wareSkuVO;
    }

    @Override
    public WareSkuBaseVO getWareSkuBase(String skuid) {
        YdPaypointWaresSku paypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(paypointWaresSku==null){
            return null;
        }
        return this.getWareSkuBase(paypointWaresSku);
    }

    @Override
    public WareSkuBaseVO getWareSkuBase(YdPaypointWaresSku paypointWaresSku) {
        String skuid = paypointWaresSku.getSkuid();
        WareSkuBaseVO wareSkuBaseVO = doMapper.map(paypointWaresSku,WareSkuBaseVO.class);
        WareSkuDTO wareSkuDTO = doMapper.map(paypointWaresSku,WareSkuDTO.class);
        if(wareSkuDTO.isExistFlag(WareSkuFlagConstants.HASSPACPRICE)){
            wareSkuBaseVO.setHasSpacPrice(true);
        }else {
            wareSkuBaseVO.setHasSpacPrice(false);
        }

        wareSkuBaseVO.setWareimg(paypointWaresSku.getWareimg());

        YdPaypointWaresSkuExt ydPaypointWaresSkuExt = ydPaypointWaresSkuExtMapper.selectBySkuId(wareSkuBaseVO.getSkuid());
        YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(wareSkuBaseVO.getShopid());
        if(ydPaypointWaresSkuExt!=null){

            wareSkuBaseVO.setDiscount(ydPaypointWaresSkuExt.getDiscount());

            if(ydPaypointShopInfoExt!=null) {
                /**
                 * 是否推荐
                 */
                if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getCommend())) {
                    List<String> commendList = FeatureUtil.strToList(ydPaypointShopInfoExt.getCommend());
                    if (commendList != null) {
                        if (commendList.contains(skuid)) {
                            wareSkuBaseVO.setIsCommend(true);
                        } else {
                            wareSkuBaseVO.setIsCommend(false);
                        }
                    } else {
                        wareSkuBaseVO.setIsCommend(false);
                    }
                }

                /**
                 * 是否必选商品
                 */
                if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getMustWare())) {
                    List<String> mustList = FeatureUtil.strToList(ydPaypointShopInfoExt.getMustWare());
                    if (mustList != null) {
                        if (mustList.contains(skuid)) {
                            wareSkuBaseVO.setIsMustWare(true);
                        } else {
                            wareSkuBaseVO.setIsMustWare(false);
                        }
                    } else {
                        wareSkuBaseVO.setIsMustWare(false);
                    }
                }
            }else {
                wareSkuBaseVO.setIsCommend(false);
                wareSkuBaseVO.setIsMustWare(false);
            }
        }else {
            wareSkuBaseVO.setDiscount(new BigDecimal("1.0"));
        }

        return wareSkuBaseVO;
    }

    @Override
    public Map<String, Object> getWareInfoByShopId(String shopid) {
        //"wareItemList":List<WareItemVO>，"wareSkuList":List<WareSkuVO>
        Map<String,Object> result = new HashMap<>();
        result.put("wareItemList",this.getWareItemByShopId(shopid));
        result.put("wareSkuList",this.getWareSku(shopid,null,false));
        return result;
    }

    @Override
    public Map<String, Object> getWareInfo2CByShopId(String shopid) {
        Map<String,Object> result = new HashMap<>();
        List<WareSkuVO> wareSkuVOS = new ArrayList<>();
        List<WareSkuVO> wareMustSkuVOS = new ArrayList<>();
        List<WareSkuVO> wareSkuVOList = this.getWareSkuBy2C(shopid,null);
        for(WareSkuVO wareSkuVO:wareSkuVOList){
            if(wareSkuVO.getIsCommend()){
                wareSkuVOS.add(wareSkuVO);
            }
            if(wareSkuVO.getIsMustWare()){
                wareMustSkuVOS.add(wareSkuVO);
            }
        }
        List<WareItemVO> wareItemVOS = this.getWareItemByShopId(shopid);
        if(wareItemVOS==null){
            wareItemVOS = new ArrayList<>();
        }
        Iterator<WareItemVO> itemVOIterator = wareItemVOS.iterator();
        while (itemVOIterator.hasNext()){
            WareItemVO wareItemVO = itemVOIterator.next();
            /**
             * C端不显示已经下架的类目
             */
            if(wareItemVO.getStatus().intValue()<0){
                itemVOIterator.remove();
            }
        }
        result.put("wareItemList", IteratorUtils.toList(itemVOIterator));
        result.put("wareSkuList",wareSkuVOList);
        result.put("wareCommendList",wareSkuVOS);
        result.put("wareMustList",wareMustSkuVOS);
        return result;
    }

    @Override
    public String addWareItem(String openid, String shopid, String name, Integer seq,Boolean putaway) {
        if(StringUtil.isBlank(name)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"名称不能为空!");
        }
        if(seq==null||seq<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"排序值不正确!");
        }
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        String itemId = RandomUtil.getSNCode(TypeEnum.WAREITEM);
        WareItemVO wareItemVO = new WareItemVO();
        wareItemVO.setName(name);
        if(putaway) {
            wareItemVO.setStatus(0);
        }else {
            wareItemVO.setStatus(-1);
        }
        wareItemVO.setShopid(shopid);
        wareItemVO.setItemid(itemId);
        wareItemVO.setItemseq(seq);
        try{
            YdPaypointWareitem paypointWareitem = doMapper.map(wareItemVO,YdPaypointWareitem.class);
            ydPaypointWareitemMapper.insert(paypointWareitem);

        }catch (Exception ex){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"增加失败，请重试!");
        }
        return itemId;
    }

    @Override
    public boolean modifyWareItem(String openid, String shopid, String wareitemid, String name,Boolean putaway) {
        if(StringUtil.isBlank(name)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"名称不能为空!");
        }
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointWareitem paypointWareitem = ydPaypointWareitemMapper.selectByItemId(wareitemid);
        try{
            paypointWareitem.setName(name);
            if(putaway) {
                paypointWareitem.setStatus(0);
            }else {
                paypointWareitem.setStatus(-1);
            }
            ydPaypointWareitemMapper.updateByPrimaryKey(paypointWareitem);

        }catch (Exception ex){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"修改失败，请重试!");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<WareItemVO> delWareItem(String openid, String shopid, String wareitemid) {
        List<WareItemVO> wareItemVOS = null;
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointWareitem paypointWareitem = ydPaypointWareitemMapper.selectByItemId(wareitemid);
        if(paypointWareitem==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"要删除的数据不存在!");
        }
        if(!StringUtil.equals(paypointWareitem.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        Integer seq = paypointWareitem.getItemseq();
        try{
            ydPaypointWareitemMapper.deleteByPrimaryKey(paypointWareitem.getId());
            wareItemVOS = this.getWareItemByShopId(shopid);
            if(wareItemVOS==null){
                wareItemVOS=new ArrayList<>();
                return wareItemVOS;
            }
            for(WareItemVO wareItemVO:wareItemVOS){
                if(wareItemVO.getItemseq()>seq){
                    wareItemVO.setItemseq(wareItemVO.getItemseq()-1);
                    YdPaypointWareitem wareitem = doMapper.map(wareItemVO,YdPaypointWareitem.class);
                    ydPaypointWareitemMapper.updateByPrimaryKeySelective(wareitem);
                }
            }
            ydPaypointWaresSkuMapper.deleteByShopidAndItemLogic(shopid,wareitemid);

        }catch (Exception ex){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"删除失败，请重试!");
        }
        if(wareItemVOS==null){
            wareItemVOS=new ArrayList<>();
        }
        return wareItemVOS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean sortWareItem(String openid, String shopid, List<WareItemVO> wareItemVOList) {
        boolean result = true;
        if(wareItemVOList==null||wareItemVOList.size()<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"排序列表不能为空!");
        }
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        for(WareItemVO wareItemVO: wareItemVOList){
            YdPaypointWareitem paypointWareitem = ydPaypointWareitemMapper.selectByItemId(wareItemVO.getItemid());
            if(paypointWareitem==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"不存在的类别!");
            }
            if(StringUtil.equals(paypointWareitem.getShopid(),shopid)) {
                if(paypointWareitem.getItemseq().intValue()==wareItemVO.getItemseq().intValue()){
                    continue;
                }
                paypointWareitem.setItemseq(wareItemVO.getItemseq());
                ydPaypointWareitemMapper.updateByPrimaryKeySelective(paypointWareitem);
            }else{
                throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addWareSku(String openid, WareSkuVO wareSkuVO) {
        String result = null;
        if(!userinfoService.checkIsManager(openid,wareSkuVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointWareitem paypointWareitem = ydPaypointWareitemMapper.selectByItemId(wareSkuVO.getWareitemid());
        if(paypointWareitem==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"商品类目不存在!");
        }
        if(!StringUtil.equals(paypointWareitem.getShopid(),wareSkuVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"类别与商品不在同一店铺下!");
        }
        if(wareSkuVO.getStatus()==null){
            wareSkuVO.setStatus(0);
        }
        if(wareSkuVO.getStatus().intValue()!=-1&&wareSkuVO.getStatus().intValue()!=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"上下架状态设置不正确！");
        }

        result = RandomUtil.getSNCode(TypeEnum.WARESKU);
        wareSkuVO.setSkuid(result);
        if(StringUtil.isEmpty(wareSkuVO.getWareimg())&&(wareSkuVO.getWareimgList()==null||wareSkuVO.getWareimgList().size()<=0)){
            wareSkuVO.setWareimg(DiamondYdWareConfigHolder.getInstance().getDefaultGoodsPicUrl());
            wareSkuVO.setWareimgList(new ArrayList<>());
            WareSkuPicVO wareSkuPicVO = new WareSkuPicVO();
            wareSkuPicVO.setSkuid(wareSkuVO.getSkuid());
            wareSkuPicVO.setSn(1);
            wareSkuPicVO.setPicUrl(wareSkuVO.getWareimg());
            wareSkuVO.getWareimgList().add(wareSkuPicVO);
        }

        if(wareSkuVO.getWareimgList()!=null&&wareSkuVO.getWareimgList().size()>0){
            if(wareSkuVO.getWareimgList().size()>5){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"商品轮播图不能大于5张！");
            }
            if(wareSkuVO.getWareimgList().size()==1){
                wareSkuVO.setWareimg(wareSkuVO.getWareimgList().get(0).getPicUrl());
            }
            wareSkuVO.setWareimg(JSON.toJSONString(wareSkuVO.getWareimgList()));

        }

        if(StringUtil.isEmpty(wareSkuVO.getName())||wareSkuVO.getPrice()==null||wareSkuVO.getWareseq()==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"参数不完整!");
        }

        if(wareSkuVO.getIsMustWare()==null){
            wareSkuVO.setIsMustWare(false);
        }

        if(wareSkuVO.getIsCommend()==null){
            wareSkuVO.setIsCommend(false);
        }
        if(StringUtil.isEmpty(wareSkuVO.getUnitName())){
            wareSkuVO.setUnitName("个");
        }

        if(wareSkuVO.getWeight()==null){
            wareSkuVO.setWeight(new BigDecimal("0.1"));
        }
        wareSkuVO.setWeight(AmountUtils.bigDecimalBy2(wareSkuVO.getWeight()));
        if(AmountUtils.changeY2F(wareSkuVO.getWeight())<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"重量填写错误!");
        }
        String volumeStr = "";
        if(wareSkuVO.getVolume()!=null){
            volumeStr = JSON.toJSONString(wareSkuVO.getVolume());
        }


        wareSkuVO.setInventory(1);
        if(wareSkuVO.getDisprice()==null){
            wareSkuVO.setDisprice(wareSkuVO.getPrice());
        }
        YdPaypointWaresSku paypointWaresSku = doMapper.map(wareSkuVO,YdPaypointWaresSku.class);
        paypointWaresSku.setSkuid(result);
        YdPaypointWaresSkuExt ydPaypointWaresSkuExt =new YdPaypointWaresSkuExt();
        YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(wareSkuVO.getShopid());
        /**
         * 重量体积
         */
        ydPaypointWaresSkuExt.setSkuid(result);
        ydPaypointWaresSkuExt.setWeight(wareSkuVO.getWeight());
        ydPaypointWaresSkuExt.setVolume(volumeStr);
        /**
         * 折扣设置
         */
        if (wareSkuVO.getDiscount()==null){
            wareSkuVO.setDiscount(new BigDecimal("1.0"));
        }
        int discountInt = AmountUtils.changeY2F(wareSkuVO.getDiscount());
        if(discountInt<=0||discountInt>100){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"折扣设置有误!");
        }
        wareSkuVO.setDiscount(AmountUtils.bigDecimalBy2(wareSkuVO.getDiscount()));
        ydPaypointWaresSkuExt.setDiscount(wareSkuVO.getDiscount());

        paypointWaresSku.setDisprice(AmountUtils.bigDecimalBy2(paypointWaresSku.getPrice().multiply(ydPaypointWaresSkuExt.getDiscount())));

        if(ydPaypointShopInfoExt!=null) {
            /**
             * 是否推荐
             */
            if (wareSkuVO.getIsCommend()) {
                List<String> commendList = null;
                if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getCommend())) {
                    commendList = FeatureUtil.strToList(ydPaypointShopInfoExt.getCommend());
                } else {
                    commendList = new ArrayList<>();
                }
                if (!commendList.contains(wareSkuVO.getSkuid())) {
                    commendList.add(wareSkuVO.getSkuid());
                }
                if (commendList.size() > DiamondYdWareConfigHolder.getInstance().getCommend()) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "推荐的商品数量超过" + DiamondYdWareConfigHolder.getInstance().getCommend() + "个，不能再推荐了!");
                }


                ydPaypointShopInfoExt.setCommend(FeatureUtil.listToString(commendList));
            }

            /**
             * 是否必选商品
             */
            if (wareSkuVO.getIsMustWare()) {
                List<String> mustWareList = null;
                if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getMustWare())) {
                    mustWareList = FeatureUtil.strToList(ydPaypointShopInfoExt.getMustWare());
                } else {
                    mustWareList = new ArrayList<>();
                }
                if (!mustWareList.contains(wareSkuVO.getSkuid())) {
                    mustWareList.add(wareSkuVO.getSkuid());
                }
                if (mustWareList.size() > DiamondYdWareConfigHolder.getInstance().getMustWare()) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "必选商品数量超过" + DiamondYdWareConfigHolder.getInstance().getMustWare() + "个，不能再设置为必选了!");
                }

                ydPaypointShopInfoExt.setMustWare(FeatureUtil.listToString(mustWareList));
            }

            if(ydPaypointShopInfoExtMapper.updateByPrimaryKey(ydPaypointShopInfoExt)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"增加失败，请重试!");
            }
        }

        if(ydPaypointWaresSkuMapper.insert(paypointWaresSku)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"增加失败，请重试!");
        }
        if(ydPaypointWaresSkuExtMapper.insert(ydPaypointWaresSkuExt)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"增加失败，请重试!");
        }

        if(wareSkuVO.getSpecConfigInfoVOList()!=null&&wareSkuVO.getSpecConfigInfoVOList().size()>0){
            paypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(wareSkuVO.getSkuid());
            /**
             * 开始规格设置
             */
            if(wareSkuVO.getSpecConfigInfoVOList().size()>0) {
                Iterator<YdCpSpecConfigInfoVO> specConfigInfoVOIterator = wareSkuVO.getSpecConfigInfoVOList().iterator();
                while (specConfigInfoVOIterator.hasNext()) {
                    YdCpSpecConfigInfoVO cpSpecConfigInfoVO = specConfigInfoVOIterator.next();
                    cpSpecConfigInfoVO.setSkuid(result);
                }
                specConfigService.addSpecConfigInfo(wareSkuVO.getShopid(), new HashSet(wareSkuVO.getSpecConfigInfoVOList()));
            }

            /**
             * 金额要设置为规格中的最小金额
             */
            YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(paypointWaresSku.getSkuid());
            BigDecimal lowPrice = specConfigService.getLowerPriceBySpecConfigInfo(ydPaypointWaresSku);
            Integer inventory = specConfigService.getInventoryBySpecConfigInfo(ydPaypointWaresSku);
            WareSkuDTO wareSkuDTO = doMapper.map(wareSkuVO,WareSkuDTO.class);
            if(lowPrice!=null){
                wareSkuDTO.addFlag(WareSkuFlagConstants.HASSPACPRICE);
                wareSkuVO.setPrice(AmountUtils.bigDecimalBy2(lowPrice));
                wareSkuVO.setDisprice(AmountUtils.bigDecimalBy2(lowPrice.multiply(ydPaypointWaresSkuExt.getDiscount())));
                wareSkuVO.setInventory(inventory);
            }else {
                wareSkuDTO.removeFlag(WareSkuFlagConstants.HASSPACPRICE);
            }
            wareSkuVO.setFlag(wareSkuDTO.getFlag());
            paypointWaresSku.setPrice(AmountUtils.bigDecimalBy2(wareSkuVO.getPrice()));
            paypointWaresSku.setDisprice(AmountUtils.bigDecimalBy2(wareSkuVO.getDisprice()));
            paypointWaresSku.setFlag(wareSkuVO.getFlag());
            if(ydPaypointWaresSkuMapper.updateByPrimaryKeySelective(paypointWaresSku)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"增加失败，请重试!");
            }
        }



        if(wareSkuVO.getParameterInfoVOList()!=null){
            /**
             * 增加参数
             */
            if(wareSkuVO.getParameterInfoVOList().size()>0){
                Iterator<YdCpParameterInfoVO> parameterInfoVOIterator = wareSkuVO.getParameterInfoVOList().iterator();
                while (parameterInfoVOIterator.hasNext()) {
                    YdCpParameterInfoVO parameterInfoVO = parameterInfoVOIterator.next();
                    parameterInfoVO.setSkuid(wareSkuVO.getSkuid());
                }
                wareExtService.addWareParamInfos(wareSkuVO.getShopid(), new HashSet(wareSkuVO.getParameterInfoVOList()));
            }
        }

        if(wareSkuVO.getSkuPicVOList()!=null){
            /**
             * 开始图文设置
             */
            if(wareSkuVO.getSkuPicVOList().size()>0) {
                Iterator<WareSkuPicVO> wareSkuPicVOIterator = wareSkuVO.getSkuPicVOList().iterator();
                while (wareSkuPicVOIterator.hasNext()) {
                    WareSkuPicVO wareSkuPicVO = wareSkuPicVOIterator.next();
                    wareSkuPicVO.setSkuid(paypointWaresSku.getSkuid());
                }
                wareExtService.updatePicDetails(openid, wareSkuVO.getSkuPicVOList());
            }
        }

        this.wareUpdateMessage(wareSkuVO.getSkuid());

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean modifyWareSku(String openid, WareSkuVO wareSkuVO) {
        boolean result = true;
        YdPaypointWaresSku selectWARESKU = ydPaypointWaresSkuMapper.selectBySkuIdRowLock(wareSkuVO.getSkuid());
        if(selectWARESKU==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"商品不存在!");
        }
        if(!userinfoService.checkIsManager(openid,wareSkuVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointWareitem paypointWareitem = ydPaypointWareitemMapper.selectByItemId(wareSkuVO.getWareitemid());
        if(!StringUtil.equals(paypointWareitem.getShopid(),wareSkuVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"类别与商品不在同一店铺下!");
        }
        if(wareSkuVO.getStatus()==null){
            wareSkuVO.setStatus(0);
        }
        if(wareSkuVO.getStatus().intValue()!=-1&&wareSkuVO.getStatus().intValue()!=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"上下架状态设置不正确！");
        }

        /**
         * 轮播图修改
         */
        if(StringUtil.isEmpty(wareSkuVO.getWareimg())&&(wareSkuVO.getWareimgList()==null||wareSkuVO.getWareimgList().size()<=0)){
            wareSkuVO.setWareimg(DiamondYdWareConfigHolder.getInstance().getDefaultGoodsPicUrl());
            wareSkuVO.setWareimgList(new ArrayList<>());
            WareSkuPicVO wareSkuPicVO = new WareSkuPicVO();
            wareSkuPicVO.setSkuid(wareSkuVO.getSkuid());
            wareSkuPicVO.setSn(1);
            wareSkuPicVO.setPicUrl(wareSkuVO.getWareimg());
            wareSkuVO.getWareimgList().add(wareSkuPicVO);
        }

        if(wareSkuVO.getWareimgList()!=null&&wareSkuVO.getWareimgList().size()>0){
            if(wareSkuVO.getWareimgList().size()>5){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"商品轮播图不能大于5张！");
            }
            if(wareSkuVO.getWareimgList().size()==1){
                wareSkuVO.setWareimg(wareSkuVO.getWareimgList().get(0).getPicUrl());
            }
            wareSkuVO.setWareimg(JSON.toJSONString(wareSkuVO.getWareimgList()));

        }

        wareSkuVO.setId(selectWARESKU.getId());
        if(wareSkuVO.getWeight()==null){
            wareSkuVO.setWeight(new BigDecimal("0.1"));
        }
        wareSkuVO.setWeight(AmountUtils.bigDecimalBy2(wareSkuVO.getWeight()));
        if(AmountUtils.changeY2F(wareSkuVO.getWeight())<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"重量填写错误!");
        }

        if(wareSkuVO.getIsMustWare()==null){
            wareSkuVO.setIsMustWare(false);
        }

        if(wareSkuVO.getIsCommend()==null){
            wareSkuVO.setIsCommend(false);
        }
        if(StringUtil.isEmpty(wareSkuVO.getUnitName())){
            wareSkuVO.setUnitName("个");
        }

        String volumeStr = "";
        if(wareSkuVO.getVolume()!=null){
            volumeStr = JSON.toJSONString(wareSkuVO.getVolume());
        }
        boolean extIsNew = false;
        YdPaypointWaresSkuExt ydPaypointWaresSkuExt = ydPaypointWaresSkuExtMapper.selectBySkuId(wareSkuVO.getSkuid());
        YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(wareSkuVO.getShopid());
        if(ydPaypointWaresSkuExt==null){
            extIsNew = true;
            ydPaypointWaresSkuExt = new YdPaypointWaresSkuExt();
            ydPaypointWaresSkuExt.setSkuid(wareSkuVO.getSkuid());
        }

        YdPaypointWaresSku paypointWaresSku = doMapper.map(wareSkuVO,YdPaypointWaresSku.class);
        WareSkuDTO wareSkuDTO = doMapper.map(wareSkuVO,WareSkuDTO.class);

        /**
         * 折扣设置
         */
        if (wareSkuVO.getDiscount()==null){
            wareSkuVO.setDiscount(new BigDecimal("1.0"));
        }
        int discountInt = AmountUtils.changeY2F(wareSkuVO.getDiscount());
        if(discountInt<=0||discountInt>100){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"折扣设置有误!");
        }
        wareSkuVO.setPrice(AmountUtils.bigDecimalBy2(wareSkuVO.getPrice()));
        wareSkuVO.setDisprice(AmountUtils.bigDecimalBy2(AmountUtils.mul(wareSkuVO.getPrice(),wareSkuVO.getDiscount())));;
        ydPaypointWaresSkuExt.setDiscount(wareSkuVO.getDiscount());

        /**
         * 重量体积
         */
        ydPaypointWaresSkuExt.setWeight(wareSkuVO.getWeight());
        ydPaypointWaresSkuExt.setVolume(volumeStr);



        wareSkuDTO.removeFlag(WareSkuFlagConstants.HASSPACPRICE);

        /**
         * 规格修改
         */
        if(wareSkuVO.getSpecConfigInfoVOList()!=null&&wareSkuVO.getSpecConfigInfoVOList().size()>0){
                specConfigService.addSpecConfigInfo(wareSkuVO.getShopid(), new HashSet(wareSkuVO.getSpecConfigInfoVOList()));
            /**
             * 金额要设置为规格中的最小金额
             */
            YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(paypointWaresSku.getSkuid());
            BigDecimal lowPrice = specConfigService.getLowerPriceBySpecConfigInfo(ydPaypointWaresSku);
            Integer inventory = specConfigService.getInventoryBySpecConfigInfo(ydPaypointWaresSku);
            if(inventory!=null){
                if(inventory.intValue()<0){
                    inventory = 0;
                }
            }else {
                inventory = 0;
            }
            wareSkuVO.setInventory(inventory);
            if (lowPrice != null) {
                wareSkuDTO.addFlag(WareSkuFlagConstants.HASSPACPRICE);
                wareSkuVO.setPrice(AmountUtils.bigDecimalBy2(lowPrice));
                wareSkuVO.setDisprice(AmountUtils.bigDecimalBy2(lowPrice.multiply(ydPaypointWaresSkuExt.getDiscount())));
            } else {
                wareSkuDTO.removeFlag(WareSkuFlagConstants.HASSPACPRICE);
            }

        }else {
            specConfigService.deleteSpecConfigInfoBySkuId(openid,wareSkuVO.getSkuid());

        }
        wareSkuVO.setFlag(wareSkuDTO.getFlag());

        if(ydPaypointShopInfoExt!=null) {
            /**
             * 是否推荐
             */
            if (wareSkuVO.getIsCommend()) {
                List<String> commendList = null;
                if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getCommend())) {
                    commendList = FeatureUtil.strToList(ydPaypointShopInfoExt.getCommend());
                } else {
                    commendList = new ArrayList<>();
                }
                if (!commendList.contains(wareSkuVO.getSkuid())) {
                    commendList.add(wareSkuVO.getSkuid());
                }
                if (commendList.size() > DiamondYdWareConfigHolder.getInstance().getCommend()) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "推荐的商品数量超过 " + DiamondYdWareConfigHolder.getInstance().getCommend() + "个，不能再推荐了!");
                }
                ydPaypointShopInfoExt.setCommend(FeatureUtil.listToString(commendList));
            } else {
                if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getCommend())) {
                    List<String> commendList = FeatureUtil.strToList(ydPaypointShopInfoExt.getCommend());
                    if (commendList != null) {
                        if (commendList.contains(wareSkuVO.getSkuid())) {
                            commendList.remove(wareSkuVO.getSkuid());
                            ydPaypointShopInfoExt.setCommend(FeatureUtil.listToString(commendList));
                        }
                    }
                }
            }

            /**
             * 是否必选商品
             */
            if (wareSkuVO.getIsMustWare()) {
                List<String> mustWareList = null;
                if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getMustWare())) {
                    mustWareList = FeatureUtil.strToList(ydPaypointShopInfoExt.getMustWare());
                } else {
                    mustWareList = new ArrayList<>();
                }
                if (!mustWareList.contains(wareSkuVO.getSkuid())) {
                    mustWareList.add(wareSkuVO.getSkuid());
                }
                if (mustWareList.size() > DiamondYdWareConfigHolder.getInstance().getMustWare()) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "必选商品数量超过 " + DiamondYdWareConfigHolder.getInstance().getMustWare() + " 个，不能再设置为必选了!");
                }

                ydPaypointShopInfoExt.setMustWare(FeatureUtil.listToString(mustWareList));
            } else {
                if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getMustWare())) {
                    List<String> mustWareList = FeatureUtil.strToList(ydPaypointShopInfoExt.getMustWare());
                    if (mustWareList != null) {
                        if (mustWareList.contains(wareSkuVO.getSkuid())) {
                            mustWareList.remove(wareSkuVO.getSkuid());
                            ydPaypointShopInfoExt.setMustWare(FeatureUtil.listToString(mustWareList));
                        }
                    }

                }
            }

            if(ydPaypointShopInfoExtMapper.updateByPrimaryKey(ydPaypointShopInfoExt)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"修改失败，请重试!");
            }
        }

        paypointWaresSku.setFlag(wareSkuVO.getFlag());
        paypointWaresSku.setPrice(wareSkuVO.getPrice());
        paypointWaresSku.setInventory(wareSkuVO.getInventory());
        paypointWaresSku.setDisprice(wareSkuVO.getDisprice());

        if(ydPaypointWaresSkuMapper.updateByPrimaryKeySelective(paypointWaresSku)<=0){
            logger.error("paypointWaresSku is : "+ JSON.toJSONString(paypointWaresSku));
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"修改失败，请重试!");
        }

        if(extIsNew){
            if(ydPaypointWaresSkuExtMapper.insert(ydPaypointWaresSkuExt)<=0){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"修改失败，请重试!");
            }
        }else {
            if(ydPaypointWaresSkuExtMapper.updateByPrimaryKey(ydPaypointWaresSkuExt)<=0){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"修改失败，请重试!");
            }
        }

        /**
         * 商品参数增删改
         */
        if(wareSkuVO.getParameterInfoVOList()!=null){
            if(wareSkuVO.getParameterInfoVOList().size()>0){
                Iterator<YdCpParameterInfoVO> parameterInfoVOIterator = wareSkuVO.getParameterInfoVOList().iterator();
                while (parameterInfoVOIterator.hasNext()) {
                    YdCpParameterInfoVO parameterInfoVO = parameterInfoVOIterator.next();
                    parameterInfoVO.setSkuid(wareSkuVO.getSkuid());
                }
            }
            boolean isok = wareExtService.updateWareParamInfo(wareSkuVO.getShopid(),wareSkuVO.getSkuid(),wareSkuVO.getParameterInfoVOList());
            if(!isok){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"商品参数保存失败!");
            }
        }


        /**
         * 图文修改
         */
        if(wareSkuVO.getSkuPicVOList()!=null){
            if(wareSkuVO.getSkuPicVOList().size()>0) {
                wareExtService.updatePicDetails(openid, wareSkuVO.getSkuPicVOList());
            }
        }

        this.wareUpdateMessage(wareSkuVO.getSkuid());

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<WareSkuVO> delWareSku(String openid, String shopid, String skuid) {
        List<WareSkuVO> wareSkuVOS = null;
        YdPaypointWaresSku waresSku = ydPaypointWaresSkuMapper.selectBySkuIdRowLock(skuid);
        if(waresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"商品不存在!");
        }
        if(!userinfoService.checkIsManager(openid,waresSku.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        try {
            Integer seq = waresSku.getWareseq();
            ydPaypointWaresSkuMapper.deleteByPrimaryKeyLogic(waresSku.getId());
            wareSkuVOS = this.getWareSku(shopid, waresSku.getWareitemid(),false);
            if(wareSkuVOS==null){
                wareSkuVOS = new ArrayList<>();
                return wareSkuVOS;
            }
            for(WareSkuVO vo: wareSkuVOS){
                if(vo.getWareseq()>seq){
                    vo.setWareseq(vo.getWareseq()-1);
                    YdPaypointWaresSku sku = doMapper.map(vo,YdPaypointWaresSku.class);
                    ydPaypointWaresSkuMapper.updateByPrimaryKeySelective(sku);
                }
            }
        }catch (Exception ex){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"删除失败，请重试!");
        }

        this.wareUpdateMessage(waresSku.getSkuid());

        if(wareSkuVOS==null){
            wareSkuVOS = new ArrayList<>();
        }
        for(WareSkuVO vo: wareSkuVOS){
            this.wareUpdateMessage(vo.getSkuid());
        }
        return wareSkuVOS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean sortWareSku(String openid, String shopid, String wareitemid, List<WareSkuVO> wareSkuVOList) {
        boolean result = true;
        if(wareSkuVOList==null||wareSkuVOList.size()<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"排序列表不能为空!");
        }
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        for(WareSkuVO skuVO: wareSkuVOList){
            YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuVO.getSkuid());
            if(ydPaypointWaresSku==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"不存在的商品!");
            }
            if(!StringUtil.equals(shopid,ydPaypointWaresSku.getShopid())){
                throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),"店铺不正确！");
            }
            if(!StringUtil.equals(wareitemid,ydPaypointWaresSku.getWareitemid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"商品类别id错误!");
            }
            if(ydPaypointWaresSku.getWareseq().intValue()==skuVO.getWareseq().intValue())
            {
                continue;
            }
            ydPaypointWaresSku.setWareseq(skuVO.getWareseq());
            ydPaypointWaresSkuMapper.updateByPrimaryKeySelective(ydPaypointWaresSku);

        }
        for(WareSkuVO skuVO: wareSkuVOList){
            this.wareUpdateMessage(skuVO.getSkuid());
        }
        return result;
    }


    @Override
    public Map<String, Object> getOssAuthentication(String openid, String shopid,String key) throws UnsupportedEncodingException {
        if(userinfoService.checkIsManager(openid,shopid)){

            if(StringUtil.isBlank(key)){
                key = DiamondYdSystemConfigHolder.getInstance().payPointBucketKey;
            }
            return ossFileService.getOssPayPointAuthentication(key);

        }
        return null;
    }

    @Override
    public List<WareSkuVO> getIsMustWare(String shopid) {
        return null;
    }

    @Override
    public List<String> getIsMustWareId(String shopid) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateWareSellInfo(String shopOrderSkuVOSJsonStr) {
        List<ShopOrderSkuVO> shopOrderSkuVOS = JSON.parseArray(shopOrderSkuVOSJsonStr,ShopOrderSkuVO.class);
        if(shopOrderSkuVOS==null||shopOrderSkuVOS.size()<=0){
            return;
        }
        Set<String> updateSkuId = new HashSet<>();
        for(ShopOrderSkuVO shopOrderSkuVO: shopOrderSkuVOS) {
            YdPaypointWaresSku waresSku = ydPaypointWaresSkuMapper.selectBySkuIdRowLock(shopOrderSkuVO.getSkuid());
            if (waresSku == null) {
                logger.error("商品id:" + shopOrderSkuVO.getSkuid() + "没有找到，更新库存与售卖数量失败!");
                continue;
            }

            if (StringUtil.isNotEmpty(shopOrderSkuVO.getMainSpecName())) {
                YdCpSpecConfigInfo specConfigInfo = ydCpSpecConfigInfoMapper.selectBySkuidAndMainSpecName(shopOrderSkuVO.getSkuid(),shopOrderSkuVO.getMainSpecName());
                if(specConfigInfo!=null){
                    if(StringUtil.isNotEmpty(specConfigInfo.getFeature())) {
                        List<YdCpSpecConfigBaseInfoVO> specConfigBaseInfoVOS = JSON.parseArray(specConfigInfo.getFeature(),YdCpSpecConfigBaseInfoVO.class);
                        for(YdCpSpecConfigBaseInfoVO specConfigBaseInfoVO : specConfigBaseInfoVOS){
                            if(StringUtil.equals(shopOrderSkuVO.getChildSpecName().trim(),specConfigBaseInfoVO.getChildSpecName().trim())){
                                /**
                                 * 先设置一下售卖数
                                 */
                                waresSku.setSellCountMonth(waresSku.getSellCountMonth() + shopOrderSkuVO.getCount());
                                /**
                                 * 调整库存
                                 */
                                waresSku.setInventory(waresSku.getInventory() - shopOrderSkuVO.getCount());//总库存
                                specConfigBaseInfoVO.setInventory(specConfigBaseInfoVO.getInventory() - shopOrderSkuVO.getCount());//SKU库存
                                specConfigInfo.setFeature(JSON.toJSONString(specConfigBaseInfoVOS));
                                if(ydCpSpecConfigInfoMapper.updateByPrimaryKeySelective(specConfigInfo)<=0){
                                    logger.error("shopOrderSkuVO is: " + JSON.toJSONString(shopOrderSkuVO));
                                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新售卖数与库存失败, specConfigInfo is: " + JSON.toJSONString(specConfigInfo));
                                }
                                updateSkuId.add(shopOrderSkuVO.getSkuid());
                                break;
                            }
                        }
                    }
                }
            }else {
                /**
                 * 说明是没有规格的商品，直接设置售卖量
                 */
                waresSku.setSellCountMonth(waresSku.getSellCountMonth() + shopOrderSkuVO.getCount());
                waresSku.setInventory(waresSku.getInventory() - shopOrderSkuVO.getCount());
            }

            if (waresSku.getSellCountMonth().intValue() < 0) {
                waresSku.setSellCountMonth(0);
            }
            if (ydPaypointWaresSkuMapper.updateByPrimaryKeySelective(waresSku) <= 0) {
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新售卖数与库存失败, shopOrderSkuVO is: " + JSON.toJSONString(shopOrderSkuVO));
            }
            updateSkuId.add(waresSku.getSkuid());
        }

        for(String skuid:updateSkuId){
            this.wareUpdateMessage(skuid);
        }

    }

    @Override
    public String sendWareSellInfoMessage(String skuid,List<ShopOrderSkuVO> shopOrderSkuVOS) {
        Map<String,Object> msg = new HashMap<>();
        msg.put(Constant.MQTAG,MqTagEnum.WARESELL.getTag());
        msg.put(Constant.MQBODY,JSON.toJSONString(shopOrderSkuVOS));
        String msgId = mqMessageService.sendMessage(skuid,skuid,MqTagEnum.WARESELL,JSON.toJSONString(msg));
        return msgId;
    }

    @Override
    public List<WareSkuVO> getCommendWare(String shopid) {
        List<WareSkuVO> result = new ArrayList<>();
        return result;
    }

    @Override
    public List<WareSkuVO> getCommendWare(String shopid, Integer count) {
        List<WareSkuVO> result = new ArrayList<>();
        return result;
    }

    @Override
    public List<WareSkuVO> getNewArrival(String shopid, Integer count) {
        List<WareSkuVO> result = new ArrayList<>();
        return result;
    }

    @Override
    public List<WareSkuVO> getHostWare(String shopid, Integer count) {
        List<WareSkuVO> result = new ArrayList<>();
        return result;
    }

    @Override
    public List<WareSkuVO> getMoreWare(String shopid, Integer count) {
        List<WareSkuVO> result = new ArrayList<>();
        return result;
    }

    private void wareUpdateMessage(String skuid){
        if(StringUtil.isEmpty(skuid)){
            return;
        }
        Map<String,Object> msg = new HashMap<>();
        msg.put(Constant.MQTAG,MqTagEnum.WAREUPDATE.getTag());
        msg.put(Constant.MQBODY,skuid);
        String msgId = mqMessageService.sendMessage(skuid,skuid,MqTagEnum.WAREUPDATE,JSON.toJSONString(msg));
        logger.info("商品更新信息发出，SKUID:"+skuid+",msgId:"+msgId);
    }

    protected List<WareSkuVO> getWareSkuBy2C(String shopid, String wareitemid) {
        List<WareSkuVO> wareSkuVOS = new ArrayList<>();
        if(StringUtil.isNotEmpty(wareitemid)) {
            /**
             * C端只显示上架类目
             */
            YdPaypointWareitem wareitem = ydPaypointWareitemMapper.selectByItemId(wareitemid);
            if (wareitem.getStatus().intValue() < 0) {
                return wareSkuVOS;
            }
        }
        List<YdPaypointWaresSku> ydPaypointWaresSkus = ydPaypointWaresSkuMapper.selectByShopidAndItem(shopid,wareitemid);
        if(ydPaypointWaresSkus==null||ydPaypointWaresSkus.size()<=0){
            return wareSkuVOS;
        }
        for(YdPaypointWaresSku ydPaypointWaresSku:ydPaypointWaresSkus){
            WareSkuVO wareSkuVO = doMapper.map(ydPaypointWaresSku,WareSkuVO.class);
            wareSkuVO.setModifyDateInt(DateUtils.getSecondTimestamp(ydPaypointWaresSku.getModifyDate()));
            wareSkuVO.setCreateDateInt(DateUtils.getSecondTimestamp(ydPaypointWaresSku.getCreateDate()));
            WareSkuDTO wareSkuDTO = doMapper.map(ydPaypointWaresSku,WareSkuDTO.class);
            if(wareSkuDTO.isExistFlag(WareSkuFlagConstants.HASSPACPRICE)){
                wareSkuVO.setHasSpacPrice(true);
            }else {
                wareSkuVO.setHasSpacPrice(false);
            }
            YdPaypointWaresSkuExt ydPaypointWaresSkuExt = ydPaypointWaresSkuExtMapper.selectBySkuId(wareSkuVO.getSkuid());
            YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(wareSkuVO.getShopid());
            if(ydPaypointWaresSkuExt!=null){
                wareSkuVO.setWeight(ydPaypointWaresSkuExt.getWeight());
                if(StringUtil.isNotEmpty(ydPaypointWaresSkuExt.getVolume())) {
                    Map<String, Object> volume = JSON.parseObject(ydPaypointWaresSkuExt.getVolume(), Map.class);
                    wareSkuVO.setVolume(volume);
                }

                wareSkuVO.setDiscount(ydPaypointWaresSkuExt.getDiscount());

                if(ydPaypointShopInfoExt!=null) {
                    /**
                     * 是否推荐
                     */
                    if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getCommend())) {
                        List<String> commendList = FeatureUtil.strToList(ydPaypointShopInfoExt.getCommend());
                        if (commendList != null) {
                            if (commendList.contains(wareSkuVO.getSkuid())) {
                                wareSkuVO.setIsCommend(true);
                            } else {
                                wareSkuVO.setIsCommend(false);
                            }
                        } else {
                            wareSkuVO.setIsCommend(false);
                        }
                    }

                    /**
                     * 是否必选商品
                     */
                    if (StringUtil.isNotEmpty(ydPaypointShopInfoExt.getMustWare())) {
                        List<String> mustList = FeatureUtil.strToList(ydPaypointShopInfoExt.getMustWare());
                        if (mustList != null) {
                            if (mustList.contains(wareSkuVO.getSkuid())) {
                                wareSkuVO.setIsMustWare(true);
                            } else {
                                wareSkuVO.setIsMustWare(false);
                            }
                        } else {
                            wareSkuVO.setIsMustWare(false);
                        }
                    }
                }else {
                    wareSkuVO.setIsCommend(false);
                    wareSkuVO.setIsMustWare(false);
                }

            }
            /**
             * C端只显示上架商品
             */
            if(wareSkuVO.getStatus().intValue()==0) {
                wareSkuVOS.add(wareSkuVO);
            }
        }
        return wareSkuVOS;
    }
}
