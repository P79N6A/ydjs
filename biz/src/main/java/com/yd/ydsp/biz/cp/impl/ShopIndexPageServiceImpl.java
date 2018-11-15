package com.yd.ydsp.biz.cp.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.cp.ShopActivityService;
import com.yd.ydsp.biz.cp.ShopIndexPageService;
import com.yd.ydsp.biz.cp.WareService;
import com.yd.ydsp.biz.openshop.ShopPageService;
import com.yd.ydsp.biz.oss.OSSFileService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.client.domian.openshop.components.*;
import com.yd.ydsp.client.domian.paypoint.WareSkuVO;
import com.yd.ydsp.client.domian.paypoint.YdShopActivityInfoVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.dal.entity.YdPaypointShopInfo;
import com.yd.ydsp.dal.mapper.YdPaypointShopInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

public class ShopIndexPageServiceImpl implements ShopIndexPageService {
    public static final Logger logger = LoggerFactory.getLogger(ShopIndexPageServiceImpl.class);

    @Resource
    private RedisManager redisManager;
    @Resource
    private UserinfoService userinfoService;
    @Resource
    private WareService wareService;
    @Resource
    private ShopActivityService shopActivityService;
    @Resource
    private OSSFileService ossFileService;
    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    private ShopPageService shopPageService;


//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public String updateShopIndexPageInfo(ShopIndexPageVO shopIndexPageVO) {
//        /**
//         * 查询店铺中是否已经存在主页配置文件
//         */
//        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(shopIndexPageVO.getShopid());
//        if(shopInfo==null){
//            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),shopIndexPageVO.getShopid()+":店铺不存在！");
//        }
//
//        shopIndexPageVO.setWareSkuInfos(new HashMap<>());
//        shopIndexPageVO.setActivityInfos(new HashMap<>());
//        /**
//         * 自定义商品与活动刷新
//         */
//        this.homeCustomShowItemOption(shopIndexPageVO);
//
//        /**
//         * 设置shopIndexPageVO的新品/推荐品/火热品等数据
//         */
//
//        shopIndexPageVO = this.homeNewSkuOption(shopIndexPageVO);
//        shopIndexPageVO = this.homeCommedOption(shopIndexPageVO);
//        shopIndexPageVO = this.homeWareHotOption(shopIndexPageVO);
//        shopIndexPageVO = this.homeWareMoreOption(shopIndexPageVO);
//
//        String indexPageData = JSON.toJSONString(shopIndexPageVO);
//        String indexPageFileUrl = ossFileService.uploadShopIndexPageData(indexPageData.getBytes(),shopIndexPageVO.getShopid(),shopInfo.getIndexPageId());
//        if(StringUtil.isNotEmpty(indexPageFileUrl)){
//            shopInfo.setIndexPageId(indexPageFileUrl);
//            if(ydPaypointShopInfoMapper.updateByPrimaryKeySelective(shopInfo)<=0){
//                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),shopIndexPageVO.getShopid()+":主页数据更新失败！");
//            }
//        }
//
//        return shopInfo.getIndexPageId();
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public String updateShopIndexPageInfo(String openid, ShopIndexPageVO shopIndexPageVO) {
//        /**
//         * 加分布式事务缓存锁,超时时间为10秒
//         */
//        boolean lockIsTrue = redisManager.lockWithTimeout(shopIndexPageVO.getShopid()+"indexpage",openid,3000,10000);
//        if(!lockIsTrue){
//            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"上一次操作还没有完成，请稍后再试！");
//        }
//        if(!userinfoService.checkIsManager(openid,shopIndexPageVO.getShopid())){
//            if(lockIsTrue){
//                /**
//                 * 释放锁
//                 */
//                redisManager.releaseLock(shopIndexPageVO.getShopid()+"indexpage",openid);
//            }
//
//            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
//        }
//        String result = this.updateShopIndexPageInfo(shopIndexPageVO);
//        if(lockIsTrue){
//            /**
//             * 释放锁
//             */
//            redisManager.releaseLock(shopIndexPageVO.getShopid()+"indexpage",openid);
//        }
//
//        return result;
//    }
//
//    @Override
//    public PageInfoVO getShopIndexPageData2C(String shopid) {
//        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
//        if(shopInfo==null){
//            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),shopid+":店铺不存在！");
//        }
//        String fileName = shopInfo.getIndexPageId();
//        if(StringUtil.isEmpty(fileName)){
//            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),shopid+":店铺页面数据不存在！");
//        }
//        String indexPageData = ossFileService.getShopIndexPageData(fileName);
//        if(StringUtil.isEmpty(indexPageData)){
//            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),shopid+":店铺页面数据读取为空！");
//        }
//        ShopIndexPageVO shopIndexPageVO = JSON.parseObject(indexPageData,ShopIndexPageVO.class);
//        PageInfoVO pageInfoVO = shopPageService.shopIndexPage2(shopIndexPageVO);
//        shopPageService.pageInfo2c(pageInfoVO);
//        return pageInfoVO;
//    }


//    /**
//     * 新品上市数据处理
//     * @param shopIndexPageVO
//     * @return
//     */
//    private ShopIndexPageVO homeNewSkuOption(ShopIndexPageVO shopIndexPageVO){
//        CustomShowItemVO newSku = new CustomShowItemVO();
//        shopIndexPageVO.setHomeNewSku(newSku);
//        if(shopIndexPageVO.getHomeWareHot()!=null){
//            newSku.setOpen(shopIndexPageVO.getHomeNewSku().getOpen());
//        }
//
//        List<DivInfoVO> divInfoList = new ArrayList<>();
//        newSku.setDivInfoList(divInfoList);
//        List<WareSkuVO> wareSkuVOS = wareService.getNewArrival(shopIndexPageVO.getShopid(),8);
//
//        int count = wareSkuVOS.size();
//        if(count<=0){
//            return shopIndexPageVO;
//        }
//
//        Iterator<WareSkuVO> iterator = wareSkuVOS.iterator();
//        if(count>0&&count<5){
//            DivInfoVO divInfoVO = new DivInfoVO();
//            divInfoVO.setSn(1);
//            if(wareSkuVOS.size()==1) {
//                divInfoVO.setTypeset(0);
//            }else if(wareSkuVOS.size()==2) {
//                divInfoVO.setTypeset(1);
//            }else if(wareSkuVOS.size()==3) {
//                divInfoVO.setTypeset(3);
//            }else if(wareSkuVOS.size()==4) {
//                divInfoVO.setTypeset(5);
//            }
//
//            Integer index = 0;
//            while (iterator.hasNext()){
//                WareSkuVO wareSkuVO = iterator.next();
//                index = index + 1;
//                List<ResourceInfoVO> resourceInfoVOS = new ArrayList<>();
//                ResourceInfoVO resourceInfoVO = new ResourceInfoVO();
//                resourceInfoVO.setSn(index);
//                resourceInfoVO.setResourceType(0);
//                resourceInfoVO.setResourceUrl(wareSkuVO.getWareimg());
//                resourceInfoVO.setLinkType(2);
//                resourceInfoVO.setLinkValue(wareSkuVO.getSkuid());
//                resourceInfoVOS.add(resourceInfoVO);
//                divInfoVO.setResourceInfoVOS(resourceInfoVOS);
//                if(!shopIndexPageVO.getWareSkuInfos().containsKey(wareSkuVO.getSkuid())){
//                    shopIndexPageVO.getWareSkuInfos().put(wareSkuVO.getSkuid(),wareSkuVO);
//                }
//                iterator.remove();
//            }
//            newSku.getDivInfoList().add(divInfoVO);
//        }
//        if(count>4){
//            DivInfoVO divInfoVO = new DivInfoVO();
//            divInfoVO.setSn(2);
//            if(wareSkuVOS.size()==5) {
//                divInfoVO.setTypeset(0);
//            }else if(wareSkuVOS.size()==6) {
//                divInfoVO.setTypeset(1);
//            }else if(wareSkuVOS.size()==7) {
//                divInfoVO.setTypeset(3);
//            }else if(wareSkuVOS.size()==8) {
//                divInfoVO.setTypeset(5);
//            }
//
//            Integer index = 0;
//            while (iterator.hasNext()){
//                WareSkuVO wareSkuVO = iterator.next();
//                index = index + 1;
//                List<ResourceInfoVO> resourceInfoVOS = new ArrayList<>();
//                ResourceInfoVO resourceInfoVO = new ResourceInfoVO();
//                resourceInfoVO.setSn(index);
//                resourceInfoVO.setResourceType(0);
//                resourceInfoVO.setResourceUrl(wareSkuVO.getWareimg());
//                resourceInfoVO.setLinkType(2);
//                resourceInfoVO.setLinkValue(wareSkuVO.getSkuid());
//                resourceInfoVOS.add(resourceInfoVO);
//                divInfoVO.setResourceInfoVOS(resourceInfoVOS);
//                if(!shopIndexPageVO.getWareSkuInfos().containsKey(wareSkuVO.getSkuid())){
//                    shopIndexPageVO.getWareSkuInfos().put(wareSkuVO.getSkuid(),wareSkuVO);
//                }
//                iterator.remove();
//            }
//            newSku.getDivInfoList().add(divInfoVO);
//        }
//        return shopIndexPageVO;
//    }
//
//    /**
//     * 推荐区块
//     * @param shopIndexPageVO
//     * @return
//     */
//    private ShopIndexPageVO homeCommedOption(ShopIndexPageVO shopIndexPageVO){
//        CustomShowItemVO commedData = new CustomShowItemVO();
//        shopIndexPageVO.setHomeWareCommed(commedData);
//        if(shopIndexPageVO.getHomeWareHot()!=null){
//            commedData.setOpen(shopIndexPageVO.getHomeWareCommed().getOpen());
//        }
//
//        List<DivInfoVO> divInfoList = new ArrayList<>();
//        commedData.setDivInfoList(divInfoList);
//        List<WareSkuVO> wareSkuVOS = wareService.getCommendWare(shopIndexPageVO.getShopid(),8);
//
//        int count = wareSkuVOS.size();
//        if(count<=0){
//            return shopIndexPageVO;
//        }
//
//        Iterator<WareSkuVO> iterator = wareSkuVOS.iterator();
//        if(count>0&&count<5){
//            DivInfoVO divInfoVO = new DivInfoVO();
//            divInfoVO.setSn(1);
//            if(wareSkuVOS.size()==1) {
//                divInfoVO.setTypeset(0);
//            }else if(wareSkuVOS.size()==2) {
//                divInfoVO.setTypeset(1);
//            }else if(wareSkuVOS.size()==3) {
//                divInfoVO.setTypeset(3);
//            }else if(wareSkuVOS.size()==4) {
//                divInfoVO.setTypeset(5);
//            }
//
//            Integer index = 0;
//            while (iterator.hasNext()){
//                WareSkuVO wareSkuVO = iterator.next();
//                index = index + 1;
//                List<ResourceInfoVO> resourceInfoVOS = new ArrayList<>();
//                ResourceInfoVO resourceInfoVO = new ResourceInfoVO();
//                resourceInfoVO.setSn(index);
//                resourceInfoVO.setResourceType(0);
//                resourceInfoVO.setResourceUrl(wareSkuVO.getWareimg());
//                resourceInfoVO.setLinkType(2);
//                resourceInfoVO.setLinkValue(wareSkuVO.getSkuid());
//                resourceInfoVOS.add(resourceInfoVO);
//                divInfoVO.setResourceInfoVOS(resourceInfoVOS);
//                if(!shopIndexPageVO.getWareSkuInfos().containsKey(wareSkuVO.getSkuid())){
//                    shopIndexPageVO.getWareSkuInfos().put(wareSkuVO.getSkuid(),wareSkuVO);
//                }
//                iterator.remove();
//            }
//            commedData.getDivInfoList().add(divInfoVO);
//        }
//        if(count>4){
//            DivInfoVO divInfoVO = new DivInfoVO();
//            divInfoVO.setSn(2);
//            if(wareSkuVOS.size()==5) {
//                divInfoVO.setTypeset(0);
//            }else if(wareSkuVOS.size()==6) {
//                divInfoVO.setTypeset(1);
//            }else if(wareSkuVOS.size()==7) {
//                divInfoVO.setTypeset(3);
//            }else if(wareSkuVOS.size()==8) {
//                divInfoVO.setTypeset(5);
//            }
//
//            Integer index = 0;
//            while (iterator.hasNext()){
//                WareSkuVO wareSkuVO = iterator.next();
//                index = index + 1;
//                List<ResourceInfoVO> resourceInfoVOS = new ArrayList<>();
//                ResourceInfoVO resourceInfoVO = new ResourceInfoVO();
//                resourceInfoVO.setSn(index);
//                resourceInfoVO.setResourceType(0);
//                resourceInfoVO.setResourceUrl(wareSkuVO.getWareimg());
//                resourceInfoVO.setLinkType(2);
//                resourceInfoVO.setLinkValue(wareSkuVO.getSkuid());
//                resourceInfoVOS.add(resourceInfoVO);
//                divInfoVO.setResourceInfoVOS(resourceInfoVOS);
//                if(!shopIndexPageVO.getWareSkuInfos().containsKey(wareSkuVO.getSkuid())){
//                    shopIndexPageVO.getWareSkuInfos().put(wareSkuVO.getSkuid(),wareSkuVO);
//                }
//                iterator.remove();
//            }
//            commedData.getDivInfoList().add(divInfoVO);
//        }
//        return shopIndexPageVO;
//    }
//
//    /**
//     * 最热区块
//     * @param shopIndexPageVO
//     * @return
//     */
//    private ShopIndexPageVO homeWareHotOption(ShopIndexPageVO shopIndexPageVO){
//        CustomShowItemVO homeWareHot = new CustomShowItemVO();
//        shopIndexPageVO.setHomeWareHot(homeWareHot);
//        if(shopIndexPageVO.getHomeWareHot()!=null){
//            homeWareHot.setOpen(shopIndexPageVO.getHomeWareHot().getOpen());
//        }
//
//        List<DivInfoVO> divInfoList = new ArrayList<>();
//        homeWareHot.setDivInfoList(divInfoList);
//        List<WareSkuVO> wareSkuVOS = wareService.getHostWare(shopIndexPageVO.getShopid(),8);
//
//        int count = wareSkuVOS.size();
//        if(count<=0){
//            return shopIndexPageVO;
//        }
//
//        Iterator<WareSkuVO> iterator = wareSkuVOS.iterator();
//        if(count>0&&count<5){
//            DivInfoVO divInfoVO = new DivInfoVO();
//            divInfoVO.setSn(1);
//            if(wareSkuVOS.size()==1) {
//                divInfoVO.setTypeset(0);
//            }else if(wareSkuVOS.size()==2) {
//                divInfoVO.setTypeset(1);
//            }else if(wareSkuVOS.size()==3) {
//                divInfoVO.setTypeset(3);
//            }else if(wareSkuVOS.size()==4) {
//                divInfoVO.setTypeset(5);
//            }
//
//            Integer index = 0;
//            while (iterator.hasNext()){
//                WareSkuVO wareSkuVO = iterator.next();
//                index = index + 1;
//                List<ResourceInfoVO> resourceInfoVOS = new ArrayList<>();
//                ResourceInfoVO resourceInfoVO = new ResourceInfoVO();
//                resourceInfoVO.setSn(index);
//                resourceInfoVO.setResourceType(0);
//                resourceInfoVO.setResourceUrl(wareSkuVO.getWareimg());
//                resourceInfoVO.setLinkType(2);
//                resourceInfoVO.setLinkValue(wareSkuVO.getSkuid());
//                resourceInfoVOS.add(resourceInfoVO);
//                divInfoVO.setResourceInfoVOS(resourceInfoVOS);
//                if(!shopIndexPageVO.getWareSkuInfos().containsKey(wareSkuVO.getSkuid())){
//                    shopIndexPageVO.getWareSkuInfos().put(wareSkuVO.getSkuid(),wareSkuVO);
//                }
//                iterator.remove();
//            }
//            homeWareHot.getDivInfoList().add(divInfoVO);
//        }
//        if(count>4){
//            DivInfoVO divInfoVO = new DivInfoVO();
//            divInfoVO.setSn(2);
//            if(wareSkuVOS.size()==5) {
//                divInfoVO.setTypeset(0);
//            }else if(wareSkuVOS.size()==6) {
//                divInfoVO.setTypeset(1);
//            }else if(wareSkuVOS.size()==7) {
//                divInfoVO.setTypeset(3);
//            }else if(wareSkuVOS.size()==8) {
//                divInfoVO.setTypeset(5);
//            }
//
//            Integer index = 0;
//            while (iterator.hasNext()){
//                WareSkuVO wareSkuVO = iterator.next();
//                index = index + 1;
//                List<ResourceInfoVO> resourceInfoVOS = new ArrayList<>();
//                ResourceInfoVO resourceInfoVO = new ResourceInfoVO();
//                resourceInfoVO.setSn(index);
//                resourceInfoVO.setResourceType(0);
//                resourceInfoVO.setResourceUrl(wareSkuVO.getWareimg());
//                resourceInfoVO.setLinkType(2);
//                resourceInfoVO.setLinkValue(wareSkuVO.getSkuid());
//                resourceInfoVOS.add(resourceInfoVO);
//                divInfoVO.setResourceInfoVOS(resourceInfoVOS);
//                if(!shopIndexPageVO.getWareSkuInfos().containsKey(wareSkuVO.getSkuid())){
//                    shopIndexPageVO.getWareSkuInfos().put(wareSkuVO.getSkuid(),wareSkuVO);
//                }
//                iterator.remove();
//            }
//            homeWareHot.getDivInfoList().add(divInfoVO);
//        }
//        return shopIndexPageVO;
//    }
//
//
//    /**
//     * 最热区块
//     * @param shopIndexPageVO
//     * @return
//     */
//    private ShopIndexPageVO homeWareMoreOption(ShopIndexPageVO shopIndexPageVO){
//        CustomShowItemVO homeWareMore = new CustomShowItemVO();
//        shopIndexPageVO.setHomeWareMore(homeWareMore);
//        if(shopIndexPageVO.getHomeWareHot()!=null){
//            homeWareMore.setOpen(shopIndexPageVO.getHomeWareHot().getOpen());
//        }
//
//        List<DivInfoVO> divInfoList = new ArrayList<>();
//        homeWareMore.setDivInfoList(divInfoList);
//        List<WareSkuVO> wareSkuVOS = wareService.getMoreWare(shopIndexPageVO.getShopid(),8);
//
//        int count = wareSkuVOS.size();
//        if(count<=0){
//            return shopIndexPageVO;
//        }
//
//        Iterator<WareSkuVO> iterator = wareSkuVOS.iterator();
//        if(count>0&&count<5){
//            DivInfoVO divInfoVO = new DivInfoVO();
//            divInfoVO.setSn(1);
//            if(wareSkuVOS.size()==1) {
//                divInfoVO.setTypeset(0);
//            }else if(wareSkuVOS.size()==2) {
//                divInfoVO.setTypeset(1);
//            }else if(wareSkuVOS.size()==3) {
//                divInfoVO.setTypeset(3);
//            }else if(wareSkuVOS.size()==4) {
//                divInfoVO.setTypeset(5);
//            }
//
//            Integer index = 0;
//            while (iterator.hasNext()){
//                WareSkuVO wareSkuVO = iterator.next();
//                index = index + 1;
//                List<ResourceInfoVO> resourceInfoVOS = new ArrayList<>();
//                ResourceInfoVO resourceInfoVO = new ResourceInfoVO();
//                resourceInfoVO.setSn(index);
//                resourceInfoVO.setResourceType(0);
//                resourceInfoVO.setResourceUrl(wareSkuVO.getWareimg());
//                resourceInfoVO.setLinkType(2);
//                resourceInfoVO.setLinkValue(wareSkuVO.getSkuid());
//                resourceInfoVOS.add(resourceInfoVO);
//                divInfoVO.setResourceInfoVOS(resourceInfoVOS);
//                if(!shopIndexPageVO.getWareSkuInfos().containsKey(wareSkuVO.getSkuid())){
//                    shopIndexPageVO.getWareSkuInfos().put(wareSkuVO.getSkuid(),wareSkuVO);
//                }
//                iterator.remove();
//            }
//            homeWareMore.getDivInfoList().add(divInfoVO);
//        }
//        if(count>4){
//            DivInfoVO divInfoVO = new DivInfoVO();
//            divInfoVO.setSn(2);
//            if(wareSkuVOS.size()==5) {
//                divInfoVO.setTypeset(0);
//            }else if(wareSkuVOS.size()==6) {
//                divInfoVO.setTypeset(1);
//            }else if(wareSkuVOS.size()==7) {
//                divInfoVO.setTypeset(3);
//            }else if(wareSkuVOS.size()==8) {
//                divInfoVO.setTypeset(5);
//            }
//
//            Integer index = 0;
//            while (iterator.hasNext()){
//                WareSkuVO wareSkuVO = iterator.next();
//                index = index + 1;
//                List<ResourceInfoVO> resourceInfoVOS = new ArrayList<>();
//                ResourceInfoVO resourceInfoVO = new ResourceInfoVO();
//                resourceInfoVO.setSn(index);
//                resourceInfoVO.setResourceType(0);
//                resourceInfoVO.setResourceUrl(wareSkuVO.getWareimg());
//                resourceInfoVO.setLinkType(2);
//                resourceInfoVO.setLinkValue(wareSkuVO.getSkuid());
//                resourceInfoVOS.add(resourceInfoVO);
//                divInfoVO.setResourceInfoVOS(resourceInfoVOS);
//                if(!shopIndexPageVO.getWareSkuInfos().containsKey(wareSkuVO.getSkuid())){
//                    shopIndexPageVO.getWareSkuInfos().put(wareSkuVO.getSkuid(),wareSkuVO);
//                }
//                iterator.remove();
//            }
//            homeWareMore.getDivInfoList().add(divInfoVO);
//        }
//        return shopIndexPageVO;
//    }
//
//    private ShopIndexPageVO homeCustomShowItemOption(ShopIndexPageVO shopIndexPageVO){
//        List<CustomShowItemVO> customShowItemVOS = shopIndexPageVO.getHomeCustomShowItemList();
//        if(customShowItemVOS==null||customShowItemVOS.size()<=0){
//            return shopIndexPageVO;
//        }
//
//        for(CustomShowItemVO customShowItemVO: customShowItemVOS){
//            List<DivInfoVO> divInfoList = customShowItemVO.getDivInfoList();
//            if(divInfoList==null||divInfoList.size()<=0){
//                continue;
//            }
//            for (DivInfoVO divInfoVO : divInfoList){
//                List<ResourceInfoVO> resourceInfoVOS = divInfoVO.getResourceInfoVOS();
//                if(resourceInfoVOS==null||resourceInfoVOS.size()<=0){
//                    continue;
//                }
//                for(ResourceInfoVO resourceInfoVO : resourceInfoVOS){
//                    if(resourceInfoVO.getLinkType().intValue()==2){
//                        if(!shopIndexPageVO.getWareSkuInfos().containsKey(resourceInfoVO.getLinkValue())){
//                            WareSkuVO wareSkuVO = wareService.getWareSku(resourceInfoVO.getLinkValue());
//                            if(wareSkuVO!=null) {
//                                shopIndexPageVO.getWareSkuInfos().put(wareSkuVO.getSkuid(), wareSkuVO);
//                            }
//                        }
//                        continue;
//                    }
//                    if(resourceInfoVO.getLinkType().intValue()==3){
//                        if(!shopIndexPageVO.getActivityInfos().containsKey(resourceInfoVO.getLinkValue())){
//                            YdShopActivityInfoVO activity = shopActivityService.queryShopActivity(resourceInfoVO.getLinkValue());
//                            if(activity!=null) {
//                                shopIndexPageVO.getActivityInfos().put(activity.getActivityid(), activity);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return shopIndexPageVO;
//    }

}
