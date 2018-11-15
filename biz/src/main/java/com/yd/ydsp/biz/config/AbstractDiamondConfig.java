package com.yd.ydsp.biz.config;

import com.alibaba.edas.configcenter.config.ConfigChangeListener;
import com.alibaba.edas.configcenter.config.ConfigService;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.YDLogUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Description: DiamondConstantsConfig
 * User: zengyixun
 * Date: 2017/11/15
 * Time: 下午5:28
 */
public class AbstractDiamondConfig {

    private final static Logger logger = LoggerFactory.getLogger(AbstractDiamondConfig.class);

    protected String GROUP_ID = "a59398c6-86a8-4b12-9c6c-e60dc1511d1e";

    protected String DATA_ID;

    protected Object propertyInstance;

    public static final long DIAMOND_GET_DATA_TIMEOUT=10*1000;

    private static final String DATA_SPLIT = "\n";//换行分割

    private boolean init = false; // 初始化标志，表明正在初始化过程中

    private static final String DATA_KEY_VALUE_SPLIT = "=";

    public static Executor EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public void init(){
        if(init) {
            logger.error("failed@DiamondConfigHasBeanInit,{}", DATA_ID);
            return ;
        }
        init = true;

        logger.warn("info@beigin-to-init,{},date={}", DATA_ID, new Date());
        ConfigService.addListener(DATA_ID, GROUP_ID, new ConfigChangeListener() {

            public void receiveConfigInfo(String configInfo) {
                convertInfoToConfig(configInfo);
            }

            public Executor getExecutor() {
                return EXECUTOR;
            }

        });
        try {
            String configInfo = ConfigService.getConfig(DATA_ID, GROUP_ID, DIAMOND_GET_DATA_TIMEOUT);
            convertInfoToConfig(configInfo);
        } catch (IOException e) {
            logger.error("failed@AbstractDiamondConfig:error_code={},error_msg={},dataId={},group={}",
                    ErrorCodeConstants.YD00901.getErrorCode(), ErrorCodeConstants.YD00901.getErrorMessage(),
                    DATA_ID, GROUP_ID, e);
        }
        logger.warn("info@finish-to-init,{},date={}", DATA_ID, new Date());
    }


    protected void convertInfoToConfig(String configInfo){
        if(StringUtils.isNotBlank(configInfo)){
            try{
                logger.error("info@receiveConfigInfo: dataId={},configInfo={},date={}", DATA_ID, configInfo, new Date());
                String[] configInfos = configInfo.split(DATA_SPLIT);
                for(String args : configInfos){
                    String[] keyAndValue = args.split(DATA_KEY_VALUE_SPLIT,2);
                    if(keyAndValue.length!=2){
                        continue;
                    }
                    BeanUtils.copyProperty(propertyInstance,
                            StringUtil.trim(keyAndValue[0]),
                            StringUtil.trim(keyAndValue[1]));
                }

                logger.error("AbstractDiamondConfig.convertInfoToConfig, result:" + YDLogUtil.reflectionToString(propertyInstance));
            }catch(Throwable e){
                logger.error("DATA_ID,convertInfoToConfig,date="+new Date(),e);
            }
        }
    }

    public Logger getLogger() {
        return logger;
    }
}