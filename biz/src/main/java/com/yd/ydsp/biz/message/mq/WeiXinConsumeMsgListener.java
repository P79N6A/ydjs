package com.yd.ydsp.biz.message.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.yd.ydsp.biz.openshop.ShopApplyService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.weixin.WeixinSamll2ShopService;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.redis.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;

public class WeiXinConsumeMsgListener implements MessageListener {
    public static final Logger logger = LoggerFactory.getLogger(WeiXinConsumeMsgListener.class);

    @Resource
    private ShopApplyService shopApplyService;
    @Resource
    private WeixinSamll2ShopService weixinSamll2ShopService;


    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {

        logger.info("WeiXinConsumeMsgListener Receive: " + message);
        try {
            String body = (String)SerializeUtils.deserialize(message.getBody());
            logger.info("WeiXinConsumeMsgListener body is :" + body);
            Map<String,Object> userINfoMap = JSON.parseObject(body,Map.class);
            MqTagEnum mqTagEnum = MqTagEnum.tagOf((String) userINfoMap.get(Constant.MQTAG));
            if(mqTagEnum==MqTagEnum.WEIXINUSERMSG) {
                SourceEnum sourceEnum = SourceEnum.nameOf((String) userINfoMap.get("weixinType"));
                String weixinConfigId = (String) userINfoMap.get("weixinConfigId");
                String openid = (String) userINfoMap.get("openid");
            }
            if(mqTagEnum==MqTagEnum.WEIXINAUTHSHOP) {
                /**
                 * 从申请单转为一个新店铺，申请单的applyid就成为店铺的shopid
                 */
                String applyid = (String) userINfoMap.get("applyid");
                logger.info("开始创建新的商家店铺，店铺的shopid为："+applyid);
                if(!shopApplyService.applyToCreateShop(applyid)){
                    logger.error(applyid+"的申请单创建线上店铺失败了");
//                    return Action.ReconsumeLater;
                }

            }
            if(mqTagEnum==MqTagEnum.WEIXINSMALLCODE) {
                this.uploadWeiXinSmallCode(userINfoMap);
            }
            return Action.CommitMessage;
        } catch (Exception e) {
            logger.error("WeiXinConsumeMsgListener consume is error: ",e);
            //消费失败
            return Action.ReconsumeLater;
        }

    }

    protected boolean uploadWeiXinSmallCode(Map<String,Object> targetMap) throws Exception {
        String appid = (String) targetMap.get("appid");
        Integer templateid= null;
        String version = null;
        String desc = null;
        if(targetMap.containsKey("templateid")){
            templateid = (Integer)targetMap.get("templateid");
            version = (String)targetMap.get("version");
            desc = (String)targetMap.get("desc");
        }
        return weixinSamll2ShopService.uploadWeiXinSmallCode(appid,templateid,version,desc);

    }

}
