package com.yd.ydsp.biz.yunprinter.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.yunprinter.FEPrinterService;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.EncryptionUtil;
import com.yd.ydsp.common.utils.HttpUtil;
import org.apache.http.client.fluent.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/9/26.
 */
public class FEPrinterServiceImpl implements FEPrinterService {

    public static final Logger logger = LoggerFactory.getLogger(FEPrinterServiceImpl.class);

    private String apiUrl;
    private String user;
    private String userKey;
    private String debug="0";

    public void setApiUrl(String apiUrl){ this.apiUrl = apiUrl; }
    public void setUser(String user){ this.user = user; }
    public void setUserKey(String userKey){ this.userKey = userKey; }
    public void setDebug(String debug){ this.debug = debug; }


    protected Form getPublicForm(String apiname){
        Form form = Form.form();
        long stime = Instant.now().getEpochSecond();
        String sig = EncryptionUtil.sha1Hex(user+userKey+stime);
        form.add("user",user);
        form.add("stime",Long.toString(stime));
        form.add("apiname",apiname);
        form.add("debug",debug);
        form.add("sig",sig);
        return form;
    }

    @Override
    public Map<String, Object> printerAddlist(List<String> printInfo) {
        String printerContent = org.apache.commons.lang.StringUtils.join(printInfo.toArray(),"\n");
        Form form = this.getPublicForm("Open_printerAddlist");
        form.add("printerContent",printerContent);
        String resultStr = HttpUtil.post(this.apiUrl,form);
        Map<String, Object> result = JSON.parseObject(resultStr,Map.class);
        if(result.containsKey("ok")){
            result.put("success",true);
        }else {
            result.put("success",false);
        }

        return result;
    }

    @Override
    public String printMsg(String sn,String orderMsg,String times) {
        if(StringUtil.isBlank(orderMsg)||StringUtil.isBlank(sn)){
            return null;
        }
        if(StringUtil.isBlank(times)){
            times = "1";
        }
        Form form = this.getPublicForm("Open_printMsg");
        form.add("sn",sn);
        form.add("content",orderMsg);
        form.add("times",times);
        String resultStr = HttpUtil.post(this.apiUrl,form);
        if(StringUtil.isNotBlank(resultStr)){
            logger.info("printMsg is : "+resultStr);
        }
        Map<String, Object> result = JSON.parseObject(resultStr,Map.class);
        if(result.containsKey("ret")){
            int ret = (Integer)result.get("ret");
            if(ret==0){
                return (String)result.get("data");
            }else {
                if(ret==1002){
                    logger.error("设备SN-"+sn+"是不存在的，因此导致云打印不成功!");
                }
                return null;
            }
        }else {
            return null;
        }
    }

    @Override
    public boolean queryPrintOrderState(String printOrderId) {
        boolean result = false;
        if(StringUtil.isBlank(printOrderId)){
            return result;
        }
        Form form = this.getPublicForm("Open_queryOrderState");
        form.add("orderid",printOrderId);
        String resultStr = HttpUtil.post(this.apiUrl,form);
        Map<String, Object> resultMap = JSON.parseObject(resultStr,Map.class);
        if(resultMap.containsKey("ret")) {
            int ret = (Integer) resultMap.get("ret");
            if(ret==0){
                result = (Boolean)resultMap.get("data");
            }
        }
        return result;
    }
}
