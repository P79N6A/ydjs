package com.yd.ydsp.biz.config;

import com.alibaba.edas.configcenter.config.ConfigChangeListener;
import com.alibaba.edas.configcenter.config.ConfigService;
import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.cp.model.YdGoodsInfoVO;
import com.yd.ydsp.common.lang.StringUtil;

import java.util.List;

/**
 * 自营商品信息配置
 * @author zengyixun
 * @date 17/12/5
 */
public class DiamondYDGoodsConfig {
    // 属性/开关
    public static List<YdGoodsInfoVO> config;
    //初始化的时候，给配置添加监听
    public void init() {
        ConfigService.addListener("com.yd.ydsp.biz.config.DiamondYDGoodsConfig", "a59398c6-86a8-4b12-9c6c-e60dc1511d1e",
                new ConfigChangeListener() {
                    public void receiveConfigInfo(String configInfo) {
                        try {
                            //当配置更新后，马上获取新的配置
                            config = JSON.parseArray(configInfo,YdGoodsInfoVO.class);
                            System.out.println(configInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static YdGoodsInfoVO getGoodsInfoVoById(String goodsId){
        if(config==null||config.size()<=0){
            return null;
        }
        for(YdGoodsInfoVO ydGoodsInfoVO:config){
            if(StringUtil.equals(ydGoodsInfoVO.getGoodId(),goodsId)){
                return ydGoodsInfoVO;
            }
        }

        return null;
    }


}
