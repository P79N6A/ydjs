package com.yd.ydsp.biz.user;

import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.dal.entity.YdWeixinUserInfo;

import java.util.List;

/**
 * Created by zengyixun on 17/5/18.
 */
public interface Userinfo2ShopService {

    /**
     * 删除地址
     * @param unionid
     * @param addressid
     * @return
     */
    boolean deleteByAddressid(String unionid,String addressid);

    /**
     * 增加一个地址
     * @return
     */

    String newAddress(String unionid,UserAddressInfoVO userAddressInfoVO);

    /**
     * 修改地址信息，传为null值的，表明不修改
     * @param unionid
     * @return
     */
    boolean updateAddress(String unionid,UserAddressInfoVO userAddressInfoVO);

    /**
     * 根据地址id查询一个地址信息
     * @param addressid
     * @return
     */
    UserAddressInfoVO selectByAddressid(String unionid,String addressid);

    /**
     * 根据微信id来查询一个用户的地址信息列表
     * @param unionid
     * @return
     */
    List<UserAddressInfoVO> selectByUnionId(String unionid);

    /**
     * 设置用户的默认收货地址
     * @param unionid
     * @param addressid
     * @return
     */
    boolean setDefaultAddress(String unionid,String addressid);

    /**
     * 查找默认地址
     * @param unionid
     * @return
     */
    UserAddressInfoVO selectDefaultAddress(String unionid);

    YdWeixinUserInfo selectWeiXinUserInfo(String unionid);



}
