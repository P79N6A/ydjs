package com.yd.ydsp.biz.manage.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yd.ydsp.biz.manage.CpAgentService;
import com.yd.ydsp.biz.manage.YdXiaoerService;
import com.yd.ydsp.biz.manage.model.CpAgentInfoVO;
import com.yd.ydsp.biz.oss.OSSFileService;
import com.yd.ydsp.biz.weixin.WeixinSamll2ShopService;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.client.domian.manage.YdAgentApplyInfoDTO;
import com.yd.ydsp.client.domian.manage.YdAgentInfoDTO;
import com.yd.ydsp.client.domian.openshop.YdShopApplyInfoVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.manage.AgentApplyStatusEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.model.ResultPage;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

public class YdXiaoerServiceImpl implements YdXiaoerService {

    public static final Logger logger = LoggerFactory.getLogger(YdXiaoerServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;

    @Resource
    CpAgentService cpAgentService;
    @Resource
    private YdManageUserInfoMapper ydManageUserInfoMapper;
    @Resource
    private YdAgentInfoMapper ydAgentInfoMapper;
    @Resource
    private YdAgentApplyInfoMapper ydAgentApplyInfoMapper;
    @Resource
    private YdShopApplyInfoMapper ydShopApplyInfoMapper;
    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    private YdPaypointShopInfoExtMapper ydPaypointShopInfoExtMapper;
    @Resource
    private YdPaypointAgentapplyinfoMapper ydPaypointAgentapplyinfoMapper;

    @Resource
    private WeixinSamll2ShopService weixinSamll2ShopService;
    @Resource
    private OSSFileService ossFileService;

    @Resource
    private YdManageUserWhiteListMapper ydManageUserWhiteListMapper;

    @Override
    public WeixinUserInfo loginByWeiXinQrcode(String code, String state) {
        WeixinUserInfo weixinUserInfo = cpAgentService.loginByWeiXinQrcode(code,state);
        if(weixinUserInfo!=null){
            if(StringUtil.isNotEmpty(weixinUserInfo.getMobile())){
                YdManageUserWhiteList userWhiteList = ydManageUserWhiteListMapper.selectByMobile(weixinUserInfo.getMobile());
                if(userWhiteList==null){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"内部系统，请不要偿试登录!");
                }
            }

        }
        return weixinUserInfo;
    }

    @Override
    public WeixinUserInfo loginByMobile(String mobile, String passwd) {
        YdManageUserWhiteList userWhiteList = ydManageUserWhiteListMapper.selectByMobile(mobile);
        if(userWhiteList==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"内部系统，请不要偿试登录!");
        }
        return cpAgentService.loginByMobile(mobile,passwd);
    }

    @Override
    public boolean checkBindMobile(String openid) {
        return cpAgentService.checkBindMobile(openid);
    }

    @Override
    public boolean generateManageUserMobileCode(String openid, String mobile) {
        YdManageUserWhiteList userWhiteList = ydManageUserWhiteListMapper.selectByMobile(mobile);
        if(userWhiteList==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"内部系统，请不要偿试登录!");
        }
        return cpAgentService.generateManageUserMobileCode(openid,mobile);
    }

    @Override
    public boolean bindManageMobil(String openid, String mobile, String passwd, String bindCode) {
        YdManageUserWhiteList userWhiteList = ydManageUserWhiteListMapper.selectByMobile(mobile);
        if(userWhiteList==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"内部系统，请不要偿试登录!");
        }
        return cpAgentService.bindManageMobil(openid,mobile,passwd,bindCode);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean agentApplyAudit(String openid, String angentid,boolean isReject,String rejectDesc,Date contractTimeBegin, Date contractTimeEnd) {
        YdAgentApplyInfo ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByAgentidRowLock(angentid);
        if(ydAgentApplyInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"不存在的申请单!");
        }
        if(!this.isXiaoer(openid)){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        if(ydAgentApplyInfo.getStatus().intValue()==AgentApplyStatusEnum.AUDIT.getStatus().intValue()) {
            if (isReject) {

                ydAgentApplyInfo.setStatus(AgentApplyStatusEnum.APPLY.getStatus());
                ydAgentApplyInfo.setDescription(rejectDesc);

            } else {
                if (contractTimeBegin == null || contractTimeEnd == null) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "签约时间不能为空!");
                }
                if (DateUtils.getSecondTimestamp(contractTimeEnd) <= DateUtils.getSecondTimestamp(contractTimeBegin)) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "签约时间设置不正确!");
                }
                /**
                 * 生成一个签约码，只有小二才能查看，需要用户与小二线下签约完成并付款后，再由小二发送签约码到用户
                 */
                YdAgentApplyInfoDTO ydAgentApplyInfoDTO = doMapper.map(ydAgentApplyInfo, YdAgentApplyInfoDTO.class);
                Integer sigCodeInt = RandomUtil.getNotSimple(8);
                String sigCode = sigCodeInt.toString();
                if (sigCode.length() == 7) {
                    sigCode = sigCode + "0";
                }
                ydAgentApplyInfoDTO.addFeature(Constant.sigCode, sigCode);
                ydAgentApplyInfo.setFeature(ydAgentApplyInfoDTO.getFeature());
                ydAgentApplyInfo.setJointime(contractTimeBegin);
                ydAgentApplyInfo.setContractTimeBegin(contractTimeBegin);
                ydAgentApplyInfo.setContractTimeEnd(contractTimeEnd);
                ydAgentApplyInfo.setStatus(AgentApplyStatusEnum.WAITE.getStatus());
            }
            if(ydAgentApplyInfoMapper.updateByPrimaryKey(ydAgentApplyInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"审核数据提交更新失败!");
            }
        }else if(ydAgentApplyInfo.getStatus().intValue()==AgentApplyStatusEnum.AUDIT2.getStatus().intValue()) {
            if (isReject) {

                ydAgentApplyInfo.setStatus(AgentApplyStatusEnum.FINISH.getStatus());
                ydAgentApplyInfo.setDescription(rejectDesc);

            } else {
                ydAgentApplyInfo.setStatus(AgentApplyStatusEnum.FINISH.getStatus());
                ydAgentApplyInfo.setDescription("");
                YdAgentInfo ydAgentInfo = ydAgentInfoMapper.selectByOpenid(ydAgentApplyInfo.getOpenid());
                if(ydAgentInfo==null){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "数据异常，导致失败!");
                }

                ydAgentInfo = doMapper.map(ydAgentApplyInfo,YdAgentInfo.class);

                if(ydAgentInfoMapper.updateByPrimaryKey(ydAgentInfo)<=0){
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"审核数据提交更新失败!");
                }
            }
            if(ydAgentApplyInfoMapper.updateByPrimaryKey(ydAgentApplyInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"审核数据提交更新失败!");
            }

        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "并非审核状态!");
        }

        return true;
    }

    @Override
    public boolean setAgentApplyStatus(String openid, String angentid, Integer status, String desc) {
        YdAgentApplyInfo ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByAgentidRowLock(angentid);
        if(ydAgentApplyInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"不存在的申请单!");
        }
        if(!this.isXiaoer(openid)){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        if(ydAgentApplyInfo.getStatus().intValue()>0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "当前状态不能操作!");
        }
        if(status.intValue()>0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "设置的状态不正确!");
        }

        AgentApplyStatusEnum applyStatusEnum = AgentApplyStatusEnum.nameOf(status);
        if(applyStatusEnum==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "设置的状态不正确!");
        }

        ydAgentApplyInfo.setStatus(status);
        if(StringUtil.isNotEmpty(desc)){
            ydAgentApplyInfo.setDescription(desc);
        }
        if(ydAgentApplyInfoMapper.updateByPrimaryKeySelective(ydAgentApplyInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "状态更新失败!");
        }

        return true;
    }

    @Override
    public CpAgentInfoVO queryYdAgentApplyInfo(String openid, String angentid) {
        if(!this.isXiaoer(openid)){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        YdAgentApplyInfo ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByAgentid(angentid);
        if(ydAgentApplyInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"不存在的申请单!");
        }


        YdAgentApplyInfoDTO ydAgentApplyInfoDTO = doMapper.map(ydAgentApplyInfo,YdAgentApplyInfoDTO.class);
        CpAgentInfoVO cpAgentInfoVO = doMapper.map(ydAgentApplyInfo,CpAgentInfoVO.class);
        cpAgentInfoVO.setAgentMode(ydAgentApplyInfoDTO.getListFeature(Constant.cityCodes));

        return cpAgentInfoVO;
    }

    @Override
    public CpAgentInfoVO queryYdAgentInfo(String openid, String angentid) {
        if(!this.isXiaoer(openid)){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        YdAgentInfo ydAgentInfo = ydAgentInfoMapper.selectByAgentid(angentid);
        if(ydAgentInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"不存在的申请单!");
        }


        YdAgentInfoDTO ydAgentInfoDTO = doMapper.map(ydAgentInfo,YdAgentInfoDTO.class);
        CpAgentInfoVO cpAgentInfoVO = doMapper.map(ydAgentInfo,CpAgentInfoVO.class);
        cpAgentInfoVO.setAgentMode(ydAgentInfoDTO.getListFeature(Constant.cityCodes));

        Integer cpNum = 0;
        if(StringUtil.isNotEmpty(ydAgentInfoDTO.getFeature(Constant.CPNUM))){
            cpNum = Integer.valueOf(ydAgentInfoDTO.getFeature(Constant.CPNUM));
        }

        cpAgentInfoVO.setCpNum(cpNum);

        return cpAgentInfoVO;
    }

    @Override
    public String queryAgentContractCode(String openid, String angentid) {
        String result = null;
        if(!this.isXiaoer(openid)){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        YdAgentApplyInfo ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByAgentid(angentid);
        if(ydAgentApplyInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"不存在的申请单!");
        }

        YdAgentApplyInfoDTO ydAgentApplyInfoDTO = doMapper.map(ydAgentApplyInfo,YdAgentApplyInfoDTO.class);

        if(StringUtil.isNotEmpty(ydAgentApplyInfoDTO.getFeature(Constant.sigCode))){
            result = ydAgentApplyInfoDTO.getFeature(Constant.sigCode);
        }

        return result;

    }

    @Override
    public ResultPage<?> selectAgentApplyInfo(String openid, Integer pageNum, Integer pageSize) {
        if(!this.isXiaoer(openid)){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        ResultPage resultPage = new ResultPage();
        List<Map> resultMap = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<YdAgentApplyInfo> agentApplyInfos = ydAgentApplyInfoMapper.selectAll();

        if(agentApplyInfos!=null){
            PageInfo<YdAgentApplyInfo> pageInfo=new PageInfo<>(agentApplyInfos);
            for(YdAgentApplyInfo ydAgentApplyInfo: agentApplyInfos) {
                Map<String, Object> record = new HashMap<>();
                record.put("agentid",ydAgentApplyInfo.getAgentid());
                record.put("name",ydAgentApplyInfo.getName());
                if(ydAgentApplyInfo.getAgentType().intValue()==0) {
                    record.put("agentType", "个人");
                    record.put("name",ydAgentApplyInfo.getName());
                }else {
                    record.put("agentType", "公司");
                    if(StringUtil.isEmpty(ydAgentApplyInfo.getCompanyname())){
                        record.put("name",ydAgentApplyInfo.getName());
                    }else {
                        record.put("name",ydAgentApplyInfo.getCompanyname());
                    }
                }
                record.put("modifyDate",ydAgentApplyInfo.getModifyDate());
                record.put("status",ydAgentApplyInfo.getStatus());
                resultMap.add(record);
            }
            resultPage.setCount(resultMap.size());
            resultPage.setTotal(pageInfo.getTotal());
            resultPage.setPageNum(pageNum);
            resultPage.setPageSize(pageSize);
            resultPage.setItems(resultMap);
        }

        return resultPage;
    }

    @Override
    public ResultPage<?> selectAgentInfo(String openid, Integer pageNum, Integer pageSize) {
        if(!this.isXiaoer(openid)){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }

        ResultPage resultPage = new ResultPage();
        List<Map> resultMap = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<YdAgentInfo> agentInfos = ydAgentInfoMapper.selectAll();
        if(agentInfos!=null){
            PageInfo<YdAgentInfo> pageInfo = new PageInfo<>(agentInfos);
            for(YdAgentInfo ydAgentInfo: agentInfos) {
                Map<String, Object> record = new HashMap<>();
                record.put("agentid",ydAgentInfo.getAgentid());
                if(ydAgentInfo.getAgentType().intValue()==0) {
                    record.put("agentType", "个人");
                    record.put("name",ydAgentInfo.getName());
                }else {
                    record.put("agentType", "公司");
                    record.put("name",ydAgentInfo.getCompanyname());
                }
                record.put("contractTimeBegin",ydAgentInfo.getContractTimeBegin());
                record.put("contractTimeEnd",ydAgentInfo.getContractTimeEnd());
                if(DateUtils.subDate(DateUtils.date2LocalDate(new Date()),DateUtils.date2LocalDate(ydAgentInfo.getContractTimeEnd())).intValue()>0){
                    record.put("status","正常");
                }else {
                    record.put("status","到期");
                }
                resultMap.add(record);
            }
            resultPage.setCount(resultMap.size());
            resultPage.setTotal(pageInfo.getTotal());
            resultPage.setPageNum(pageNum);
            resultPage.setPageSize(pageSize);
            resultPage.setItems(resultMap);
        }

        return resultPage;
    }

    @Override
    public ResultPage<?> selectCpApplyInfo(String openid, Integer pageNum, Integer pageSize) {
        if(!this.isXiaoer(openid)){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        ResultPage resultPage = new ResultPage();
        List<Map> resultMap = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<YdShopApplyInfo> shopApplyInfos = ydShopApplyInfoMapper.selectAll();

        if(shopApplyInfos!=null){
            PageInfo<YdShopApplyInfo> pageInfo=new PageInfo<>(shopApplyInfos);
            for(YdShopApplyInfo ydShopApplyInfo: shopApplyInfos) {
                Map<String, Object> record = new HashMap<>();
                record.put("applyid",ydShopApplyInfo.getApplyid());
                record.put("name",ydShopApplyInfo.getName());
                record.put("tradeType",ydShopApplyInfo.getTradeType());
                record.put("modifyDate",ydShopApplyInfo.getModifyDate());
                record.put("status",ydShopApplyInfo.getStatus());
                resultMap.add(record);
            }
            resultPage.setCount(resultMap.size());
            resultPage.setTotal(pageInfo.getTotal());
            resultPage.setPageNum(pageNum);
            resultPage.setPageSize(pageSize);
            resultPage.setItems(resultMap);
        }

        return resultPage;
    }

    @Override
    public ResultPage<?> selectShopInfo(String openid, Integer pageNum, Integer pageSize) {
        if(!this.isXiaoer(openid)){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        ResultPage resultPage = new ResultPage();
        List<Map> resultMap = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<YdPaypointShopInfo> paypointShopInfos = ydPaypointShopInfoMapper.selectAll();

        if(paypointShopInfos!=null){
            PageInfo<YdPaypointShopInfo> pageInfo=new PageInfo<>(paypointShopInfos);
            for(YdPaypointShopInfo ydPaypointShopInfo: paypointShopInfos) {
                Map<String, Object> record = new HashMap<>();
                record.put("shopid",ydPaypointShopInfo.getShopid());
                record.put("name",ydPaypointShopInfo.getName());
                record.put("tradeType",ydPaypointShopInfo.getTrade());
                record.put("modifyDate",ydPaypointShopInfo.getModifyDate());
                record.put("status",ydPaypointShopInfo.getStatus());
                YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(ydPaypointShopInfo.getShopid());
                if(ydPaypointShopInfoExt==null){
                    record.put("agentName","无");
                }else {
                    YdAgentInfo ydAgentInfo = ydAgentInfoMapper.selectByOpenid(ydPaypointShopInfoExt.getAgentid());
                    if(ydAgentInfo==null){
                        record.put("agentName","无");
                    }else {
                        if(ydAgentInfo.getAgentType().intValue()==0) {
                            record.put("agentName",ydAgentInfo.getName());
                        }else {
                            record.put("agentName", ydAgentInfo.getCompanyname());
                        }

                    }
                }
                resultMap.add(record);
            }
            resultPage.setCount(resultMap.size());
            resultPage.setTotal(pageInfo.getTotal());
            resultPage.setPageNum(pageNum);
            resultPage.setPageSize(pageSize);
            resultPage.setItems(resultMap);
        }

        return resultPage;
    }

    @Override
    public YdShopApplyInfoVO getYdCpShopInfo(String openid, String shopid) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }

        YdShopApplyInfo ydShopApplyInfo = ydShopApplyInfoMapper.selectByApplyid(shopid);
        if(ydShopApplyInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"没找到店铺对应的申请单!");
        }

        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        YdPaypointShopInfoExt shopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopid);
        if(shopInfo==null||shopInfoExt==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"店铺不存在!");
        }

        YdShopApplyInfoVO shopApplyInfoVO = new YdShopApplyInfoVO();
        shopApplyInfoVO.setApplyid(shopid);
        shopApplyInfoVO.setAddress(shopInfo.getAddress());
        shopApplyInfoVO.setAgentid(shopInfoExt.getAgentid());
        shopApplyInfoVO.setBusinessCode(shopInfoExt.getBusinessCode());
        shopApplyInfoVO.setBusinessLicense(shopInfoExt.getBusinessLicense());
        shopApplyInfoVO.setBusinessLicenseImg(shopInfoExt.getBusinessLicenseImg());
        shopApplyInfoVO.setCity(shopInfo.getCity());
        shopApplyInfoVO.setContact(shopInfo.getContact());
        shopApplyInfoVO.setContractTimeBegin(shopInfo.getContractTimeBegin());
        shopApplyInfoVO.setContractTimeEnd(shopInfo.getContractTimeEnd());
        shopApplyInfoVO.setCorporation(shopInfo.getCorporation());
        shopApplyInfoVO.setCountry(shopInfo.getCountry());
        shopApplyInfoVO.setDescription(shopInfo.getDescription());
        shopApplyInfoVO.setDistrict(shopInfo.getDistrict());
        shopApplyInfoVO.setEmail(shopInfo.getEmail());
        shopApplyInfoVO.setIdentificationCardImg1(shopInfoExt.getIdentificationCardImg1());
        shopApplyInfoVO.setIdentificationCardImg2(shopInfoExt.getIdentificationCardImg2());
        shopApplyInfoVO.setLatitude(shopInfoExt.getLatitude());
        shopApplyInfoVO.setLongitude(shopInfoExt.getLongitude());
        shopApplyInfoVO.setMobile(shopInfo.getMobile());
        shopApplyInfoVO.setName(shopInfo.getName());
        shopApplyInfoVO.setOwnerIdentificationCard(shopInfoExt.getOwnerIdentificationCard());
        shopApplyInfoVO.setProvince(shopInfo.getProvince());
        shopApplyInfoVO.setPublicAppid(ydShopApplyInfo.getPublicAppid());
        shopApplyInfoVO.setShopImg(shopInfo.getShopImg());
        shopApplyInfoVO.setSmallAppid(ydShopApplyInfo.getSmallAppid());
        shopApplyInfoVO.setZipcode(shopInfo.getZipcode());
        shopApplyInfoVO.setTradeType(shopInfo.getTrade());
        shopApplyInfoVO.setTelephone(shopInfo.getTelephone());
        shopApplyInfoVO.setStatus(shopInfo.getStatus());

        return shopApplyInfoVO;
    }

    @Override
    public Map getDefaultXiaoer(String openid) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        String mobile =  ydManageUserInfo.getMobile();
        if(StringUtil.isEmpty(mobile)){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        if(ydManageUserWhiteListMapper.selectByMobile(mobile)==null){
            logger.error("非法的小二操作，openid is : "+openid);
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"非法操作!");
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("mobile",mobile);
        return resultMap;
    }

    @Override
    public ResultPage<?> getApplyInfoRecordList(String openid, Integer pageNum, Integer pageSize) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }

        ResultPage resultPage = new ResultPage();
        List<YdPaypointAgentapplyinfo> resultMap = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<YdPaypointAgentapplyinfo> agentapplyinfos = ydPaypointAgentapplyinfoMapper.selectAll();

        if(agentapplyinfos!=null){
            PageInfo<YdPaypointAgentapplyinfo> pageInfo=new PageInfo<>(agentapplyinfos);
            resultPage.setCount(resultMap.size());
            resultPage.setTotal(pageInfo.getTotal());
            resultPage.setPageNum(pageNum);
            resultPage.setPageSize(pageSize);
            resultPage.setItems(agentapplyinfos);
        }

        return resultPage;
    }

    /**
     * --------------------------------------与小程序代码管理审核以及发布有关的操作接口--------------------------------------
     */

    @Override
    public Map<String, Object> getCategory(String openid, String appid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.getCategory(appid);
    }

    @Override
    public Map<String, Object> getPage(String openid, String appid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.getPage(appid);
    }

    @Override
    public Map<String, Object> getTesterList(String openid, String appid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.getTesterList(appid);
    }

    @Override
    public Map<String, Object> bindTester(String openid, String appid, String wechatid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.bindTester(appid,wechatid);
    }

    @Override
    public Map<String, Object> unBindTester(String openid, String appid, String wechatid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.unBindTester(appid,wechatid);
    }

    @Override
    public boolean uploadWeiXinSmallCode(String openid, String appid, Integer templateid, String version, String desc) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.uploadWeiXinSmallCode(appid,templateid,version,desc);
    }

    @Override
    public Map<String, Object> submitWeiXinSmallCodeAudit(String openid, String appid, String postData) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        logger.info("old postData is :"+postData);
        postData = new String(postData.toString().getBytes(), "ISO8859-1");
        logger.info("new postData is :"+postData);
        return weixinSamll2ShopService.submitWeiXinSmallCodeAudit(appid,postData);
    }

    @Override
    public Map<String, Object> undoCodeAudit(String openid, String appid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.undoCodeAudit(appid);
    }

    @Override
    public Map<String, Object> getXinSmallCodeAuditResult(String openid, String appid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.getXinSmallCodeAuditResult(appid);
    }

    @Override
    public Map<String, Object> releaseWeiXinSmallCode(String openid, String appid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.releaseWeiXinSmallCode(appid);
    }

    @Override
    public Map<String, Object> grayReleaseWeiXinSmallCode(String openid, String appid, Integer percentage) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.grayReleaseWeiXinSmallCode(appid,percentage);
    }

    @Override
    public Map<String, Object> cancelGrayReleaseWeiXinSmallCode(String openid, String appid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.cancelGrayReleaseWeiXinSmallCode(appid);
    }

    @Override
    public Map<String, Object> getGrayReleaseWeiXinSmallCode(String openid, String appid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.getGrayReleaseWeiXinSmallCode(appid);
    }

    @Override
    public Map<String, Object> getTemplateDraftList(String openid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.getTemplateDraftList();
    }

    @Override
    public Map<String, Object> getTemplateList(String openid) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.getTemplateList();
    }

    @Override
    public Map<String, Object> addToTemplate(String openid, Integer draftId) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.addToTemplate(draftId);
    }

    @Override
    public Map<String, Object> deleteTemplate(String openid, Integer templateId) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.deleteTemplate(templateId);
    }

    @Override
    public Map<String,Object> modifySmallDomain(String openid,String appid,String action,String domain) throws Exception {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        return weixinSamll2ShopService.modifySmallDomain(appid,action,domain);
    }

    @Override
    public boolean releaseFile(String openid, String ids) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isXiaoer(openid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        ossFileService.releaseFile(ids);
        return true;
    }

    /**
     * --------------------------------------与小程序代码管理审核以及发布有关的操作接口--------------------------------------
     */


    protected boolean isXiaoer(String openid){
        boolean result = true;
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            result = false;
        }
        String mobile =  ydManageUserInfo.getMobile();
        if(StringUtil.isEmpty(mobile)){
            result = false;
        }
        if(ydManageUserWhiteListMapper.selectByMobile(mobile)==null){
            result = false;
        }

        return result;

    }
}
