package com.yd.ydsp.biz.openshop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yd.ydsp.biz.cp.WareService;
import com.yd.ydsp.biz.openshop.ShopPageService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.client.domian.openshop.components.*;
import com.yd.ydsp.client.domian.paypoint.WareSkuBaseVO;
import com.yd.ydsp.client.domian.paypoint.WareSkuVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.YdPaypointShopInfo;
import com.yd.ydsp.dal.entity.YdShopPageInfo;
import com.yd.ydsp.dal.mapper.YdPaypointShopInfoMapper;
import com.yd.ydsp.dal.mapper.YdShopPageInfoMapper;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 * @author zengyixun
 * @date 18/9/13
 */
public class ShopPageServiceImpl implements ShopPageService {
    public static final Logger logger = LoggerFactory.getLogger(ShopPageServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private UserinfoService userinfoService;
    @Resource
    private YdShopPageInfoMapper ydShopPageInfoMapper;
    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    private WareService wareService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updatePageInfo(String openid, PageInfoVO pageInfoVO) {

        if(StringUtil.isEmpty(pageInfoVO.getPageName())||pageInfoVO.getPageType()==null||
                pageInfoVO.getComponentList()==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"信息不完整，请检查！");
        }
        if(!(userinfoService.checkIsManager(openid,pageInfoVO.getShopid())||
                userinfoService.checkIsOwner(openid,pageInfoVO.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        pageInfoVO.setStatus(1);
        for(ComponentVO componentVO: pageInfoVO.getComponentList()){
            if(componentVO.getComponentType().intValue()==0){
                if(componentVO.getComponent()==null){
                    continue;
                }
                List<ResourceInfoVOExt> voList = JSON.parseArray(JSON.toJSONString(componentVO.getComponent()),ResourceInfoVOExt.class);
                componentVO.setComponent(voList);
                for (ResourceInfoVOExt resourceInfoVOExt : voList){
                    if(resourceInfoVOExt!=null){
                        /**
                         * 加工资源组件
                         */
                        resourceInfoVOExt.setExtData(null);
                    }
                }

            }else if(componentVO.getComponentType().intValue()==1){
                ResourceInfoVOExt resourceInfoVOExt = (ResourceInfoVOExt) componentVO.getComponent();
                if(resourceInfoVOExt!=null){
                    /**
                     * 加工资源组件
                     */
                    resourceInfoVOExt.setExtData(null);
                }
                continue;
            }else if(componentVO.getComponentType().intValue()==5){
                CustomShowItemVO customShowItemVO = JSON.parseObject(JSON.toJSONString(componentVO.getComponent()),CustomShowItemVO.class);
                List<DivInfoVO> divInfoList = customShowItemVO.getDivInfoList();
                customShowItemVO.setDivInfoList(divInfoList);
                componentVO.setComponent(customShowItemVO);
                if(divInfoList==null||divInfoList.size()<=0){
                    continue;
                }
                for (DivInfoVO divInfoVO : divInfoList){
                    List<ResourceInfoVOExt> resourceInfoVOS = divInfoVO.getResourceInfoVOS();
                    if(resourceInfoVOS!=null){

                        for (ResourceInfoVOExt resourceInfoVO: resourceInfoVOS){

                            /**
                             * 加工资源组件
                             */
                            resourceInfoVO.setExtData(null);
                        }

                    }
                }
            }
        }
        boolean isNew = false;
        YdShopPageInfo shopPageInfo = null;
        if(StringUtil.isEmpty(pageInfoVO.getPageId())){
            isNew = true;
            pageInfoVO.setPageId(RandomUtil.getSNCode(TypeEnum.PAGE));
            shopPageInfo = new YdShopPageInfo();
            shopPageInfo.setPageid(pageInfoVO.getPageId());
        }else {
            shopPageInfo = ydShopPageInfoMapper.selectByPageIdRowLock(pageInfoVO.getPageId());
            if(shopPageInfo==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"页面不存在！");
            }
        }
        shopPageInfo.setStatus(pageInfoVO.getStatus());
        shopPageInfo.setPageName(pageInfoVO.getPageName());
        shopPageInfo.setComponents(JSON.toJSONString(pageInfoVO.getComponentList()));
        shopPageInfo.setShopid(pageInfoVO.getShopid());
        PageInfoDTO pageInfoDTO = doMapper.map(pageInfoVO,PageInfoDTO.class);
        pageInfoDTO.addFeature(Constant.BackColor,pageInfoVO.getBackColor());
        shopPageInfo.setFeature(pageInfoDTO.getFeature());
        if(isNew){
            YdShopPageInfo pageInfo = ydShopPageInfoMapper.selectByShopIdAndName(pageInfoVO.getShopid(),pageInfoVO.getPageName());
            if(pageInfo!=null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"页面名称重复!");
            }
            /**
             * 增加
             */
            if(ydShopPageInfoMapper.insert(shopPageInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"新增页面配置失败!");
            }
        }else {
            /**
             * 修改
             */
            YdShopPageInfo pageInfo = ydShopPageInfoMapper.selectByShopIdAndName(pageInfoVO.getShopid(),pageInfoVO.getPageName());
            if(pageInfo!=null){
                if(!StringUtil.equals(pageInfo.getPageid(),shopPageInfo.getPageid())) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "页面名称重复!");
                }
            }
            if(ydShopPageInfoMapper.updateByPrimaryKeySelective(shopPageInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"修改页面配置失败!");
            }
        }

        return pageInfoVO.getPageId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setShopIndexPage(String openid, String shopid, String pageid) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdShopPageInfo pageInfo = ydShopPageInfoMapper.selectByPageId(pageid);
        if(pageInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"页面不存在！");
        }

        if(!StringUtil.equals(pageInfo.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(shopid);
        if(shopInfo == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"店铺不存在！");
        }
        shopInfo.setIndexPageId(pageid);
        if(ydPaypointShopInfoMapper.updateByPrimaryKeySelective(shopInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"设置为首页失败！");
        }
        return true;
    }

    @Override
    public boolean setChannelIndexPage(String openid, String channelid, String pageid) {
        return false;
    }

    @Override
    public PageInfoVO queryPageInfo(String pageid) {
        YdShopPageInfo pageInfo = ydShopPageInfoMapper.selectByPageId(pageid);
        PageInfoVO pageInfoVO = null;
        if(pageInfo!=null){
            pageInfoVO = this.doMap(pageInfo);
        }
        return pageInfoVO;
    }

    @Override
    public PageInfoVO queryPageInfoRelease(String pageid) {
        YdShopPageInfo pageInfo = ydShopPageInfoMapper.selectByPageIdRelease(pageid);
        PageInfoVO pageInfoVO = null;
        if(pageInfo!=null){
            pageInfoVO = this.doMap(pageInfo);
        }
        return pageInfoVO;
    }

    @Override
    public List<PageInfoVO> queryPageInfo(String shopid, Integer pageIndex, Integer count) {
        if(count.intValue()<=0||count.intValue()>20){
            count = 20;
        }
        Integer indexPoint = (pageIndex-1)*count;
        if(indexPoint<=0){
            indexPoint = 0;
        }

        List<PageInfoVO> result = new ArrayList<>();

        List<YdShopPageInfo> shopPageInfos = ydShopPageInfoMapper.selectByShopId(shopid,indexPoint,count);
        if(shopPageInfos!=null){
            for (YdShopPageInfo pageInfo : shopPageInfos){
                result.add(this.doMap(pageInfo));
            }
        }

        return result;
    }

    @Override
    public List<PageInfoVO> queryPageInfo(String shopid, Integer status, Integer pageIndex, Integer count) {
        if(count.intValue()<=0||count.intValue()>20){
            count = 20;
        }
        Integer indexPoint = (pageIndex-1)*count;
        if(indexPoint<=0){
            indexPoint = 0;
        }

        List<PageInfoVO> result = new ArrayList<>();

        List<YdShopPageInfo> shopPageInfos = ydShopPageInfoMapper.selectByShopIdAndStatus(shopid,status,indexPoint,count);
        if(shopPageInfos!=null){
            for (YdShopPageInfo pageInfo : shopPageInfos){
                result.add(this.doMap(pageInfo));
            }
        }

        return result;
    }

//    @Override
//    public PageInfoVO shopIndexPage2(ShopIndexPageVO shopIndexPageVO) {
//        PageInfoVO pageInfoVO = new PageInfoVO();
//        Integer sn = 0;
//        pageInfoVO.setPageId(shopIndexPageVO.getShopid());
//        ComponentVO componentVO = new ComponentVO();
//        componentVO.setComponentType(0);
//        componentVO.setSn(sn += 1);
//        componentVO.setComponent(shopIndexPageVO.getBannerInfoList());
//        List<CustomShowItemVO> customShowItemVOS = shopIndexPageVO.getHomeCustomShowItemList();
//        if(customShowItemVOS!=null){
//            Collections.sort(customShowItemVOS);
//            for (CustomShowItemVO customShowItemVO: customShowItemVOS) {
//                ComponentVO cus = new ComponentVO();
//                cus.setComponentType(5);
//                cus.setSn(sn += 1);
//                cus.setComponent(customShowItemVO);
//            }
//        }
//
//        if(shopIndexPageVO.getHomeNewSku()!=null){
//            if(shopIndexPageVO.getHomeNewSku().getOpen()){
//                ComponentVO cus = new ComponentVO();
//                cus.setComponentType(5);
//                cus.setSn(sn += 1);
//                cus.setComponent(shopIndexPageVO.getHomeNewSku());
//            }
//        }
//
//        if(shopIndexPageVO.getHomeWareCommed()!=null){
//            if(shopIndexPageVO.getHomeWareCommed().getOpen()){
//                ComponentVO cus = new ComponentVO();
//                cus.setComponentType(5);
//                cus.setSn(sn += 1);
//                cus.setComponent(shopIndexPageVO.getHomeWareCommed());
//            }
//        }
//
//        if(shopIndexPageVO.getHomeWareHot()!=null){
//            if(shopIndexPageVO.getHomeWareHot().getOpen()){
//                ComponentVO cus = new ComponentVO();
//                cus.setComponentType(5);
//                cus.setSn(sn += 1);
//                cus.setComponent(shopIndexPageVO.getHomeWareHot());
//            }
//        }
//
//        if(shopIndexPageVO.getHomeWareMore()!=null){
//            if(shopIndexPageVO.getHomeWareMore().getOpen()){
//                ComponentVO cus = new ComponentVO();
//                cus.setComponentType(5);
//                cus.setSn(sn += 1);
//                cus.setComponent(shopIndexPageVO.getHomeWareMore());
//            }
//        }
//
//        return pageInfoVO;
//    }

    @Override
    public PageInfoVO pageInfo2c(PageInfoVO pageInfoVO) {
        if(pageInfoVO==null) {
            return pageInfoVO;
        }
        if(pageInfoVO.getComponentList()==null){
            return pageInfoVO;
        }
        List<ComponentVO> componentList = pageInfoVO.getComponentList();
        Collections.sort(componentList);
        logger.info("pageInfoVO2C:"+JSON.toJSONString(pageInfoVO));
        for(ComponentVO componentVO: componentList){
            logger.info(JSON.toJSONString(componentVO));
            if(componentVO.getComponentType().intValue()==0){
                if(componentVO.getComponent()==null){
                    continue;
                }
                List<ResourceInfoVOExt> voList = JSON.parseArray(JSON.toJSONString(componentVO.getComponent()),ResourceInfoVOExt.class);
                componentVO.setComponent(voList);
                for (ResourceInfoVOExt resourceInfoVOExt : voList){
                    if(resourceInfoVOExt!=null){
                        /**
                         * 加工资源组件
                         */

                            resourceInfoVOExt = this.processResourceInfoVO(resourceInfoVOExt);

                    }
                }

            }else if(componentVO.getComponentType().intValue()==1){
                ResourceInfoVOExt resourceInfoVOExt = (ResourceInfoVOExt) componentVO.getComponent();
                logger.info("resourceInfoVOExt:"+JSON.toJSONString(resourceInfoVOExt));
                if(resourceInfoVOExt!=null){
                    /**
                     * 加工资源组件
                     */
                    if(resourceInfoVOExt!=null) {
                        resourceInfoVOExt = this.processResourceInfoVO(resourceInfoVOExt);
                    }
                }
                continue;
            }else if(componentVO.getComponentType().intValue()==5){
                componentVO.setComponent(JSON.toJavaObject((JSONObject)componentVO.getComponent(),CustomShowItemVO.class));
                List<DivInfoVO> divInfoList = ((CustomShowItemVO)componentVO.getComponent()).getDivInfoList();
                ((CustomShowItemVO) componentVO.getComponent()).setDivInfoList(divInfoList);
                if(divInfoList==null||divInfoList.size()<=0){
                    continue;
                }
                for (DivInfoVO divInfoVO : divInfoList){
                    List<ResourceInfoVOExt> resourceInfoVOS = divInfoVO.getResourceInfoVOS();
                    if(resourceInfoVOS!=null){

                        for (ResourceInfoVOExt resourceInfoVO: resourceInfoVOS){
                            /**
                             * 加工资源组件
                             */
                            if(resourceInfoVOS!=null) {

                                resourceInfoVO = this.processResourceInfoVO(resourceInfoVO);
                            }
                        }

                    }
                }
            }
        }
        return pageInfoVO;
    }

    @Override
    public PageInfoVO getPageInfo2C(String pageid) {
        PageInfoVO pageInfoVO = this.queryPageInfoRelease(pageid);
        if(pageInfoVO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "页面数据不存在!");
        }

        pageInfoVO = this.pageInfo2c(pageInfoVO);

        return pageInfoVO;
    }

    @Override
    public PageInfoVO getPageInfo2BReview(String pageid) {
        PageInfoVO pageInfoVO = this.queryPageInfo(pageid);
        if(pageInfoVO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "页面数据不存在!");
        }

        pageInfoVO = this.pageInfo2c(pageInfoVO);

        return pageInfoVO;
    }

    @Override
    public boolean releasePageInfo(String openid, String pageid) {
        YdShopPageInfo pageInfo = ydShopPageInfoMapper.selectByPageId(pageid);
        if(pageInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "页面数据不存在!");
        }

        if(!(userinfoService.checkIsManager(openid,pageInfo.getShopid())||
                userinfoService.checkIsOwner(openid,pageInfo.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(pageInfo.getStatus().intValue()==0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "没有新的修改，无需发布!");
        }

        pageInfo.setStatus(0);

        if(ydShopPageInfoMapper.updateByPrimaryKeySelectiveRelease(pageInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "页面发布失败!");
        }

        return true;
    }

    @Override
    public String getPageInfoVM(PageInfoVO pageInfoVO) {
        String resultHtml = "";
        List<ComponentVO> componentList = pageInfoVO.getComponentList();
        if(componentList==null){
            return resultHtml;
        }

        for(ComponentVO componentVO : componentList){

        }

        return null;
    }

    private String getResourceHtml(ResourceInfoVO resourceInfoVO){
        String resourceHtml = "";
        if(resourceInfoVO == null){
            return resourceHtml;
        }

        return resourceHtml;
    }

    private ResourceInfoVOExt processResourceInfoVO(ResourceInfoVOExt resourceInfoVO){
        if(resourceInfoVO.getLinkType().intValue()==2) {
            if (StringUtil.isNotEmpty(resourceInfoVO.getLinkValue())) {
                WareSkuBaseVO wareSkuBaseVO = wareService.getWareSkuBase(resourceInfoVO.getLinkValue().trim());
                if (wareSkuBaseVO != null) {
                    resourceInfoVO.setExtData(wareSkuBaseVO);
                }
            }
        }

        return resourceInfoVO;

    }

    private PageInfoVO doMap(YdShopPageInfo pageInfo){
        if(pageInfo==null){
            return null;
        }
        PageInfoVO pageInfoVO = new PageInfoVO();
        PageInfoDTO pageInfoDTO = doMapper.map(pageInfo,PageInfoDTO.class);
        pageInfoVO.setPageId(pageInfoDTO.getPageid());
        pageInfoVO.setShopid(pageInfoDTO.getShopid());
        pageInfoVO.setPageName(pageInfoDTO.getPageName());
        pageInfoVO.setPageType(pageInfoDTO.getPageType());
        pageInfoVO.setStatus(pageInfoDTO.getStatus());
        pageInfoVO.setBackColor(pageInfoDTO.getFeature(Constant.BackColor));
        pageInfoVO.setComponentList(pageInfoDTO.getComponentList());
        pageInfoVO.setModifyDate(pageInfoDTO.getModifyDate());

        if(StringUtil.isNotEmpty(pageInfo.getComponents())) {
            pageInfoVO.setComponentList(JSON.parseArray(pageInfo.getComponents(),ComponentVO.class));
        }
        return pageInfoVO;
    }

}
