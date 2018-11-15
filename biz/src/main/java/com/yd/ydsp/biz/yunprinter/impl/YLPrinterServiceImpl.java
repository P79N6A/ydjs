package com.yd.ydsp.biz.yunprinter.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yd.ydsp.biz.yunprinter.YLPrinterService;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.EncryptionUtil;
import com.yd.ydsp.common.utils.HttpUtil;
import org.apache.http.client.fluent.Form;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * https://www.10ss.net/print/php/sample.php  这是配置WiFi的地址但只能在微信里面显示
 * Created by zengyixun on 17/9/25.
 */
public class YLPrinterServiceImpl implements YLPrinterService {


    /**
     * 易联云打印开放api主站url
     */
    private String apiUrl;
    private String appId;
    private String secret;
    private String grantType= "client_credentials";
    private String scope = "all";
    private boolean cacheOn=true;
    private String cashAccessToken="YLPrinterAccessToken";
    private String cashRefreshToken="YLPrinterRefreshToken";
    private String accessToken;
    private String refreshToken;

    /**

     * 获取token  and  refresh Token

     */
    public static final String GET_TOKEN = "/oauth/oauth";

    /**

     * 急速授权

     */
    public static final String SPEED_AUTHORIZE = "/oauth/scancodemodel";


    /**

     * api 打印

     */
    public static final String API_PRINT = "/print/index";

    /**

     * api 添加终端授权

     */
    public static final String API_ADD_PRINTER = "/printer/addprinter";

    /**

     * api 删除终端授权

     */
    public static final String API_DELET_PRINTER = "/printer/deleteprinter";

    /**

     * api 添加应用菜单

     */
    public static final String API_ADD_PRINT_MENU = "/printmenu/addprintmenu";

    /**

     * api 关机重启接口

     */
    public static final String API_SHUTDOWN_RESTART = "/printer/shutdownrestart";

    /**

     * api 声音调节接口

     */
    public static final String API_SET_SOUND = "/printer/setsound";

    /**

     * api 获取机型打印宽度接口

     */
    public static final String API_PRINT_INFO = "/printer/printinfo";

    /**

     * api 获取机型软硬件版本接口

     */
    public static final String API_GET_VIERSION = "/printer/getversion";

    /**

     * api 取消所有未打印订单

     */
    public static final String API_CANCEL_ALL = "/printer/cancelall";

    /**

     * api 取消单条未打印订单

     */
    public static final String API_CANCEL_ONE = "/printer/cancelone";

    /**

     * api 设置logo接口

     */
    public static final String API_SET_ICON = "/printer/seticon";

    /**

     * api 取消logo接口

     */
    public static final String API_DELET_ICON = "/printer/deleteicon";

    /**

     * api 接单拒单设置接口

     */
    public static final String API_GET_ORDER = "/printer/getorder";

    /**

     * api 打印方式接口

     */
    public static final String API_BTN_PRINT = "/printer/btnprint";


    @Resource
    RedisManager redisManager;

    public void setApiUrl(String apiUrl){ this.apiUrl = apiUrl; }
    public void setAppId(String appId){ this.appId = appId; }
    public void setSecret(String secret){ this.secret = secret; }
    public void setCacheOn(boolean cacheOn){ this.cacheOn = cacheOn; }


    private String generateSign(long timestamp){
        String tmpStr = appId+ timestamp +secret;
        return EncryptionUtil.md5Hex(tmpStr).toLowerCase();
    }

    private Form getPublicForm(long timestamp) throws IOException, ClassNotFoundException {
        Form form = Form.form();
        String sign = this.generateSign(timestamp);
        form.add("client_id",appId);
        form.add("sign",sign);
        form.add("id", UUID.randomUUID().toString());
        form.add("timestamp",Long.toString(timestamp));
        form.add("access_token",this.getAccessToken());
        return form;
    }

    @Override
    public void setAccessToken() throws IOException {
        String url = apiUrl+GET_TOKEN;
        long timestamp = Instant.now().getEpochSecond();
        String sign = this.generateSign(timestamp);
        Form form = Form.form();
        form.add("client_id",appId);
        form.add("grant_type",grantType);
        form.add("scope",scope);
        form.add("sign",sign);
        form.add("id", UUID.randomUUID().toString());
        form.add("timestamp",Long.toString(timestamp));
        String result = HttpUtil.post(url,form);
        JSONObject resultJson = JSON.parseObject(result);
        String errorMsg = (String)resultJson.get("error_description");
        if(errorMsg.trim().equals("success")){
            this.accessToken = (String)resultJson.getJSONObject("body").get("access_token");
            this.refreshToken = (String)resultJson.getJSONObject("body").get("refresh_token");
            if(this.cacheOn) {
                //10天有效，实际上是30天有效，为了保险设置为10天
                redisManager.set(SerializeUtils.serialize(cashAccessToken),SerializeUtils.serialize(accessToken),864000);
                redisManager.set(SerializeUtils.serialize(cashRefreshToken),SerializeUtils.serialize(refreshToken),864000);
            }
        }

    }

    @Override
    public void refreshToken() throws IOException, ClassNotFoundException {
        String url = apiUrl+GET_TOKEN;
        long timestamp = Instant.now().getEpochSecond();
        String sign = this.generateSign(timestamp);
        Form form = Form.form();
        form.add("client_id",appId);
        form.add("grant_type",grantType);
        form.add("scope",scope);
        form.add("sign",sign);
        form.add("id", UUID.randomUUID().toString());
        form.add("timestamp",Long.toString(timestamp));
        form.add("refresh_token",this.getRefreshToken());
        String result = HttpUtil.post(url,form);
        JSONObject resultJson = JSON.parseObject(result);
        String errorMsg = (String)resultJson.get("error_description");
        if(errorMsg.trim().equals("success")){
            this.accessToken = (String)resultJson.getJSONObject("body").get("access_token");
            this.refreshToken = (String)resultJson.getJSONObject("body").get("refresh_token");
            if(this.cacheOn) {
                //10天有效，实际上是30天有效，为了保险设置为10天
                redisManager.set(SerializeUtils.serialize(cashAccessToken),SerializeUtils.serialize(accessToken),864000);
                redisManager.set(SerializeUtils.serialize(cashRefreshToken),SerializeUtils.serialize(refreshToken),864000);
            }
        }

    }

    @Override
    public String getRefreshToken() throws IOException, ClassNotFoundException {
        if(this.cacheOn) {
            byte[] bToken = redisManager.get(SerializeUtils.serialize(cashRefreshToken));
            if (bToken != null) {
                refreshToken = (String) SerializeUtils.deserialize(bToken);
            }
        }
        return refreshToken;
    }


    @Override
    public String getAccessToken() throws IOException, ClassNotFoundException {
        if(this.cacheOn) {
            byte[] bToken = redisManager.get(SerializeUtils.serialize(cashAccessToken));
            if (bToken != null) {
                accessToken = (String) SerializeUtils.deserialize(bToken);
            }
        }
        return accessToken;
    }
    //生成10位服务器时间戳:Instant.now().getEpochSecond();
    //生成UUID:UUID.randomUUID().toString();

    @Override
    public boolean addprinter(String machineCode, String msign) throws IOException, ClassNotFoundException {
        if(StringUtil.isBlank(machineCode)||StringUtil.isBlank(msign)){
            return false;
        }

        String url = apiUrl+API_ADD_PRINTER;
        long timestamp = Instant.now().getEpochSecond();
        Form form = this.getPublicForm(timestamp);
        form.add("machine_code",machineCode);
        form.add("msign",msign);
        String result = HttpUtil.post(url,form);
        JSONObject resultJson = JSON.parseObject(result);
        String errorMsg = (String)resultJson.get("error_description");
        if(errorMsg.trim().equals("success")){
            return true;
        }else{
            return false;
        }
    }



}
