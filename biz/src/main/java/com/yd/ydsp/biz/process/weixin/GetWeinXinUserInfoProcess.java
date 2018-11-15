package com.yd.ydsp.biz.process.weixin;

import com.yd.ydsp.biz.process.JobProcessor;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.common.enums.SourceEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zengyixun on 17/9/4.
 */

/**
 * 根据指定的公众号weixinAppName以及openid,放到线程池里更新用户信息，特别是更新unionid
 */

public class GetWeinXinUserInfoProcess implements JobProcessor {

    private static final Logger logger = LoggerFactory.getLogger(GetWeinXinUserInfoProcess.class);

    @Resource
    public UserinfoService userinfoService;

    @Async
    @Override
    public void process(Object o) {
        try {
            Map in = (HashMap<String,Object>)o;
            SourceEnum weixinType = SourceEnum.valueOf((String)in.get("weixinType"));
            String openid = (String)in.get("openid");
            Boolean isSns = (Boolean)in.get("isSns");
            if(weixinType==SourceEnum.WEIXIN2B){
                boolean success = userinfoService.updateCPUserInfo(openid,isSns);
                if(success) {
                    logger.info("openid:" + openid + " , updateCPUserInfo isSuccess!");
                }else{
                    logger.error("openid:" + openid + " , updateCPUserInfo isError!");
                }
            }
            if(weixinType==SourceEnum.WEIXIN2C){
                boolean success = userinfoService.update2CUserInfo(openid,isSns);
                if(success) {
                    logger.info("openid:" + openid + " , update2CUserInfo isSuccess!");
                }else {
                    logger.error("openid:"+openid+" , update2CUserInfo isError!");
                }
            }

        }catch (Exception ex){
            logger.error("GetWeinXinUserInfoProcess error: ",ex);
        }catch (Throwable tr){
            logger.error("GetWeinXinUserInfoProcess Throwable error: ",tr.fillInStackTrace());
        }
    }
}
