package com.yd.ydsp.biz.sso;

import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.common.enums.SourceEnum;

/**
 * Created by zengyixun on 17/5/27.
 */
public interface UserSessionService {

    String newSession(String openid,String unionid,String mobile,String email,String userIp,SourceEnum weixinType,Integer sessionExpire);
    String newSession(String appid,String weixinConfigId, String openid,String unionid,String mobile,String email,String userIp,SourceEnum weixinType,Integer sessionExpire,String yidValue);
    String newSessionWithTest(String openid,String unionid,String mobile,String email,String userIp,SourceEnum weixinType,Integer sessionExpire);
    String newSessionWithTest(String appid,String weixinConfigId,String openid,String unionid,String mobile,String email,String userIp,SourceEnum weixinType,Integer sessionExpire);
    UserSession getSession(String yid);
    UserSession getSessionByOpenid(String openid,SourceEnum weixinType);
    String updateSessionForUserInfo(String yid,String unionid,String mobile,String email,String userIp); //返回新的yid
    String updateSession(String yid);//返回新的yid
    String getSessionidByOpenId(String openid,SourceEnum weixinType);
    boolean deleteSession(String yid);
    boolean deleteSessionByOpenid(String openid,SourceEnum weixinType);

}
