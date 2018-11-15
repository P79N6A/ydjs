package com.yd.ydsp.biz.ys7;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.common.enums.monitor.MonitorAddressTypeEnum;
import com.yd.ydsp.common.enums.monitor.MonitorBaseAddressTypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.HttpUtil;
import org.apache.http.client.fluent.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/11/27
 */
public class Ys7ServiceImpl implements Ys7Service {

    public static final Logger logger = LoggerFactory.getLogger(Ys7ServiceImpl.class);

    @Resource
    RedisManager redisManager;

    @Override
    public String getToken() throws IOException, ClassNotFoundException {

        byte[] bToken = redisManager.get(SerializeUtils.serialize("Ys7Token"));
        if (bToken != null) {
            return (String) SerializeUtils.deserialize(bToken);
        }else{
            Form form = Form.form();
            form.add("appKey", DiamondYdSystemConfigHolder.getInstance().ysAppKey);
            form.add("appSecret",DiamondYdSystemConfigHolder.getInstance().ysSecret);
            String resultStr = HttpUtil.post("https://open.ys7.com/api/lapp/token/get",form);
            Map<String, Object> result = JSON.parseObject(resultStr,Map.class);
            if(((String)result.get("code")).equals("200"))
            {
                String accessToken = (String)((Map)result.get("data")).get("accessToken");
                if(StringUtil.isNotBlank(accessToken)) {
                    /**
                     * 注意：获取到的accessToken有效期是7天，请在即将过期或者接口报错10002时重新获取，请勿频繁调用，频繁调用将会被萤石云被拉入限制黑名单。
                     * 所以，我们在这里直接设置为3天失效token
                     */
                    redisManager.set(SerializeUtils.serialize("Ys7Token"), SerializeUtils.serialize(accessToken), DiamondYdSystemConfigHolder.getInstance().ysTokenExpire);
                }
                return accessToken;

            }else{
                return null;
            }

        }

    }

    @Override
    public void delToken() throws IOException {
        redisManager.del(SerializeUtils.serialize("Ys7Token"));
    }

    @Override
    public String getPlayerAddress(String deviceSerial, Integer channelNo, Integer expireTime, MonitorAddressTypeEnum type) throws IOException, ClassNotFoundException {
        String payStr = null;
        Form form = Form.form();
        form.add("accessToken", this.getToken());
        form.add("deviceSerial",deviceSerial);
        form.add("channelNo",Integer.toString(channelNo));
        form.add("expireTime",Integer.toString(expireTime));
        String resultStr = HttpUtil.post("https://open.ys7.com/api/lapp/live/address/limited",form);
        Map<String, Object> result = JSON.parseObject(resultStr,Map.class);
        if(((String)result.get("code")).equals("200"))
        {

            payStr = (String)((Map)result.get("data")).get(type.getName());


        }
        logger.info("deviceSerial is :"+deviceSerial+" payAddress is: "+ payStr);
        return payStr;
    }

    @Override
    public String getPlayerAddress(String deviceSerial, Integer channelNo, Integer expireTime) throws IOException, ClassNotFoundException {
        return this.getPlayerAddress(deviceSerial,channelNo,expireTime,MonitorAddressTypeEnum.LIVEADDRESS);
    }

    @Override
    public String getPlayerAddress(String deviceSerial, Integer channelNo) throws IOException, ClassNotFoundException {
        return this.getPlayerAddress(deviceSerial,channelNo,300);
    }

    @Override
    public String getPlayerAddress(String deviceSerial) throws IOException, ClassNotFoundException {
        return this.getPlayerAddress(deviceSerial,1);
    }

    @Override
    public String getPlayerOnlyAddress(String deviceSerial, Integer channelNo, MonitorBaseAddressTypeEnum type) throws IOException, ClassNotFoundException {
        String payStr = null;
        Form form = Form.form();
        form.add("accessToken", this.getToken());
        form.add("source",deviceSerial+":"+channelNo.intValue());
        String resultStr = HttpUtil.post("https://open.ys7.com/api/lapp/live/address/get",form);
        Map<String, Object> result = JSON.parseObject(resultStr,Map.class);
        if(((String)result.get("code")).equals("200"))
        {
            List dataList = (List)result.get("data");
            if(dataList.size()>0){
                payStr = (String)((Map)dataList.get(0)).get(type.getName());
            }
        }

        return payStr;
    }

    @Override
    public Map<String,Object> getChannelInfo(String deviceSerial, Integer channelNo) throws IOException, ClassNotFoundException {
        Form form = Form.form();
        form.add("accessToken", this.getToken());
        form.add("deviceSerial",deviceSerial);
        form.add("channelNo",Integer.toString(channelNo));
        String resultStr = HttpUtil.post("https://open.ys7.com/api/lapp/device/camera/list",form);
        Map<String, Object> result = JSON.parseObject(resultStr,Map.class);
        if(((String)result.get("code")).equals("200"))
        {

            List<Map<String,Object>> data = (List<Map<String,Object>>)result.get("data");
            if(data==null || data.size()<=0){
                return null;
            }
            return data.get(0);

        }
        return null;
    }
}
