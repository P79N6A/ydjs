package com.yd.ydsp.biz.weixin;

import com.yd.ydsp.biz.weixin.model.WeixinAppCode2SessionResult;
import com.yd.ydsp.biz.weixin.model.WeixinTokenInfo;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.common.enums.paypoint.WeiXinTypeEnum;
import com.yd.ydsp.common.model.Result;
import com.yd.ydsp.common.weixin.mp.AesException;
import com.yd.ydsp.dal.entity.YdWeixinUserInfo;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/5/16.
 */
public interface WeixinOauth2ShopService {

    String getWeixinOpenAppId();

    /**
     * 取一个公众号的访问token
     * @param appid
     * @return
     */
    String getAccessToken(String appid) throws Exception;
    /**
     * 用户授权公众号访问
     */

    Map<String,Object> authorize(String appid, String code);

    /**
     * 根据accessToken取得
     * 过期时间为expires_in:7200秒也就是2个小时
     * @return
     */
    String getJsapiTicket(String appid);

    /**
     * 签名算法

     签名生成规则如下：参与签名的字段包括noncestr（随机字符串）, 有效的jsapi_ticket, timestamp（时间戳）, url（当前网页的URL，不包含#及其后面部分） 。对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1。这里需要注意的是所有参数名均为小写字符。对string1作sha1加密，字段名和字段值都采用原始值，不进行URL 转义。
     * @param url
     * @return
     */
//    Map<String, String> getJsapiTicket(String weixinConfigId,String url) throws IOException, ClassNotFoundException;

    /**
     * 取用户信息
     * @param openId
     * @param accessToken
     * @return
     */
    Result<WeixinUserInfo> getWeiXinUserInfo(String weixinConfigId,String openId, String accessToken, boolean isSns);
    Result<Map<String,Object>> accessTokenIsOk(String weixinConfigId,String openId, String accessToken);
    Result<List<String>> getWeiXinServerIPList(String weixinConfigId,String accessToken);


    /**
     * 将公众平台回复用户的消息加密打包.
     * <ol>
     * 	<li>对要发送的消息进行AES-CBC加密</li>
     * 	<li>生成安全签名</li>
     * 	<li>将消息密文和安全签名打包成xml格式</li>
     * </ol>
     *
     * @param replyMsg 公众平台待回复用户的消息，xml格式的字符串
     * @param timeStamp 时间戳，可以自己生成，也可以用URL参数的timestamp
     * @param nonce 随机串，可以自己生成，也可以用URL参数的nonce
     *
     * @return 加密后的可以直接回复用户的密文，包括msg_signature, timestamp, nonce, encrypt的xml格式的字符串
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String encryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException;

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * 	<li>利用收到的密文生成安全签名，进行签名验证</li>
     * 	<li>若验证通过，则提取xml中的加密消息</li>
     * 	<li>对消息进行解密</li>
     * </ol>
     *
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param postData 密文，对应POST请求的数据
     *
     * @return 解密后的原文
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decryptMsg(String msgSignature, String timeStamp, String nonce, String postData) throws AesException;

    /**
     * 验证URL
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param echoStr 随机串，对应URL参数的echostr
     *
     * @return 解密之后的echostr
     * @throws AesException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String verifyUrl(String msgSignature, String timeStamp, String nonce, String echoStr) throws AesException;

    public String getComponentVerifyTicket() throws IOException, ClassNotFoundException;

    public void setComponentVerifyTicket(String componentVerifyTicket) throws IOException;

    /**
     * 第三方平台通过自己的component_appid（即在微信开放平台管理中心的第三方平台详情页中的AppID和AppSecret）和component_appsecret，以及component_verify_ticket（每10分钟推送一次的安全ticket）来获取自己的接口调用凭据（component_access_token）
     * 第三方平台component_access_token是第三方平台的下文中接口的调用凭据，也叫做令牌（component_access_token）。每个令牌是存在有效期（2小时）的，且令牌的调用不是无限制的，请第三方平台做好令牌的管理，在令牌快过期时（比如1小时50分）再进行刷新
     * @return
     */
    public String getComponentAccessToken() throws Exception;

    /**
     * 第三方平台通过自己的接口调用凭据（component_access_token）来获取用于授权流程准备的预授权码（pre_auth_code）
     * @return
     */
    public String getPreAuthCode() throws Exception;

    /**
     * 使用授权码换取公众号或小程序的接口调用凭据和授权信息
     * @param queryAuthCode
     * @return
     */
    public String getCPWeiXinAccesssToken(String appid,String queryAuthCode) throws Exception;

    /**
     * 客服接口-发消息
     * @param accessToken
     * @param msg
     */
    public void sendCustormMessage(String accessToken,String msg) throws Exception;

    /**
     * 用户扫我们自己的授权二维码进来后，我们要生成返顺一个真实的微信授权链接
     * @param shopid
     * @param preAuthLinkCode
     * @param redirectUri
     * @return
     */
    String createWeixinAuthLink(String shopid,String preAuthLinkCode, String appid,Integer weixinType,String redirectUri) throws Exception;

    /**
     * 用户成功授权后，微信回调到此接口进行商城创建
     * @param shopid
     * @param authCode 授权成功后，微信会返回这些的格式：redirect_url?auth_code=xxx&expires_in=600， 所以此参数就是从这里的auth_code得来的
     * @return
     * @throws Exception
     */
    boolean authShop(String shopid, WeiXinTypeEnum typeEnum, String authCode) throws Exception;



}
