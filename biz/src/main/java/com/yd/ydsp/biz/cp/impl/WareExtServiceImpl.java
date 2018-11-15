package com.yd.ydsp.biz.cp.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondYdWareConfigHolder;
import com.yd.ydsp.biz.cp.WareExtService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.client.domian.paypoint.WareSkuPicVO;
import com.yd.ydsp.client.domian.paypoint.YdCpParameterInfoVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.FeatureUtil;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.YdCpParameterInfo;
import com.yd.ydsp.dal.entity.YdPaypointWaresSku;
import com.yd.ydsp.dal.entity.YdPaypointWaresSkuPic;
import com.yd.ydsp.dal.entity.YdShopWareUnit;
import com.yd.ydsp.dal.mapper.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

public class WareExtServiceImpl implements WareExtService {

    public static final Logger logger = LoggerFactory.getLogger(WareExtServiceImpl.class);


    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private UserinfoService userinfoService;
    @Resource
    private YdCpParameterInfoMapper ydCpParameterInfoMapper;
    @Resource
    private YdPaypointWaresSkuMapper ydPaypointWaresSkuMapper;
    @Resource
    private YdShopWareUnitMapper ydShopWareUnitMapper;
    @Resource
    private YdPaypointWaresSkuPicMapper ydPaypointWaresSkuPicMapper;;


    private String addWareParamInfo(String shopid, YdCpParameterInfoVO cpParameterInfoVO) {

        if(StringUtil.isEmpty(cpParameterInfoVO.getParameterGroup())){
            cpParameterInfoVO.setParameterGroup("商品参数");
        }

        if(StringUtil.isEmpty(cpParameterInfoVO.getParameterKey())||StringUtil.isEmpty(cpParameterInfoVO.getParameterValue())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "数据填写不完整!");
        }

        YdCpParameterInfo ydCpParameterInfo = doMapper.map(cpParameterInfoVO,YdCpParameterInfo.class);
        ydCpParameterInfo.setShopid(shopid);
        ydCpParameterInfo.setSkuid(cpParameterInfoVO.getSkuid());
        ydCpParameterInfo.setParameterid(RandomUtil.getSNCode(TypeEnum.OTHER));
        ydCpParameterInfo.setIsDelete(0);

//        logger.info("ydCpParameterInfoVO is :"+ JSON.toJSONString(cpParameterInfoVO));
        if(ydCpParameterInfoMapper.insert(ydCpParameterInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "增加商品参数失败!");
        }

        return ydCpParameterInfo.getParameterid();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateWareParamInfo(String shopid, String skuid, List<YdCpParameterInfoVO> cpParameterInfoVOS) {
        if(cpParameterInfoVOS!=null&&cpParameterInfoVOS.size()>0){
            List<YdCpParameterInfo> ydCpParameterInfos = ydCpParameterInfoMapper.selectBySkuid(skuid);
            if(ydCpParameterInfos==null){
                ydCpParameterInfos = new ArrayList<>();
            }

            List<String> isUpdateList = new ArrayList<>();


            Iterator<YdCpParameterInfoVO> cpParameterInfoVOIterator = cpParameterInfoVOS.iterator();
            while (cpParameterInfoVOIterator.hasNext()){
                YdCpParameterInfoVO cpParameterInfoVO = cpParameterInfoVOIterator.next();
                if(StringUtil.isEmpty(cpParameterInfoVO.getParameterKey())||StringUtil.isEmpty(cpParameterInfoVO.getParameterValue())){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "数据填写不完整!");
                }
                if(StringUtil.isEmpty(cpParameterInfoVO.getParameterid())){
                    /**
                     * parameterid如果是空的，说明是新增
                     */
                    this.addWareParamInfo(shopid,cpParameterInfoVO);
                }else{
                    /**
                     * 不为空说明要修改
                     */
                    if(StringUtil.isEmpty(cpParameterInfoVO.getParameterGroup())){
                        cpParameterInfoVO.setParameterGroup("商品参数");
                    }
                    String paramId = cpParameterInfoVO.getParameterid();
                    Iterator<YdCpParameterInfo> cpParameterInfoIterator = ydCpParameterInfos.iterator();
                    if(cpParameterInfoIterator!=null) {
                        while (cpParameterInfoIterator.hasNext()) {
                            YdCpParameterInfo cpParameterInfo = cpParameterInfoIterator.next();
                            if (StringUtil.equals(cpParameterInfo.getParameterid(), paramId)) {
                                cpParameterInfo.setIsDelete(0);
                                cpParameterInfo.setSkuid(cpParameterInfoVO.getSkuid());
                                cpParameterInfo.setParameterGroup(cpParameterInfoVO.getParameterGroup());
                                cpParameterInfo.setParameterKey(cpParameterInfoVO.getParameterKey());
                                cpParameterInfo.setParameterValue(cpParameterInfoVO.getParameterValue());

                                if(ydCpParameterInfoMapper.updateByPrimaryKey(cpParameterInfo)<=0){
                                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品参数修改失败!");
                                }
                                isUpdateList.add(cpParameterInfo.getParameterid());
                            }
                        }
                    }
                }
                /**
                 * 说明要删除
                 */
            }
            /**
             * 增加与修改完成，应该看看是不是有需要删除的了
             */

            Iterator<YdCpParameterInfo> ydCpParameterInfoIterator = ydCpParameterInfos.iterator();
            if(ydCpParameterInfoIterator!=null) {
                while (ydCpParameterInfoIterator.hasNext()) {
                    YdCpParameterInfo cpParameterInfo = ydCpParameterInfoIterator.next();
                    boolean needDelete = true;
                    for(String pid:isUpdateList) {
                        if(StringUtil.equals(pid,cpParameterInfo.getParameterid())){
                            needDelete = false;
                        }

                    }
                    if(needDelete) {
                        cpParameterInfo.setIsDelete(1);
                        if (ydCpParameterInfoMapper.updateByPrimaryKey(cpParameterInfo) <= 0) {
                            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "删除商品参数失败!");
                        }
                    }
                }
            }

        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Set<String> addWareParamInfos(String shopid, Set<YdCpParameterInfoVO> ydCpParameterInfoVOList) {
        if(ydCpParameterInfoVOList==null||ydCpParameterInfoVOList.isEmpty()){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "数据不能为空!");
        }
        Set<String> result = new HashSet<>();
        for (YdCpParameterInfoVO ydCpParameterInfoVO : ydCpParameterInfoVOList){
            String paramId = this.addWareParamInfo(shopid,ydCpParameterInfoVO);
            result.add(paramId);
        }

        return result;
    }

//    @Override
//    public boolean deleteWareParamInfo(String openid, String parameterid) {
//        YdCpParameterInfo ydCpParameterInfo = ydCpParameterInfoMapper.selectByParameterid(parameterid);
//        if(!(userinfoService.checkIsOwner(openid,ydCpParameterInfo.getShopid())||userinfoService.checkIsManager(openid,ydCpParameterInfo.getShopid()))){
//            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
//        }
//        if(ydCpParameterInfo!=null){
//            ydCpParameterInfo.setIsDelete(1);
//            if(ydCpParameterInfoMapper.updateByPrimaryKey(ydCpParameterInfo)<=0){
//                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "删除商品参数失败!");
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public boolean deleteWareParamInfoList(String openid, Set<String> parameteridList) {
//        if(parameteridList==null||parameteridList.isEmpty()){
//            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "数据不能为空!");
//        }
//
//        for(String parameterid: parameteridList){
//            this.deleteWareParamInfo(openid,parameterid);
//        }
//
//        return true;
//    }

    @Override
    public YdCpParameterInfoVO queryWareParamInfo(String parameterid) {
        YdCpParameterInfo ydCpParameterInfo = ydCpParameterInfoMapper.selectByParameterid(parameterid);
        YdCpParameterInfoVO ydCpParameterInfoVO = null;
        if(ydCpParameterInfo!=null){
            ydCpParameterInfoVO = doMapper.map(ydCpParameterInfo,YdCpParameterInfoVO.class);
        }
        return ydCpParameterInfoVO;
    }

    @Override
    public List<YdCpParameterInfoVO> queryWareParamInfoList(String skuid) {
        List<YdCpParameterInfo> ydCpParameterInfos = ydCpParameterInfoMapper.selectBySkuid(skuid);
        List<YdCpParameterInfoVO> ydCpParameterInfoVOS = new ArrayList<>();
        if(ydCpParameterInfos!=null){
            for (YdCpParameterInfo cpParameterInfo : ydCpParameterInfos){
                YdCpParameterInfoVO cpParameterInfoVO = doMapper.map(cpParameterInfo,YdCpParameterInfoVO.class);
                ydCpParameterInfoVOS.add(cpParameterInfoVO);
            }
        }
        return ydCpParameterInfoVOS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addWareUnitName(String openid, String shopid, String unitName) {
        if(!(userinfoService.checkIsOwner(openid,shopid)||userinfoService.checkIsManager(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if(StringUtil.isEmpty(unitName)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "单位不能为空!");
        }
        boolean result = false;
        YdShopWareUnit ydShopWareUnit = ydShopWareUnitMapper.selectByShopidRowLock(shopid);
        boolean isNew = false;
        if(ydShopWareUnit!=null){
            if(StringUtil.isNotEmpty(ydShopWareUnit.getUnitNames())) {
                List<String> resultList = FeatureUtil.strToList(ydShopWareUnit.getUnitNames());
                if(resultList!=null){
                    for(String uName:resultList){
                        if(StringUtil.equals(uName.trim(),unitName.trim())){
                            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "请不要增加已经存在的单位!");
                        }
                    }
                }
                result = resultList.add(unitName);
                ydShopWareUnit.setUnitNames(FeatureUtil.listToString(resultList));
            }
        }else {
            isNew = true;
            List<String> resultList = new ArrayList<>();
            resultList.add(unitName);
            ydShopWareUnit = new YdShopWareUnit();
            ydShopWareUnit.setUnitNames(FeatureUtil.listToString(resultList));
            ydShopWareUnit.setShopid(shopid);
        }

        if(isNew){
            if (ydShopWareUnitMapper.insert(ydShopWareUnit) <= 0) {
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "增加失败!");
            }

        }else {
            if (ydShopWareUnitMapper.updateByPrimaryKey(ydShopWareUnit) <= 0) {
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "增加失败!");
            }
        }
        return result;
    }

    @Override
    public Map queryUnitName(String openid, String shopid) {
        if(!(userinfoService.checkIsOwner(openid,shopid)||userinfoService.checkIsManager(openid,shopid)||userinfoService.checkIsWaiter(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdShopWareUnit ydShopWareUnit = ydShopWareUnitMapper.selectByShopid(shopid);

        List<String> resultList = null;
        if(ydShopWareUnit!=null){
            if(StringUtil.isNotEmpty(ydShopWareUnit.getUnitNames())) {
                resultList = FeatureUtil.strToList(ydShopWareUnit.getUnitNames());
            }
        }

        Map<String,List<String>> result = new HashMap<>();
        result.put("sysUnit",FeatureUtil.strToList(DiamondYdWareConfigHolder.getInstance().getSysUnits()));
        result.put("userUnit",resultList);
        return result;
    }

    @Override
    public boolean deleteUnitName(String openid, String shopid, String unitName) {
        if(!(userinfoService.checkIsOwner(openid,shopid)||userinfoService.checkIsManager(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        boolean result = false;
        YdShopWareUnit ydShopWareUnit = ydShopWareUnitMapper.selectByShopid(shopid);
        if(ydShopWareUnit!=null){
            if(StringUtil.isNotEmpty(ydShopWareUnit.getUnitNames())) {
                List<String> resultList = FeatureUtil.strToList(ydShopWareUnit.getUnitNames());
                result = resultList.remove(unitName);
                ydShopWareUnit.setUnitNames(FeatureUtil.listToString(resultList));
                if(ydShopWareUnitMapper.updateByPrimaryKey(ydShopWareUnit)<=0){
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "删除失败!");
                }
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updatePicDetails(String openid, List<WareSkuPicVO> wareSkuPicVOS) {
        if(wareSkuPicVOS==null||wareSkuPicVOS.size()<=0){
            return true;
        }
        String skuid = wareSkuPicVOS.get(0).getSkuid();
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }
        if(!(userinfoService.checkIsOwner(openid,ydPaypointWaresSku.getShopid())||userinfoService.checkIsManager(openid,ydPaypointWaresSku.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        List<YdPaypointWaresSkuPic> ydPaypointWaresSkuPics = ydPaypointWaresSkuPicMapper.selectPicDetailBySkuid(skuid);
        Iterator<YdPaypointWaresSkuPic> paypointWaresSkuPicIterator = ydPaypointWaresSkuPics.iterator();
        for(WareSkuPicVO wareSkuPicVO : wareSkuPicVOS){
            if(!StringUtil.equals(wareSkuPicVO.getSkuid(),skuid)){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "数据中存在不一致的skuid!");
            }
            boolean needInsert = false;
            if (paypointWaresSkuPicIterator!=null){
                /**
                 * 这里应该用if，不要改成while了哈，因为是顺序的用新的列表覆盖数据库中的列表
                 * 数据库查出的列表被覆盖完了，新列表还有，就新增，新传入的列表都设置完了，数据库列表还有多的，就应该把多的删除掉。
                 */
                if (paypointWaresSkuPicIterator.hasNext()){
                    YdPaypointWaresSkuPic paypointWaresSkuPic = paypointWaresSkuPicIterator.next();
                    paypointWaresSkuPic.setIsDelete(0);
                    paypointWaresSkuPic.setSkuid(skuid);
                    paypointWaresSkuPic.setSn(wareSkuPicVO.getSn());
                    paypointWaresSkuPic.setPicUrl(wareSkuPicVO.getPicUrl());
                    if(ydPaypointWaresSkuPicMapper.updateByPrimaryKey(paypointWaresSkuPic)<=0){
                        throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新失败!");
                    }
                    paypointWaresSkuPicIterator.remove();
                    continue;
                }else {
                    needInsert = true;
                }
            }else {
                needInsert = true;
            }

            if(needInsert) {
                /**
                 * 说明大于数据库中已经有的值，需要新增
                 */
                YdPaypointWaresSkuPic paypointWaresSkuPicNew = new YdPaypointWaresSkuPic();
                paypointWaresSkuPicNew.setSkuid(skuid);
                paypointWaresSkuPicNew.setSn(wareSkuPicVO.getSn());
                paypointWaresSkuPicNew.setPicUrl(wareSkuPicVO.getPicUrl());
                paypointWaresSkuPicNew.setIsDelete(0);
                paypointWaresSkuPicNew.setPicType(0);
                if (ydPaypointWaresSkuPicMapper.insert(paypointWaresSkuPicNew) <= 0) {
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新失败!");
                }
            }
        }

        /**
         * 还要判断数据库中是不是还有剩下的图文，有的话，说明是要删除的
         */
        while (paypointWaresSkuPicIterator.hasNext()){
            YdPaypointWaresSkuPic paypointWaresSkuPic = paypointWaresSkuPicIterator.next();
            paypointWaresSkuPic.setIsDelete(1);
            if(ydPaypointWaresSkuPicMapper.updateByPrimaryKey(paypointWaresSkuPic)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新失败!");
            }
            paypointWaresSkuPicIterator.remove();
            continue;
        }


        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteAllPicDetails(String openid, String skuid) {
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }
        if(!(userinfoService.checkIsOwner(openid,ydPaypointWaresSku.getShopid())||userinfoService.checkIsManager(openid,ydPaypointWaresSku.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        List<YdPaypointWaresSkuPic> ydPaypointWaresSkuPics = ydPaypointWaresSkuPicMapper.selectPicDetailBySkuid(skuid);
        if(ydPaypointWaresSkuPics!=null){
            for (YdPaypointWaresSkuPic paypointWaresSkuPic: ydPaypointWaresSkuPics){
                paypointWaresSkuPic.setIsDelete(1);
                if(ydPaypointWaresSkuPicMapper.updateByPrimaryKey(paypointWaresSkuPic)<=0){
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "删除失败!");
                }
            }
        }

        return true;
    }

    @Override
    public List<WareSkuPicVO> queryPicDetails(String skuid) {
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }

        List<YdPaypointWaresSkuPic> ydPaypointWaresSkuPics = ydPaypointWaresSkuPicMapper.selectPicDetailBySkuid(skuid);
        List<WareSkuPicVO> result = new ArrayList<>();
        if(ydPaypointWaresSkuPics!=null){
            for (YdPaypointWaresSkuPic paypointWaresSkuPic: ydPaypointWaresSkuPics){
                WareSkuPicVO wareSkuPicVO = new WareSkuPicVO();
                wareSkuPicVO.setSkuid(skuid);
                wareSkuPicVO.setSn(paypointWaresSkuPic.getSn());
                wareSkuPicVO.setPicUrl(paypointWaresSkuPic.getPicUrl());
                result.add(wareSkuPicVO);
            }
        }

        return result;
    }
}
