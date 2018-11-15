package com.yd.ydsp.biz.manage;

import com.yd.ydsp.biz.manage.model.CpAgentInfoVO;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.client.domian.openshop.YdShopApplyInfoVO;
import com.yd.ydsp.common.model.ResultPage;
import com.yd.ydsp.dal.entity.YdPaypointAgentapplyinfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface YdXiaoerService {
    /**
     * 微信扫码方式登录
     * @param code
     * @param state
     * @return
     */
    WeixinUserInfo loginByWeiXinQrcode(String code, String state);

    /**
     * 手机+密码方式登录
     * @param mobile
     * @param passwd
     * @return
     */
    WeixinUserInfo loginByMobile(String mobile, String passwd);

    /**
     * 检查有没有绑定过手机号
     * @param openid
     * @return
     */
    boolean checkBindMobile(String openid);

    /**
     * 绑定手机密码时，生成验证码
     * @param openid
     * @param mobile
     * @return
     */
    boolean generateManageUserMobileCode(String openid, String mobile);

    /**
     * 绑定手机密码
     * @param openid
     * @param mobile
     * @param passwd
     * @param bindCode
     * @return
     */
    boolean bindManageMobil(String openid,String mobile,String passwd,String bindCode);

    /**
     *
     * @param openid
     * @param angentid
     * @param isReject
     * @param rejectDesc 审核不通过的原因
     * @return
     */
    boolean agentApplyAudit(String openid,String angentid,boolean isReject,String rejectDesc, Date contractTimeBegin, Date contractTimeEnd);

    /**
     * 可以从0设置为-2，也可以从-2设置为0
     * @param angentid
     * @param status
     * @param desc
     * @return
     */
    boolean setAgentApplyStatus(String openid, String angentid,Integer status,String desc);


    public CpAgentInfoVO queryYdAgentApplyInfo(String openid, String angentid);
    /**
     *
     * @param openid
     * @param angentid
     * @return
     */
    CpAgentInfoVO queryYdAgentInfo(String openid,String angentid);

    /**
     * 查询代理商签约码
     * @param openid
     * @param angentid
     * @return
     */
    String queryAgentContractCode(String openid,String angentid);


    /**
     * 服务商申请信息列表分页查询
     * @param openid
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultPage<?> selectAgentApplyInfo(String openid, Integer pageNum, Integer pageSize);

    /**
     * 服务商信息列表分页查询
     * @param openid
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultPage<?> selectAgentInfo(String openid, Integer pageNum, Integer pageSize);

    /**
     * 商家申请信息列表分页查询
     * @param openid
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultPage<?> selectCpApplyInfo(String openid, Integer pageNum, Integer pageSize);

    /**
     * 商家店铺信息列表分页查询
     * @param openid
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultPage<?> selectShopInfo(String openid, Integer pageNum, Integer pageSize);

    /**
     * 获取指定店铺的信息
     * @param openid
     * @param shopid
     * @return
     */
    YdShopApplyInfoVO getYdCpShopInfo(String openid, String shopid);

    /**
     * 取当前小二的基本信息
     * @param openid
     * @return
     */
    Map getDefaultXiaoer(String openid);

    /**
     * 取用户在后台各个入口提交的申请与留言信息列表
     * @param openid
     * @return
     */
    ResultPage<?> getApplyInfoRecordList(String openid, Integer pageNum, Integer pageSize);


    /**
     * --------------------------------------与小程序代码管理审核以及发布有关的操作接口--------------------------------------
     */

    Map<String,Object> getCategory(String openid,String appid) throws Exception;

    Map<String,Object> getPage(String openid,String appid) throws Exception;

    Map<String,Object> getTesterList(String openid,String appid) throws Exception;

    Map<String,Object> bindTester(String openid,String appid,String wechatid) throws Exception;

    Map<String,Object> unBindTester(String openid,String appid,String wechatid) throws Exception;

    boolean uploadWeiXinSmallCode(String openid,String appid,Integer templateid,String version,String desc) throws Exception;

    Map<String,Object> submitWeiXinSmallCodeAudit(String openid,String appid,String postData) throws Exception;

    Map<String,Object> undoCodeAudit(String openid,String appid) throws Exception;

    Map<String,Object> getXinSmallCodeAuditResult(String openid,String appid) throws Exception;

    Map<String,Object> releaseWeiXinSmallCode(String openid,String appid) throws Exception;

    Map<String,Object> grayReleaseWeiXinSmallCode(String openid,String appid,Integer percentage) throws Exception;

    Map<String,Object> cancelGrayReleaseWeiXinSmallCode(String openid,String appid) throws Exception;

    Map<String,Object> getGrayReleaseWeiXinSmallCode(String openid,String appid) throws Exception;
    /**
     * 获取草稿箱内的所有临时代码草稿
     * @return
     */
    Map<String,Object> getTemplateDraftList(String openid) throws Exception;

    /**
     * 获取代码模版库中的所有小程序代码模版
     * @return
     */
    Map<String,Object> getTemplateList(String openid) throws Exception;

    /**
     * 将草稿箱的草稿选为小程序代码模版
     * @param draftId
     * @return
     */
    Map<String,Object> addToTemplate(String openid,Integer draftId) throws Exception;


    /**
     * 删除指定小程序代码模版
     * @param templateId
     * @return
     */
    Map<String,Object> deleteTemplate(String openid,Integer templateId) throws Exception;

    Map<String,Object> modifySmallDomain(String openid,String appid,String action,String domain) throws Exception;


    /**
     * --------------------------------------与小程序代码管理审核以及发布有关的操作接口--------------------------------------
     */


    /**
     * 要发布的文件id
     * @param openid
     * @param ids
     * @return
     */
    boolean releaseFile(String openid,String ids);


}
