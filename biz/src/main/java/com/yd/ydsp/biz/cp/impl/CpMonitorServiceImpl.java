package com.yd.ydsp.biz.cp.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.cp.CpMonitorService;
import com.yd.ydsp.biz.cp.model.CPMonitorVO;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.utils.OSSUtils;
import com.yd.ydsp.biz.ys7.Ys7Service;
import com.yd.ydsp.client.domian.monitor.CPMonitorDTO;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.FeatureUtil;
import com.yd.ydsp.dal.entity.YdMonitorInfo;
import com.yd.ydsp.dal.mapper.YdMonitorInfoMapper;
import org.dozer.DozerBeanMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/17
 */
public class CpMonitorServiceImpl implements CpMonitorService {

    @Resource
    private DozerBeanMapper doMapper;

    @Resource
    private YdMonitorInfoMapper ydMonitorInfoMapper;

    @Resource
    private Ys7Service ys7Service;

    @Resource
    private UserinfoService userinfoService;

    @Override
    public CPMonitorVO getShopMonitorInfo(String shopid) {
//        List<CPMonitorVO> config = DiamondYDMonitorConfig.config;
//
//        if(config==null){
//            return null;
//        }
//
//        for (CPMonitorVO cpMonitorVO : config){
//            if(shopid.equals(cpMonitorVO.getShopid())){
//                return cpMonitorVO;
//            }
//        }

        YdMonitorInfo monitorInfo = ydMonitorInfoMapper.selectByShopid(shopid);
        if(monitorInfo==null){
            return null;
        }

        CPMonitorDTO monitorDTO = doMapper.map(monitorInfo,CPMonitorDTO.class);
        CPMonitorVO monitorVO = new CPMonitorVO();
        monitorVO.setShopid(shopid);
        monitorVO.setName(monitorDTO.getName());
        if(StringUtil.isEmpty(monitorDTO.getTel())){
            monitorVO.setTel("xxxxxxxx");
        }else {
            monitorVO.setTel(monitorDTO.getTel());
        }
        Map<String,List<Integer>> deviceSerials = JSON.parseObject(monitorDTO.getDeviceSerials(),Map.class);
        monitorVO.setDeviceSerials(deviceSerials);
        Map<String ,String> deviceSerialNames = JSON.parseObject(monitorDTO.getDeviceSerialnames(),Map.class);
        if(deviceSerialNames!=null){
            monitorVO.setDeviceSerialNames(deviceSerialNames);
        }
        if(StringUtil.isEmpty(monitorDTO.getShopDesc())){
            List<String> shopDesc = new ArrayList<>();
            shopDesc.add("正在建设中......");
            monitorVO.setShopDesc(shopDesc);
        }else{
            List<String> shopDesc = java.util.Arrays.asList(monitorDTO.getShopDesc().split("\\^+"));
            monitorVO.setShopDesc(shopDesc);
        }
        String shopImage1 = monitorDTO.getFeature(Constant.BUSINESS_LICENCE_IMAGE);
        if(StringUtil.isNotEmpty(shopImage1)){
            monitorVO.setShopImage1(monitorDTO.getOssPath()+shopImage1);
        }
        String shopImage2 = monitorDTO.getFeature("shopImage2");
        if(StringUtil.isNotEmpty(shopImage2)){
            monitorVO.setShopImage2(monitorDTO.getOssPath()+shopImage2);
        }
        List<String> shopImag3 = monitorDTO.getListFeature(Constant.HEALTH_IMAGE);
        if(shopImag3==null){
            shopImag3 = new ArrayList<>();
        }else {
            for(int i=0; i<shopImag3.size(); i++){
                shopImag3.set(i,monitorDTO.getOssPath()+shopImag3.get(i).trim());
            }
        }
        monitorVO.setShopImag3(shopImag3);
        List<String> shopImagDaily = monitorDTO.getListFeature(Constant.SHOP_DAILY_IMAGE);
        if(shopImagDaily==null){
            shopImagDaily = new ArrayList<>();
        }else {
            for(int i=0; i<shopImagDaily.size(); i++){
                shopImagDaily.set(i,monitorDTO.getOssPath()+shopImagDaily.get(i).trim());
            }
        }
        monitorVO.setShopImagDaily(shopImagDaily);
        monitorVO.setOssPath(monitorDTO.getOssPath());

        return monitorVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean setShopMonitorInfo(CPMonitorVO monitorVO) {
        if(monitorVO==null){
            return false;
        }
        CPMonitorDTO monitorDTO = null;
        YdMonitorInfo ydMonitorInfo = ydMonitorInfoMapper.selectByShopidLowRow(monitorVO.getShopid());
        if(ydMonitorInfo!=null){
            monitorDTO = doMapper.map(ydMonitorInfo,CPMonitorDTO.class);
        }else{
            monitorDTO = new CPMonitorDTO();
            monitorDTO.setShopid(monitorVO.getShopid());
        }
        monitorDTO.setName(monitorVO.getName());
        monitorDTO.setOssPath(monitorVO.getOssPath());
        monitorDTO.setTel(monitorVO.getTel());
        if(monitorVO.getShopDesc()!=null) {
            monitorDTO.setShopDesc(org.apache.commons.lang.StringUtils.join(monitorVO.getShopDesc().toArray(), "^^^"));
        }
        if(monitorVO.getDeviceSerialNames()!=null){
            monitorDTO.setDeviceSerialnames(JSON.toJSONString(monitorVO.getDeviceSerialNames()));
        }
        monitorDTO.setDeviceSerials(JSON.toJSONString(monitorVO.getDeviceSerials()));
        if(StringUtil.isNotBlank(monitorVO.getShopImage1())){
            monitorDTO.addFeature(Constant.BUSINESS_LICENCE_IMAGE,monitorVO.getShopImage1());
        }else{
            monitorDTO.removeFeature(Constant.BUSINESS_LICENCE_IMAGE);
        }
        if(StringUtil.isNotBlank(monitorVO.getShopImage2())){
            monitorDTO.addFeature("shopImage2",monitorVO.getShopImage2());
        }else {
            monitorDTO.removeFeature("shopImage2");
        }
        if(monitorVO.getShopImag3()!=null&&monitorVO.getShopImag3().size()>0){
            monitorDTO.addFeature(Constant.HEALTH_IMAGE, FeatureUtil.listToString(monitorVO.getShopImag3()));
        }else {
            monitorDTO.removeFeature(Constant.HEALTH_IMAGE);
        }
        if(monitorVO.getShopImagDaily()!=null&&monitorVO.getShopImagDaily().size()>0){
            monitorDTO.addFeature(Constant.SHOP_DAILY_IMAGE, FeatureUtil.listToString(monitorVO.getShopImagDaily()));
        }else {
            monitorDTO.removeFeature(Constant.SHOP_DAILY_IMAGE);
        }

        if(ydMonitorInfo!=null){
            ydMonitorInfo = doMapper.map(monitorDTO,YdMonitorInfo.class);
            ydMonitorInfoMapper.updateByPrimaryKey(ydMonitorInfo);

        }else{
            ydMonitorInfo = doMapper.map(monitorDTO,YdMonitorInfo.class);
            ydMonitorInfoMapper.insert(ydMonitorInfo);
        }

        return true;
    }


    @Override
    public String getPlayAddress(String deviceSerial, Integer channelNo) throws IOException, ClassNotFoundException {
        return ys7Service.getPlayerAddress(deviceSerial,channelNo);
    }

    @Override
    public Map<String, Object> getChannelInfo(String deviceSerial, Integer channelNo) throws IOException, ClassNotFoundException {
        return ys7Service.getChannelInfo(deviceSerial,channelNo);
    }

    @Override
    public Map<String, Object> getOssAuthentication(String mobile, String code,String key) throws IOException, ClassNotFoundException {
        if(userinfoService.checkCodeByRootUser(mobile,code)){

            if(StringUtil.isBlank(key)){
                key = DiamondYdSystemConfigHolder.getInstance().qjCpBucketKey;
            }
            return OSSUtils.getOssAuthentication("qjcp",key);

        }
        return null;
    }
}
