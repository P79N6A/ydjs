package com.yd.ydsp.biz.config;

import com.alibaba.edas.configcenter.config.ConfigChangeListener;
import com.alibaba.edas.configcenter.config.ConfigService;
import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.cp.model.YdDeliveryInfoVO;

import java.util.List;

/**
 * 自营商品信息配置
 * @author zengyixun
 * @date 17/12/5
 */
public class DiamondYDdeliveryConfig {
    // 属性/开关
    public static List<YdDeliveryInfoVO> config;
    //初始化的时候，给配置添加监听
    public void init() {
        ConfigService.addListener("com.yd.ydsp.biz.config.DiamondYDdeliveryConfig", "a59398c6-86a8-4b12-9c6c-e60dc1511d1e",
                new ConfigChangeListener() {
                    public void receiveConfigInfo(String configInfo) {
                        try {
                            //当配置更新后，马上获取新的配置
                            config = JSON.parseArray(configInfo,YdDeliveryInfoVO.class);
                            System.out.println(configInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static YdDeliveryInfoVO getDeliveryInfoById(Integer id){
        if(config==null||config.size()<=0){
            return null;
        }
        for(YdDeliveryInfoVO deliveryInfoVO:config){
            if(deliveryInfoVO.getId().intValue()==id.intValue()){
                return deliveryInfoVO;
            }
        }

        return null;
    }


}
