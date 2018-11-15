package com.yd.ydsp.biz.user;

import com.yd.ydsp.biz.user.model.CheckMobileCodeTypeEnum;
import com.yd.ydsp.client.domian.paypoint.OwnerInfoVO;
import com.yd.ydsp.client.domian.paypoint.WorkerInfoVO;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.dal.entity.YdConsumerInfo;
import com.yd.ydsp.dal.entity.YdPaypointCpuserInfo;
import com.yd.ydsp.dal.entity.YdWeixinUserInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/5/18.
 */
public interface UserinfoService {
    /**
     * 向超级管理员发送手机验证码，验证码3小时有效
     * @param mobile
     * @return
     */
    boolean sendCheckCodeByRootUser(String mobile) throws IOException, ClassNotFoundException;

    /**
     * 验证超级用户的验证码是否正确
     * @param mobile
     * @param code
     * @return
     */
    boolean checkCodeByRootUser(String mobile,String code) throws IOException, ClassNotFoundException;

    /**
     * 按openId进行用户查询-C端
     * @param openId
     * @return
     */
    YdConsumerInfo select2CByOpenId(String openId);

    /**
     * 按不同的方式查CP用户信息
     * @param openId
     * @return
     */
    YdPaypointCpuserInfo select2BByOpenId(String openId);
    YdPaypointCpuserInfo select2BByUnionId(String unionid);
    YdPaypointCpuserInfo select2BByMobile(String mobile);

    /**
     * 新增一个用户信息，C端有新的关注或者第一次扫码
     * @param ydConsumerInfo
     * @return
     */
    int newUser2C(YdConsumerInfo ydConsumerInfo);
    int newCPUser(YdPaypointCpuserInfo paypointCpuserInfo);

    /**
     * 更新用户token信息-Ｃ端
     * @return
     */
    int update2CUserToken(String openid,String accessToken,String refreshToken);

    /**
     * 更新用户信息-Ｃ端
     * @param openid
     * @return
     */
    boolean update2CUserInfo(String openid,boolean isSns);

    /**
     * 商家端公众号用户手机绑定验证码生成，isShopBindMobile=true时也用于第一次店铺申请信息填写修改时使用
     * @param openId
     * @param mobile
     * @return
     */
    boolean generateCPUserMobileCode(String openId,String mobile,CheckMobileCodeTypeEnum type);

    /**
     * 商家端公众号用户手机绑定验证码校验
     * @param openId
     * @param mobile
     * @param code
     * @param passwdord
     * @return
     */
    boolean checkCPUserMobileCode(String openId,String mobile,String code,String passwdord,CheckMobileCodeTypeEnum type);
    boolean checkCPUserMobileCodeByUnionId(String unionId,String mobile,String code,String passwdord,CheckMobileCodeTypeEnum type);


    /**
     * 解绑商家公众号用户与手机之间的关系
     * @param openid
     * @return
     */
    boolean unBindCPUserMobile(String openid, String mobile);
    /**
     * 商家店铺完成入驻后，进行信息修改
     * @param openId
     * @param mobile
     * @return
     */
    boolean generateShopMobileCode(String openId,String mobile);

    /**
     * 商家申请店铺入驻时填写的手机验证码校验
     * @param openId
     * @param mobile
     * @param code
     * @return
     */
    boolean checkShopMobileCode(String openId,String mobile,String code);

    int updateCPUserToken(String openid,String accessToken,String refreshToken);
    /**
     * 更新商家端用户信息
     * @param openid
     * @return
     */
    boolean updateCPUserInfo(String openid,boolean isSns);

    /**
     * 设置商家用户使用的默认店铺
     * @param openid
     * @param shopid
     * @return
     */
    boolean setCPDefaultShop(String openid,String shopid);

    /**
     * 获取当前负责人用户所拥有的店铺
     * @param openid
     * @return
     */
    List<String> getShopsByOwner(String openid);

    /**
     * 获取当前管理员用户所拥有的店铺
     * @param openid
     * @return
     */
    List<String> getShopsByManager(String openid);

    /**
     * 获取当前服务员用户所拥有的店铺
     * @param openid
     * @return
     */
    List<String> getShopsByWaiter(String openid);

    /**
     * 取用户的默认店铺
     * @param openid
     * @return
     */
    String getDefaultShop(String openid);

    /**
     * 检查用户是不是店铺Owner
     * @param opendid
     * @param shopid
     * @return
     */
    boolean checkIsOwner(String opendid,String shopid);

    /**
     * 检查用户是不是店铺管理员
     * @param opendid
     * @param shopid
     * @return
     */
    boolean checkIsManager(String opendid,String shopid);

    /**
     * 检查用户是不是店铺服务员
     * @param opendid
     * @param shopid
     * @return
     */
    boolean checkIsWaiter(String opendid,String shopid);

    /**
     * 取合作平台微信appId
     * @return
     */
    String getWeiXinAppIdForCP();

    /**
     * 取消费者端平台微信appId
     * @return
     */
    String getWeiXinAppIdFor2C();

    /**
     * 取合作商保存的地址信息
     * @param openid
     * @return
     */
    List<String> getCpAddressIdList(String openid);

    /**
     * 取消费者保存的地址信息
     * @param openid
     * @return
     */
    List<String> get2cAddressIdList(String openid);

    /**
     * 取当前用户的用户信息及对应的店铺权限
     * @param openid
     * @return
     */
    Object getCpUserInfo(String openid, String shopid);

    /**
     * 新增员工
     * @param openid
     * @param workerInfo
     * @return
     */
    boolean addShopWorker(String openid, WorkerInfoVO workerInfo);

    /**
     * 删除员工
     * @param openid
     * @param workerInfo
     * @return
     */
    boolean delShopWorker(String openid, WorkerInfoVO workerInfo);

    /**
     * 查询员工
     * @param openid
     * @param shopid
     * @return
     */
    Map<String,List<WorkerInfoVO>> getShopWorker(String openid, String shopid);
    Map<String,List<WorkerInfoVO>> getShopWorker(String openid, String shopid, SourceEnum source);

    /**
     *
     * @param openid
     * @param shopid
     * @param mobile
     * @param checkCode
     * @return
     */
    boolean modifyShopOwnerWithStep1(String openid, String shopid, String mobile ,String checkCode) throws IOException;
    /**
     * 负责人信息修改，如果手机号不再一样时，可能意味着转移owner身份，所以新手机要绑定过公众号，然后找到这个新手机的openid用户
     * 将新的openid用户的owner/manager信息增加上，将旧的openid的用户中的owner与manager信息删除
     * 注意在《商家服务协议》中规避新老负责人对订单金额的所有权争议风险
     * @param openid
     * @param ownerInfoVO
     * @return
     */
    boolean modifyShopOwnerWithStep2(String openid, OwnerInfoVO ownerInfoVO) throws IOException;

    /**
     * 是否可以直接到第二步进行负责人信息修改
     * @param openid
     * @param shopid
     * @return
     */
    boolean youCanModifyOwnerInfo(String openid,String shopid) throws IOException;

    /**
     * 取C端用户信息
     * @param unionid
     * @return
     */
    YdWeixinUserInfo queryUserInfo2C(String unionid);

}
