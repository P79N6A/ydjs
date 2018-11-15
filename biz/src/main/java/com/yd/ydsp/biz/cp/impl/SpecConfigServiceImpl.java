package com.yd.ydsp.biz.cp.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.cp.SpecConfigService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.client.domian.paypoint.YdCpSpecConfigBaseInfoVO;
import com.yd.ydsp.client.domian.paypoint.YdCpSpecConfigInfoVO;
import com.yd.ydsp.client.domian.paypoint.YdCpSpecInfoVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.AmountUtils;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.YdCpSpecConfigInfo;
import com.yd.ydsp.dal.entity.YdCpSpecInfo;
import com.yd.ydsp.dal.entity.YdPaypointWaresSku;
import com.yd.ydsp.dal.entity.YdPaypointWaresSkuExt;
import com.yd.ydsp.dal.mapper.YdCpSpecConfigInfoMapper;
import com.yd.ydsp.dal.mapper.YdCpSpecInfoMapper;
import com.yd.ydsp.dal.mapper.YdPaypointWaresSkuExtMapper;
import com.yd.ydsp.dal.mapper.YdPaypointWaresSkuMapper;
import org.apache.commons.collections.list.TreeList;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

public class SpecConfigServiceImpl implements SpecConfigService {

    public static final Logger logger = LoggerFactory.getLogger(SpecConfigServiceImpl.class);


    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private YdCpSpecInfoMapper ydCpSpecInfoMapper;
    @Resource
    private YdCpSpecConfigInfoMapper ydCpSpecConfigInfoMapper;
    @Resource
    private UserinfoService userinfoService;
    @Resource
    private YdPaypointWaresSkuMapper ydPaypointWaresSkuMapper;
    @Resource
    private YdPaypointWaresSkuExtMapper ydPaypointWaresSkuExtMapper;

    @Override
    public String addSpecInfo(String openid, YdCpSpecInfoVO cpSpecInfoVO) {
        if(!(userinfoService.checkIsOwner(openid,cpSpecInfoVO.getShopid())||userinfoService.checkIsManager(openid,cpSpecInfoVO.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(StringUtil.isBlank(cpSpecInfoVO.getSpecName())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "规格名称不能为空!");
        }

        List<YdCpSpecInfo> ydCpSpecInfos = ydCpSpecInfoMapper.selectByShopid(cpSpecInfoVO.getShopid());
        if(ydCpSpecInfos!=null){
            for (YdCpSpecInfo ydCpSpecInfo : ydCpSpecInfos){
                if(StringUtil.equals(cpSpecInfoVO.getSpecName().trim(),ydCpSpecInfo.getSpecName().trim())){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "规格名称已经存在!");
                }
            }
        }

        YdCpSpecInfo ydCpSpecInfoData = doMapper.map(cpSpecInfoVO,YdCpSpecInfo.class);
        ydCpSpecInfoData.setSpecType(0);
        ydCpSpecInfoData.setIsDelete(0);
        ydCpSpecInfoData.setSpecid(RandomUtil.getSNCode(TypeEnum.SPEC));
        if(cpSpecInfoVO.getItems()!=null) {
            ydCpSpecInfoData.setFeature(JSON.toJSONString(cpSpecInfoVO.getItems()));
        }

        if(ydCpSpecInfoMapper.insert(ydCpSpecInfoData)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "增加规格失败!");
        }

        return ydCpSpecInfoData.getSpecid();
    }

    @Override
    public boolean updateSpecInfo(String openid, YdCpSpecInfoVO cpSpecInfoVO) {
        if(!(userinfoService.checkIsOwner(openid,cpSpecInfoVO.getShopid())||userinfoService.checkIsManager(openid,cpSpecInfoVO.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(StringUtil.isBlank(cpSpecInfoVO.getSpecName())||StringUtil.isBlank(cpSpecInfoVO.getSpecid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "规格不能为空!");
        }

        YdCpSpecInfo ydCpSpecInfoDB = ydCpSpecInfoMapper.selectBySpecid(cpSpecInfoVO.getSpecid());
        if(ydCpSpecInfoDB==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "规格信息不存在，无法更新!");
        }

        List<YdCpSpecInfo> ydCpSpecInfos = ydCpSpecInfoMapper.selectByShopid(cpSpecInfoVO.getShopid());
        if(ydCpSpecInfos!=null){
            for (YdCpSpecInfo ydCpSpecInfo : ydCpSpecInfos){
                if(StringUtil.equals(cpSpecInfoVO.getSpecName().trim(),ydCpSpecInfo.getSpecName().trim())&&
                        !StringUtil.equals(cpSpecInfoVO.getSpecid(),ydCpSpecInfo.getSpecid())){

                        throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "规格名称已经存在!");

                }
            }
        }

        YdCpSpecInfo ydCpSpecInfoData = doMapper.map(cpSpecInfoVO,YdCpSpecInfo.class);
        ydCpSpecInfoData.setId(ydCpSpecInfoDB.getId());
        ydCpSpecInfoData.setSpecType(0);
        ydCpSpecInfoData.setIsDelete(0);
        if(cpSpecInfoVO.getItems()==null) {
            cpSpecInfoVO.setItems(new HashSet<>());
        }
        ydCpSpecInfoData.setFeature(JSON.toJSONString(cpSpecInfoVO.getItems()));

        if(ydCpSpecInfoMapper.updateByPrimaryKey(ydCpSpecInfoData)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "规格更新失败!");
        }


        return true;
    }

    @Override
    public boolean deleteSpecInfo(String openid, String specid) {

        YdCpSpecInfo cpSpecInfo = ydCpSpecInfoMapper.selectBySpecid(specid);
        if(cpSpecInfo==null){
            return true;
        }

        if(!(userinfoService.checkIsOwner(openid,cpSpecInfo.getShopid())||userinfoService.checkIsManager(openid,cpSpecInfo.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        cpSpecInfo.setSpecName(cpSpecInfo.getSpecid());
        cpSpecInfo.setIsDelete(1);
        if(ydCpSpecInfoMapper.updateByPrimaryKey(cpSpecInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "规格删除失败!");
        }

        return true;
    }

    @Override
    public YdCpSpecInfoVO querySpecInfo(String openid, String specid) {
        YdCpSpecInfo cpSpecInfo = ydCpSpecInfoMapper.selectBySpecid(specid);
        if(cpSpecInfo==null){
            return null;
        }

        if(!(userinfoService.checkIsOwner(openid,cpSpecInfo.getShopid())||userinfoService.checkIsManager(openid,cpSpecInfo.getShopid())||
                userinfoService.checkIsWaiter(openid,cpSpecInfo.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdCpSpecInfoVO ydCpSpecInfoVO = doMapper.map(cpSpecInfo,YdCpSpecInfoVO.class);
        if(StringUtil.isNotEmpty(cpSpecInfo.getFeature())){
            ydCpSpecInfoVO.setItems(JSON.parseObject(cpSpecInfo.getFeature(),HashSet.class));
        }


        return ydCpSpecInfoVO;
    }

    @Override
    public List<Map> querySpecNameList(String openid, String shopid) {
        if(!(userinfoService.checkIsOwner(openid,shopid)||userinfoService.checkIsManager(openid,shopid)||
                userinfoService.checkIsWaiter(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        List<YdCpSpecInfo> ydCpSpecInfos = ydCpSpecInfoMapper.selectByShopid(shopid);
        if(ydCpSpecInfos==null){
            return null;
        }
        List<Map> result = new ArrayList<>();
        for (YdCpSpecInfo ydCpSpecInfo : ydCpSpecInfos){
            Map<String,String> mapInfo = new HashMap<>();
            mapInfo.put("specid", ydCpSpecInfo.getSpecid());
            mapInfo.put("name", ydCpSpecInfo.getSpecName());
            result.add(mapInfo);
        }
        return result;
    }

    @Override
    public List<YdCpSpecConfigInfoVO> mergeSpecConfig(String openid, String shopid, String skuid, Set<YdCpSpecConfigInfoVO> cpBaseSpecConfigInfoVOList, Set<YdCpSpecConfigInfoVO> cpAddSpecConfigInfoVOList) {
        if(!(userinfoService.checkIsOwner(openid,shopid)||userinfoService.checkIsManager(openid,shopid)||
                userinfoService.checkIsWaiter(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(cpBaseSpecConfigInfoVOList==null||cpAddSpecConfigInfoVOList==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "合并列表不能为空");
        }

        Map<String,Set<YdCpSpecConfigInfoVO>> mapConfig = this.mergeSpecConfig(cpBaseSpecConfigInfoVOList);


        return this.mergeSpecConfig(mapConfig,cpAddSpecConfigInfoVOList);
    }

    /**
     * --------------------------------以下开始对一个商品进行具体的规格组合及价格设置---------------------------------
     */

    protected Map<String,Set<YdCpSpecConfigInfoVO>> mergeSpecConfig(Set<YdCpSpecConfigInfoVO> sourceConfig){
        if(sourceConfig==null||sourceConfig.isEmpty()){
            return null;
        }

        Map<String,Set<YdCpSpecConfigInfoVO>> mapConfig = new HashMap<>();
        for(YdCpSpecConfigInfoVO targetConfig:sourceConfig){
            if(mapConfig.containsKey(targetConfig.getMainSpecName().trim())){
                Set<YdCpSpecConfigInfoVO> mapValue = (Set)mapConfig.get(targetConfig.getMainSpecName());
                boolean isExist = false;
                for(YdCpSpecConfigInfoVO mergeValue:mapValue) {
                    if(StringUtil.equals(mergeValue.getChildSpecName().trim(),targetConfig.getChildSpecName().trim())){
                        isExist = true;
                        mergeValue.setPrice(targetConfig.getPrice());
                    }
                }
                if(!isExist){
                    mapValue.add(targetConfig);
                }
            }else {
                Set<YdCpSpecConfigInfoVO> mapValue = new HashSet<>();
                mapValue.add(targetConfig);
                mapConfig.put(targetConfig.getMainSpecName().trim(),mapValue);
            }
        }

        return mapConfig;

//        return new ArrayList<Set<YdCpSpecConfigInfoVO>>(mapConfig.values());

    }

    protected List<YdCpSpecConfigInfoVO> mergeSpecConfig(Map<String,Set<YdCpSpecConfigInfoVO>> mapConfig,Set<YdCpSpecConfigInfoVO> sourceConfig){
        if(sourceConfig==null||sourceConfig.isEmpty()){
            return null;
        }
        for(YdCpSpecConfigInfoVO targetConfig:sourceConfig){
            if(mapConfig.containsKey(targetConfig.getMainSpecName().trim())){
                Set<YdCpSpecConfigInfoVO> mapValue = (Set)mapConfig.get(targetConfig.getMainSpecName());
                boolean isExist = false;
                for(YdCpSpecConfigInfoVO mergeValue:mapValue) {
                    if(StringUtil.equals(mergeValue.getChildSpecName().trim(),targetConfig.getChildSpecName().trim())){
                        isExist = true;
                        mergeValue.setPrice(targetConfig.getPrice());
                    }
                }
                if(!isExist){
                    mapValue.add(targetConfig);
                }
            }else {
                Set<YdCpSpecConfigInfoVO> mapValue = new HashSet<>();
                mapValue.add(targetConfig);
                mapConfig.put(targetConfig.getMainSpecName().trim(),mapValue);
            }
        }

        List<YdCpSpecConfigInfoVO> specConfigInfoVOS = new TreeList();

        for(Set<YdCpSpecConfigInfoVO> specConfigInfoVOSet: mapConfig.values()){
            for(YdCpSpecConfigInfoVO ydCpSpecConfigInfoVO : specConfigInfoVOSet){
                specConfigInfoVOS.add(ydCpSpecConfigInfoVO);
            }
        }

        return specConfigInfoVOS;

//        return new ArrayList<Set<YdCpSpecConfigInfoVO>>(mapConfig.values());

    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addSpecConfigInfo(String shopid, Set<YdCpSpecConfigInfoVO> cpSpecConfigInfoVOList) {

        if(cpSpecConfigInfoVOList==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "规格数据不能为空!");
        }

        /**
         * 主规格名称相同的不能有多条，如果有多条，必须合并成一条
         */

        Map<String,Set<YdCpSpecConfigInfoVO>>  mergeConfigData = this.mergeSpecConfig(cpSpecConfigInfoVOList);

        String skuid = null;
        for(String mainSpecName:mergeConfigData.keySet()) {

            mainSpecName = mainSpecName.trim();
            Set<YdCpSpecConfigInfoVO> cpSpecConfigInfoVOS = mergeConfigData.get(mainSpecName);

            YdCpSpecConfigInfoVO ydCpSpecConfigInfoVOFirst = cpSpecConfigInfoVOS.iterator().next();
            if(skuid==null) {
                skuid = ydCpSpecConfigInfoVOFirst.getSkuid();
            }
            if (StringUtil.isEmpty(mainSpecName) || StringUtil.isEmpty(ydCpSpecConfigInfoVOFirst.getSkuid())) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "非法的规格数据!");
            }

            Map<String, Set<YdCpSpecConfigInfoVO>> cellConfig = new HashMap<>();
            Iterator<YdCpSpecConfigInfoVO> iterator = cpSpecConfigInfoVOS.iterator();
            while (iterator.hasNext()) {
                YdCpSpecConfigInfoVO cpSpecConfigInfoVO = iterator.next();
                if (!StringUtil.equals(skuid, cpSpecConfigInfoVO.getSkuid()) ||
                        cpSpecConfigInfoVO.getPrice() == null || StringUtil.isEmpty(cpSpecConfigInfoVO.getMainSpecName())) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "非法的规格数据!");
                }

            }


            YdCpSpecConfigInfo ydCpSpecConfigInfoDB = ydCpSpecConfigInfoMapper.selectBySkuidAndMainSpecName(ydCpSpecConfigInfoVOFirst.getSkuid(), mainSpecName);

            boolean isNew = false;
            if (ydCpSpecConfigInfoDB == null) {
                isNew = true;
                ydCpSpecConfigInfoDB = new YdCpSpecConfigInfo();
            }

            List<YdCpSpecConfigBaseInfoVO> ydCpSpecConfigInfoVODB = null;
            if (StringUtil.isEmpty(ydCpSpecConfigInfoDB.getFeature())) {
                ydCpSpecConfigInfoVODB = new ArrayList<>();
            } else {
                ydCpSpecConfigInfoVODB = JSON.parseArray(ydCpSpecConfigInfoDB.getFeature(), YdCpSpecConfigBaseInfoVO.class);
            }

            for (YdCpSpecConfigInfoVO ydCpSpecConfigInfoVO : cpSpecConfigInfoVOS) {

                if (ydCpSpecConfigInfoVO.getPrice() == null || ydCpSpecConfigInfoVO.getInventory()==null|| StringUtil.isEmpty(ydCpSpecConfigInfoVO.getMainSpecName())) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "规格数据不完整!");
                }

                ydCpSpecConfigInfoVO.setPrice(AmountUtils.bigDecimalBy2(ydCpSpecConfigInfoVO.getPrice()));

                if (!StringUtil.equals(skuid, ydCpSpecConfigInfoVO.getSkuid())) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "数据中有不一致的规格信息!");
                }

                if (AmountUtils.changeY2F(ydCpSpecConfigInfoVO.getPrice()) <= 0) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "规格金额不能小于等于0!");
                }
                if(ydCpSpecConfigInfoVO.getInventory().intValue()<0){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "库存不能小于0!");
                }

                /**
                 * 对比当前数据是不是存在，如果不存在就增加，存在就修改
                 */
                boolean isExist = false;
                if (!ydCpSpecConfigInfoVODB.isEmpty()) {
                    for (YdCpSpecConfigBaseInfoVO cpSpecConfigInfoVONew : ydCpSpecConfigInfoVODB) {

                        if (StringUtil.equals(cpSpecConfigInfoVONew.getMainSpecName(), ydCpSpecConfigInfoVO.getMainSpecName()) &&
                                StringUtil.equals(cpSpecConfigInfoVONew.getChildSpecName(), ydCpSpecConfigInfoVO.getChildSpecName())) {
                            isExist = true;
                            cpSpecConfigInfoVONew.setPrice(ydCpSpecConfigInfoVO.getPrice());
                            cpSpecConfigInfoVONew.setInventory(ydCpSpecConfigInfoVO.getInventory());
                        }

                    }
                }
                if (!isExist) {
                    YdCpSpecConfigBaseInfoVO ydCpSpecConfigInfoVONew = new YdCpSpecConfigBaseInfoVO();
                    ydCpSpecConfigInfoVONew.setMainSpecName(mainSpecName);
                    ydCpSpecConfigInfoVONew.setChildSpecName(ydCpSpecConfigInfoVO.getChildSpecName());
                    ydCpSpecConfigInfoVONew.setPrice(ydCpSpecConfigInfoVO.getPrice());
                    ydCpSpecConfigInfoVONew.setInventory(ydCpSpecConfigInfoVO.getInventory());
                    ydCpSpecConfigInfoVODB.add(ydCpSpecConfigInfoVONew);
                }


            }

            ydCpSpecConfigInfoDB.setIsDelete(0);
            ydCpSpecConfigInfoDB.setShopid(shopid);
            ydCpSpecConfigInfoDB.setSkuid(skuid);
            ydCpSpecConfigInfoDB.setMainSpecName(mainSpecName);
            ydCpSpecConfigInfoDB.setFeature(JSON.toJSONString(ydCpSpecConfigInfoVODB));

            if (isNew) {
                /**
                 * 新增
                 */
                if (ydCpSpecConfigInfoMapper.insert(ydCpSpecConfigInfoDB) <= 0) {
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "商品规格配置增加失败!");
                }
            } else {
                /**
                 * 修改
                 */
                if (ydCpSpecConfigInfoMapper.updateByPrimaryKey(ydCpSpecConfigInfoDB) <= 0) {
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "商品规格配置更新失败!");
                }
            }

        }

        List<YdCpSpecConfigInfo> cpSpecConfigInfosInDB = ydCpSpecConfigInfoMapper.selectBySkuid(skuid);

        Set<String> mainSpaceNameList = mergeConfigData.keySet();
        if(cpSpecConfigInfosInDB!=null){
            for(YdCpSpecConfigInfo configInfo:cpSpecConfigInfosInDB){
                String mainSepceName = configInfo.getMainSpecName().trim();
                if(!mainSpaceNameList.contains(mainSepceName)){
                    configInfo.setIsDelete(1);
                    configInfo.setMainSpecName(RandomUtil.getSNCode(TypeEnum.DELETE));
                    if (ydCpSpecConfigInfoMapper.updateByPrimaryKey(configInfo) <= 0) {
                        throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "商品规格配置更新失败!");
                    }
                }
            }
        }

        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteSpecConfigInfoBySkuId(String openid,String skuid) {
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }
        String shopid = ydPaypointWaresSku.getShopid();
        if(!(userinfoService.checkIsOwner(openid,shopid)||userinfoService.checkIsManager(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        List<YdCpSpecConfigInfo> cpSpecConfigInfosInDB = ydCpSpecConfigInfoMapper.selectBySkuid(skuid);

        if(cpSpecConfigInfosInDB!=null){
            for(YdCpSpecConfigInfo configInfo:cpSpecConfigInfosInDB){
                configInfo.setIsDelete(1);
                configInfo.setMainSpecName(RandomUtil.getSNCode(TypeEnum.DELETE));
                if (ydCpSpecConfigInfoMapper.updateByPrimaryKey(configInfo) <= 0) {
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "商品规格配置更新失败!");
                }

            }
        }

        return true;
    }


    @Override
    public Set<YdCpSpecConfigInfoVO> querySpecConfigInfo(String openid, String skuid) {
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }
        if(!(userinfoService.checkIsOwner(openid,ydPaypointWaresSku.getShopid())||userinfoService.checkIsManager(openid,ydPaypointWaresSku.getShopid())||userinfoService.checkIsWaiter(openid,ydPaypointWaresSku.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        return new HashSet<>(this.querySpecConfigInfo(skuid));
    }

    @Override
    public List<YdCpSpecConfigInfoVO> querySpecConfigInfo(String skuid) {
        List<YdCpSpecConfigInfoVO> voList = new ArrayList<>();
        List<YdCpSpecConfigInfo> ydCpSpecConfigInfos = ydCpSpecConfigInfoMapper.selectBySkuid(skuid);
        if(ydCpSpecConfigInfos==null){
            return voList;
        }

        for(YdCpSpecConfigInfo ydCpSpecConfigInfo:ydCpSpecConfigInfos){
            List<YdCpSpecConfigInfoVO> ydCpSpecConfigInfoVOS = JSON.parseArray(ydCpSpecConfigInfo.getFeature(),YdCpSpecConfigInfoVO.class);
            if(ydCpSpecConfigInfoVOS!=null){
                if(!ydCpSpecConfigInfoVOS.isEmpty()){
                    for(YdCpSpecConfigInfoVO vo:ydCpSpecConfigInfoVOS){
                        /**
                         * 压入集合中，准备返回
                         */
                        vo.setSkuid(skuid);
                        voList.add(vo);
                    }
                }
            }

        }


        return voList;
    }

    @Override
    public Set<String> getMainSpecNameList(String skuid) {
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }
        List<YdCpSpecConfigInfo> ydCpSpecConfigInfos = ydCpSpecConfigInfoMapper.selectBySkuid(skuid);
        Set<String> mainSpecNameList = new HashSet<>();
        if(ydCpSpecConfigInfos==null){
            return mainSpecNameList;
        }

        for(YdCpSpecConfigInfo ydCpSpecConfigInfo : ydCpSpecConfigInfos){
            mainSpecNameList.add(ydCpSpecConfigInfo.getMainSpecName());
        }

        return mainSpecNameList;
    }

    @Override
    public Set<String> getChildSpecNameList(String skuid, String mainSpecName) {
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }
        mainSpecName = mainSpecName.trim();
        Set<String> childSpecNameList = new HashSet<>();
        YdCpSpecConfigInfo ydCpSpecConfigInfo = ydCpSpecConfigInfoMapper.selectBySkuidAndMainSpecName(skuid,mainSpecName);
        if(ydCpSpecConfigInfo==null){
            return childSpecNameList;
        }

        List<YdCpSpecConfigBaseInfoVO> ydCpSpecConfigInfoVOS = JSON.parseArray(ydCpSpecConfigInfo.getFeature(),YdCpSpecConfigBaseInfoVO.class);
        if(ydCpSpecConfigInfoVOS==null){
            return childSpecNameList;
        }

        for(YdCpSpecConfigBaseInfoVO ydCpSpecConfigInfoVO : ydCpSpecConfigInfoVOS){
            if(StringUtil.equals(mainSpecName,ydCpSpecConfigInfoVO.getMainSpecName())){
                childSpecNameList.add(ydCpSpecConfigInfoVO.getChildSpecName());
            }
        }

        return childSpecNameList;
    }

    @Override
    public Map<String,Object> getPriceBySku(String skuid, String mainSpecName, String childSpecName) {
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }
        Map<String,Object> result = new HashMap<>();
        if(StringUtil.isEmpty(mainSpecName)){
            result.put("price",AmountUtils.bigDecimalBy2(ydPaypointWaresSku.getPrice()));
            result.put("disPrice",AmountUtils.bigDecimalBy2(ydPaypointWaresSku.getDisprice()));
            return result;
        }
        YdPaypointWaresSkuExt ydPaypointWaresSkuExt = ydPaypointWaresSkuExtMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSkuExt==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品扩展信息不存在!");
        }
        mainSpecName = mainSpecName.trim();
        if(childSpecName==null){
            childSpecName = "";
        }else {
            childSpecName = childSpecName.trim();
        }

        YdCpSpecConfigInfo ydCpSpecConfigInfo = ydCpSpecConfigInfoMapper.selectBySkuidAndMainSpecName(skuid,mainSpecName);
        if(ydCpSpecConfigInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "此主规格不存在!");
        }

        List<YdCpSpecConfigBaseInfoVO> ydCpSpecConfigInfoVOS = JSON.parseArray(ydCpSpecConfigInfo.getFeature(),YdCpSpecConfigBaseInfoVO.class);
        if(ydCpSpecConfigInfoVOS==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "规格信息发生错误!");
        }

        for(YdCpSpecConfigBaseInfoVO ydCpSpecConfigInfoVO : ydCpSpecConfigInfoVOS){
            if(StringUtil.equals(mainSpecName,ydCpSpecConfigInfoVO.getMainSpecName())&&StringUtil.equals(childSpecName,ydCpSpecConfigInfoVO.getChildSpecName())){
                BigDecimal spacePrice = ydCpSpecConfigInfoVO.getPrice();
                result.put("price",AmountUtils.bigDecimalBy2(spacePrice));
                BigDecimal disPrice = AmountUtils.bigDecimalBy2(AmountUtils.mul(spacePrice,ydPaypointWaresSkuExt.getDiscount()));
                result.put("disPrice",disPrice);
                break;
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getInfoBySku(String skuid, String mainSpecName, String childSpecName) {
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }
        Map<String,Object> result = new HashMap<>();
        if(StringUtil.isEmpty(mainSpecName)){
            result.put(Constant.PRICE,AmountUtils.bigDecimalBy2(ydPaypointWaresSku.getPrice()));
            result.put(Constant.DISPRICE,AmountUtils.bigDecimalBy2(ydPaypointWaresSku.getDisprice()));
            result.put(Constant.INVENTORY,ydPaypointWaresSku.getInventory());
            return result;
        }
        YdPaypointWaresSkuExt ydPaypointWaresSkuExt = ydPaypointWaresSkuExtMapper.selectBySkuId(skuid);
        if(ydPaypointWaresSkuExt==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品扩展信息不存在!");
        }
        mainSpecName = mainSpecName.trim();
        if(childSpecName==null){
            childSpecName = "";
        }else {
            childSpecName = childSpecName.trim();
        }

        YdCpSpecConfigInfo ydCpSpecConfigInfo = ydCpSpecConfigInfoMapper.selectBySkuidAndMainSpecName(skuid,mainSpecName);
        if(ydCpSpecConfigInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "此主规格不存在!");
        }

        List<YdCpSpecConfigBaseInfoVO> ydCpSpecConfigInfoVOS = JSON.parseArray(ydCpSpecConfigInfo.getFeature(),YdCpSpecConfigBaseInfoVO.class);
        if(ydCpSpecConfigInfoVOS==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "规格信息发生错误!");
        }

        for(YdCpSpecConfigBaseInfoVO ydCpSpecConfigInfoVO : ydCpSpecConfigInfoVOS){
            if(StringUtil.equals(mainSpecName,ydCpSpecConfigInfoVO.getMainSpecName())&&StringUtil.equals(childSpecName,ydCpSpecConfigInfoVO.getChildSpecName())){
                BigDecimal spacePrice = ydCpSpecConfigInfoVO.getPrice();
                result.put(Constant.PRICE,AmountUtils.bigDecimalBy2(spacePrice));
                BigDecimal disPrice = AmountUtils.bigDecimalBy2(AmountUtils.mul(spacePrice,ydPaypointWaresSkuExt.getDiscount()));
                result.put(Constant.DISPRICE,disPrice);
                result.put(Constant.INVENTORY,ydCpSpecConfigInfoVO.getInventory());
                break;
            }
        }
        return result;
    }

    @Override
    public BigDecimal getLowerPriceBySpecConfigInfo(String skuid) {
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        return this.getLowerPriceBySpecConfigInfo(ydPaypointWaresSku);
    }

    @Override
    public BigDecimal getLowerPriceBySpecConfigInfo(YdPaypointWaresSku ydPaypointWaresSku) {
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }

        BigDecimal price = null;
        List<YdCpSpecConfigInfo> ydCpSpecConfigInfos = ydCpSpecConfigInfoMapper.selectBySkuid(ydPaypointWaresSku.getSkuid());
        if(ydCpSpecConfigInfos!=null&&ydCpSpecConfigInfos.size()>0){
            for(YdCpSpecConfigInfo ydCpSpecConfigInfo : ydCpSpecConfigInfos){
                List<YdCpSpecConfigBaseInfoVO> ydCpSpecConfigInfoVOS = JSON.parseArray(ydCpSpecConfigInfo.getFeature(),YdCpSpecConfigBaseInfoVO.class);
                if(ydCpSpecConfigInfoVOS!=null&&!ydCpSpecConfigInfoVOS.isEmpty()){
                    for(YdCpSpecConfigBaseInfoVO vo: ydCpSpecConfigInfoVOS){
                        if(vo.getPrice()!=null){
                            if(price==null){
                                price = vo.getPrice();
                            }
                            if(price.compareTo(vo.getPrice())>0){

                                price = vo.getPrice();

                            }
                        }

                    }
                }
            }
        }


        return price;
    }

    @Override
    public Integer getInventoryBySpecConfigInfo(String skuid) {
        YdPaypointWaresSku ydPaypointWaresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
        return this.getInventoryBySpecConfigInfo(ydPaypointWaresSku);
    }

    @Override
    public Integer getInventoryBySpecConfigInfo(YdPaypointWaresSku ydPaypointWaresSku) {
        if(ydPaypointWaresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品不存在!");
        }

        Integer inventory = 0;
        List<YdCpSpecConfigInfo> ydCpSpecConfigInfos = ydCpSpecConfigInfoMapper.selectBySkuid(ydPaypointWaresSku.getSkuid());
        if(ydCpSpecConfigInfos!=null&&ydCpSpecConfigInfos.size()>0){
            for(YdCpSpecConfigInfo ydCpSpecConfigInfo : ydCpSpecConfigInfos){
                List<YdCpSpecConfigBaseInfoVO> ydCpSpecConfigInfoVOS = JSON.parseArray(ydCpSpecConfigInfo.getFeature(),YdCpSpecConfigBaseInfoVO.class);
                if(ydCpSpecConfigInfoVOS!=null&&!ydCpSpecConfigInfoVOS.isEmpty()){
                    for(YdCpSpecConfigBaseInfoVO vo: ydCpSpecConfigInfoVOS){
                        if(vo.getInventory()!=null){
                            inventory = inventory + vo.getInventory();
                        }
                    }
                }
            }
        }


        return inventory;
    }
}
