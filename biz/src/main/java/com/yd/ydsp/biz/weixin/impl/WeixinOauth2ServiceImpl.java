package com.yd.ydsp.biz.weixin.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yd.ydsp.biz.weixin.WeixinOauth2Service;
import com.yd.ydsp.biz.weixin.model.WeixinAppCode2SessionResult;
import com.yd.ydsp.biz.weixin.model.WeixinTokenInfo;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.HttpUtil;
import com.yd.ydsp.common.utils.WeiXinSignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/5/16.
 */
public class WeixinOauth2ServiceImpl implements WeixinOauth2Service {

    /**公众号appid
     *
     */
    private String appid;
    /**
     * 公从号secret
     */
    private String secret;

    private String type;
    private SourceEnum weixinType;
    /**
     * 接收微信消息时，我们在微信设置回调api的token
     */
    private String msgToken;
    /**
     * 小程序appid
     */
    private String smallAppid;
    /**
     * 小程序secret
     */
    private String smallAppSecret;

    private boolean cacheOn=true;

    private String wixin2bjsapiticket = "wixin2bjsapiticket";
    private String wixin2cjsapiticket = "wixin2cjsapiticket";


    @Resource
    RedisManager redisManager;

    public static final Logger logger = LoggerFactory.getLogger(WeixinOauth2ServiceImpl.class);

    public void setAppid(String appid){ this.appid = appid;}
    public void setSecret(String secret){ this.secret = secret;}
    public void setType(String type){ this.type = type.trim();  this.weixinType = SourceEnum.valueOf(this.type);}
    public void setMsgToken(String msgToken){ this.msgToken = msgToken; }
    public void setSmallAppid(String smallAppid){ this.smallAppid = smallAppid; }
    public void setSmallAppSecret(String smallAppSecret){ this.smallAppSecret = smallAppSecret; }
    public void setCacheOn(boolean cacheOn){ this.cacheOn = cacheOn; }

    @Override
    public String getAccessToken() {
        try {
            if(this.cacheOn) {
                byte[] bToken = redisManager.get(SerializeUtils.serialize(weixinType.getName()));
                if (bToken != null) {
                    return (String) SerializeUtils.deserialize(bToken);
                }
            }
            String url = MessageFormat.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}", this.appid, this.secret);
            String jsonStr = HttpUtil.post(url, null);
            Map<String,Object> result = JSON.parseObject(jsonStr, Map.class);
            String accessToken = (String)result.get("access_token");
            logger.info("weixin result: " + jsonStr);

            if(this.cacheOn) {
                redisManager.set(SerializeUtils.serialize(weixinType.getName()), SerializeUtils.serialize(accessToken), (Integer) result.get("expires_in") - 120);
            }
            return accessToken;

        }
        catch (Exception e){
            logger.error("getAccessToken error:",e);
        }
        return null;
    }

    @Override
    public String getJsapiTicket() {
        try {
            String cacheKey = wixin2bjsapiticket;
            if(weixinType==SourceEnum.WEIXIN2C){
                cacheKey = wixin2cjsapiticket;
            }else{
                cacheKey = wixin2bjsapiticket;
            }
            if(this.cacheOn) {
                byte[] bToken = redisManager.get(SerializeUtils.serialize(cacheKey));
                if (bToken != null) {
                    return (String) SerializeUtils.deserialize(bToken);
                }
            }

            String urlGetTick = MessageFormat.format("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={0}&type=jsapi", this.getAccessToken());
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
    public Map<String, String> getJsapiTicket(String url) {
        Map<String, String> result = WeiXinSignUtil.sign(this.getJsapiTicket(),url);
        if(result!=null){
            result.put("appId",this.getAppid());
        }
        return result;
    }

    @Override
    public String getAppid() {
        return appid;
    }

    @Override
    public Result<WeixinTokenInfo> authorize(String code) {
        Result<WeixinTokenInfo> result = new Result<WeixinTokenInfo>();
        try {
            String url = MessageFormat.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code", this.appid, this.secret, code);
            String jsonStr = HttpUtil.post(url, null);
            WeixinTokenInfo token = JSON.parseObject(jsonStr,WeixinTokenInfo.class);
            result.setResult(token);
            if(token.getErrcode()>0){
                result.setSuccess(false);
                result.setMsgInfo(token.getErrmsg());
            }else {
                result.setSuccess(true);
            }
        }
        catch (Exception e){
            logger.error("weixin login error:",e);
            result.setSuccess(false);
            result.setMsgInfo(e.getMessage());
        }
        return result;
    }

    @Override
    public Result<WeixinTokenInfo> refreshToken(String refreshToken) {
        Result<WeixinTokenInfo> result = new Result<WeixinTokenInfo>();
        try {
            String url = MessageFormat.format("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid={0}&grant_type=refresh_token&refresh_token={1}",this.appid,refreshToken);
            String jsonStr = HttpUtil.post(url, null);
            logger.info("WeiXin refreshToken :"+jsonStr);
            WeixinTokenInfo token = JSON.parseObject(jsonStr,WeixinTokenInfo.class);
            result.setResult(token);
            if(token.getErrcode()>0){
                result.setSuccess(false);
                result.setMsgInfo(token.getErrmsg());
            }else{
                result.setSuccess(true);
            }
        }catch (Exception e){
            logger.error("weixin refreshToken error:",e);
            result.setSuccess(false);
            result.setMsgInfo(e.getMessage());
        }
        return result;
    }


    @Override
    public Result<WeixinUserInfo> getWeiXinUserInfo(String openId, String accessToken,boolean isSns) {
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
    public Result<Map<String, Object>> accessTokenIsOk(String openId, String accessToken) {
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
    public Result<List<String>> getWeiXinServerIPList(String accessToken) {
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
    public String getWeiXinMsgToken() {
        return msgToken;
    }

    @Override
    public Result<WeixinAppCode2SessionResult> weixinSmallAppAuthorize(String code) {
        Result<WeixinAppCode2SessionResult> result = new Result<WeixinAppCode2SessionResult>();
        try{
            String url = MessageFormat.format("https://api.weixin.qq.com/sns/jscode2session?appid={0}&secret={1}&js_code={2}&grant_type=authorization_code",this.smallAppid,this.smallAppSecret,code);
            String jsonStr = HttpUtil.get(url,null);
            WeixinAppCode2SessionResult weixinAppCode2SessionResult = JSON.parseObject(
                    jsonStr,WeixinAppCode2SessionResult.class);
            if(weixinAppCode2SessionResult.getErrcode()>0){
                result.setSuccess(false);
            }else{
                result.setSuccess(true);
            }
            result.setResult(weixinAppCode2SessionResult);

        }catch (Exception e){
            logger.error("call weixinSmallAppAuthorize error:",e);
            result.setSuccess(false);

        }
        return result;
    }
}
