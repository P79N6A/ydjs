package com.yd.ydsp.biz.manage;

import com.yd.ydsp.biz.manage.model.CpAgentInfoVO;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;
import com.yd.ydsp.client.domian.manage.YDManageUserInfoVO;
import com.yd.ydsp.client.domian.openshop.YdShopApplyInfoVO;
import com.yd.ydsp.common.model.ResultPage;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

public interface CpAgentService {

    /**
     * 取上传文件临时token接口
     * @param openid
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    Map<String, Object> getOssAuthentication(String openid, String key) throws UnsupportedEncodingException;

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
     * 提交加盟商信息
     * @param cpAgentInfoVO
     * @return
     */
    String setYdAgentApplyInfo(String openid,CpAgentInfoVO cpAgentInfoVO);


    /**
     * 代理商提交签约码
     * @param openid
     * @param contractCode
     * @return
     */
    boolean submitContractCode(String openid,String contractCode);

    /**
     * 查询加盟商的申请信息
     * @param openid
     * @return
     */
    CpAgentInfoVO queryYdAgentApplyInfo(String openid,String agentid);

    /**
     * 查询加盟商信息
     * @param openid
     * @param agentid
     * @return
     */
    CpAgentInfoVO queryYdAgentInfo(String openid,String agentid);

    /**
     * 查询加盟商的申请状态
     * @param openid
     * @return
     */
    Integer queryYdAgentApplyStatus(String openid,String agentid);

    /**
     * 增加指定手机号的用户为指定代理商的管理员
     * @param openid
     * @param mobile
     * @param agentid
     * @return
     */
    Map addToAgentManager(String openid,String mobile, String agentid);

    /**
     * 删除指定代理商的管理员
     * @param openid
     * @param mobile
     * @param agentid
     * @return
     */
    boolean delAgentManager(String openid,String mobile,String agentid);

    /**
     * 查询一个指定的手机帐号，看是不是指定的代理商的管理员
     * @param mobile
     * @param agentid
     * @return
     */
    boolean isAgentManager(String mobile, String agentid);

    /**
     * 查询一个代理商下的管理员列表
     * @param openid
     * @return 姓名，手机号列表
     */
    Map queryManagerInfoByAgentId(String openid,String agentid);

    /**
     * 查询当前用户下所具有管理权限的服务商信息
     * @param openid
     * @return
     */
    Map queryAgentBaseInfoByManager(String openid);

    /**
     * 设置一个主服务商（默认的服务商）
     * @param openid
     * @param agentid
     * @return
     */
    boolean setDefualtAgent(String openid,String agentid);

    /**
     * 取默认的服务商ID
     * @param openid
     * @return
     */
    String getDefualtAgent(String openid);

    /**
     * 创建商家基本信息
     * @param openid
     * @param shopApplyInfoVO
     * @return
     */
    String setYdCpApplyInfo(String openid, YdShopApplyInfoVO shopApplyInfoVO);

    /**
     * 查询指定申请编号的申请单信息
     * @param openid
     * @param applyid
     * @return
     */
    YdShopApplyInfoVO getYdCpApplyInfo(String openid,String applyid);

    /**
     * 获取指定店铺的信息
     * @param openid
     * @param shopid
     * @return
     */
    YdShopApplyInfoVO getYdCpShopInfo(String openid,String shopid);
//
//    /**
//     * 返回申请单的第一步，重新填资料
//     * @param openid
//     * @param applyid
//     * @return
//     */
//    boolean backFirstStepWithCpApply(String openid, String applyid);

    /**
     * 删除一个申请单，只有在草稿状态下才能删除
     * @param openid
     * @param applyid
     * @return
     */
    boolean delYdCpApplyInfo(String openid, String applyid);

    /**
     * 商家申请信息列表分页查询
     * @param openid
     * @param agentid
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultPage<?> selectCpApplyInfo(String openid, String agentid, Integer pageNum, Integer pageSize);

    /**
     * 商家信息列表分页查询
     * @param openid
     * @param agentid
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultPage<?> selectShopInfo(String openid, String agentid, Integer pageNum, Integer pageSize);

    /**
     * 根据openid查询服务商基本信息
     * @param openid
     * @return
     */
    YDManageUserInfoVO queryBaseYdAgentByOpenId(String openid);

    /**
     * 根据手机号发生验证码，跳过拦截
     * @param mobile
     * @return
     */
    boolean generatePublicMobileCode(String mobile);

    /**
     * 修改密码
     * @param mobile 手机号
     * @param password 密码
     * @param code 验证码
     * @return
     */
    boolean bindPublicManageMobile(String mobile, String password, String code);

    /**
     * 合同续约
     * @param openid
     * @param shopid
     * @param agentid
     * @param contractDateBegin
     * @param contractDateEnd
     * @return
     */
    boolean updateContractDate(String openid, String shopid, String agentid, Date contractDateBegin, Date contractDateEnd);
}
