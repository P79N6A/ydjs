package com.yd.ydsp.biz.sso.impl;

import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.sso.UserSessionService;
import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.enums.paypoint.WeiXinTypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.common.utils.UUIDGenerator;
import com.yd.ydsp.dal.entity.YdConsumerInfo;
import com.yd.ydsp.dal.entity.YdWeixinServiceConfig;
import com.yd.ydsp.dal.entity.YdWeixinUserInfo;
import com.yd.ydsp.dal.mapper.YdConsumerInfoMapper;
import com.yd.ydsp.dal.mapper.YdWeixinServiceConfigMapper;
import com.yd.ydsp.dal.mapper.YdWeixinUserInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * Created by zengyixun on 17/5/27.
 */
public class UserSessionServiceImpl implements UserSessionService {
    public static final Logger logger = LoggerFactory.getLogger(UserSessionServiceImpl.class);

    @Resource
    private RedisManager redisManager;
    @Resource
    private YdWeixinUserInfoMapper ydWeixinUserInfoMapper;
    @Resource
    private YdConsumerInfoMapper ydConsumerInfoMapper;

    @Override
    public String newSession(String openid,String unionid,String mobile,String email,String userIp,SourceEnum weixinType,Integer sessionExpire) {
        UserSession session = new UserSession();
        BASE64Encoder encoder = new BASE64Encoder();
        String yid = null;
        session.setOpenid(openid);
        session.setUnionid(unionid);
        session.setMobile(mobile);
        session.setEmail(email);
        session.setUserIp(userIp);
        session.setType(weixinType);
        try{
            byte[] oldSession = redisManager.get(SerializeUtils.serialize(session.getOpenid()+weixinType.getName()));
            if(!SerializeUtils.isEmpty(oldSession)){
                yid = (String)SerializeUtils.deserialize(oldSession);
//                redisManager.del(SerializeUtils.serialize(oldYid));
            }else {
                yid = encoder.encode(SerializeUtils.serialize(UUIDGenerator.getUUID()+ RandomUtil.getSNCode(TypeEnum.SESSIONID)));
            }
            yid = yid.trim();
            session.setYid(yid);
            /**
             * 配置小于微信给出的token过期时间就用配置的时间，否则就用微信的时间
             */
            if(weixinType==SourceEnum.WEIXIN2C||weixinType==SourceEnum.WEIXIN2B) {
                if (DiamondYdSystemConfigHolder.getInstance().sessionExpire > sessionExpire) {
                    sessionExpire = DiamondYdSystemConfigHolder.getInstance().sessionExpire;
                }
            }
            if(weixinType==SourceEnum.WEIXIN2C){
                YdConsumerInfo userInfo = ydConsumerInfoMapper.selectByOpenid(openid);
                if(userInfo!=null){
                    if(StringUtil.isNotEmpty(userInfo.getUnionid())) {
                        session.setUnionid(userInfo.getUnionid());
                    }
                    if(StringUtil.isNotEmpty(userInfo.getMobile())){
                        session.setMobile(userInfo.getMobile());
                    }
                }
            }
            logger.info("sessionExpire:"+sessionExpire);
            redisManager.set(SerializeUtils.serialize(session.getOpenid()+weixinType.getName()),SerializeUtils.serialize(session.getYid()), sessionExpire);
            redisManager.set(SerializeUtils.serialize(session.getYid()),SerializeUtils.serialize(session),sessionExpire);

        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.newSession is error: ",e);
            yid = null;

        }
        return yid;
    }

    @Override
    public String newSession(String appid, String weixinConfigId, String openid, String unionid, String mobile, String email, String userIp, SourceEnum weixinType, Integer sessionExpire,String yidValue) {
        UserSession session = new UserSession();
        BASE64Encoder encoder = new BASE64Encoder();
        String yid = yidValue;
        session.setAppid(appid);
        session.setWeixinConfigId(weixinConfigId);
        session.setOpenid(openid);
        session.setUnionid(unionid);
        session.setMobile(mobile);
        session.setEmail(email);
        session.setUserIp(userIp);
        session.setType(weixinType);
        try{
            byte[] oldSession = redisManager.get(SerializeUtils.serialize(session.getOpenid()+weixinType.getName()));
            if(!SerializeUtils.isEmpty(oldSession)){
                if(StringUtil.isEmpty(yid)) {
                    yid = (String) SerializeUtils.deserialize(oldSession);
                }else {
                    String oldYid = (String) SerializeUtils.deserialize(oldSession);
                    redisManager.del(SerializeUtils.serialize(oldYid));
                }
            }else {
                if(StringUtil.isEmpty(yid)) {
                    yid = encoder.encode(SerializeUtils.serialize(UUIDGenerator.getUUID() + RandomUtil.getSNCode(TypeEnum.SESSIONID)));
                }
            }
            yid = yid.trim();
            session.setYid(yid);
            /**
             * 配置小于微信给出的token过期时间就用配置的时间，否则就用微信的时间
             */
            if(weixinType==SourceEnum.WEIXIN2C||weixinType==SourceEnum.WEIXIN2B) {
                if (DiamondYdSystemConfigHolder.getInstance().sessionExpire > sessionExpire) {
                    sessionExpire = DiamondYdSystemConfigHolder.getInstance().sessionExpire;
                }
            }
            if(weixinType==SourceEnum.WEIXIN2C){
                YdWeixinUserInfo userInfo = ydWeixinUserInfoMapper.selectByOpenid(weixinConfigId,openid);
                if(userInfo!=null){
                    if(StringUtil.isNotEmpty(userInfo.getUnionid())) {
                        session.setUnionid(userInfo.getUnionid());
                    }
                    if(StringUtil.isNotEmpty(userInfo.getMobile())){
                        session.setMobile(userInfo.getMobile());
                    }
                }
            }
            logger.info("sessionExpire:"+sessionExpire);
            if(StringUtil.isEmpty(session.getWeixinConfigId())) {
                redisManager.set(SerializeUtils.serialize(session.getOpenid()+weixinType.getName()),SerializeUtils.serialize(session.getYid()), sessionExpire);
            }
            redisManager.set(SerializeUtils.serialize(session.getYid()),SerializeUtils.serialize(session),sessionExpire);

        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.newSession is error: ",e);
            yid = null;

        }
        return yid;
    }

    @Override
    public String newSessionWithTest(String openid,String unionid,String mobile,String email,String userIp,SourceEnum weixinType,Integer sessionExpire) {
        UserSession session = new UserSession();
        String yid = null;
        session.setOpenid(openid);
        session.setUnionid(unionid);
        session.setMobile(mobile);
        session.setEmail(email);
        session.setUserIp(userIp);
        session.setType(weixinType);
        try{
            yid = UUIDGenerator.getUUID();
            byte[] oldSession = redisManager.get(SerializeUtils.serialize(MessageFormat.format("{0}{1}",session.getOpenid(),weixinType.getName())));
            if(!SerializeUtils.isEmpty(oldSession)){
                yid = (String)SerializeUtils.deserialize(oldSession);
//                redisManager.del(SerializeUtils.serialize(oldYid));
            }
            yid = yid.trim();
            session.setYid(yid);
            if(weixinType==SourceEnum.WEIXIN2C){
                YdConsumerInfo userInfo = ydConsumerInfoMapper.selectByOpenid(openid);
                if(userInfo!=null){
                    if(StringUtil.isNotEmpty(userInfo.getUnionid())) {
                        session.setUnionid(userInfo.getUnionid());
                    }
                    if(StringUtil.isNotEmpty(userInfo.getMobile())){
                        session.setMobile(userInfo.getMobile());
                    }
                }
            }
            redisManager.set(SerializeUtils.serialize(session.getOpenid()+weixinType.getName()),SerializeUtils.serialize(session.getYid()), sessionExpire);
            redisManager.set(SerializeUtils.serialize(session.getYid()),SerializeUtils.serialize(session),sessionExpire);

        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.newSessionWithTest is error: ",e);
            yid = null;

        }
        return yid;
    }

    @Override
    public String newSessionWithTest(String appid, String weixinConfigId, String openid, String unionid, String mobile, String email, String userIp, SourceEnum weixinType, Integer sessionExpire) {
        UserSession session = new UserSession();
        String yid = null;
        session.setAppid(appid);
        session.setWeixinConfigId(weixinConfigId);
        session.setOpenid(openid);
        session.setUnionid(unionid);
        session.setMobile(mobile);
        session.setEmail(email);
        session.setUserIp(userIp);
        session.setType(weixinType);
        try{
            yid = UUIDGenerator.getUUID();
            byte[] oldSession = redisManager.get(SerializeUtils.serialize(MessageFormat.format("{0}{1}",session.getOpenid(),weixinType.getName())));
            if(!SerializeUtils.isEmpty(oldSession)){
                yid = (String)SerializeUtils.deserialize(oldSession);
//                redisManager.del(SerializeUtils.serialize(oldYid));
            }
            yid = yid.trim();
            session.setYid(yid);
            if(weixinType==SourceEnum.WEIXIN2C){
                YdWeixinUserInfo userInfo = ydWeixinUserInfoMapper.selectByOpenid(weixinConfigId,openid);
                if(userInfo!=null){
                    if(StringUtil.isNotEmpty(userInfo.getUnionid())) {
                        session.setUnionid(userInfo.getUnionid());
                    }
                    if(StringUtil.isNotEmpty(userInfo.getMobile())){
                        session.setMobile(userInfo.getMobile());
                    }
                }
            }
            if(StringUtil.isEmpty(session.getWeixinConfigId())){
                redisManager.set(SerializeUtils.serialize(session.getOpenid() + weixinType.getName()), SerializeUtils.serialize(session.getYid()), sessionExpire);
            }
            redisManager.set(SerializeUtils.serialize(session.getYid()),SerializeUtils.serialize(session),sessionExpire);

        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.newSessionWithTest is error: ",e);
            yid = null;

        }
        return yid;
    }

    @Override
    public UserSession getSession(String yid) {
        UserSession session = null;
        try{
            byte[] sessionBytes = redisManager.get(SerializeUtils.serialize(yid.trim()));
            if(SerializeUtils.isEmpty(sessionBytes)){
                logger.error("WeixinSessionServiceImpl.getSession is null : "+yid.trim());
                return null;
            }
            session = (UserSession)SerializeUtils.deserialize(sessionBytes);

        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.getSession is error: ",e);
            session = null;
        }
        return session;
    }

    @Override
    public UserSession getSessionByOpenid(String openid,SourceEnum weixinType) {
        UserSession session = null;
        if(weixinType==null){
            return null;
        }
        try{
            byte[] yidBytes = redisManager.get(SerializeUtils.serialize(openid+weixinType.getName()));
            if(SerializeUtils.isEmpty(yidBytes)){
                return null;
            }
            String yid = (String)SerializeUtils.deserialize(yidBytes);
            byte[] sessionBytes = redisManager.get(SerializeUtils.serialize(yid.trim()));
            if(SerializeUtils.isEmpty(sessionBytes)){
                return null;
            }
            session = (UserSession)SerializeUtils.deserialize(sessionBytes);

        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.getSession is error: ",e);
            session = null;
        }
        return session;
    }

    @Override
    public String updateSessionForUserInfo(String yid, String unionid,String mobile,String email,String userIp ) {
        UserSession session = this.getSession(yid);
        if(session==null){ return null;}
        try{
            if(StringUtil.isNotBlank(unionid)) {
                session.setUnionid(unionid);
            }
            if(StringUtil.isNotBlank(mobile)) {
                session.setMobile(mobile);
            }
            if(StringUtil.isNotBlank(email)) {
                session.setEmail(email);
            }
            if(StringUtil.isNotBlank(userIp)){
                session.setUserIp(userIp);
            }
            redisManager.set(SerializeUtils.serialize(MessageFormat.format("{0}{1}",session.getOpenid(),session.getType().getName())),SerializeUtils.serialize(session.getYid()),DiamondYdSystemConfigHolder.getInstance().sessionExpire);
            redisManager.set(SerializeUtils.serialize(session.getYid()),SerializeUtils.serialize(session),DiamondYdSystemConfigHolder.getInstance().sessionExpire);

        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.updateSessionForUserInfo is error: ",e);
            session.setYid(null);
        }
        return session.getYid();
    }

    @Override
    public String updateSession(String yid) {
        UserSession session = this.getSession(yid);
        if(session==null){ return null;}
        try {
            session.setYid(yid);
            redisManager.set(SerializeUtils.serialize(MessageFormat.format("{0}{1}",session.getOpenid(),session.getType().getName())),SerializeUtils.serialize(session.getYid()),DiamondYdSystemConfigHolder.getInstance().sessionExpire);
            redisManager.set(SerializeUtils.serialize(session.getYid()), SerializeUtils.serialize(session), DiamondYdSystemConfigHolder.getInstance().sessionExpire);
        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.updateSession is error: ",e);
            session.setYid(null);
        }
        return session.getYid();
    }

    @Override
    public String getSessionidByOpenId(String openid,SourceEnum weixinType) {
        String yid;
        try{
            byte[] bytes = redisManager.get(SerializeUtils.serialize(MessageFormat.format("{0}{1}",openid,weixinType.getName())));
            if(SerializeUtils.isEmpty(bytes)){
                return null;
            }
            yid = (String)SerializeUtils.deserialize(bytes);
        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.getSessionidByOpenId is error: ",e);
            yid = null;
        }
        return yid;
    }

    @Override
    public boolean deleteSession(String yid) {
        boolean isSuccess = true;
        UserSession session = this.getSession(yid);
        if(session==null){ return false;}
        try{
            redisManager.del(SerializeUtils.serialize(MessageFormat.format("{0}{1}",session.getOpenid(),session.getType().getName())));
            redisManager.del(SerializeUtils.serialize(session.getYid()));

        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.deleteSession is error: ",e);
            isSuccess = false;

        }
        return isSuccess;
    }

    @Override
    public boolean deleteSessionByOpenid(String openid,SourceEnum weixinType) {
        if(weixinType==null){
            return false;
        }
        boolean isSuccess = true;
        UserSession session = this.getSessionByOpenid(openid,weixinType);
        if(session==null){ return false;}
        try{
            redisManager.del(SerializeUtils.serialize(MessageFormat.format("{0}{1}",session.getOpenid(),session.getType().getName())));
            redisManager.del(SerializeUtils.serialize(session.getYid()));

        }catch (Exception e){
            logger.error("WeixinSessionServiceImpl.deleteSessionByOpenid is error: ",e);
            isSuccess = false;

        }
        return isSuccess;
    }
}
