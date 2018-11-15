package com.yd.ydsp.biz.weixin.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.weixin.WeixinOauth2ShopService;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.enums.paypoint.WeiXinTypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.common.utils.HttpUtil;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.common.utils.UUIDGenerator;
import com.yd.ydsp.common.weixin.mp.AesException;
import com.yd.ydsp.common.weixin.mp.WXBizMsgCrypt;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/5/16.
 */
public class WeixinOauth2ShopServiceImpl implements WeixinOauth2ShopService {

    public static final Logger logger = LoggerFactory.getLogger(WeixinOauth2ShopServiceImpl.class);

    private boolean cacheOn=true;

    public String weixinOpenToken;
    public String weixinOpenAppId;
    public String weixinOpenSecret;
    public String weixinOpenAesKey;//第三方平台申请时的接收消息的加密symmetric_key（也称为EncodingAESKey）

    private WXBizMsgCrypt wxBizMsgCrypt;

    @Resource
    private RedisManager redisManager;
    @Resource
    private YdWeixinServiceConfigMapper ydWeixinServiceConfigMapper;
    @Resource
    private YdWeixinUserInfoMapper ydWeixinUserInfoMapper;
    @Resource
    YdPaypointShopInfoExtMapper ydPaypointShopInfoExtMapper;
    @Resource
    private YdShopApplyInfoMapper ydShopApplyInfoMapper;
    @Resource
    private MqMessageService mqMessageService;



    public void setCacheOn(boolean cacheOn){ this.cacheOn = cacheOn; }

    @Override
    public String getWeixinOpenAppId() {
        return weixinOpenAppId;
    }

    public void setWeixinOpenAppId(String weixinOpenAppId) {
        this.weixinOpenAppId = weixinOpenAppId;
    }

    public void setWeixinOpenSecret(String weixinOpenSecret) {
        this.weixinOpenSecret = weixinOpenSecret;
    }

    public void setWeixinOpenToken(String weixinOpenToken) {
        this.weixinOpenToken = weixinOpenToken;
    }

    public void setWeixinOpenAesKey(String weixinOpenAesKey) {
        this.weixinOpenAesKey = weixinOpenAesKey;
    }

    public void init() {

        try {
            this.wxBizMsgCrypt = new WXBizMsgCrypt(this.weixinOpenToken, this.weixinOpenAesKey, this.weixinOpenAppId);
        }catch (Exception e){
            logger.error("init WXBizMsgCrypt error:",e);
        }

    }

    @Override
    public String getAccessToken(String appid) throws Exception {
        Integer curTime = DateUtils.getSecondTimestamp(new Date());

        YdWeixinServiceConfig ydWeixinServiceConfig = ydWeixinServiceConfigMapper.selectByAppid(appid);
        if(ydWeixinServiceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "请管理员先进行授权！");
        }
        Integer expIn = DateUtils.getSecondTimestamp(ydWeixinServiceConfig.getExpiresInDate());
        if(curTime<expIn){
            return ydWeixinServiceConfig.getAccessToken();
        }
        /**
         * 过期，需要刷新公众号或者小程序的token
         */
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("component_appid",this.weixinOpenAppId);
        requestBody.put("authorizer_appid",ydWeixinServiceConfig.getAppid());
        requestBody.put("authorizer_refresh_token",ydWeixinServiceConfig.getAuthorizerRefreshToken());
        String url = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token="+this.getComponentAccessToken();
        String jsonStr = HttpUtil.postJson(url, JSON.toJSONString(requestBody));
        Map<String,Object> resultWeiXin = JSON.parseObject(jsonStr, Map.class);
        String accessToken = (String)resultWeiXin.get("authorizer_access_token");
        String refreshToken = (String)resultWeiXin.get("authorizer_refresh_token");
        Integer expiresIn = (Integer) resultWeiXin.get("expires_in");
        ydWeixinServiceConfig.setAccessToken(accessToken);
        ydWeixinServiceConfig.setAuthorizerRefreshToken(refreshToken);
        ydWeixinServiceConfig.setExpiresInDate(DateUtils.getDateBySecondTimestamp(DateUtils.getSecondTimestamp(new Date())+expiresIn-120));
        ydWeixinServiceConfigMapper.updateByPrimaryKeySelective(ydWeixinServiceConfig);
        return accessToken;
    }

    @Override
    public Map<String,Object> authorize(String appid,String code) {
        Map<String,Object> result = new HashMap<>();
        result.put("success",false);
        YdWeixinUserInfo ydWeixinUserInfo = null;
        try {
            Integer curTime = DateUtils.getSecondTimestamp(new Date());

            YdWeixinServiceConfig ydWeixinServiceConfig = ydWeixinServiceConfigMapper.selectByAppid(appid);
            if(ydWeixinServiceConfig==null){
                return null;
            }

            /**
             * 从腾讯取accessToken
             */

            String url = MessageFormat.format("https://api.weixin.qq.com/sns/oauth2/component/access_token?appid={0}&code={1}&grant_type=authorization_code&component_appid={2}&component_access_token={3}",
                    appid,code, this.weixinOpenAppId,this.getComponentAccessToken());
            String jsonStr = HttpUtil.post(url, null);
            Map<String,Object> resultWeiXin = JSON.parseObject(jsonStr, Map.class);
            String accessToken = (String)resultWeiXin.get("access_token");
            String refreshToken = (String)resultWeiXin.get("refresh_token");
            Integer expiresIn = (Integer) resultWeiXin.get("expires_in");
            String openid = (String)resultWeiXin.get("openid");
            Date expiresDate = DateUtils.getDateBySecondTimestamp(curTime+expiresIn-300);
            ydWeixinUserInfo = ydWeixinUserInfoMapper.selectByOpenid(ydWeixinServiceConfig.getWeixinConfigId(),openid);
            boolean isNew = false;
            if(ydWeixinUserInfo==null){
                isNew = true;
                ydWeixinUserInfo = new YdWeixinUserInfo();
                ydWeixinUserInfo.setOpenid(openid);
                ydWeixinUserInfo.setWeixinConfigId(ydWeixinServiceConfig.getWeixinConfigId());
            }
            ydWeixinUserInfo.setWeixinRefreshToken(refreshToken);
            ydWeixinUserInfo.setWeixinAccessToken(accessToken);
            ydWeixinUserInfo.setWeixinTokenExpireIn(expiresDate);
            /**
             * 更新用户数据
             */
            url = MessageFormat.format("https://api.weixin.qq.com/sns/userinfo?access_token={0}&openid={1}&lang=zh_CN",
                    accessToken,openid);
            jsonStr = HttpUtil.get(url);
            resultWeiXin = JSON.parseObject(jsonStr, Map.class);
            String headimgurl = (String)resultWeiXin.get("headimgurl");
            String nickname = (String)resultWeiXin.get("nickname");
            String unionid = null;
            if(resultWeiXin.containsKey("unionid")){
                unionid = (String)resultWeiXin.get("unionid");
            }
            Integer sex = new Integer((String)resultWeiXin.get("sex"));
            String country = null;
            if(resultWeiXin.containsKey("country")){
                country = (String)resultWeiXin.get("country");
            }
            String city = null;
            if(resultWeiXin.containsKey("city")){
                city = (String)resultWeiXin.get("city");
            }
            String province = null;
            if(resultWeiXin.containsKey("province")){
                province = (String)resultWeiXin.get("province");
            }
            if(unionid!=null) {
                ydWeixinUserInfo.setUnionid(unionid);
            }
            ydWeixinUserInfo.setHeadImgType(0);
            ydWeixinUserInfo.setHeadImgUrl(headimgurl);
            ydWeixinUserInfo.setNick(nickname);
            ydWeixinUserInfo.setCity(city);
            ydWeixinUserInfo.setCountry(country);
            ydWeixinUserInfo.setProvince(province);
            ydWeixinUserInfo.setSex(sex);
            if(isNew){
                ydWeixinUserInfoMapper.insert(ydWeixinUserInfo);
            }else {
                ydWeixinUserInfoMapper.updateByPrimaryKeySelective(ydWeixinUserInfo);
            }
            UserSession userSession = new UserSession();
            BASE64Encoder encoder = new BASE64Encoder();
            String yid = encoder.encode(SerializeUtils.serialize(UUIDGenerator.getUUID() + RandomUtil.getSNCode(TypeEnum.SESSIONID)));
            userSession.setYid(yid);
            userSession.setWeixinConfigId(ydWeixinServiceConfig.getWeixinConfigId());
            userSession.setAppid(appid);
            userSession.setOpenid(ydWeixinUserInfo.getOpenid());
            userSession.setUnionid(ydWeixinUserInfo.getUnionid());
            userSession.setMobile(ydWeixinUserInfo.getMobile());
            userSession.setEmail(ydWeixinUserInfo.getEmail());
            result.put("success",true);
            result.put("userSession",userSession);

        }
        catch (Exception e){
            logger.error("WeixinOauth2ShopServiceImpl getUserAuthorization error:",e);
        }
        return result;
    }

    @Override
    public String getJsapiTicket(String appid) {
        try {
            String cacheKey = appid+"jsapiticket";
            if(this.cacheOn) {
                byte[] bToken = redisManager.get(SerializeUtils.serialize(cacheKey));
                if (bToken != null) {
                    return (String) SerializeUtils.deserialize(bToken);
                }
            }

            String accessToken = this.getAccessToken(appid);

            if(accessToken==null){
                return null;
            }
            String urlGetTick = MessageFormat.format("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={0}&type=jsapi", accessToken);
            String jsonStrTick = HttpUtil.get(urlGetTick, null);
            Map<String,Object> resultTick = JSON.parseObject(jsonStrTick, Map.class);
            String jsapiTicket = (String)resultTick.get("ticket");

            if(this.cacheOn) {
                redisManager.set(SerializeUtils.serialize(cacheKey), SerializeUtils.serialize(jsapiTicket), (Integer) resultTick.get("expires_in") - 120);
            }
            return jsapiTicket;

        }
        catch (Exception e){
            logger.error("getJsapiTicket error:",e);
        }
        return null;
    }


    @Override
    public Result<WeixinUserInfo> getWeiXinUserInfo(String weixinConfigId,String openId, String accessToken,boolean isSns) {
        Result<WeixinUserInfo> result = new Result<WeixinUserInfo>();
        try{
            String url = MessageFormat.format("https://api.weixin.qq.com/cgi-bin/user/info?access_token={0}&openid={1}&lang=zh_CN", accessToken, openId);
            if(isSns){
                url = MessageFormat.format("https://api.weixin.qq.com/sns/userinfo?access_token={0}&openid={1}&lang=zh_CN", accessToken, openId);
            }
//            if(type==WeixinAppTypeEnum.YDJS) {
//                url = MessageFormat.format("https://api.weixin.qq.com/sns/userinfo?access_token={0}&openid={1}&lang=zh_CN", accessToken, openId);
//            }
            String jsonStr = HttpUtil.get(url,null);
            WeixinUserInfo userInfo = JSON.parseObject(jsonStr,WeixinUserInfo.class);
            result.setResult(userInfo);
            if(userInfo.getErrcode()>0){
                result.setSuccess(false);
                result.setMsgInfo(userInfo.getErrmsg());
                logger.error("weixin getUserInfo error: error code is -",userInfo.getErrcode()+";   errorMsg is - "+result.getMsgInfo());
            }else {
                result.setSuccess(true);
            }
        }catch (Exception e){
            logger.error("weixin getUserInfo error:",e);
            result.setSuccess(false);
            result.setMsgInfo(e.getMessage());
        }
        return result;
    }

    @Override
    public Result<Map<String, Object>> accessTokenIsOk(String weixinConfigId,String openId, String accessToken) {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        try{
            String url = MessageFormat.format("https://api.weixin.qq.com/sns/auth?access_token={0}&openid={1}",accessToken,openId);
            String jsonStr = HttpUtil.get(url,null);
            Map<String, Object> map = JSON.parseObject(
                    jsonStr,new TypeReference<Map<String, Object>>(){} );
            result.setResult(map);
            if((Integer)map.get("errcode")>0){
                result.setSuccess(false);
                result.setMsgInfo((String)map.get("errmsg"));
            }else {
                result.setSuccess(true);
            }
        }catch (Exception e){
            logger.error("weixin accessTokenIsOk error:",e);
            result.setSuccess(false);
            result.setMsgInfo(e.getMessage());
        }
        return result;
    }

    @Override
    public Result<List<String>> getWeiXinServerIPList(String weixinConfigId,String accessToken) {
        Result<List<String>> result = new Result<List<String>>();
        try{
            String url = MessageFormat.format("https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token={0}",accessToken);
            String jsonStr = HttpUtil.get(url,null);
            List<String> list = JSON.parseObject(
                    jsonStr,new TypeReference<List<String>>(){} );
            result.setResult(list);
            result.setSuccess(true);
        }catch (Exception e){
            logger.error("weixin getcallbackip error:",e);
            result.setSuccess(false);
            result.setMsgInfo(e.getMessage());
        }
        return result;
    }


    @Override
    public String encryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException {
        return this.wxBizMsgCrypt.encryptMsg(replyMsg,timeStamp,nonce);
    }

    @Override
    public String decryptMsg(String msgSignature, String timeStamp, String nonce, String postData) throws AesException {
        return this.wxBizMsgCrypt.decryptMsg(msgSignature,timeStamp,nonce,postData);
    }

    @Override
    public String verifyUrl(String msgSignature, String timeStamp, String nonce, String echoStr) throws AesException {
        return this.wxBizMsgCrypt.verifyUrl(msgSignature,timeStamp,nonce,echoStr);
    }

    @Override
    public String getComponentVerifyTicket() throws IOException, ClassNotFoundException {
        String result = null;

        byte[] bToken = redisManager.get(SerializeUtils.serialize("component_verify_ticket"));
        if (bToken != null) {
            result = (String) SerializeUtils.deserialize(bToken);
        }

        return result;

    }

    @Override
    public void setComponentVerifyTicket(String componentVerifyTicket) throws IOException {
        redisManager.set(SerializeUtils.serialize("component_verify_ticket"), SerializeUtils.serialize(componentVerifyTicket));
    }

    @Override
    public String getComponentAccessToken() throws Exception {
        String result = null;
        byte[] bToken = redisManager.get(SerializeUtils.serialize("component_access_token"));
        if (bToken != null) {
            result =  (String) SerializeUtils.deserialize(bToken);
        }else {
            Map<String, String> postMap = new HashMap<>();
            postMap.put("component_appid", this.weixinOpenAppId);
            postMap.put("component_appsecret", this.weixinOpenSecret);
            postMap.put("component_verify_ticket", this.getComponentVerifyTicket());

            String resultStr = HttpUtil.postJson("https://api.weixin.qq.com/cgi-bin/component/api_component_token", JSON.toJSONString(postMap));
            if (StringUtil.isNotBlank(resultStr)) {

                Map<String, Object> resultMap = JSON.parseObject(resultStr, Map.class);

                logger.info(resultStr);
                result = (String)resultMap.get("component_access_token");
                logger.info("component_access_token is : "+result);
                Integer expIn = (Integer)resultMap.get("expires_in");
                logger.info("component_access_token expires_in is : "+expIn);
                redisManager.set(SerializeUtils.serialize("component_access_token"), SerializeUtils.serialize(result), expIn-600);

            }
        }

        return result;
    }

    @Override
    public String getPreAuthCode() throws Exception {
        String result = null;
        byte[] bToken = redisManager.get(SerializeUtils.serialize("component_pre_auth_code"));
        if (bToken != null) {
            result =  (String) SerializeUtils.deserialize(bToken);
        }else {
            String componentAccessToken = this.getComponentAccessToken();
            if (StringUtil.isNotBlank(componentAccessToken)) {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("component_appid", this.weixinOpenAppId);

                String resultStr = HttpUtil.postJson("https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=" + componentAccessToken, JSON.toJSONString(postMap));
                if (StringUtil.isNotBlank(resultStr)) {
                    Map<String, Object> resultMap = JSON.parseObject(resultStr, Map.class);

                    result = (String) resultMap.get("pre_auth_code");
                    Integer expIn = (Integer)resultMap.get("expires_in");
                    redisManager.set(SerializeUtils.serialize("component_pre_auth_code"), SerializeUtils.serialize(result), expIn-90);
                }
            }
        }

        return result;
    }

    @Override
    public String getCPWeiXinAccesssToken(String appid,String queryAuthCode) throws Exception {
        String[] authCodeSplit = queryAuthCode.split(":");
        String componentAccessToken = this.getComponentAccessToken();
        Map<String, String> postMap = new HashMap<>();
        postMap.put("component_appid", this.weixinOpenAppId);
        postMap.put("authorization_code", authCodeSplit[1]);

        String resultStr = HttpUtil.postJson("https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=" + componentAccessToken, JSON.toJSONString(postMap));

        logger.info("getTestWeiXinAccesssToken: "+resultStr);
        Map<String, Object> dataMap = JSON.parseObject(resultStr, Map.class);

        Map<String,Object> resultMap = (Map<String,Object>)dataMap.get("authorization_info");
        String accessTokey = (String)resultMap.get("authorizer_access_token");
        return accessTokey;
    }

    @Override
    public void sendCustormMessage(String accessToken, String msg) throws Exception {

        String resultStr = HttpUtil.postJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken, msg);
        logger.info("sendCustormMessage result info is :"+resultStr);

    }





    /*
       -----------------------------------------进行公众号（小程序）授权给我们作为第三方开发商的流程begin------------------------------------
     */


    @Override
    public String createWeixinAuthLink(String shopid, String preAuthLinkCode, String appid, Integer weixinType,String redirectUri) throws Exception {
        String applyidrem = shopid+weixinType;
        String authLinkCode = null;
        byte[] bToken = redisManager.get(SerializeUtils.serialize(applyidrem));
        if (bToken != null) {
            authLinkCode = (String) SerializeUtils.deserialize(bToken);
        }
        if(!StringUtil.equals(authLinkCode,preAuthLinkCode)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "授权二维码已经失效，请联系服务商重新生成！");
        }
        YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopid);
        if(ydPaypointShopInfoExt==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "无效的二维码，请联系服务商检查！");
        }

        WeiXinTypeEnum weiXinTypeEnum = WeiXinTypeEnum.nameOf(weixinType);
        if(weiXinTypeEnum!=WeiXinTypeEnum.OPENPUBLIC&&weiXinTypeEnum!=WeiXinTypeEnum.OPENSMALL){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "无效的二维码，请联系服务商检查！");
        }

        Integer authType = 1;//公众号为1
        if(weiXinTypeEnum==WeiXinTypeEnum.OPENSMALL){
            authType = 2; //小程序为2
        }
        String url = "https://mp.weixin.qq.com/safe/bindcomponent?action=bindcomponent&no_scan=1&component_appid="+
                this.weixinOpenAppId+"&pre_auth_code="+this.getPreAuthCode()+"&redirect_uri="+redirectUri+"&auth_type="+authType;
        if(appid!=null){
            url = url + "&biz_appid="+appid;
        }
        url = url + "#wechat_redirect";

        return url;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean authShop(String shopid, WeiXinTypeEnum weiXinTypeEnum, String authCode) throws Exception {


//        Date curDate = new Date();
//        Integer curTime = DateUtils.getSecondTimestamp(curDate);

        if(weiXinTypeEnum==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "无效的二维码，请联系服务商检查！");
        }
        if((weiXinTypeEnum!=WeiXinTypeEnum.OPENPUBLIC)&&(weiXinTypeEnum!=WeiXinTypeEnum.OPENSMALL)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "无效的二维码，请联系服务商检查！");
        }
        String componentAccessToken = this.getComponentAccessToken();
        Map<String, String> postMap = new HashMap<>();
        postMap.put("component_appid", this.weixinOpenAppId);
        postMap.put("authorization_code", authCode);

        String resultStr = HttpUtil.postJson("https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=" + componentAccessToken, JSON.toJSONString(postMap));

        Map<String, Object> dataMap = JSON.parseObject(resultStr, Map.class);

        Map<String,Object> resultMap = (Map<String,Object>)dataMap.get("authorization_info");
        String appid = (String)resultMap.get("authorizer_appid");
        String accessTokey = (String)resultMap.get("authorizer_access_token");
        String refreshTokey = (String)resultMap.get("authorizer_refresh_token");
        Integer expIn = (Integer)resultMap.get("expires_in");
        Date expInField = DateUtils.getDateBySecondTimestamp(DateUtils.getSecondTimestamp(new Date())+(expIn-300));

        if(StringUtil.isBlank(accessTokey)||StringUtil.isBlank(refreshTokey)){
            throw new NullPointerException("访问token不能为空！");
        }

        /**
         * 查申请单信息，回填appid
         */
        YdShopApplyInfo ydShopApplyInfo = ydShopApplyInfoMapper.selectByApplyid(shopid);
        if(ydShopApplyInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "申请单信息不存在，请联系服务商检查！");
        }

        if(weiXinTypeEnum==WeiXinTypeEnum.OPENPUBLIC){
            ydShopApplyInfo.setPublicAppid(appid);
        }else if(weiXinTypeEnum==WeiXinTypeEnum.OPENSMALL){
            ydShopApplyInfo.setSmallAppid(appid);
        }

        if(ydShopApplyInfoMapper.updateByPrimaryKeySelective(ydShopApplyInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新申请单信息错误，请联系服务商检查！");
        }

        /**
         * 查询公众号或者小程序的授权成功配置记录，有则更新，无则新增
         */
        YdWeixinServiceConfig ydWeixinServiceConfig = ydWeixinServiceConfigMapper.selectByAppid(appid);
        if(ydWeixinServiceConfig==null){
            ydWeixinServiceConfig = new YdWeixinServiceConfig();
            ydWeixinServiceConfig.setAccessToken(accessTokey);
            ydWeixinServiceConfig.setAuthorizerRefreshToken(refreshTokey);
            ydWeixinServiceConfig.setExpiresInDate(expInField);
            ydWeixinServiceConfig.setWeixinConfigId(RandomUtil.getSNCode(TypeEnum.WEIXINCONFIG));
            ydWeixinServiceConfig.setWeixinType(weiXinTypeEnum.getType());
            ydWeixinServiceConfig.setAppid(appid);
            ydWeixinServiceConfig.setShopid(shopid);
            if(ydWeixinServiceConfigMapper.insertSelective(ydWeixinServiceConfig)<=0){
                throw new YdException(ErrorCodeConstants.YD10001.getErrorCode(),ErrorCodeConstants.YD10001.getErrorMessage());
            }
        }else {
            //修改
            ydWeixinServiceConfig.setAccessToken(accessTokey);
            ydWeixinServiceConfig.setAuthorizerRefreshToken(refreshTokey);
            ydWeixinServiceConfig.setExpiresInDate(expInField);
            if(ydWeixinServiceConfigMapper.updateByPrimaryKey(ydWeixinServiceConfig)<=0){
                throw new YdException(ErrorCodeConstants.YD10001.getErrorCode(),ErrorCodeConstants.YD10001.getErrorMessage());
            }
        }

        /**
         * 如果是小程序还需要为此小程序帐号上传小程序代码
         */
        if(weiXinTypeEnum==WeiXinTypeEnum.OPENSMALL){
            /**
             * 发送消息，上传小程序代码
             */
            Map<String , Object> createShopMap = new HashMap<>();
            createShopMap.put(Constant.MQTAG, MqTagEnum.WEIXINSMALLCODE.getTag());
            createShopMap.put("appid",appid);
            mqMessageService.sendMessage(shopid+MqTagEnum.WEIXINSMALLCODE.getTag(),MqTagEnum.WEIXINAUTHSHOP, JSON.toJSONString(createShopMap));
        }

        return true;

    }

    /*
       -----------------------------------------进行公众号（小程序）授权给我们作为第三方开发商的流程end------------------------------------
     */



}
