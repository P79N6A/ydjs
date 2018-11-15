package com.yd.ydsp.biz.weixin.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondWeiXinInfoConfigHolder;
import com.yd.ydsp.biz.sso.UserSessionService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.weixin.WeixinOauth2ShopService;
import com.yd.ydsp.biz.weixin.WeixinSamll2ShopService;
import com.yd.ydsp.biz.weixin.model.WeixinSmallUserInfo;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.enums.paypoint.WeiXinTypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.EncryptionUtil;
import com.yd.ydsp.common.utils.HttpUtil;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.common.utils.UUIDGenerator;
import com.yd.ydsp.dal.entity.YdWeixinServiceConfig;
import com.yd.ydsp.dal.entity.YdWeixinUserInfo;
import com.yd.ydsp.dal.mapper.YdWeixinServiceConfigMapper;
import com.yd.ydsp.dal.mapper.YdWeixinUserInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeixinSamll2ShopServiceImpl implements WeixinSamll2ShopService {

    public static final Logger logger = LoggerFactory.getLogger(WeixinSamll2ShopServiceImpl.class);

//    @Resource
//    private RedisManager redisManager;
    @Resource
    private YdWeixinServiceConfigMapper ydWeixinServiceConfigMapper;
    @Resource
    private YdWeixinUserInfoMapper ydWeixinUserInfoMapper;
    @Resource
    private WeixinOauth2ShopService weixinOauth2ShopService;


    @Override
    public Map<String, Object> loginUserByWeixinSmall(String appid, String code) throws Exception {

        YdWeixinServiceConfig ydWeixinServiceConfig = ydWeixinServiceConfigMapper.selectByAppid(appid);
        if(ydWeixinServiceConfig==null){
            Map<String,Object> result = new HashMap<>();
            result.put("success",false);
            result.put("errmsg","此小程序还没有授权信息，请联系服务商!");
            return result;
        }

        String url = MessageFormat.format("https://api.weixin.qq.com/sns/component/jscode2session?appid={0}&js_code={1}&grant_type=authorization_code&component_appid={2}&component_access_token={3}",
                appid,code, weixinOauth2ShopService.getWeixinOpenAppId(),weixinOauth2ShopService.getComponentAccessToken());
        String jsonStr = HttpUtil.post(url, null);
        logger.info("loginUserByWeixinSmall url is :"+url);
        logger.info("loginUserByWeixinSmall is :"+jsonStr);
        Map<String,Object> result = JSON.parseObject(jsonStr, Map.class);
        result.put("success",false);
        String sessionKey = null;
        String openid = null;
        String unionid = null;
        if(result.containsKey("session_key")){
            result.put("success",true);
            sessionKey = (String) result.get("session_key");
            openid = (String)result.get("openid");

            if(StringUtil.isNotEmpty(sessionKey)){

                if(result.containsKey("unionid")){
                    unionid = (String)result.get("unionid");
                }

                boolean isNew = false;
                BASE64Encoder encoder = new BASE64Encoder();
                String yid = encoder.encode(SerializeUtils.serialize(UUIDGenerator.getUUID() + RandomUtil.getSNCode(TypeEnum.SESSIONID)));
                YdWeixinUserInfo ydWeixinUserInfo = ydWeixinUserInfoMapper.selectByOpenid(ydWeixinServiceConfig.getWeixinConfigId(),openid);
                if(ydWeixinUserInfo == null){
                    isNew = true;
                    ydWeixinUserInfo = new YdWeixinUserInfo();
                    ydWeixinUserInfo.setOpenid(openid);
                    ydWeixinUserInfo.setWeixinConfigId(ydWeixinServiceConfig.getWeixinConfigId());
                    ydWeixinUserInfo.setModifier("system");
                }
                if(StringUtil.isNotEmpty(unionid)) {
                    ydWeixinUserInfo.setUnionid(unionid);
                }else {
                    ydWeixinUserInfo.setUnionid(openid+ydWeixinServiceConfig.getWeixinConfigId());
                }
                ydWeixinUserInfo.setWeixinAccessToken(sessionKey);
                ydWeixinUserInfo.setWeixinRefreshToken(yid);
                if(isNew){
                    ydWeixinUserInfoMapper.insert(ydWeixinUserInfo);
                }else {
                    ydWeixinUserInfoMapper.updateByPrimaryKeySelective(ydWeixinUserInfo);
                }
                result.remove("session_key");

                if(StringUtil.isEmpty(ydWeixinServiceConfig.getShopid())){
                    result.put("errmsg","此小程序没有对引灯授权！");
                    result.put("success",false);
                }else {
                    result.put("shopid",ydWeixinServiceConfig.getShopid());
                }

                /**
                 * 说明登录成功，需要保存登录session数据
                 */

//                String yid = userSessionService.newSession(appid,ydWeixinServiceConfig.getWeixinConfigId(),openid,ydWeixinUserInfo.getUnionid(),ydWeixinUserInfo.getMobile(),ydWeixinUserInfo.getEmail(),)
                UserSession userSession = new UserSession();
                userSession.setYid(yid);
                userSession.setAppid(appid);
                userSession.setWeixinConfigId(ydWeixinServiceConfig.getWeixinConfigId());
                userSession.setOpenid(openid);
                userSession.setUnionid(ydWeixinUserInfo.getUnionid());
                userSession.setMobile(ydWeixinUserInfo.getMobile());
                userSession.setEmail(ydWeixinUserInfo.getEmail());
                result.put("userSession",userSession);
            }
        }

        return result;
    }

    @Override
    public boolean saveUserInfo(String appid, WeixinSmallUserInfo weixinUserInfo) throws Exception {
        boolean result = false;
        YdWeixinServiceConfig ydWeixinServiceConfig = ydWeixinServiceConfigMapper.selectByAppid(appid);
        if(ydWeixinServiceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "授权信息不存在！");
        }
        if(WeiXinTypeEnum.nameOf(ydWeixinServiceConfig.getWeixinType())!=WeiXinTypeEnum.OPENSMALL){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "只有小程序才能更新用户！");
        }

        YdWeixinUserInfo ydWeixinUserInfo = ydWeixinUserInfoMapper.selectByOpenid(ydWeixinServiceConfig.getWeixinConfigId(),weixinUserInfo.getOpenid());
        if(ydWeixinUserInfo!=null){
            logger.info("weixinUserInfo is "+ JSON.toJSONString(weixinUserInfo));
            logger.info("weixinUserInfo.getEncryptedData() is :"+weixinUserInfo.getEncryptedData());
//            String weixinDataStr = EncryptionUtil.encrypteWeiXinData(weixinUserInfo.getEncryptedData(),
//                    weixinUserInfo.getIv(),ydWeixinUserInfo.getWeixinAccessToken());
//            logger.info("weixinDataStr is :"+weixinDataStr);
//            Map<String,Object> weixinDataMap = JSON.parseObject(weixinDataStr,Map.class);
//            if(weixinDataMap.containsKey("unionId")){
//                ydWeixinUserInfo.setUnionid((String)weixinDataMap.get("unionId"));
//            }
            ydWeixinUserInfo.setSex(weixinUserInfo.getSex());
            ydWeixinUserInfo.setHeadImgType(0);
            ydWeixinUserInfo.setHeadImgUrl(weixinUserInfo.getHeadimgurl());
            ydWeixinUserInfo.setNick(weixinUserInfo.getNickname());
            ydWeixinUserInfo.setProvince(weixinUserInfo.getProvince());
            ydWeixinUserInfo.setCity(weixinUserInfo.getCity());
            ydWeixinUserInfo.setCountry(weixinUserInfo.getCountry());
            ydWeixinUserInfo.setSex(weixinUserInfo.getSex());
            if(ydWeixinUserInfoMapper.updateByPrimaryKeySelective(ydWeixinUserInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "用户信息更新失败！");
            }
            result = true;
        }

        return result;
    }

    @Override
    public boolean uploadWeiXinSmallCode(String appid,Integer templateid,String version,String desc) throws Exception {
        YdWeixinServiceConfig ydWeixinServiceConfig = ydWeixinServiceConfigMapper.selectByAppid(appid);
        if(ydWeixinServiceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "小程序授权信息不存在！");
        }
        if(StringUtil.isEmpty(ydWeixinServiceConfig.getShopid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "小程序没有绑定店铺！");
        }
        if(WeiXinTypeEnum.nameOf(ydWeixinServiceConfig.getWeixinType())!=WeiXinTypeEnum.OPENSMALL){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "此appid不是小程序:"+appid);
        }

        Map<String, Object> postMap = new HashMap<>();

//        Map<String, Object> extJson = new HashMap<>();
        String extJson = "{\"extEnable\": true,\"extAppid\": \""+appid+"\",\"ext\": {\"appid\": \""+appid+"\"}}";
        postMap.put("ext_json", extJson);

        if(templateid==null) {
            postMap.put("template_id", DiamondWeiXinInfoConfigHolder.getInstance().getWeixinSamllShopMainTemplateId());
            postMap.put("user_version", DiamondWeiXinInfoConfigHolder.getInstance().getWeixinSamllShopMainTemplateVersion());
            postMap.put("user_desc", DiamondWeiXinInfoConfigHolder.getInstance().getWeixinSamllShopMainTemplateDesc());
        }else {
            postMap.put("template_id", templateid);
            postMap.put("user_version", version);
            postMap.put("user_desc", desc);
        }

        logger.info("uploadWeiXinSmallCode postMap is :"+JSON.toJSONString(postMap));

        String resultStr = HttpUtil.postJson("https://api.weixin.qq.com/wxa/commit?access_token=" + weixinOauth2ShopService.getAccessToken(appid), JSON.toJSONString(postMap));

        Map<String, Object> dataMap = JSON.parseObject(resultStr, Map.class);

        if(((Integer)dataMap.get("errcode")).intValue()!=0){

            logger.error(resultStr);
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "部署小程序代码失败！");
        }

        return true;
    }

    @Override
    public Map<String, Object> submitWeiXinSmallCodeAudit(String appid, String postData) throws Exception {
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/submit_audit?access_token="+accessToken;
        String resultStr = HttpUtil.postJson(url,postData);
        logger.info("submitWeiXinSmallCodeAudit is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> undoCodeAudit(String appid) throws Exception {
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/undocodeaudit?access_token="+accessToken;
        String resultStr = HttpUtil.get(url);
        logger.info("undoCodeAudit is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> getXinSmallCodeAuditResult(String appid) throws Exception {
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/get_latest_auditstatus?access_token="+accessToken;
        String resultStr = HttpUtil.get(url);
        logger.info("getXinSmallCodeAuditResult is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> releaseWeiXinSmallCode(String appid) throws Exception {
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/release?access_token="+accessToken;
        String resultStr = HttpUtil.postJson(url,"{}");
        logger.info("releaseWeiXinSmallCode is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> grayReleaseWeiXinSmallCode(String appid,Integer percentage) throws Exception {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("gray_percentage",percentage);
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/grayrelease?access_token="+accessToken;
        String resultStr = HttpUtil.postJson(url,JSON.toJSONString(requestBody));
        logger.info("grayReleaseWeiXinSmallCode is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> getGrayReleaseWeiXinSmallCode(String appid) throws Exception {
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/getgrayreleaseplan?access_token="+accessToken;
        String resultStr = HttpUtil.get(url);
        logger.info("getGrayReleaseWeiXinSmallCode is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> cancelGrayReleaseWeiXinSmallCode(String appid) throws Exception {
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/revertgrayrelease?access_token="+accessToken;
        String resultStr = HttpUtil.get(url);
        logger.info("cancelGrayReleaseWeiXinSmallCode is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> getCategory(String appid) throws Exception {
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/get_category?access_token="+accessToken;
        String resultStr = HttpUtil.get(url);
        logger.info("getCategory is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> getPage(String appid) throws Exception {
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/get_page?access_token="+accessToken;
        String resultStr = HttpUtil.get(url);
        logger.info("getPage is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public UserSession getNewSession(String appid, String openid,String yidc) throws IOException {
        if(StringUtil.isEmpty(appid)||StringUtil.isEmpty(openid)||StringUtil.isEmpty(yidc)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "非法操作！");
        }
        YdWeixinServiceConfig ydWeixinServiceConfig = ydWeixinServiceConfigMapper.selectByAppid(appid);
        if(ydWeixinServiceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "小程序授权信息不存在！");
        }
        YdWeixinUserInfo ydWeixinUserInfo = ydWeixinUserInfoMapper.selectByOpenid(ydWeixinServiceConfig.getWeixinConfigId(),openid);
        if(ydWeixinUserInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "非法的用户操作！");
        }
        if(!StringUtil.equals(ydWeixinUserInfo.getWeixinRefreshToken(),yidc)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "用户非法操作！");
        }

        /**
         * 开始生成新的UserSession
         */
        UserSession userSession = new UserSession();
        BASE64Encoder encoder = new BASE64Encoder();
        String yid = encoder.encode(SerializeUtils.serialize(UUIDGenerator.getUUID() + RandomUtil.getSNCode(TypeEnum.SESSIONID)));
        ydWeixinUserInfo.setWeixinRefreshToken(yid);
        if(ydWeixinUserInfoMapper.updateByPrimaryKeySelective(ydWeixinUserInfo)<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "数据库意外错误！");
        }
        userSession.setYid(yid);
        userSession.setAppid(appid);
        userSession.setWeixinConfigId(ydWeixinServiceConfig.getWeixinConfigId());
        userSession.setAppid(openid);
        userSession.setAppid(ydWeixinUserInfo.getUnionid());
        userSession.setAppid(ydWeixinUserInfo.getMobile());
        userSession.setAppid(ydWeixinUserInfo.getEmail());

        return userSession;
    }

    @Override
    public byte[] getWxAcodeUnlimit(String shopid,String scene, String page) throws Exception {
        YdWeixinServiceConfig weixinServiceConfig = ydWeixinServiceConfigMapper.selectByShopIdAndType(shopid,WeiXinTypeEnum.OPENSMALL.getType());
        if(weixinServiceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "请管理员先对小程序进行授权！");
        }
        String accessToken = weixinOauth2ShopService.getAccessToken(weixinServiceConfig.getAppid());
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("scene",scene);
        requestBody.put("page",page);
        byte[] content = HttpUtil.postJsonGetByte("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accessToken,JSON.toJSONString(requestBody));
        return content;
    }

    @Override
    public Map<String,Object> modifySmallDomain(String appid,String action,String domain) throws Exception {
        
//        String postData = "{\"action\":\"add\",\"requestdomain\":[\"https://www.ydjs360.com\"],\"wsrequestdomain\":[\"wss://www.ydjs360.com\"],\"uploaddomain\":[\"https://www.ydjs360.com\"],\"downloaddomain\":[\"https://www.ydjs360.com\"]}";
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("action",action);
        Map<String,Object> setWebViewBody = new HashMap<>();
        setWebViewBody.put("action",action);
        if(StringUtil.isNotEmpty(domain)) {
            List<String> httpsList = new ArrayList<>();
            List<String> wssList = new ArrayList<>();
            httpsList.add("https://"+domain);
            wssList.add("wss://"+domain);
            requestBody.put("requestdomain",httpsList);
            requestBody.put("uploaddomain",httpsList);
            requestBody.put("downloaddomain",httpsList);
            requestBody.put("wsrequestdomain",wssList);
            setWebViewBody.put("webviewdomain",httpsList);
        }
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/modify_domain?access_token="+accessToken;
        String resultModifyDomainStr = HttpUtil.postJson(url,JSON.toJSONString(requestBody));
        logger.info("modifySmallDomain1 result is: " +resultModifyDomainStr);

        url = "https://api.weixin.qq.com/wxa/setwebviewdomain?access_token="+accessToken;
        String resultSetWebViewDomainStr = HttpUtil.postJson(url,JSON.toJSONString(setWebViewBody));
        logger.info("modifySmallDomain2 result is: " +resultSetWebViewDomainStr);
        Map<String,Object> result = new HashMap<>();
        result.put("modifyDomain",resultModifyDomainStr);
        result.put("setWebViewDomain",resultSetWebViewDomainStr);

        return result;
    }

    @Override
    public Map<String, Object> getTesterList(String appid) throws Exception {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("action","get_experiencer");
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/memberauth?access_token="+accessToken;
        String resultStr = HttpUtil.postJson(url,JSON.toJSONString(requestBody));
        logger.info("getTesterList is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> bindTester(String appid, String wechatid) throws Exception {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("wechatid",wechatid);
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/bind_tester?access_token="+accessToken;
        String resultStr = HttpUtil.postJson(url,JSON.toJSONString(requestBody));
        logger.info("getTesterList is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> unBindTester(String appid, String wechatid) throws Exception {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("wechatid",wechatid);
        String accessToken = weixinOauth2ShopService.getAccessToken(appid);
        String url = "https://api.weixin.qq.com/wxa/unbind_tester?access_token="+accessToken;
        String resultStr = HttpUtil.postJson(url,JSON.toJSONString(requestBody));
        logger.info("getTesterList is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> getTemplateDraftList() throws Exception {
        String accessToken = weixinOauth2ShopService.getComponentAccessToken();
        String url = "https://api.weixin.qq.com/wxa/gettemplatedraftlist?access_token="+accessToken;
        String resultStr = HttpUtil.get(url);
        logger.info("getTemplateDraftList is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> getTemplateList() throws Exception {
        String accessToken = weixinOauth2ShopService.getComponentAccessToken();
        String url = "https://api.weixin.qq.com/wxa/gettemplatelist?access_token="+accessToken;
        String resultStr = HttpUtil.get(url);
        logger.info("gettemplatelist is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> addToTemplate(Integer draftId) throws Exception {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("draft_id",draftId);
        String accessToken = weixinOauth2ShopService.getComponentAccessToken();
        String url = "https://api.weixin.qq.com/wxa/addtotemplate?access_token="+accessToken;
        String resultStr = HttpUtil.postJson(url,JSON.toJSONString(requestBody));
        logger.info("addToTemplate is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }

    @Override
    public Map<String, Object> deleteTemplate(Integer templateId) throws Exception {
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("template_id",templateId);
        String accessToken = weixinOauth2ShopService.getComponentAccessToken();
        String url = "https://api.weixin.qq.com/wxa/deletetemplate?access_token="+accessToken;
        String resultStr = HttpUtil.postJson(url,JSON.toJSONString(requestBody));
        logger.info("deletetemplate is :"+resultStr);
        return JSON.parseObject(resultStr,Map.class);
    }
}
