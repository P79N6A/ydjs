package com.yd.ydsp.biz.config;

import com.alibaba.edas.configcenter.config.ConfigChangeListener;
import com.alibaba.edas.configcenter.config.ConfigService;
import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.model.YdReleaseFileInfoVO;
import com.yd.ydsp.biz.config.model.YdReleaseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 自营商品信息配置
 * @author zengyixun
 * @date 17/12/5
 */
public class DiamondReleaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DiamondReleaseConfig.class);
    // 属性/开关
    public static YdReleaseVO config;
    //初始化的时候，给配置添加监听
    public void init() {
        ConfigService.addListener("com.yd.ydsp.biz.config.DiamondReleaseConfig", "a59398c6-86a8-4b12-9c6c-e60dc1511d1e",
                new ConfigChangeListener() {
                    public void receiveConfigInfo(String configInfo) {
                        try {
                            //当配置更新后，马上获取新的配置
                            config = JSON.parseObject(configInfo,YdReleaseVO.class);
                            logger.info(configInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static YdReleaseFileInfoVO getFileInfo(Integer id){
        List<YdReleaseFileInfoVO> ydReleaseFileInfoVOS = config.getYdReleaseFileInfoVOList();
        if(ydReleaseFileInfoVOS==null||ydReleaseFileInfoVOS.size()<=0){
            return null;
        }
        for(YdReleaseFileInfoVO fileInfoVO:ydReleaseFileInfoVOS){
            if(fileInfoVO.getId().intValue()==id.intValue()){
                return fileInfoVO;
            }
        }

        return null;
    }

    public static Integer getCpVersion(){
        return config.getCpSysVersion();
    }


}
