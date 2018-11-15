package com.yd.ydsp.biz.cp.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.cp.ShopActivityService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.client.domian.paypoint.PicVO;
import com.yd.ydsp.client.domian.paypoint.YdShopActivityInfoVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.YdShopActivityInfo;
import com.yd.ydsp.dal.mapper.YdShopActivityInfoMapper;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShopActivityServiceImpl implements ShopActivityService {
    public static final Logger logger = LoggerFactory.getLogger(ShopActivityServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private UserinfoService userinfoService;
    @Resource
    private YdShopActivityInfoMapper ydShopActivityInfoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateShopActivity(String openid, YdShopActivityInfoVO shopActivityInfoVO) {
        if(!userinfoService.checkIsManager(openid,shopActivityInfoVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(shopActivityInfoVO.getBeginDate()==null||shopActivityInfoVO.getEndDate()==null||
                shopActivityInfoVO.getResouceType()==null||shopActivityInfoVO.getResouceUrl()==null||
                StringUtil.isEmpty(shopActivityInfoVO.getActivityName())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"信息不完整，请检查！");
        }

        if(shopActivityInfoVO.getPics()==null){
            shopActivityInfoVO.setPics(new ArrayList<>());
        }

        boolean isNew = true;
        YdShopActivityInfo shopActivityInfo = doMapper.map(shopActivityInfoVO,YdShopActivityInfo.class);
        if(StringUtil.isNotEmpty(shopActivityInfoVO.getActivityid())){
            YdShopActivityInfo shopActivityInfoDB = ydShopActivityInfoMapper.selectByActivityidRowLock(shopActivityInfoVO.getActivityid());
            if(shopActivityInfoDB==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"活动没找到！");
            }
            if(!StringUtil.equals(shopActivityInfoDB.getShopid(),shopActivityInfoVO.getShopid())){
                throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
            }
            if(shopActivityInfoDB.getStatus().intValue()!=2){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"只能修改[准备]状态的活动!");
            }
            isNew = false;
            shopActivityInfo.setId(shopActivityInfoDB.getId());
            shopActivityInfo.setYearInfo(shopActivityInfoDB.getYearInfo());
            shopActivityInfo.setMonthInfo(shopActivityInfoDB.getMonthInfo());
            shopActivityInfo.setStatus(shopActivityInfoVO.getStatus());
        }else {

            shopActivityInfo.setActivityid(RandomUtil.getSNCode(TypeEnum.PAGE));
            String year_month = DateUtils.date2StrWithYearAndMonth(new Date());
            String[] monthYear = year_month.split("-");
            shopActivityInfoVO.setYearInfo(monthYear[0]);
            shopActivityInfoVO.setMonthInfo(monthYear[1]);
            shopActivityInfo.setYearInfo(shopActivityInfoVO.getYearInfo());
            shopActivityInfo.setMonthInfo(shopActivityInfoVO.getMonthInfo());
            shopActivityInfo.setStatus(shopActivityInfoVO.getStatus());
        }

        shopActivityInfo.setPicList(JSON.toJSONString(shopActivityInfoVO.getPics()));
        shopActivityInfo.setShopid(shopActivityInfoVO.getShopid());
        shopActivityInfo.setActivityName(shopActivityInfoVO.getActivityName());
        if(isNew){
            if(ydShopActivityInfoMapper.insert(shopActivityInfo)<=0){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"新增活动失败!");
            }
        }else {
            if(ydShopActivityInfoMapper.updateByPrimaryKeySelective(shopActivityInfo)<=0){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"保存活动失败!");
            }
        }

        return shopActivityInfo.getActivityid();
    }

    @Override
    public List<YdShopActivityInfoVO> queryShopActivity(String openid, String shopid, String year, String month, Integer status, Integer pageIndex, Integer count) {
        if(!(userinfoService.checkIsOwner(openid,shopid)||userinfoService.checkIsManager(openid,shopid)||userinfoService.checkIsWaiter(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        Integer indexPoint = (pageIndex-1)*count;
        if(indexPoint<=0){
            indexPoint = 0;
        }
        List<YdShopActivityInfo> resultDb = null;
        List<YdShopActivityInfoVO> result = new ArrayList<>();
        if(status!=null){
            if(status.intValue()<0){
                status = null;
            }
        }
        if(StringUtil.isEmpty(month)) {
            if (status == null) {
                resultDb = ydShopActivityInfoMapper.selectByShopidInYear(shopid, year, indexPoint, count);
            } else {
                resultDb = ydShopActivityInfoMapper.selectByShopidInYearStatus(shopid, year, status, indexPoint, count);
            }
            if (resultDb != null) {
                for (YdShopActivityInfo shopActivityInfo : resultDb) {
                    result.add(this.doMap(shopActivityInfo));
                }
            }
        }else {
            if (status == null) {
                resultDb = ydShopActivityInfoMapper.selectByShopidInMoth(shopid, year,month, pageIndex, count);
            } else {
                resultDb = ydShopActivityInfoMapper.selectByShopidInMonthStatus(shopid, year,month, status, pageIndex, count);
            }
            if (resultDb != null) {
                for (YdShopActivityInfo shopActivityInfo : resultDb) {
                    result.add(this.doMap(shopActivityInfo));
                }
            }
        }

        return result;
    }

    @Override
    public YdShopActivityInfoVO queryShopActivity(String activityid) {
        YdShopActivityInfo shopActivityInfo = ydShopActivityInfoMapper.selectByActivityid(activityid);
        YdShopActivityInfoVO shopActivityInfoVO = null;
        if(shopActivityInfo!=null){
            shopActivityInfoVO = this.doMap(shopActivityInfo);
        }
        return shopActivityInfoVO;
    }

    @Override
    public boolean closeShopActivity(String openid,String activityid) {
        YdShopActivityInfo shopActivityInfo = ydShopActivityInfoMapper.selectByActivityid(activityid);
        if(shopActivityInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"活动不存在!");
        }
        if(!userinfoService.checkIsManager(openid,shopActivityInfo.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        shopActivityInfo.setStatus(1);
        if(ydShopActivityInfoMapper.updateByPrimaryKeySelective(shopActivityInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"活动关闭失败!");
        }
        return true;
    }

    protected YdShopActivityInfoVO doMap(YdShopActivityInfo ydShopActivityInfo){
        YdShopActivityInfoVO ydShopActivityInfoVO = doMapper.map(ydShopActivityInfo,YdShopActivityInfoVO.class);
        ydShopActivityInfoVO.setPics(JSON.parseArray(ydShopActivityInfo.getPicList(), PicVO.class));
        return ydShopActivityInfoVO;
    }
}
