package com.yd.ydsp.biz.user.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.manage.CpAgentService;
import com.yd.ydsp.biz.message.SmsMessageService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.user.model.CheckMobileCodeTypeEnum;
import com.yd.ydsp.biz.weixin.WeixinOauth2Service;
import com.yd.ydsp.biz.weixin.model.WeixinHeadImgTypeEnum;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.client.domian.OwnerUserInfoVO;
import com.yd.ydsp.client.domian.ShopInfoDTO;
import com.yd.ydsp.client.domian.UserInfoVO;
import com.yd.ydsp.client.domian.paypoint.ConsumerInfoDTO;
import com.yd.ydsp.client.domian.paypoint.CpUserInfoDTO;
import com.yd.ydsp.client.domian.paypoint.OwnerInfoVO;
import com.yd.ydsp.client.domian.paypoint.WorkerInfoVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.enums.UserTypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.EncryptionUtil;
import com.yd.ydsp.common.utils.FeatureUtil;
import com.yd.ydsp.common.utils.PublicUtils;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/5/18.
 */
public class UserinfoServiceImpl implements UserinfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserinfoServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;

    @Resource
    private YdPaypointCpuserInfoMapper ydPaypointCpuserInfoMapper;
    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    private YdPaypointShopworkerMapper ydPaypointShopworkerMapper;
    @Resource
    private YdPaypointContractinfoMapper ydPaypointContractinfoMapper;
    @Resource
    private YdPaypointUserContractMapper ydPaypointUserContractMapper;

    @Resource
    private YdConsumerInfoMapper ydConsumerInfoMapper;
    @Resource
    private YdWeixinUserInfoMapper ydWeixinUserInfoMapper;
    @Resource
    private WeixinOauth2Service weixinOauth2Service2b;
    @Resource
    private WeixinOauth2Service weixinOauth2Service2c;

    @Resource
    private SmsMessageService smsMessageService;
    @Resource
    private RedisManager redisManager;
    @Resource
    private YdManageUserInfoMapper ydManageUserInfoMapper;
    @Resource
    private YdManageUserWhiteListMapper ydManageUserWhiteListMapper;
    @Resource
    private YdPaypointShopInfoExtMapper ydPaypointShopInfoExtMapper;
    @Resource
    private CpAgentService cpAgentService;
    @Override
    public boolean sendCheckCodeByRootUser(String mobile) throws IOException, ClassNotFoundException {
        if(!PublicUtils.isMobile(mobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
        }
        mobile = mobile.trim();
        byte[] codeBytes = redisManager.get(SerializeUtils.serialize(mobile+"root"));
        if(SerializeUtils.isEmpty(codeBytes)){
            List<String> mobils = java.util.Arrays.asList(DiamondYdSystemConfigHolder.getInstance().rootUserMobiles.split(","));
            for(String mobileRoot:mobils){
                if(mobileRoot.trim().equals(mobile)){
                    Integer code = RandomUtil.getNotSimple(6);
                    if(smsMessageService.identitySmsMessage(mobile,Integer.toString(code))){
                        redisManager.set(SerializeUtils.serialize(mobile+"root"),SerializeUtils.serialize(Integer.toString(code)),10800);
                        redisManager.set(SerializeUtils.serialize(mobile+"roottime"),SerializeUtils.serialize(Instant.now().getEpochSecond()),10800);
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        }else{
            String resultCode = (String) SerializeUtils.deserialize(codeBytes);
            byte[] timeBytes = redisManager.get(SerializeUtils.serialize(mobile+"roottime"));
            if(SerializeUtils.isEmpty(timeBytes)){
                redisManager.del(SerializeUtils.serialize(mobile+"root"));
                redisManager.del(SerializeUtils.serialize(mobile+"roottime"));
                return false;
            }
            long sourceTime = (Long)SerializeUtils.deserialize(redisManager.get(SerializeUtils.serialize(mobile+"roottime")));
            if((Instant.now().getEpochSecond()-sourceTime)>300){
                if(smsMessageService.identitySmsMessage(mobile,resultCode)){
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean checkCodeByRootUser(String mobile, String code) throws IOException, ClassNotFoundException {
        if(!PublicUtils.isMobile(mobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
        }
        mobile = mobile.trim();
        byte[] codeBytes = redisManager.get(SerializeUtils.serialize(mobile+"root"));
        if(SerializeUtils.isEmpty(codeBytes)){
            return false;
        }
        String resultCode = (String) SerializeUtils.deserialize(codeBytes);
        /**
         * 对比成功不要删除，3小时自然失效
         */
        if(code.equals(resultCode)){
            return true;
        }
        return false;
    }

    @Override
    public YdConsumerInfo select2CByOpenId(String openId) {
        YdConsumerInfo userInfo = ydConsumerInfoMapper.selectByOpenid(openId);
        return userInfo;
    }

    @Override
    public YdPaypointCpuserInfo select2BByOpenId(String openId) {
        YdPaypointCpuserInfo userInfo = ydPaypointCpuserInfoMapper.selectByOpenid(openId);
        return userInfo;
    }

    @Override
    public YdPaypointCpuserInfo select2BByUnionId(String unionid) {
        YdPaypointCpuserInfo userInfo = ydPaypointCpuserInfoMapper.selectByUnionId(unionid);
        return userInfo;
    }

    @Override
    public YdPaypointCpuserInfo select2BByMobile(String mobile) {
        YdPaypointCpuserInfo userInfo = ydPaypointCpuserInfoMapper.selectByMobile(mobile);
        return userInfo;
    }

    /**
     * @param ydConsumerInfo
     * @return
     */
    @Override
    public int newUser2C(YdConsumerInfo ydConsumerInfo) {
        if(StringUtil.isEmpty(ydConsumerInfo.getModifier())){
            ydConsumerInfo.setModifier("system");
        }

        return ydConsumerInfoMapper.insertSelective(ydConsumerInfo);
    }

    @Override
    public int newCPUser(YdPaypointCpuserInfo paypointCpuserInfo) {
        if(StringUtil.isEmpty(paypointCpuserInfo.getModifier())){
            paypointCpuserInfo.setModifier("system");
        }

        return ydPaypointCpuserInfoMapper.insertSelective(paypointCpuserInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update2CUserToken(String openid,String accessToken,String refreshToken) {
        YdConsumerInfo consumerInfo = ydConsumerInfoMapper.selectByOpenidLockRow(openid);
        consumerInfo.setWeixinAccessToken(accessToken);
        consumerInfo.setWeixinRefreshToken(refreshToken);
        return ydConsumerInfoMapper.updateByPrimaryKeySelective(consumerInfo);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update2CUserInfo(String openid,boolean isSns) {
        YdConsumerInfo consumerInfo = ydConsumerInfoMapper.selectByOpenidLockRow(openid);
        if(consumerInfo==null){
            return false;
        }
        String accessToken = null;
        if(isSns){
            accessToken = consumerInfo.getWeixinAccessToken();
        }else {
            accessToken = weixinOauth2Service2c.getAccessToken();
        }
        Result<WeixinUserInfo> result = weixinOauth2Service2c.getWeiXinUserInfo(openid,accessToken,isSns);
        if(result==null){
            return false;
        }
        logger.info("weixinOauth2Service2c.getWeiXinUserInfo result is :"+JSON.toJSONString(result));
        if(!result.isSuccess()){
            return false;
        }
        WeixinUserInfo weixinUserInfo = result.getResult();

        consumerInfo.setCity(weixinUserInfo.getCity());
        consumerInfo.setCountry(weixinUserInfo.getCountry());
        consumerInfo.setProvince(weixinUserInfo.getProvince());
        consumerInfo.setHeadImgType(WeixinHeadImgTypeEnum.WEIXINURL.getType());
        consumerInfo.setHeadImgUrl(weixinUserInfo.getHeadimgurl());
        consumerInfo.setNick(weixinUserInfo.getNickname());
        consumerInfo.setSex(weixinUserInfo.getSex());
        consumerInfo.setUnionid(weixinUserInfo.getUnionid());
        if(ydConsumerInfoMapper.updateByPrimaryKey(consumerInfo)>0) {
            return true;
        }else {
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean generateCPUserMobileCode(String openId, String mobile,CheckMobileCodeTypeEnum type) {
        if(!PublicUtils.isMobile(mobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
        }
        YdPaypointCpuserInfo paypointCpuserInfo;
        if(type!=CheckMobileCodeTypeEnum.BINDNEWOWNERMOBILE) {
            paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openId);
            if(paypointCpuserInfo==null){
                throw new YdException(ErrorCodeConstants.YD10008.getErrorCode(),ErrorCodeConstants.YD10008.getErrorMessage());
            }
            if (StringUtil.isNotBlank(paypointCpuserInfo.getMobile())) {
                if (!paypointCpuserInfo.getMobile().equals(mobile)) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "手机与公众号绑定的手机不一致！");
                }
            }
        }else {
            paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByMobileLockRow(mobile);
            if(paypointCpuserInfo==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"此手机号没有与公众号绑定！");
            }
        }
        String bindMobileKey = Constant.BIND_MOBILE;
        String bindMobileEndTimeKey = Constant.BIND_MOBILE_ENDTIME;
        String bindMobileCodeKey = Constant.BIND_MOBILE_CODE;
        if(type==CheckMobileCodeTypeEnum.BINDSHOPMOBILE){

            bindMobileKey = Constant.BIND_SHOP_MOBILE;
            bindMobileCodeKey = Constant.BIND_SHOP_MOBILE_CODE;
            bindMobileEndTimeKey = Constant.BIND_SHOP_MOBILE_ENDTIME;

        }else if(type==CheckMobileCodeTypeEnum.BINDOWNERMOBILE){

            bindMobileKey = Constant.BIND_OWNER_MOBILE;
            bindMobileCodeKey = Constant.BIND_OWNER_MOBILE_CODE;
            bindMobileEndTimeKey = Constant.BIND_OWNER_MOBILE_ENDTIME;

        }else if(type==CheckMobileCodeTypeEnum.BINDNEWOWNERMOBILE){
            bindMobileKey = Constant.BIND_OWNER_NEW_MOBILE;
            bindMobileCodeKey = Constant.BIND_OWNER_NEW_MOBILE_CODE;
            bindMobileEndTimeKey = Constant.BIND_OWNER_NEW_MOBILE_ENDTIME;
        }
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo, CpUserInfoDTO.class);
        Integer code = RandomUtil.getNotSimple(6);
        String codeStr= Integer.toString(code);
        if(codeStr.length()==5){
            codeStr = codeStr+"0";
        }
        Long bindMobileEndTime = Instant.now().getEpochSecond()+300;
        if(StringUtil.isNotEmpty(cpUserInfoDTO.getFeature(bindMobileEndTimeKey))){
            Long lastTime = Long.valueOf(cpUserInfoDTO.getFeature(bindMobileEndTimeKey));
            if(Instant.now().getEpochSecond()-lastTime<=0){
                throw new YdException(ErrorCodeConstants.YD10007.getErrorCode(),ErrorCodeConstants.YD10007.getErrorMessage());
            }
        }
        cpUserInfoDTO.addFeature(bindMobileKey,mobile);
        cpUserInfoDTO.addFeature(bindMobileCodeKey,codeStr);
        cpUserInfoDTO.addFeature(bindMobileEndTimeKey,bindMobileEndTime.toString());
        paypointCpuserInfo.setFeature(cpUserInfoDTO.getFeature());
        if(ydPaypointCpuserInfoMapper.updateByPrimaryKey(paypointCpuserInfo)>0){
            //在这里调用发送短信验证码接口
            smsMessageService.identitySmsMessage(mobile,codeStr);
            return true;
        }else {
            throw new YdException(ErrorCodeConstants.YDSPEXCEPTION_DAO.getErrorCode(),ErrorCodeConstants.YDSPEXCEPTION_DAO.getErrorMessage());
        }
    }


    @Override
    public boolean checkCPUserMobileCodeByUnionId(String unionId,String mobile, String code, String passwdord,CheckMobileCodeTypeEnum type) {
        YdPaypointCpuserInfo ydPaypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByUnionId(unionId);
        if(ydPaypointCpuserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"用户不存在！");
        }
        return this.checkCPUserMobileCode(ydPaypointCpuserInfo.getOpenid(),mobile,code,passwdord,type);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean checkCPUserMobileCode(String openId,String mobile, String code, String passwdord,CheckMobileCodeTypeEnum type) {
        if(DiamondYdSystemConfigHolder.getInstance().getTestSwitch()) {
            /**
             * 如果测试状态，直接使用万能码验证
             */
            if (StringUtil.equals(code, DiamondYdSystemConfigHolder.getInstance().getTestCode())) {
                return true;
            }

        }
        if(!PublicUtils.isMobile(mobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
        }
        String bindMobileKey = Constant.BIND_MOBILE;
        String bindMobileEndTimeKey = Constant.BIND_MOBILE_ENDTIME;
        String bindMobileCodeKey = Constant.BIND_MOBILE_CODE;
        if(type==CheckMobileCodeTypeEnum.BINDSHOPMOBILE){

            bindMobileKey = Constant.BIND_SHOP_MOBILE;
            bindMobileCodeKey = Constant.BIND_SHOP_MOBILE_CODE;
            bindMobileEndTimeKey = Constant.BIND_SHOP_MOBILE_ENDTIME;

        }else if(type==CheckMobileCodeTypeEnum.BINDOWNERMOBILE){

            bindMobileKey = Constant.BIND_OWNER_MOBILE;
            bindMobileCodeKey = Constant.BIND_OWNER_MOBILE_CODE;
            bindMobileEndTimeKey = Constant.BIND_OWNER_MOBILE_ENDTIME;

        }else if(type==CheckMobileCodeTypeEnum.BINDNEWOWNERMOBILE){

            bindMobileKey = Constant.BIND_OWNER_NEW_MOBILE;
            bindMobileCodeKey = Constant.BIND_OWNER_NEW_MOBILE_CODE;
            bindMobileEndTimeKey = Constant.BIND_OWNER_NEW_MOBILE_ENDTIME;

        }
        YdPaypointCpuserInfo paypointCpuserInfo;
        if(type!=CheckMobileCodeTypeEnum.BINDNEWOWNERMOBILE) {
            paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openId);
        }
        else {
            paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByMobileLockRow(mobile);
        }
        if(paypointCpuserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"用户不存在！");
        }
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo, CpUserInfoDTO.class);
        if(cpUserInfoDTO.getFeature(bindMobileEndTimeKey)==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"请先获取验证码!");
        }
        long bindMobileEndTime = Long.parseLong(cpUserInfoDTO.getFeature(bindMobileEndTimeKey));
        if(Instant.now().getEpochSecond()<=bindMobileEndTime) {
            if (StringUtil.equals(mobile, cpUserInfoDTO.getFeature(bindMobileKey))) {
                if (StringUtil.equals(code, cpUserInfoDTO.getFeature(bindMobileCodeKey))) {
                    cpUserInfoDTO.removeFeature(bindMobileEndTimeKey);
                    cpUserInfoDTO.removeFeature(bindMobileKey);
                    cpUserInfoDTO.removeFeature(bindMobileCodeKey);
                    paypointCpuserInfo.setFeature(cpUserInfoDTO.getFeature());
                    paypointCpuserInfo.setMobile(mobile);
                    if(StringUtil.isNotEmpty(passwdord)) {
                        String randomNum = String.valueOf(RandomUtil.getNotSimple(8));
                        paypointCpuserInfo.setOriginal(randomNum);
                        paypointCpuserInfo.setPassword(EncryptionUtil.getPassword(passwdord,randomNum));
                    }
                    if (ydPaypointCpuserInfoMapper.updateByPrimaryKey(paypointCpuserInfo) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean unBindCPUserMobile(String openid,String mobile) {
        if(StringUtil.isBlank(openid)){
            return false;
        }
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openid);
        if(paypointCpuserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"用户不存在！");
        }
        if(!StringUtil.equals(mobile,paypointCpuserInfo.getMobile())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
        }
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo,CpUserInfoDTO.class);
        List<String> shopsOwner = cpUserInfoDTO.getListFeature(Constant.CP_SHOPS_OWNER);
        if(shopsOwner!=null){
            if(shopsOwner.size()>0){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"不能解绑，店铺负责人请联系服务商处理！");
            }
        }
        paypointCpuserInfo.setMobile(null);
        if(ydPaypointCpuserInfoMapper.updateByPrimaryKey(paypointCpuserInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"解绑失败，请联系服务商处理！");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean generateShopMobileCode(String openId, String mobile) {
        if(!PublicUtils.isMobile(mobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
        }
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openId);
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo, CpUserInfoDTO.class);
        Integer code = RandomUtil.getNotSimple(4);
        Long bindMobileEndTime = Instant.now().getEpochSecond()+900;
        cpUserInfoDTO.addFeature(Constant.BIND_SHOP_MOBILE,mobile);
        cpUserInfoDTO.addFeature(Constant.BIND_SHOP_MOBILE_CODE,code.toString());
        cpUserInfoDTO.addFeature(Constant.BIND_SHOP_MOBILE_ENDTIME,bindMobileEndTime.toString());
        paypointCpuserInfo.setFeature(cpUserInfoDTO.getFeature());
        if(ydPaypointCpuserInfoMapper.updateByPrimaryKey(paypointCpuserInfo)>0){
            //在这里调用发送短信验证码接口
            smsMessageService.identitySmsMessage(mobile,Integer.toString(code));
            return true;
        }else {
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean checkShopMobileCode(String openId, String mobile, String code) {
        if(!PublicUtils.isMobile(mobile)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"手机号不正确！");
        }
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openId);
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo, CpUserInfoDTO.class);
        long bindMobileEndTime = Long.parseLong(cpUserInfoDTO.getFeature(Constant.BIND_SHOP_MOBILE_ENDTIME));
        if(Instant.now().getEpochSecond()<=bindMobileEndTime) {
            if (StringUtil.equals(mobile, cpUserInfoDTO.getFeature(Constant.BIND_SHOP_MOBILE))) {
                if (StringUtil.equals(code, cpUserInfoDTO.getFeature(Constant.BIND_SHOP_MOBILE_CODE))) {
                    cpUserInfoDTO.removeFeature(Constant.BIND_SHOP_MOBILE_ENDTIME);
                    cpUserInfoDTO.removeFeature(Constant.BIND_SHOP_MOBILE);
                    cpUserInfoDTO.removeFeature(Constant.BIND_SHOP_MOBILE_CODE);
                    paypointCpuserInfo.setFeature(cpUserInfoDTO.getFeature());
                    if (ydPaypointCpuserInfoMapper.updateByPrimaryKey(paypointCpuserInfo) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateCPUserToken(String openid,String accessToken,String refreshToken) {
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openid);
        paypointCpuserInfo.setWeixinAccessToken(accessToken);
        paypointCpuserInfo.setWeixinRefreshToken(refreshToken);
        return ydPaypointCpuserInfoMapper.updateByPrimaryKeySelective(paypointCpuserInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateCPUserInfo(String openid,boolean isSns) {
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openid);
        if(paypointCpuserInfo==null){
            return false;
        }
        String accessToken = null;
        if(isSns){
            accessToken = paypointCpuserInfo.getWeixinAccessToken();
        }else {
            accessToken = weixinOauth2Service2b.getAccessToken();
        }
        Result<WeixinUserInfo> result = weixinOauth2Service2b.getWeiXinUserInfo(openid,accessToken,isSns);
        if(result==null){
            return false;
        }
        logger.info("weixinOauth2Service2b.getWeiXinUserInfo result is : "+JSON.toJSONString(result));
        if(!result.isSuccess()){
            return false;
        }
        WeixinUserInfo weixinUserInfo = result.getResult();

        paypointCpuserInfo.setCity(weixinUserInfo.getCity());
        paypointCpuserInfo.setCountry(weixinUserInfo.getCountry());
        paypointCpuserInfo.setProvince(weixinUserInfo.getProvince());
        paypointCpuserInfo.setHeadImgType(WeixinHeadImgTypeEnum.WEIXINURL.getType());
        paypointCpuserInfo.setHeadImgUrl(weixinUserInfo.getHeadimgurl());
        paypointCpuserInfo.setNick(weixinUserInfo.getNickname());
        paypointCpuserInfo.setSex(weixinUserInfo.getSex());
        paypointCpuserInfo.setUnionid(weixinUserInfo.getUnionid());
        ydPaypointCpuserInfoMapper.updateByPrimaryKey(paypointCpuserInfo);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setCPDefaultShop(String openid, String shopid) {
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openid);
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo,CpUserInfoDTO.class);
        cpUserInfoDTO.addFeature(Constant.DEFAULT_SHOP,shopid);
        paypointCpuserInfo.setFeature(cpUserInfoDTO.getFeature());
        ydPaypointCpuserInfoMapper.updateByPrimaryKey(paypointCpuserInfo);
        return true;
    }

    @Override
    public List<String> getShopsByOwner(String openid) {
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenid(openid);
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo,CpUserInfoDTO.class);
        return cpUserInfoDTO.getListFeature(Constant.CP_SHOPS_OWNER);
    }

    @Override
    public List<String> getShopsByManager(String openid) {
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenid(openid);
        if(paypointCpuserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"用户不存在!");
        }
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo,CpUserInfoDTO.class);
        return cpUserInfoDTO.getListFeature(Constant.CP_SHOPS_MANAGER);
    }

    @Override
    public List<String> getShopsByWaiter(String openid) {
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenid(openid);
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo,CpUserInfoDTO.class);
        return cpUserInfoDTO.getListFeature(Constant.CP_SHOPS_WAITER);
    }

    @Override
    public String getDefaultShop(String openid) {
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenid(openid);
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo,CpUserInfoDTO.class);
        return cpUserInfoDTO.getFeature(Constant.DEFAULT_SHOP);
    }

    @Override
    public boolean checkIsOwner(String opendid, String shopid) {
        if(StringUtil.isBlank(shopid)){
            return false;
        }
        List<String> shops = this.getShopsByOwner(opendid);
        if(shops==null){
            return false;
        }
        return shops.contains(shopid);
    }

    @Override
    public boolean checkIsManager(String opendid, String shopid) {
        if(StringUtil.isBlank(shopid)){
            return false;
        }
        List<String> shops = this.getShopsByManager(opendid);
        if(shops==null){
            return false;
        }
        return shops.contains(shopid);
    }

    @Override
    public boolean checkIsWaiter(String opendid, String shopid) {
        if(StringUtil.isBlank(shopid)){
            return false;
        }
        List<String> shops = this.getShopsByWaiter(opendid);
        if(shops==null){
            return false;
        }
        return shops.contains(shopid);
    }

    @Override
    public String getWeiXinAppIdForCP() {
        return weixinOauth2Service2b.getAppid();
    }

    @Override
    public String getWeiXinAppIdFor2C() {
        return weixinOauth2Service2c.getAppid();
    }

    @Override
    public List<String> getCpAddressIdList(String openid) {
        YdPaypointCpuserInfo cpuserInfo = this.select2BByOpenId(openid);
        if(cpuserInfo==null){
            return null;
        }
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfo,CpUserInfoDTO.class);

        return cpUserInfoDTO.getListFeature(Constant.CPADDRESS);
    }

    @Override
    public List<String> get2cAddressIdList(String openid) {
        YdConsumerInfo consumerInfo = this.select2CByOpenId(openid);
        if(consumerInfo==null){
            return null;
        }
        ConsumerInfoDTO consumerInfoDTO = doMapper.map(consumerInfo,ConsumerInfoDTO.class);

        return consumerInfoDTO.getListFeature(Constant.CONSUMERADDRESS);
    }

    @Override
    public Object getCpUserInfo(String openid, String shopid) {
        if(StringUtil.isBlank(openid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenid(openid);
        if(cpuserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "用户不存在！");
        }
        UserInfoVO userInfoVO = doMapper.map(cpuserInfo,UserInfoVO.class);
        userInfoVO.setUserType(UserTypeEnum.GUEST.getType());
        if(StringUtil.isBlank(shopid)) {
            return userInfoVO;
        }
        if(this.checkIsOwner(openid,shopid)){
            userInfoVO.setUserType(UserTypeEnum.SHOPOWNER.getType());
            YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
            if(shopInfo==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "店铺不存在！");
            }
            ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
            OwnerUserInfoVO ownerUserInfoVO = doMapper.map(userInfoVO,OwnerUserInfoVO.class);
            ownerUserInfoVO.setIdentityNumber(shopInfoDTO.getFeature(Constant.IDENTITY_NUMBER));
            return ownerUserInfoVO;
        }else if(this.checkIsManager(openid,shopid)){
            userInfoVO.setUserType(UserTypeEnum.SHOPMANAGER.getType());
        }else if(this.checkIsWaiter(openid,shopid)){
            userInfoVO.setUserType(UserTypeEnum.SHOPWAITER.getType());
        }
        return userInfoVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addShopWorker(String openid, WorkerInfoVO workerInfo) {
        if(workerInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "参数不能为空！");
        }
        if(StringUtil.isBlank(workerInfo.getShopid())||StringUtil.isBlank(workerInfo.getWorkerMobile())||
                StringUtil.isBlank(workerInfo.getWorkerName())||workerInfo.getWorkerType()==null)
        {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "参数不能为空！");
        }
        UserTypeEnum userTypeEnum = UserTypeEnum.nameOf(workerInfo.getWorkerType());
        if(!(userTypeEnum==UserTypeEnum.SHOPMANAGER||userTypeEnum==UserTypeEnum.SHOPWAITER)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "用户类型不正确！");
        }
        if(!this.checkIsManager(openid,workerInfo.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdPaypointShopworker shopworker = ydPaypointShopworkerMapper.selectByShopidLockRow(workerInfo.getShopid());
        if(shopworker==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "系统数据错误！");
        }
        YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByMobile(workerInfo.getWorkerMobile());
        if(cpuserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "手机号没有与公众号绑定，不能增加!");
        }
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfo,CpUserInfoDTO.class);
        if(UserTypeEnum.SHOPMANAGER==userTypeEnum){
            String managerMobile = shopworker.getManagerMobile();
            if(StringUtil.isBlank(managerMobile)){
                managerMobile = "";
            }
            List<String> managerList = FeatureUtil.strToList(managerMobile);
            if(managerList.contains(workerInfo.getWorkerMobile().trim())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "请不要重复添加!");
            }
            managerList.add(workerInfo.getWorkerMobile().trim());
            shopworker.setManagerMobile(FeatureUtil.listToString(managerList));
            cpuserInfo.setNick(workerInfo.getWorkerName());
            cpUserInfoDTO.addListFeature(Constant.CP_SHOPS_MANAGER,workerInfo.getShopid());
            cpuserInfo.setFeature(cpUserInfoDTO.getFeature());

        } else if(UserTypeEnum.SHOPWAITER==userTypeEnum){

            String waiterMobile = shopworker.getWaiterMobile();
            if(StringUtil.isBlank(waiterMobile)){
                waiterMobile = "";
            }
            List<String> waiterList = FeatureUtil.strToList(waiterMobile);
            if(waiterList.contains(workerInfo.getWorkerMobile().trim())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "请不要重复添加!");
            }
            waiterList.add(workerInfo.getWorkerMobile().trim());
            shopworker.setWaiterMobile(FeatureUtil.listToString(waiterList));
            cpuserInfo.setNick(workerInfo.getWorkerName());
            cpUserInfoDTO.addListFeature(Constant.CP_SHOPS_WAITER,workerInfo.getShopid());
            cpuserInfo.setFeature(cpUserInfoDTO.getFeature());

        }

        if(ydPaypointCpuserInfoMapper.updateByPrimaryKeySelective(cpuserInfo)>0){
            if(ydPaypointShopworkerMapper.updateByPrimaryKeySelective(shopworker)<=0){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "信息修改失败!");
            }

        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "信息修改失败!");
        }

        return true;
    }

    @Override
    public boolean delShopWorker(String openid, WorkerInfoVO workerInfo) {
        if(workerInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "参数不能为空！");
        }
        if(StringUtil.isBlank(workerInfo.getShopid())||StringUtil.isBlank(workerInfo.getWorkerMobile())||
                StringUtil.isBlank(workerInfo.getWorkerName())||workerInfo.getWorkerType()==null)
        {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "参数不能为空！");
        }
        UserTypeEnum userTypeEnum = UserTypeEnum.nameOf(workerInfo.getWorkerType());
        if(!(userTypeEnum==UserTypeEnum.SHOPMANAGER||userTypeEnum==UserTypeEnum.SHOPWAITER)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "用户类型不正确！");
        }
        if(!this.checkIsManager(openid,workerInfo.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdPaypointShopworker shopworker = ydPaypointShopworkerMapper.selectByShopidLockRow(workerInfo.getShopid());
        if(shopworker==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "系统数据错误！");
        }

        YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByMobile(workerInfo.getWorkerMobile());
        if(cpuserInfo!=null){
            if(this.checkIsOwner(cpuserInfo.getOpenid(),workerInfo.getShopid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "此管理员为负责人，不能删除！");
            }
        }

        if(UserTypeEnum.SHOPMANAGER==userTypeEnum){
            String managerMobile = shopworker.getManagerMobile();
            if(StringUtil.isBlank(managerMobile)){
                managerMobile = "";
            }
            List<String> managerList = FeatureUtil.strToList(managerMobile);
            if(!managerList.contains(workerInfo.getWorkerMobile().trim())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "不存在此管理员，不需要删除!");
            }
            managerList.remove(workerInfo.getWorkerMobile().trim());
            shopworker.setManagerMobile(FeatureUtil.listToString(managerList));
            CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfo,CpUserInfoDTO.class);
            cpUserInfoDTO.delListFeature(Constant.CP_SHOPS_MANAGER,workerInfo.getShopid());
            if(StringUtil.equals(workerInfo.getShopid(),cpUserInfoDTO.getFeature(Constant.DEFAULT_SHOP))){
                cpUserInfoDTO.removeFeature(Constant.DEFAULT_SHOP);
            }
            cpuserInfo.setFeature(cpUserInfoDTO.getFeature());


        }else if(UserTypeEnum.SHOPWAITER==userTypeEnum){

            String waiterMobile = shopworker.getWaiterMobile();
            if(StringUtil.isBlank(waiterMobile)){
                waiterMobile = "";
            }
            List<String> waiterList = FeatureUtil.strToList(waiterMobile);
            if(!waiterList.contains(workerInfo.getWorkerMobile().trim())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "不存在此服务员，不需要删除!");
            }
            waiterList.remove(workerInfo.getWorkerMobile().trim());
            shopworker.setWaiterMobile(FeatureUtil.listToString(waiterList));
            CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfo,CpUserInfoDTO.class);
            cpUserInfoDTO.delListFeature(Constant.CP_SHOPS_WAITER,workerInfo.getShopid());
            if(StringUtil.equals(workerInfo.getShopid(),cpUserInfoDTO.getFeature(Constant.DEFAULT_SHOP))){
                cpUserInfoDTO.removeFeature(Constant.DEFAULT_SHOP);
            }
            cpuserInfo.setFeature(cpUserInfoDTO.getFeature());

        }
        if(ydPaypointCpuserInfoMapper.updateByPrimaryKeySelective(cpuserInfo)>0) {
            if (ydPaypointShopworkerMapper.updateByPrimaryKeySelective(shopworker) <= 0) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "删除失败!");
            }
        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "删除失败!");
        }

        return true;
    }

    @Override
    public Map<String,List<WorkerInfoVO>> getShopWorker(String openid, String shopid) {
        Map<String,List<WorkerInfoVO>> result = new HashMap<>();
        List<WorkerInfoVO> waiterInfoList = new ArrayList<>();
        List<WorkerInfoVO> managerInfoList = new ArrayList<>();
        List<WorkerInfoVO> ownerInfoList = new ArrayList<>();
        YdPaypointShopworker shopworker = ydPaypointShopworkerMapper.selectByShopid(shopid);
        if(shopworker==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "店铺不存在！");
        }

        /**
         * 构造服务员信息数据
         */
        String waiterMobile = shopworker.getWaiterMobile();
        if(StringUtil.isBlank(waiterMobile)){
            waiterMobile = "";
        }
        List<String> waiters = FeatureUtil.strToList(waiterMobile);
        for(String mobile:waiters) {
            YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByMobile(mobile);
            if(cpuserInfo==null){
                continue;
            }
            WorkerInfoVO waiterInfoVO = new WorkerInfoVO();
            waiterInfoVO.setShopid(shopid);
            waiterInfoVO.setWorkerMobile(mobile);
            waiterInfoVO.setShowMobile(StringUtil.mobleReplace(mobile));
            waiterInfoVO.setWorkerName(cpuserInfo.getNick());
            waiterInfoVO.setWorkerType(UserTypeEnum.SHOPWAITER.getType());
            waiterInfoList.add(waiterInfoVO);
        }

        /**
         * 按权限设置显示数据
         */

        if(this.checkIsWaiter(openid,shopid)){
            result.put(UserTypeEnum.SHOPWAITER.getName(),waiterInfoList);

        }
        if(this.checkIsManager(openid,shopid)){
            /**
             * 构造管理员数据
             */

            String managerMobile = shopworker.getManagerMobile();
            if(StringUtil.isBlank(managerMobile)){
                managerMobile = "";
            }
            List<String> managers = FeatureUtil.strToList(managerMobile);
            for(String mobile:managers) {
                YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByMobile(mobile);
                if(cpuserInfo==null){
                    continue;
                }
                WorkerInfoVO managerInfoVO = new WorkerInfoVO();
                managerInfoVO.setShopid(shopid);
                managerInfoVO.setWorkerMobile(mobile);
                managerInfoVO.setShowMobile(StringUtil.mobleReplace(mobile));
                managerInfoVO.setWorkerName(cpuserInfo.getNick());
                managerInfoVO.setWorkerType(UserTypeEnum.SHOPMANAGER.getType());
                managerInfoList.add(managerInfoVO);
            }


        }
        if(this.checkIsOwner(openid,shopid)){

            String ownerMobile = shopworker.getOwnerMobile();
            YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
            if(ydPaypointShopInfo==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "店铺不存在！");
            }
            YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByMobile(ownerMobile);
            if(cpuserInfo!=null){
                WorkerInfoVO ownerInfoVO = new WorkerInfoVO();
                ownerInfoVO.setShopid(shopid);
                ownerInfoVO.setWorkerMobile(ownerMobile);
                ownerInfoVO.setShowMobile(ownerMobile);
                ownerInfoVO.setWorkerName(ydPaypointShopInfo.getCorporation());
                ownerInfoVO.setWorkerType(UserTypeEnum.SHOPOWNER.getType());
                ownerInfoList.add(ownerInfoVO);
            }

        }

        result.put(UserTypeEnum.SHOPWAITER.getName(),waiterInfoList);
        result.put(UserTypeEnum.SHOPMANAGER.getName(),managerInfoList);
        result.put(UserTypeEnum.SHOPOWNER.getName(),ownerInfoList);

        return result;
    }

    @Override
    public Map<String,List<WorkerInfoVO>> getShopWorker(String openid, String shopid, SourceEnum source) {

        if (source==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"来源不能为空");
        }

        if (!(source.equals(SourceEnum.WEIXINAGENT)||source.equals(SourceEnum.WEIXINXIAOER))){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"来源错误");
        }

        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }

        YdManageUserWhiteList ydManageUserWhiteList = ydManageUserWhiteListMapper.selectByMobile(ydManageUserInfo.getMobile());
        if (ydManageUserWhiteList==null){
            YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopid);
            if(ydPaypointShopInfoExt==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"店铺不存在！");
            }
            if (!StringUtil.equals(cpAgentService.getDefualtAgent(openid),ydPaypointShopInfoExt.getAgentid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"店铺不在代理商下！");
            }
        }

        Map<String,List<WorkerInfoVO>> result = new HashMap<>();
        List<WorkerInfoVO> waiterInfoList = new ArrayList<>();
        List<WorkerInfoVO> managerInfoList = new ArrayList<>();
        List<WorkerInfoVO> ownerInfoList = new ArrayList<>();
        YdPaypointShopworker shopworker = ydPaypointShopworkerMapper.selectByShopid(shopid);

        if(shopworker==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "店铺不存在！");
        }

        /**
         * 构造服务员信息数据
         */
        String waiterMobile = shopworker.getWaiterMobile();
        if(StringUtil.isBlank(waiterMobile)){
            waiterMobile = "";
        }
        List<String> waiters = FeatureUtil.strToList(waiterMobile);
        for(String mobile:waiters) {
            YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByMobile(mobile);
            if(cpuserInfo==null){
                continue;
            }
            WorkerInfoVO waiterInfoVO = new WorkerInfoVO();
            waiterInfoVO.setShopid(shopid);
            waiterInfoVO.setWorkerMobile(mobile);
            waiterInfoVO.setShowMobile(StringUtil.mobleReplace(mobile));
            waiterInfoVO.setWorkerName(cpuserInfo.getNick());
            waiterInfoVO.setWorkerType(UserTypeEnum.SHOPWAITER.getType());
            waiterInfoList.add(waiterInfoVO);
        }

        /**
         * 构造管理员数据
         */

        String managerMobile = shopworker.getManagerMobile();
        if(StringUtil.isBlank(managerMobile)){
            managerMobile = "";
        }
        List<String> managers = FeatureUtil.strToList(managerMobile);
        for(String mobile:managers) {
            YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByMobile(mobile);
            if(cpuserInfo==null){
                continue;
            }
            WorkerInfoVO managerInfoVO = new WorkerInfoVO();
            managerInfoVO.setShopid(shopid);
            managerInfoVO.setWorkerMobile(mobile);
            managerInfoVO.setShowMobile(StringUtil.mobleReplace(mobile));
            managerInfoVO.setWorkerName(cpuserInfo.getNick());
            managerInfoVO.setWorkerType(UserTypeEnum.SHOPMANAGER.getType());
            managerInfoList.add(managerInfoVO);
        }

        String ownerMobile = shopworker.getOwnerMobile();
        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByMobile(ownerMobile);
        if(cpuserInfo!=null){
            WorkerInfoVO ownerInfoVO = new WorkerInfoVO();
            ownerInfoVO.setShopid(shopid);
            ownerInfoVO.setWorkerMobile(ownerMobile);
            ownerInfoVO.setWorkerName(ydPaypointShopInfo.getCorporation());
            ownerInfoVO.setWorkerType(UserTypeEnum.SHOPOWNER.getType());
            ownerInfoList.add(ownerInfoVO);
        }

        result.put(UserTypeEnum.SHOPWAITER.getName(),waiterInfoList);
        result.put(UserTypeEnum.SHOPMANAGER.getName(),managerInfoList);
        result.put(UserTypeEnum.SHOPOWNER.getName(),ownerInfoList);

        return result;
    }


    @Override
    public boolean modifyShopOwnerWithStep1(String openid, String shopid, String mobile, String checkCode) throws IOException {
        if(!this.checkIsOwner(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        boolean isok = this.checkCPUserMobileCode(openid,mobile,checkCode,null,CheckMobileCodeTypeEnum.BINDOWNERMOBILE);
        if(isok) {
            /**
             * 24小时有效
             */
            redisManager.set(SerializeUtils.serialize(shopid + "ownermodifyinfo"), SerializeUtils.serialize(isok),86400);
        }

        return isok;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean modifyShopOwnerWithStep2(String openid, OwnerInfoVO ownerInfoVO) throws IOException {

        if(ownerInfoVO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "参数不完整！");
        }
        if(StringUtil.isBlank(ownerInfoVO.getShopid())||StringUtil.isBlank(ownerInfoVO.getCheckCode())||
                StringUtil.isBlank(ownerInfoVO.getIdentityNumber())||StringUtil.isBlank(ownerInfoVO.getWorkerMobile())||
                StringUtil.isBlank(ownerInfoVO.getWorkerName())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "参数不完整！");
        }
        ownerInfoVO.setWorkerType(UserTypeEnum.SHOPOWNER.getType());
        if(!this.checkIsOwner(openid,ownerInfoVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        /**
         * 先看一下是否成功经过了第一步的验证
         */
        byte[] timeBytes = redisManager.get(SerializeUtils.serialize(ownerInfoVO.getShopid()+"ownermodifyinfo"));
        if(SerializeUtils.isEmpty(timeBytes)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), "请原负责人先进行身份验证！");
        }

        YdPaypointCpuserInfo paypointCpuserInfoNew = ydPaypointCpuserInfoMapper.selectByMobile(ownerInfoVO.getWorkerMobile());

        /**
         * 验证手机验证码
         */
        if(!this.checkCPUserMobileCode(openid,ownerInfoVO.getWorkerMobile(),ownerInfoVO.getCheckCode(),null,CheckMobileCodeTypeEnum.BINDNEWOWNERMOBILE)){
            throw new YdException(ErrorCodeConstants.YD10006.getErrorCode(), ErrorCodeConstants.YD10006.getErrorMessage());
        }

        /**
         * 查的新手机用户是否是公众号绑定手机的用户
         */
        YdPaypointCpuserInfo paypointCpuserInfoOld = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openid);

        if(paypointCpuserInfoNew==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "此手机用户没有绑定公众号，请先绑定！");
        }

        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(ownerInfoVO.getShopid());
        if(ydPaypointShopInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺不存在！");
        }


        /**
         * 重新更新店铺负责人信息
         */
        ShopInfoDTO shopInfoDTO = doMapper.map(ydPaypointShopInfo,ShopInfoDTO.class);
        shopInfoDTO.removeFeature(Constant.IDENTITY_NUMBER);
        shopInfoDTO.addFeature(Constant.IDENTITY_NUMBER,ownerInfoVO.getIdentityNumber());
        ydPaypointShopInfo.setFeature(shopInfoDTO.getFeature());
        ydPaypointShopInfo.setMobile(ownerInfoVO.getWorkerMobile());
        ydPaypointShopInfo.setContact(ownerInfoVO.getWorkerName());
        ydPaypointShopInfo.setCorporation(ownerInfoVO.getWorkerName());

        if(ydPaypointShopInfoMapper.updateByPrimaryKeySelective(ydPaypointShopInfo)<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "负责人信息更新失败！");
        }

        if(paypointCpuserInfoOld.getId().longValue()!=paypointCpuserInfoNew.getId().longValue()) {
            /**
             * 不是同一个人，是要进行负责人转移
             */
            CpUserInfoDTO cpUserInfoOld = doMapper.map(paypointCpuserInfoOld, CpUserInfoDTO.class);
            cpUserInfoOld.delListFeature(Constant.CP_SHOPS_OWNER, ownerInfoVO.getShopid());
            cpUserInfoOld.delListFeature(Constant.CP_SHOPS_MANAGER, ownerInfoVO.getShopid());
            cpUserInfoOld.removeFeature(Constant.DEFAULT_SHOP);
            paypointCpuserInfoOld.setFeature(cpUserInfoOld.getFeature());

            if (ydPaypointCpuserInfoMapper.updateByPrimaryKeySelective(paypointCpuserInfoOld) <= 0) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "负责人信息更新失败！");
            }

            paypointCpuserInfoNew = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(paypointCpuserInfoNew.getOpenid());
            CpUserInfoDTO cpUserInfoDTONew = doMapper.map(paypointCpuserInfoNew, CpUserInfoDTO.class);
            cpUserInfoDTONew.addFeature(Constant.DEFAULT_SHOP,ownerInfoVO.getShopid());
            cpUserInfoDTONew.addListFeature(Constant.CP_SHOPS_OWNER, ownerInfoVO.getShopid());
            cpUserInfoDTONew.addListFeature(Constant.CP_SHOPS_MANAGER, ownerInfoVO.getShopid());
            paypointCpuserInfoNew.setFeature(cpUserInfoDTONew.getFeature());
            if (ydPaypointCpuserInfoMapper.updateByPrimaryKeySelective(paypointCpuserInfoNew) <= 0) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "负责人信息更新失败！");
            }


            /**
             * 更新店铺角色（员工）关系管理表
             */
            YdPaypointShopworker shopworker = ydPaypointShopworkerMapper.selectByShopidLockRow(ownerInfoVO.getShopid());
            if (shopworker == null) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺角色关系管理信息不存在！");
            }

            String managerMobile = shopworker.getManagerMobile();
            if (StringUtil.isBlank(managerMobile)) {
                managerMobile = "";
            }
            List<String> managerList = FeatureUtil.strToList(managerMobile);
            if (managerList.contains(paypointCpuserInfoOld.getMobile())) {
                managerList.remove(paypointCpuserInfoOld.getMobile());

            }
            managerList.add(ownerInfoVO.getWorkerMobile());
            shopworker.setManagerMobile(FeatureUtil.listToString(managerList));
            shopworker.setOwnerMobile(ownerInfoVO.getWorkerMobile());

            if (ydPaypointShopworkerMapper.updateByPrimaryKeySelective(shopworker) <= 0) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "店铺角色关系重建失败!");
            }

            /**
             * 新负责人签约
             */

            this.newModifyOwnerContract(paypointCpuserInfoNew.getOpenid(), ownerInfoVO.getShopid(),
                    ownerInfoVO.getWorkerMobile(), ownerInfoVO.getWorkerName(), ownerInfoVO.getIdentityNumber());

        }

        if(!SerializeUtils.isEmpty(timeBytes)){
            redisManager.del(SerializeUtils.serialize(ownerInfoVO.getShopid()+"ownermodifyinfo"));
        }

        return true;
    }

    @Override
    public boolean youCanModifyOwnerInfo(String openid, String shopid) throws IOException {
        if(!this.checkIsOwner(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        /**
         * 看一下是否成功经过了第一步的验证
         */
        byte[] timeBytes = redisManager.get(SerializeUtils.serialize(shopid+"ownermodifyinfo"));
        if(SerializeUtils.isEmpty(timeBytes)){
            return false;
        }
        return true;
    }

    @Override
    public YdWeixinUserInfo queryUserInfo2C(String unionid) {
        return ydWeixinUserInfoMapper.selectByUnionid(unionid);
    }

    @Async
    protected void newModifyOwnerContract(String newOwnerOpenid,String shopid,String mobile,String corporation,String identityNumber){
        String contractid = "298820180130002";
        /**
         * 开始签约流程
         */
        YdPaypointContractinfo contractinfo = ydPaypointContractinfoMapper.selectByContractId(contractid);
        if(contractinfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "合约信息不存在！");
        }
        YdPaypointUserContract userContract = ydPaypointUserContractMapper.selectByOpenidAndContractIdAndShopid(newOwnerOpenid,contractid,shopid);
        if(userContract==null){
            userContract = new YdPaypointUserContract();
            userContract.setContractId(contractid);
            userContract.setShopid(shopid);
            userContract.setUserName(corporation);
            userContract.setIdentityNumber(identityNumber);
            userContract.setMobile(mobile);
            userContract.setOpenid(newOwnerOpenid);
            userContract.setContractName(contractinfo.getContractName());
            userContract.setContractScene(contractinfo.getContractScene());
            userContract.setContractUrl(contractinfo.getContractUrl());
            if(ydPaypointUserContractMapper.insert(userContract)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "签约失败请检查数据！");
            }

        }else {

            userContract.setContractId(contractid);
            userContract.setShopid(shopid);
            userContract.setUserName(corporation);
            userContract.setIdentityNumber(identityNumber);
            userContract.setMobile(mobile);
            userContract.setOpenid(newOwnerOpenid);
            userContract.setContractName(contractinfo.getContractName());
            userContract.setContractScene(contractinfo.getContractScene());
            userContract.setContractUrl(contractinfo.getContractUrl());
            if(ydPaypointUserContractMapper.updateByPrimaryKeySelective(userContract)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "签约失败请检查数据！");
            }
        }

    }

}
