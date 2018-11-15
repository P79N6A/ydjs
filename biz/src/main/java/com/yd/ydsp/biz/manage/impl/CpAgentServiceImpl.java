package com.yd.ydsp.biz.manage.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yd.ydsp.biz.config.DiamondYdAgentConfigHolder;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.manage.CpAgentService;
import com.yd.ydsp.biz.manage.model.CpAgentInfoVO;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.message.SmsMessageService;
import com.yd.ydsp.biz.oss.OSSFileService;
import com.yd.ydsp.biz.weixin.WeixinOauth2Service;
import com.yd.ydsp.biz.weixin.model.WeixinTokenInfo;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.client.domian.manage.YDManageUserInfoVO;
import com.yd.ydsp.client.domian.manage.YdAgentApplyInfoDTO;
import com.yd.ydsp.client.domian.manage.YdAgentInfoDTO;
import com.yd.ydsp.client.domian.manage.YdManageUserInfoDTO;
import com.yd.ydsp.client.domian.openshop.YdShopApplyInfoVO;
import com.yd.ydsp.client.domian.paypoint.ManageUserInfoDTO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.enums.manage.AgentApplyStatusEnum;
import com.yd.ydsp.common.enums.manage.ShopApplyStatusEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.common.model.ResultPage;
import com.yd.ydsp.common.utils.*;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;


public class CpAgentServiceImpl implements CpAgentService {

    public static final Logger logger = LoggerFactory.getLogger(CpAgentServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;

    @Resource
    private WeixinOauth2Service weixinOauth2ServiceWeb;
    @Resource
    private YdManageUserInfoMapper ydManageUserInfoMapper;
    @Resource
    private YdAgentApplyInfoMapper ydAgentApplyInfoMapper;
    @Resource
    private YdAgentInfoMapper ydAgentInfoMapper;
    @Resource
    private YdShopApplyInfoMapper ydShopApplyInfoMapper;
    @Resource
    private YdAgentWorkerMapper ydAgentWorkerMapper;
    @Resource
    private SmsMessageService smsMessageService;
    @Resource
    private OSSFileService ossFileService;
    @Resource
    private YdPaypointCpuserInfoMapper ydPaypointCpuserInfoMapper;
    @Resource
    private YdPaypointShopInfoExtMapper ydPaypointShopInfoExtMapper;
    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    MqMessageService mqMessageService;

    @Override
    public Map<String, Object> getOssAuthentication(String openid, String key) throws UnsupportedEncodingException {
        if(StringUtil.isEmpty(key)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "文件路径与文件名不能为空！");
        }

        if (StringUtil.isBlank(key)) {
            key = DiamondYdSystemConfigHolder.getInstance().payPointBucketKey;
        }
        return ossFileService.getOssPayPointAuthentication(key);

    }

    @Override
    public WeixinUserInfo loginByWeiXinQrcode(String code, String state) {
        if (StringUtil.isNotBlank(code)) {
            //开始验证微信登录
            Result<WeixinTokenInfo> weixinTokenInfoResult = weixinOauth2ServiceWeb.authorize(code);
            if (weixinTokenInfoResult != null) {
                WeixinTokenInfo weixinTokenInfo = weixinTokenInfoResult.getResult();
                String openid = weixinTokenInfo.getOpenid();
                if(StringUtil.isNotEmpty(openid)){
                    /**
                     * 取用户信息
                     */
                    Result<WeixinUserInfo> weixinUserInfoResult = weixinOauth2ServiceWeb.getWeiXinUserInfo(openid,weixinTokenInfo.getAccess_token(),true);
                    if(weixinUserInfoResult!=null){
                        WeixinUserInfo weixinUserInfo = weixinUserInfoResult.getResult();
                        /**
                         * 开始刷新数据库
                         */
                        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
                        if(ydManageUserInfo==null){
                            /**
                             * 新增用户
                             */
                            ydManageUserInfo = new YdManageUserInfo();
                            ydManageUserInfo.setOpenid(weixinUserInfo.getOpenid());
                            ydManageUserInfo.setUnionid(weixinUserInfo.getUnionid());
                            ydManageUserInfo.setNick(weixinUserInfo.getNickname());
                            ydManageUserInfo.setSex(weixinUserInfo.getSex());
                            ydManageUserInfo.setCountry(weixinUserInfo.getCountry());
                            ydManageUserInfo.setProvince(weixinUserInfo.getProvince());
                            ydManageUserInfo.setCity(weixinUserInfo.getCity());
                            ydManageUserInfo.setHeadImgUrl(weixinUserInfo.getHeadimgurl());
                            ydManageUserInfo.setHeadImgType(1);
                            ydManageUserInfo.setModifier("system");
                            ydManageUserInfo.setWeixinAccessToken(weixinTokenInfo.getAccess_token());
                            ydManageUserInfo.setWeixinRefreshToken(weixinTokenInfo.getRefresh_token());
                            if(ydManageUserInfoMapper.insert(ydManageUserInfo)<=0){
                                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "管理员入库失败！");
                            }

                        }else {
                            /**
                             * 修改用户
                             */
                            ydManageUserInfo.setNick(weixinUserInfo.getNickname());
                            ydManageUserInfo.setSex(weixinUserInfo.getSex());
                            ydManageUserInfo.setCountry(weixinUserInfo.getCountry());
                            ydManageUserInfo.setProvince(weixinUserInfo.getProvince());
                            ydManageUserInfo.setCity(weixinUserInfo.getCity());
                            ydManageUserInfo.setHeadImgUrl(weixinUserInfo.getHeadimgurl());
                            ydManageUserInfo.setHeadImgType(1);
                            ydManageUserInfo.setModifier("system");
                            weixinUserInfo.setMobile(ydManageUserInfo.getMobile());
                            if(ydManageUserInfoMapper.updateByPrimaryKey(ydManageUserInfo)<=0){
                                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "信息更新失败！");
                            }
                        }

                        return weixinUserInfo;

                    }else {
                        throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "取用户信息失败！");
                    }
                }else {
                    throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "微信登录验证失败！");
                }
            }else {
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "微信登录验证失败！");
            }
        }
        return null;
    }

    @Override
    public WeixinUserInfo loginByMobile(String mobile, String passwd) {
        if(StringUtil.isEmpty(passwd)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "密码不能为空！");
        }
        /**
         * 使用手机方式登录
         */
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByMobile(mobile);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "用户不存在，请先使用微信扫码登录！");
        }
        if(StringUtil.isNotEmpty(ydManageUserInfo.getPassword())){
            if(StringUtil.equals(EncryptionUtil.getPassword(passwd,ydManageUserInfo.getOriginal()),ydManageUserInfo.getPassword())){
                /**
                 * 登录成功
                 */
                WeixinUserInfo weixinUserInfo = new WeixinUserInfo();
                weixinUserInfo.setOpenid(ydManageUserInfo.getOpenid());
                weixinUserInfo.setUnionid(ydManageUserInfo.getUnionid());
                weixinUserInfo.setSex(ydManageUserInfo.getSex());
                weixinUserInfo.setMobile(ydManageUserInfo.getMobile());
                return weixinUserInfo;

            }else {
                throw new YdException(ErrorCodeConstants.L100002.getErrorCode(), ErrorCodeConstants.L100002.getErrorMessage());
            }

        }else {
            throw new YdException(ErrorCodeConstants.L100001.getErrorCode(), ErrorCodeConstants.L100001.getErrorMessage());
        }
    }

    @Override
    public boolean checkBindMobile(String openid) {
        boolean result = true;
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            result = false;
        }
        if(StringUtil.isEmpty(ydManageUserInfo.getMobile())||StringUtil.isEmpty(ydManageUserInfo.getPassword())){
            result = false;
        }
        return result;
    }

    @Override
    public boolean generateManageUserMobileCode(String openid, String mobile) {
        if(!PublicUtils.isMobile(mobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
        }
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenidLockRow(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"用户不存在！");
        }

        ManageUserInfoDTO manageUserInfoDTO = doMapper.map(ydManageUserInfo,ManageUserInfoDTO.class);
        Integer code = RandomUtil.getNotSimple(6);
        String codeStr= Integer.toString(code);
        if(codeStr.length()==5){
            codeStr = codeStr+"0";
        }
        Long bindMobileEndTime = Instant.now().getEpochSecond()+ DiamondYdAgentConfigHolder.getInstance().getBindMobileExpin();
        if(StringUtil.isNotEmpty(manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE_ENDTIME))){
            Long lastTime = Long.valueOf(manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE_ENDTIME));
            if(Instant.now().getEpochSecond()-lastTime<=0){
                throw new YdException(ErrorCodeConstants.YD10007.getErrorCode(),ErrorCodeConstants.YD10007.getErrorMessage());
            }
        }
        manageUserInfoDTO.addFeature(Constant.BIND_AGENT_MOBILE,mobile);
        manageUserInfoDTO.addFeature(Constant.BIND_AGENT_MOBILE_CODE,codeStr);
        manageUserInfoDTO.addFeature(Constant.BIND_AGENT_MOBILE_ENDTIME,bindMobileEndTime.toString());

        ydManageUserInfo.setFeature(manageUserInfoDTO.getFeature());
        if(ydManageUserInfoMapper.updateByPrimaryKey(ydManageUserInfo)<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"数据更新失败！");
        }

        return smsMessageService.identitySmsMessage(mobile,codeStr);
    }

    @Override
    public boolean bindManageMobil(String openid, String mobile, String passwd, String bindCode) {
        if(!PublicUtils.isMobile(mobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
        }

        if(StringUtil.isBlank(passwd)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"密码不能为空！");
        }
        passwd = passwd.trim();
        if(passwd.length()<8){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"密码不能少于8位！");
        }
        if(StringUtil.isEmpty(bindCode)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"验证码不能为空！");
        }

        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenidLockRow(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"用户不存在！");
        }

        ManageUserInfoDTO manageUserInfoDTO = doMapper.map(ydManageUserInfo,ManageUserInfoDTO.class);


        if (!(DiamondYdSystemConfigHolder.getInstance().getTestSwitch()&&StringUtil.equals(bindCode, DiamondYdSystemConfigHolder.getInstance().getTestCode()))) {
            if(!StringUtil.equals(mobile, manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE))){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "手机号与验证码不匹配!");
            }
            if (manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE_ENDTIME) == null) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "请先获取验证码!");
            }
            long bindMobileEndTime = Long.parseLong(manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE_ENDTIME));
            if (Instant.now().getEpochSecond() > bindMobileEndTime) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "验证码已过期!");
            }

            if (!StringUtil.equals(bindCode, manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE_CODE))) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "验证码输入不正确!");
            }
        }
        manageUserInfoDTO.removeFeature(Constant.BIND_AGENT_MOBILE_ENDTIME);
        manageUserInfoDTO.removeFeature(Constant.BIND_AGENT_MOBILE);
        manageUserInfoDTO.removeFeature(Constant.BIND_AGENT_MOBILE_CODE);
        ydManageUserInfo.setFeature(manageUserInfoDTO.getFeature());
        ydManageUserInfo.setMobile(mobile);
        if (StringUtil.isNotEmpty(passwd)) {
            String randomNum = String.valueOf(RandomUtil.getNotSimple(8));
            ydManageUserInfo.setOriginal(randomNum);
            ydManageUserInfo.setPassword(EncryptionUtil.getPassword(passwd, randomNum));
        }
        if (ydManageUserInfoMapper.updateByPrimaryKey(ydManageUserInfo) <= 0) {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "绑定失败!");
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String setYdAgentApplyInfo(String openid, CpAgentInfoVO cpAgentInfoVO) {
        String resultAgentid = null;
        if(cpAgentInfoVO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"参数不正确!");
        }
        if(cpAgentInfoVO.getSubmitType().intValue()<0||cpAgentInfoVO.getSubmitType().intValue()>2){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"提交类型不正确!");
        }
        if(cpAgentInfoVO.getAgentType().intValue()<0||cpAgentInfoVO.getAgentType().intValue()>1){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"代理商类型不正确!");
        }
        if(cpAgentInfoVO.getAgentType().intValue()==0){
            if(StringUtil.isEmpty(cpAgentInfoVO.getName())||StringUtil.isEmpty(cpAgentInfoVO.getMobile())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"姓名与手机不能为空!");
            }
        }else {
            if(StringUtil.isEmpty(cpAgentInfoVO.getContact())||StringUtil.isEmpty(cpAgentInfoVO.getCompanyname())||(StringUtil.isEmpty(cpAgentInfoVO.getMobile())&&StringUtil.isEmpty(cpAgentInfoVO.getTelephone()))){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"公司名称与负责人及联系方式不能为空!");
            }
        }
        if(cpAgentInfoVO.getSubmitType().intValue()==1||cpAgentInfoVO.getSubmitType().intValue()==2){
            if(cpAgentInfoVO.getSex()==null||StringUtil.isEmpty(cpAgentInfoVO.getDistrict())||StringUtil.isEmpty(cpAgentInfoVO.getCity())||
                    StringUtil.isEmpty(cpAgentInfoVO.getProvince())||StringUtil.isEmpty(cpAgentInfoVO.getAddress())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"请将您的资料先填写完整!");
            }
            if(cpAgentInfoVO.getAgentMode()==null||cpAgentInfoVO.getAgentMode().size()<=0){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"请选择要加盟的城市!");
            }
        }
        if(cpAgentInfoVO.getSubmitType().intValue()==2){
            if (StringUtil.isEmpty(cpAgentInfoVO.getOwnerIdentificationCard())||StringUtil.isEmpty(cpAgentInfoVO.getIdentificationCardImg1())||
                    StringUtil.isEmpty(cpAgentInfoVO.getIdentificationCardImg2())) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "请将您的身份证资料先填写完整!");
            }
            if(cpAgentInfoVO.getAgentType().intValue()==1) {
                if(StringUtil.isEmpty(cpAgentInfoVO.getBusinessLicense())||StringUtil.isEmpty(cpAgentInfoVO.getBusinessLicenseImg())){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "请将您的营业执照资料先填写完整!");
                }
            }
        }
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenidLockRow(openid);
        if(ydManageUserInfo ==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "登录用户不正确!");
        }
        YdAgentApplyInfo ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByOpenid(openid);
        boolean isNewAgent = true;
        if(ydAgentApplyInfo==null){
            /**
             * 新增
             */
            ydAgentApplyInfo = new YdAgentApplyInfo();
            ydAgentApplyInfo.setOpenid(openid);
            ydAgentApplyInfo.setAgentid(RandomUtil.getSNCode(TypeEnum.AGENTID));
        }else {
            /**
             * 修改
             */
            if(StringUtil.isEmpty(cpAgentInfoVO.getAgentid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"加盟商id不能为空!");
            }
            isNewAgent = false;
            if(!StringUtil.equals(cpAgentInfoVO.getAgentid(),ydAgentApplyInfo.getAgentid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"请不要乱来，你并非此代理商!");
            }
            if(cpAgentInfoVO.getSubmitType().intValue()==2&&ydAgentApplyInfo.getStatus().intValue()==AgentApplyStatusEnum.APPLY.getStatus().intValue()){
                ydAgentApplyInfo.setStatus(AgentApplyStatusEnum.AUDIT.getStatus());
            }
            if(ydAgentApplyInfo.getStatus().intValue()==AgentApplyStatusEnum.FINISH.getStatus().intValue()){
                ydAgentApplyInfo.setStatus(AgentApplyStatusEnum.AUDIT2.getStatus());
            }

        }
        ydAgentApplyInfo.setAddress(cpAgentInfoVO.getAddress());
        ydAgentApplyInfo.setAgentType(cpAgentInfoVO.getAgentType());
        ydAgentApplyInfo.setBusinessLicense(cpAgentInfoVO.getBusinessLicense());
        ydAgentApplyInfo.setBusinessLicenseImg(cpAgentInfoVO.getBusinessLicenseImg());
        ydAgentApplyInfo.setCity(cpAgentInfoVO.getCity());
        ydAgentApplyInfo.setCompanyname(cpAgentInfoVO.getCompanyname());
        ydAgentApplyInfo.setContact(cpAgentInfoVO.getContact());
        ydAgentApplyInfo.setCountry(cpAgentInfoVO.getCountry());
        ydAgentApplyInfo.setDistrict(cpAgentInfoVO.getDistrict());
        ydAgentApplyInfo.setProvince(cpAgentInfoVO.getProvince());
        ydAgentApplyInfo.setEmail(cpAgentInfoVO.getEmail());
        ydAgentApplyInfo.setOwnerIdentificationCard(cpAgentInfoVO.getOwnerIdentificationCard());
        ydAgentApplyInfo.setIdentificationCardImg1(cpAgentInfoVO.getIdentificationCardImg1());
        ydAgentApplyInfo.setIdentificationCardImg2(cpAgentInfoVO.getIdentificationCardImg2());
        ydAgentApplyInfo.setMobile(cpAgentInfoVO.getMobile());
        ydAgentApplyInfo.setName(cpAgentInfoVO.getName());
        ydAgentApplyInfo.setSex(cpAgentInfoVO.getSex());
        ydAgentApplyInfo.setTelephone(cpAgentInfoVO.getTelephone());
        ydAgentApplyInfo.setZipcode(cpAgentInfoVO.getZipcode());

        if((ydAgentApplyInfo.getStatus()>AgentApplyStatusEnum.APPLY.getStatus())&&(ydAgentApplyInfo.getStatus()<AgentApplyStatusEnum.MANAGER.getStatus())) {
            if (cpAgentInfoVO.getSubmitType().intValue() == 0) {
                ydAgentApplyInfo.setStatus(AgentApplyStatusEnum.APPLY.getStatus());
            }
            if ((cpAgentInfoVO.getSubmitType().intValue() == 1) || (cpAgentInfoVO.getSubmitType().intValue() == 0)) {
                ydAgentApplyInfo.setStatus(AgentApplyStatusEnum.APPLY.getStatus());
            }

            if (cpAgentInfoVO.getSubmitType().intValue() == 2) {
                ydAgentApplyInfo.setStatus(AgentApplyStatusEnum.AUDIT.getStatus());
            }
        }


        if(cpAgentInfoVO.getAgentMode()!=null&&cpAgentInfoVO.getAgentMode().size()>0) {
            YdAgentApplyInfoDTO ydAgentApplyInfoDTO = doMapper.map(ydAgentApplyInfo, YdAgentApplyInfoDTO.class);
            ydAgentApplyInfoDTO.removeFeature(Constant.cityCodes);
            for(String cityCode : cpAgentInfoVO.getAgentMode()){
                if(StringUtil.isNotEmpty(cityCode)) {
                    ydAgentApplyInfoDTO.addListFeature(Constant.cityCodes, cityCode);
                }
            }
            ydAgentApplyInfo.setFeature(ydAgentApplyInfoDTO.getFeature());
        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"加盟城市不能为空!");
        }
        if(isNewAgent){
            /**
             * 新增
             */
            if(ydAgentApplyInfoMapper.insert(ydAgentApplyInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"新增信息失败!");
            }
        }else {
            /**
             * 更新
             */

            if(ydAgentApplyInfoMapper.updateByPrimaryKey(ydAgentApplyInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"更新信息失败!");
            }
        }
        if((ydAgentApplyInfo.getStatus()>AgentApplyStatusEnum.APPLY.getStatus())&&(ydAgentApplyInfo.getStatus()<AgentApplyStatusEnum.MANAGER.getStatus())) {
            /**
             * 增加或者修改权限表数据
             */
            boolean isNewWork = false;
            YdAgentWorker ydAgentWorker = ydAgentWorkerMapper.selectByAgentId(ydAgentApplyInfo.getAgentid());
            if (ydAgentWorker == null) {
                ydAgentWorker = new YdAgentWorker();
                ydAgentWorker.setAgentid(ydAgentApplyInfo.getAgentid());
                isNewWork = true;
            }
            ydAgentWorker.setOwnerMobile(ydManageUserInfo.getMobile());
            if (isNewWork) {
                if (ydAgentWorkerMapper.insert(ydAgentWorker) <= 0) {
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "新增权限信息失败!");
                }
            } else {
                if (ydAgentWorkerMapper.updateByPrimaryKey(ydAgentWorker) <= 0) {
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新权限信息失败!");
                }
            }

            YdManageUserInfoDTO ydManageUserInfoDTO = doMapper.map(ydManageUserInfo, YdManageUserInfoDTO.class);
            ydManageUserInfoDTO.addFeature(Constant.DEFAULT_AGENTID, ydAgentApplyInfo.getAgentid());
            ydManageUserInfoDTO.addListFeature(Constant.AGENTIDList, ydAgentApplyInfo.getAgentid());
            ydManageUserInfo.setFeature(ydManageUserInfoDTO.getFeature());
            if (ydManageUserInfoMapper.updateByPrimaryKey(ydManageUserInfo) <= 0) {
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新用户服务商列表信息失败!");
            }
        }

        resultAgentid = ydAgentApplyInfo.getAgentid();

        return resultAgentid;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitContractCode(String openid, String contractCode) {
        if(StringUtil.isEmpty(contractCode)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"签约码不能为空!");
        }
        YdAgentApplyInfo ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByOpenid(openid);
        if(ydAgentApplyInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无申请信息!");
        }
        if(ydAgentApplyInfo.getStatus().intValue()!=AgentApplyStatusEnum.WAITE.getStatus()){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"申请单状态不正确!");
        }
        YdAgentApplyInfoDTO ydAgentApplyInfoDTO = doMapper.map(ydAgentApplyInfo,YdAgentApplyInfoDTO.class);
        if(StringUtil.isEmpty(ydAgentApplyInfoDTO.getFeature(Constant.sigCode))){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"签约码还未生成，请稍后再试!");
        }
        if(StringUtil.equals(ydAgentApplyInfoDTO.getFeature(Constant.sigCode),contractCode)){
            ydAgentApplyInfo.setStatus(AgentApplyStatusEnum.FINISH.getStatus());
            if(ydAgentApplyInfoMapper.updateByPrimaryKey(ydAgentApplyInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"数据更新异常!");
            }
            /**
             * 到此说明成功，可以把申请信息复制到服务商信息表中了
             */
            YdAgentInfo ydAgentInfo = ydAgentInfoMapper.selectByOpenid(ydAgentApplyInfo.getOpenid());
            YdAgentInfo ydAgentInfoData = doMapper.map(ydAgentApplyInfo,YdAgentInfo.class);
            if(ydAgentInfo == null){
                YdAgentInfoDTO ydAgentInfoDTO = doMapper.map(ydAgentInfoData,YdAgentInfoDTO.class);
                ydAgentInfoDTO.addFeature(Constant.CPNUM,"0");
                ydAgentInfoData.setFeature(ydAgentInfoDTO.getFeature());
                if(ydAgentInfoMapper.insert(ydAgentInfoData)<=0){
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"数据更新异常!");
                }
            }else {
                YdAgentInfoDTO ydAgentInfoDTO = doMapper.map(ydAgentInfo,YdAgentInfoDTO.class);
                if(StringUtil.isEmpty(ydAgentInfoDTO.getFeature(Constant.CPNUM))){
                    ydAgentInfoDTO.addFeature(Constant.CPNUM,"0");
                    ydAgentInfoData.setFeature(ydAgentInfoDTO.getFeature());
                }
                ydAgentInfoData.setId(ydAgentInfo.getId());
                if(ydAgentInfoMapper.updateByPrimaryKeySelective(ydAgentInfoData)<=0){
                    throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"数据更新异常!");
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public CpAgentInfoVO queryYdAgentApplyInfo(String openid,String agentid) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo ==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "登录用户不正确!");
        }
        YdAgentApplyInfo ydAgentApplyInfo = null;
        if(StringUtil.isEmpty(agentid)){
            ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByOpenid(openid);
            if(ydAgentApplyInfo == null){
                return null;
            }
        }else {
            if(!this.isAgentManager(ydManageUserInfo.getMobile(),agentid)){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "无权进行此操作!");
            }
            ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByAgentid(agentid);
            if(ydAgentApplyInfo==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "服务商id不存在!");
            }
        }
        /**
         * 填充数据
         */
        YdAgentApplyInfoDTO ydAgentApplyInfoDTO = doMapper.map(ydAgentApplyInfo,YdAgentApplyInfoDTO.class);
        CpAgentInfoVO cpAgentInfoVO = doMapper.map(ydAgentApplyInfo,CpAgentInfoVO.class);
        cpAgentInfoVO.setAgentMode(ydAgentApplyInfoDTO.getListFeature(Constant.cityCodes));
        return cpAgentInfoVO;
    }

    @Override
    public CpAgentInfoVO queryYdAgentInfo(String openid, String agentid) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo ==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "登录用户不正确!");
        }
        YdAgentInfo ydAgentInfo = null;
        if(StringUtil.isEmpty(agentid)){
            ydAgentInfo = ydAgentInfoMapper.selectByOpenid(openid);
            if(ydAgentInfo == null){
                return null;
            }
        }else {
            if(!this.isAgentManager(ydManageUserInfo.getMobile(),agentid)){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "无权进行此操作!");
            }
            ydAgentInfo = ydAgentInfoMapper.selectByAgentid(agentid);
            if(ydAgentInfo==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "服务商id不存在!");
            }
        }
        /**
         * 填充数据
         */
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
    public Integer queryYdAgentApplyStatus(String openid,String agentid) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo ==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "登录用户不正确!");
        }
        boolean checkMobile = this.checkBindMobile(openid);
        if(!checkMobile){
            return -2;
        }
        YdAgentApplyInfo ydAgentApplyInfo = null;
        if(StringUtil.isEmpty(agentid)) {
            ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByOpenid(openid);
            if (ydAgentApplyInfo == null) {
                return -1;
            }
        }else {
            if(!this.isAgentManager(ydManageUserInfo.getMobile(),agentid)){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "无权进入此服务商页面!");
            }
            ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByAgentid(agentid);
            if(ydAgentApplyInfo==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "服务商id不存在!");
            }
            /**
             * 如果不相等，说明不是owner，是管理员
             */
            if(!StringUtil.equals(ydAgentApplyInfo.getOpenid(),openid)){
                if(ydAgentApplyInfo.getStatus()>=AgentApplyStatusEnum.FINISH.getStatus()) {
                    return AgentApplyStatusEnum.MANAGER.getStatus();
                }else {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "服务商还没有申请完成!");
                }
            }
        }
        if(ydAgentApplyInfo.getStatus()>=AgentApplyStatusEnum.FINISH.getStatus()){
            return AgentApplyStatusEnum.FINISH.getStatus();
        }
        return ydAgentApplyInfo.getStatus();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map addToAgentManager(String openid, String mobile, String agentid) {
        Map<String,String> result = null;
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }

        String userMobile = ydManageUserInfo.getMobile();
        if(StringUtil.isBlank(userMobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录手机号错误!");
        }

        YdAgentApplyInfo ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByAgentid(agentid);
        if(ydAgentApplyInfo == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"代理商信息不存在!");
        }

        YdAgentWorker ydAgentWorker = ydAgentWorkerMapper.selectByAgentIdRowLock(agentid);
        if(ydAgentWorker==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"代理商信息不完整!");
        }

        if(this.isAgentManager(userMobile,agentid)){
            YdManageUserInfo ydManageUserInfoTaget = ydManageUserInfoMapper.selectByMobile(mobile);
            if(ydManageUserInfoTaget==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"此手机用户不存在!");
            }
            result = new HashMap<>();
            result.put("name",ydManageUserInfoTaget.getNick());
            result.put("mobile",mobile);
            /**
             * 增加管理员
             */
            String managerMobile = ydAgentWorker.getManagerMobile();
            if(StringUtil.isEmpty(managerMobile)){
                managerMobile = "";
            }

            List<String> managerMobileList = FeatureUtil.strToList(managerMobile);
            if(managerMobileList.contains(mobile.trim())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"已经存在此管理员!");
            }

            managerMobileList.add(mobile.trim());

            ydAgentWorker.setManagerMobile(FeatureUtil.listToString(managerMobileList));

            if(ydAgentWorkerMapper.updateByPrimaryKey(ydAgentWorker)<=0){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"增加失败!");
            }

            /**
             * 更新用户信息中的加盟商列表信息
             *
             */
            YdManageUserInfoDTO ydManageUserInfoDTO = doMapper.map(ydManageUserInfoTaget,YdManageUserInfoDTO.class);
            ydManageUserInfoDTO.addFeature(Constant.DEFAULT_AGENTID,ydAgentWorker.getAgentid());
            ydManageUserInfoDTO.addListFeature(Constant.AGENTIDList,ydAgentWorker.getAgentid());
            ydManageUserInfo.setFeature(ydManageUserInfoDTO.getFeature());
            if(ydManageUserInfoMapper.updateByPrimaryKey(ydManageUserInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"更新用户服务商列表信息失败!");
            }

        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"你无权进行此操作!");
        }

        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delAgentManager(String openid, String mobile, String agentid) {
        boolean result = true;
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenidLockRow(openid);
        if(ydManageUserInfo == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }

        String userMobile = ydManageUserInfo.getMobile();
        if(StringUtil.isBlank(userMobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录手机号错误!");
        }

        YdAgentApplyInfo ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByAgentid(agentid);
        if(ydAgentApplyInfo == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"代理商信息不存在!");
        }

        YdAgentWorker ydAgentWorker = ydAgentWorkerMapper.selectByAgentIdRowLock(agentid);
        if(ydAgentWorker==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"代理商信息不完整!");
        }
        if(this.isAgentManager(userMobile,agentid)){
            YdManageUserInfo ydManageUserInfoTaget = ydManageUserInfoMapper.selectByMobile(mobile);
            if(ydManageUserInfoTaget==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"此手机用户不存在!");
            }
            /**
             * 删除管理员
             */
            String managerMobile = ydAgentWorker.getManagerMobile();
            if(StringUtil.isEmpty(managerMobile)){
                managerMobile = "";
            }

            List<String> managerMobileList = FeatureUtil.strToList(managerMobile);
            if(!managerMobileList.contains(mobile.trim())){
                return result;
            }

            managerMobileList.remove(mobile.trim());

            ydAgentWorker.setManagerMobile(FeatureUtil.listToString(managerMobileList));

            if(ydAgentWorkerMapper.updateByPrimaryKey(ydAgentWorker)<=0){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"删除失败!");
            }

            /**
             * 更新用户信息中的加盟商列表信息
             *
             */
            YdManageUserInfoDTO ydManageUserInfoDTO = doMapper.map(ydManageUserInfoTaget,YdManageUserInfoDTO.class);
            ydManageUserInfoDTO.delListFeature(Constant.AGENTIDList,ydAgentWorker.getAgentid());
            if(StringUtil.equals(ydAgentWorker.getAgentid(),ydManageUserInfoDTO.getFeature(Constant.DEFAULT_AGENTID))){
                ydManageUserInfoDTO.removeFeature(Constant.DEFAULT_AGENTID);
                List<String> agents = ydManageUserInfoDTO.getListFeature(Constant.AGENTIDList);
                if(agents!=null){
                    if(agents.size()>0){
                        ydManageUserInfoDTO.addFeature(Constant.DEFAULT_AGENTID,agents.get(0));
                    }
                }

            }
            ydManageUserInfo.setFeature(ydManageUserInfoDTO.getFeature());
            if(ydManageUserInfoMapper.updateByPrimaryKey(ydManageUserInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"更新用户服务商列表信息失败!");
            }


        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"你无权进行此操作!");
        }

        return result;
    }

    @Override
    public boolean isAgentManager(String mobile,String agentid){
        if(StringUtil.isBlank(mobile)){
            return false;
        }
        YdAgentWorker ydAgentWorker = ydAgentWorkerMapper.selectByAgentId(agentid);
        if(ydAgentWorker==null){
            return false;
        }

        if(StringUtil.equals(ydAgentWorker.getOwnerMobile(),mobile)){
            return true;
        }

        String managers = ydAgentWorker.getManagerMobile();
        if(StringUtil.isBlank(managers)){
            return false;
        }

        List<String> managerList = FeatureUtil.strToList(managers);
        if(managerList==null){
            return false;
        }

        for (String managerMobile:managerList){
            if(StringUtil.equals(mobile,managerMobile)){
                return true;
            }
        }
        return false;

    }

    @Override
    public Map queryManagerInfoByAgentId(String openid, String agentid) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        String mobile = ydManageUserInfo.getMobile();
        if(!isAgentManager(mobile,agentid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"没有查询权限!");
        }
        YdAgentWorker ydAgentWorker = ydAgentWorkerMapper.selectByAgentId(agentid);
        if(ydAgentWorker==null){
            return null;
        }

        Map<String,Object> result = new HashMap<>();

        YdManageUserInfo ydManageUserInfoOwner = ydManageUserInfoMapper.selectByMobile(ydAgentWorker.getOwnerMobile());
        Map<String,String> ownerInfo = new HashMap<>();
        ownerInfo.put("ownerName",ydManageUserInfoOwner.getNick());
        ownerInfo.put("ownerMobile",ydManageUserInfoOwner.getMobile());
        result.put("owner",ownerInfo);

        List<Map> managerList = new ArrayList<>();
        String managerMobile = ydAgentWorker.getManagerMobile();
        if(StringUtil.isEmpty(managerMobile)){
            managerMobile = "";
        }
        List<String> managerMobileList = FeatureUtil.strToList(managerMobile);
        for(String mobileStr:managerMobileList){
            YdManageUserInfo userInfo = ydManageUserInfoMapper.selectByMobile(mobileStr);
            if(userInfo!=null) {
                Map<String, String> userInfoMap = new HashMap<>();
                userInfoMap.put("name", userInfo.getNick());
                userInfoMap.put("mobile", userInfo.getMobile());
                managerList.add(userInfoMap);
            }
        }

        result.put("manager",managerList);

        return result;
    }

    @Override
    public Map queryAgentBaseInfoByManager(String openid) {
        Map<String,Object> result = new HashMap<>();
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        YdManageUserInfoDTO ydManageUserInfoDTO = doMapper.map(ydManageUserInfo,YdManageUserInfoDTO.class);
        List<Map> agentObjList = new ArrayList<>();

        String defaultAgentId = ydManageUserInfoDTO.getFeature(Constant.DEFAULT_AGENTID);
        if(StringUtil.isNotEmpty(defaultAgentId)){
            result.put(Constant.DEFAULT_AGENTID,defaultAgentId);
        }
        List<String> agentList = ydManageUserInfoDTO.getListFeature(Constant.AGENTIDList);
        if(agentList!=null){
            if(agentList.size()>0){
                for(String agentid:agentList){
                    YdAgentApplyInfo ydAgentApplyInfo = ydAgentApplyInfoMapper.selectByAgentid(agentid);
                    if(ydAgentApplyInfo!=null) {
                        Map<String, Object> agentinfo = new HashMap<>();
                        agentinfo.put("name", ydAgentApplyInfo.getName());
                        agentinfo.put("companyname", ydAgentApplyInfo.getCompanyname());
                        agentinfo.put("agentid", ydAgentApplyInfo.getAgentid());
                        agentinfo.put("agentType", ydAgentApplyInfo.getAgentType());
                        agentObjList.add(agentinfo);
                    }
                }
            }
        }
        result.put(Constant.AGENTIDList,agentObjList);
        return result;
    }

    @Override
    public boolean setDefualtAgent(String openid, String agentid) {
        Map<String,Object> result = new HashMap<>();
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenidLockRow(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isAgentManager(ydManageUserInfo.getMobile(),agentid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"你无权进行此操作!");
        }

        YdManageUserInfoDTO ydManageUserInfoDTO = doMapper.map(ydManageUserInfo,YdManageUserInfoDTO.class);
        ydManageUserInfoDTO.addFeature(Constant.DEFAULT_AGENTID,agentid);
        ydManageUserInfo.setFeature(ydManageUserInfoDTO.getFeature());
        if(ydManageUserInfoMapper.updateByPrimaryKey(ydManageUserInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"设置主服务商失败!");
        }

        return true;
    }

    @Override
    public String getDefualtAgent(String openid) {
        String result = null;
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenidLockRow(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }

        YdManageUserInfoDTO ydManageUserInfoDTO = doMapper.map(ydManageUserInfo,YdManageUserInfoDTO.class);
        result = ydManageUserInfoDTO.getFeature(Constant.DEFAULT_AGENTID);
        if(StringUtil.isEmpty(result)){
            result = null;
        }

        return result;
    }

    @Override
    public String setYdCpApplyInfo(String openid, YdShopApplyInfoVO shopApplyInfoVO) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        String resultApplyid = null;
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(StringUtil.isEmpty(shopApplyInfoVO.getAgentid()) || StringUtil.isEmpty(shopApplyInfoVO.getName())||StringUtil.isEmpty(shopApplyInfoVO.getCorporation())||
                StringUtil.isEmpty(shopApplyInfoVO.getMobile())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"店铺名称/负责人姓名/手机号码必须填写!");
        }
        if(!this.isAgentManager(ydManageUserInfo.getMobile(),shopApplyInfoVO.getAgentid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }

        if(shopApplyInfoVO.getSubmitType().intValue()==1) {
            if (shopApplyInfoVO.getTradeType() == null || shopApplyInfoVO.getSubmitType() == null || StringUtil.isEmpty(shopApplyInfoVO.getCorporation()) ||
                    StringUtil.isEmpty(shopApplyInfoVO.getOwnerIdentificationCard()) ||
                    StringUtil.isEmpty(shopApplyInfoVO.getIdentificationCardImg1()) || StringUtil.isEmpty(shopApplyInfoVO.getIdentificationCardImg2()) ||
//                    (StringUtil.isEmpty(shopApplyInfoVO.getSmallAppid()) && StringUtil.isEmpty(shopApplyInfoVO.getPublicAppid())) ||
                    StringUtil.isEmpty(shopApplyInfoVO.getProvince()) || StringUtil.isEmpty(shopApplyInfoVO.getCity()) || StringUtil.isEmpty(shopApplyInfoVO.getDistrict()) ||
                    StringUtil.isEmpty(shopApplyInfoVO.getAddress())
                    ) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "缺少必要的数据，请填写完整!");
            }



            YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByMobile(shopApplyInfoVO.getMobile());
            if (paypointCpuserInfo == null) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "请先在[引灯智能店铺]公众号中绑定此手机号!");
            }
        }

        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopName(shopApplyInfoVO.getName());
        if(ydPaypointShopInfo!=null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺名称已经存在!");
        }
        YdShopApplyInfo ydShopApplyInfo = null;
        boolean isNew = false;

        if(StringUtil.isEmpty(shopApplyInfoVO.getApplyid())){
            isNew = true;
            shopApplyInfoVO.setApplyid(RandomUtil.getSNCode(TypeEnum.SHOP));
            ydShopApplyInfo = new YdShopApplyInfo();
        }else {
            ydShopApplyInfo = ydShopApplyInfoMapper.selectByApplyid(shopApplyInfoVO.getApplyid());
            if(ShopApplyStatusEnum.nameOf(ydShopApplyInfo.getStatus())==ShopApplyStatusEnum.FINISH){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "申请单已经完结，不可修改!");
            }

        }

        Long id = ydShopApplyInfo.getId();
        ydShopApplyInfo = doMapper.map(shopApplyInfoVO,YdShopApplyInfo.class);
        ydShopApplyInfo.setId(id);

        if (shopApplyInfoVO.getSubmitType().intValue() == 1) {
            ydShopApplyInfo.setStatus(ShopApplyStatusEnum.CREATING.getStatus());
        } else {
            ydShopApplyInfo.setStatus(ShopApplyStatusEnum.NEW.getStatus());
        }

        if(isNew){
            if(ydShopApplyInfoMapper.insert(ydShopApplyInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"信息保存失败!");
            }
        }else {
            if(ydShopApplyInfoMapper.updateByPrimaryKeySelective(ydShopApplyInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"信息保存失败!");
            }
        }
        resultApplyid = ydShopApplyInfo.getApplyid();

        /**
         * 发消息创建店铺
         */
        if (shopApplyInfoVO.getSubmitType().intValue() == 1) {
            if(ydShopApplyInfo.getStatus()== ShopApplyStatusEnum.CREATING.getStatus()) {
                /**
                 * 发消息去创建一个店铺
                 */
                Map<String , String> createShopMap = new HashMap<>();
                createShopMap.put(Constant.MQTAG, MqTagEnum.WEIXINAUTHSHOP.getTag());
                createShopMap.put("applyid",resultApplyid);
                mqMessageService.sendMessage(resultApplyid,MqTagEnum.WEIXINAUTHSHOP, JSON.toJSONString(createShopMap));
            }
        }
        return resultApplyid;
    }

    @Override
    public YdShopApplyInfoVO getYdCpApplyInfo(String openid, String applyid) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }

        YdShopApplyInfo ydShopApplyInfo = ydShopApplyInfoMapper.selectByApplyid(applyid);
        if(ydShopApplyInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"不存在的申请单!");
        }

        if(!this.isAgentManager(ydManageUserInfo.getMobile(),ydShopApplyInfo.getAgentid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }

        YdShopApplyInfoVO ydShopApplyInfoVO = doMapper.map(ydShopApplyInfo,YdShopApplyInfoVO.class);

        return ydShopApplyInfoVO;
    }

    @Override
    public YdShopApplyInfoVO getYdCpShopInfo(String openid, String shopid) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
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

        if(!this.isAgentManager(ydManageUserInfo.getMobile(),shopInfoExt.getAgentid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
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

//    @Override
//    public boolean backFirstStepWithCpApply(String openid, String applyid) {
//        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
//        if(ydManageUserInfo==null){
//            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
//        }
//        YdShopApplyInfo ydShopApplyInfo = ydShopApplyInfoMapper.selectByApplyid(applyid);
//        if(ydShopApplyInfo==null){
//            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无此申请单!");
//        }
//        if(!this.isAgentManager(ydManageUserInfo.getMobile(),ydShopApplyInfo.getAgentid())){
//            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"你无权进行此操作!");
//        }
//        if(ydShopApplyInfo.getStatus()>=ShopApplyStatusEnum.CREATING.getStatus()){
//            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"当前状态已经不能回退!");
//        }
//        ydShopApplyInfo.setStatus(ShopApplyStatusEnum.NEW.getStatus());
//        if(ydShopApplyInfoMapper.updateByPrimaryKey(ydShopApplyInfo)<=0){
//            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"信息保存失败!");
//        }
//        return true;
//    }

    @Override
    public boolean delYdCpApplyInfo(String openid, String applyid) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        YdShopApplyInfo ydShopApplyInfo = ydShopApplyInfoMapper.selectByApplyidRowLock(applyid);
        if(ydShopApplyInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无此申请单!");
        }
        if(!this.isAgentManager(ydManageUserInfo.getMobile(),ydShopApplyInfo.getAgentid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"你无权进行此操作!");
        }
        if(ydShopApplyInfo.getStatus()>=ShopApplyStatusEnum.CREATING.getStatus()){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"当前状态已经不能删除!");
        }
        if(ydShopApplyInfoMapper.deleteByPrimaryKey(ydShopApplyInfo.getId())>0){
            return true;
        }
        return false;
    }

    @Override
    public ResultPage<?> selectCpApplyInfo(String openid, String agentid, Integer pageNum, Integer pageSize) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isAgentManager(ydManageUserInfo.getMobile(),agentid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"你无权进行此操作!");
        }
        ResultPage resultPage = new ResultPage();
        List<Map> resultMap = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<YdShopApplyInfo> shopApplyInfos = ydShopApplyInfoMapper.selectByAgentid(agentid);
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
    public ResultPage<?> selectShopInfo(String openid, String agentid, Integer pageNum, Integer pageSize) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isAgentManager(ydManageUserInfo.getMobile(),agentid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"你无权进行此操作!");
        }
        ResultPage resultPage = new ResultPage();
        List<Map> resultMap = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<YdPaypointShopInfoExt> paypointShopInfoExts = ydPaypointShopInfoExtMapper.selectByAgentId(agentid);

        if(paypointShopInfoExts!=null){
            PageInfo<YdPaypointShopInfoExt> pageInfo=new PageInfo<>(paypointShopInfoExts);
            for(YdPaypointShopInfoExt ydPaypointShopInfoExt: paypointShopInfoExts) {
                YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(ydPaypointShopInfoExt.getShopid());
                Map<String, Object> record = new HashMap<>();
                record.put("shopid",ydPaypointShopInfo.getShopid());
                record.put("name",ydPaypointShopInfo.getName());
                record.put("tradeType",ydPaypointShopInfo.getTrade());
                record.put("modifyDate",ydPaypointShopInfo.getModifyDate());
                record.put("status",ydPaypointShopInfo.getStatus());
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
    public YDManageUserInfoVO queryBaseYdAgentByOpenId(String openid) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        YDManageUserInfoVO ydManageUserInfoVO = doMapper.map(ydManageUserInfo,YDManageUserInfoVO.class);
        return ydManageUserInfoVO;
    }

    @Override
    public boolean generatePublicMobileCode(String mobile) {
        {
            if(!PublicUtils.isMobile(mobile)){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
            }
            YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByMobile(mobile);
            if(ydManageUserInfo==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"用户不存在！");
            }

            ManageUserInfoDTO manageUserInfoDTO = doMapper.map(ydManageUserInfo,ManageUserInfoDTO.class);
            Integer code = RandomUtil.getNotSimple(6);
            String codeStr= Integer.toString(code);
            if(codeStr.length()==5){
                codeStr = codeStr+"0";
            }
            Long bindMobileEndTime = Instant.now().getEpochSecond()+ DiamondYdAgentConfigHolder.getInstance().getBindMobileExpin();
            if(StringUtil.isNotEmpty(manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE_ENDTIME))){
                Long lastTime = Long.valueOf(manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE_ENDTIME));
                if(Instant.now().getEpochSecond()-lastTime<=0){
                    throw new YdException(ErrorCodeConstants.YD10007.getErrorCode(),ErrorCodeConstants.YD10007.getErrorMessage());
                }
            }
            manageUserInfoDTO.addFeature(Constant.BIND_AGENT_MOBILE,mobile);
            manageUserInfoDTO.addFeature(Constant.BIND_AGENT_MOBILE_CODE,codeStr);
            manageUserInfoDTO.addFeature(Constant.BIND_AGENT_MOBILE_ENDTIME,bindMobileEndTime.toString());

            ydManageUserInfo.setFeature(manageUserInfoDTO.getFeature());
            if(ydManageUserInfoMapper.updateByPrimaryKey(ydManageUserInfo)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"数据更新失败！");
            }

            return smsMessageService.identitySmsMessage(mobile,codeStr);
        }
    }

    @Override
    public boolean bindPublicManageMobile(String mobile, String passwd, String bindCode) {
        if(!PublicUtils.isMobile(mobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
        }

        if(StringUtil.isBlank(passwd)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"密码不能为空！");
        }
        passwd = passwd.trim();
        if(passwd.length()<8){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"密码不能少于8位！");
        }
        if(StringUtil.isEmpty(bindCode)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"验证码不能为空！");
        }

        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByMobile(mobile);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"用户不存在！");
        }

        ManageUserInfoDTO manageUserInfoDTO = doMapper.map(ydManageUserInfo,ManageUserInfoDTO.class);


        if (!(DiamondYdSystemConfigHolder.getInstance().getTestSwitch()&&StringUtil.equals(bindCode, DiamondYdSystemConfigHolder.getInstance().getTestCode()))) {
            if(!StringUtil.equals(mobile, manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE))){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "手机号与验证码不匹配!");
            }
            if (manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE_ENDTIME) == null) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "请先获取验证码!");
            }
            long bindMobileEndTime = Long.parseLong(manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE_ENDTIME));
            if (Instant.now().getEpochSecond() > bindMobileEndTime) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "验证码已过期!");
            }

            if (!StringUtil.equals(bindCode, manageUserInfoDTO.getFeature(Constant.BIND_AGENT_MOBILE_CODE))) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "验证码输入不正确!");
            }
        }
        manageUserInfoDTO.removeFeature(Constant.BIND_AGENT_MOBILE_ENDTIME);
        manageUserInfoDTO.removeFeature(Constant.BIND_AGENT_MOBILE);
        manageUserInfoDTO.removeFeature(Constant.BIND_AGENT_MOBILE_CODE);
        ydManageUserInfo.setFeature(manageUserInfoDTO.getFeature());
        ydManageUserInfo.setMobile(mobile);
        if (StringUtil.isNotEmpty(passwd)) {
            String randomNum = String.valueOf(RandomUtil.getNotSimple(8));
            ydManageUserInfo.setOriginal(randomNum);
            ydManageUserInfo.setPassword(EncryptionUtil.getPassword(passwd, randomNum));
        }
        if (ydManageUserInfoMapper.updateByPrimaryKey(ydManageUserInfo) <= 0) {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "绑定失败!");
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateContractDate(String openid, String shopid, String agentid, Date contractDateBegin, Date contractDateEnd) {
        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }
        if(!this.isAgentManager(ydManageUserInfo.getMobile(),agentid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"无权进行此操作!");
        }
        if (contractDateBegin == null || contractDateEnd == null) {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "签约时间不能为空!");
        }
        if (DateUtils.getSecondTimestamp(contractDateEnd) <= DateUtils.getSecondTimestamp(contractDateBegin)) {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "签约时间设置不正确!");
        }

        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(ydPaypointShopInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "不存在此店铺!");
        }

        YdShopApplyInfo ydShopApplyInfo = ydShopApplyInfoMapper.selectByApplyid(shopid);
        if (ydShopApplyInfo == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "不存在该店铺的申请单信息!");
        }
        if(!StringUtil.equals(agentid,ydShopApplyInfo.getAgentid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "不属于该代理商下的店铺!");
        }
        Date currentContractTimeBegin = ydShopApplyInfo.getContractTimeBegin();
        Date currentContractTimeEnd = ydShopApplyInfo.getContractTimeEnd();
        // 日期比对
        if (DateUtils.getSecondTimestamp(contractDateBegin)<=DateUtils.getSecondTimestamp(currentContractTimeBegin)||
                DateUtils.getSecondTimestamp(contractDateBegin)< DateUtils.getSecondTimestamp(new Date())||
                DateUtils.getSecondTimestamp(contractDateEnd)<= DateUtils.getSecondTimestamp(currentContractTimeEnd)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "续约时间设置错误!");
        }
        // 更新数据库
        ydShopApplyInfo.setContractTimeBegin(contractDateBegin);
        ydShopApplyInfo.setContractTimeEnd(contractDateEnd);
        if (ydShopApplyInfoMapper.updateByPrimaryKey(ydShopApplyInfo)<1){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "数据库更新数据出错!");
        }
        ydPaypointShopInfo.setContractTimeBegin(contractDateBegin);
        ydPaypointShopInfo.setContractTimeEnd(contractDateEnd);
        if (ydPaypointShopInfoMapper.updateByPrimaryKey(ydPaypointShopInfo)<1){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "数据库更新数据出错!");
        }
        return true;
    }


//    public static void main(String[] args) throws Exception {
//
//        DozerBeanMapper doMaper = new DozerBeanMapper();
//        CpAgentInfoVO cpAgentInfoVO = null;
//        YdAgentApplyInfo ydAgentApplyInfo = new YdAgentApplyInfo();
//        List<String> list = new ArrayList<>();
//        list.add("adfafafa");
//        ydAgentApplyInfo.setContractTimeEnd(new Date());
//        ydAgentApplyInfo.setStatus(2);
//        ydAgentApplyInfo.setId(242525252L);
//        ydAgentApplyInfo.setCountry("中国");
//
//        cpAgentInfoVO = doMaper.map(ydAgentApplyInfo,CpAgentInfoVO.class);
//
//        cpAgentInfoVO.setSubmitType(1);
//
//        YdAgentApplyInfo ydAgentApplyInfo1 = doMaper.map(cpAgentInfoVO,YdAgentApplyInfo.class);
//
//        System.out.println(cpAgentInfoVO.getStatus());
//
//
//    }
}
