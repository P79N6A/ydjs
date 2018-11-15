package com.yd.ydsp.biz.weixin;

import com.yd.ydsp.biz.weixin.model.WeixinAppCode2SessionResult;
import com.yd.ydsp.biz.weixin.model.WeixinTokenInfo;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.common.model.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/5/16.
 */
public interface WeixinOauth2Service {

    /**
     * 调用微信接口时有的需要公众号的access_token，这个token根据appid与secret来取得，过期时间为expires_in:7200秒也就是2个小时
     */

    String getAccessToken();

    /**
     * 根据accessToken取得
     * 过期时间为expires_in:7200秒也就是2个小时
     * @return
     */
    String getJsapiTicket();

    /**
     * 签名算法

     签名生成规则如下：参与签名的字段包括noncestr（随机字符串）, 有效的jsapi_ticket, timestamp（时间戳）, url（当前网页的URL，不包含#及其后面部分） 。对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1。这里需要注意的是所有参数名均为小写字符。对string1作sha1加密，字段名和字段值都采用原始值，不进行URL 转义。
     * @param url
     * @return
     */
    Map<String, String> getJsapiTicket(String url);

    String getAppid();

    /**
     * 微信跳转网页认证
     * @param code
     * @return
     */
    Result<WeixinTokenInfo> authorize(String code);



    Result<WeixinTokenInfo> refreshToken(String refreshToken);

    /**
     * 取用户信息
     * @param openId
     * @param accessToken
     * @return
     */
    Result<WeixinUserInfo> getWeiXinUserInfo(String openId, String accessToken,boolean isSns);
    Result<Map<String,Object>> accessTokenIsOk(String openId,String accessToken);
    Result<List<String>> getWeiXinServerIPList(String accessToken);

    /**
     * 公众号接收微信消息时，我们在微信设置的验证token
     * @return
     */
    String getWeiXinMsgToken();

    Result<WeixinAppCode2SessionResult> weixinSmallAppAuthorize(String code);

}
