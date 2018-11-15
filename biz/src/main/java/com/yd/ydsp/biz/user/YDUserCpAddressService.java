package com.yd.ydsp.biz.user;

import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.dal.entity.YdPaypointCpAddress;

import java.util.List;

/**
 * Created by zengyixun on 17/10/3.
 */
public interface YDUserCpAddressService {

    /**
     * 删除一个地址，调用此方法后，记得要同时调用UpdateAddressCacheInfoProcess.process方法来更新缓存
     * @param addressid
     * @return
     */
    int deleteByAddressid(String openid,String addressid);

    /**
     * 增加一个地址
     * @param record
     * @return
     */
    int insert(YdPaypointCpAddress record);

    int insert(String openid,String mobile,String name,
               String address,String zipcode, String district, String city,String province,String country);

    /**
     * 修改地址信息，传为null值的，表明不修改
     * @param openid
     * @param addressid
     * @param mobile
     * @param name
     * @param address
     * @param zipcode
     * @param district
     * @param city
     * @param province
     * @param country
     * @return
     */
    int update(String openid,String addressid,String mobile,String name,
               String address,String zipcode, String district, String city,String province,String country);

    /**
     * 根据地址id查询一个地址信息
     * @param addressid
     * @return
     */
    YdPaypointCpAddress selectByAddressid(String openid,String addressid);

    /**
     * 根据微信id来查询一个用户的地址信息列表
     * @param openid
     * @return
     */
    List<UserAddressInfoVO> selectByOpendId(String openid);

    /**
     * 设置用户的默认收货地址
     * @param openid
     * @param addressid
     * @return
     */
    boolean setDefaultAddress(String openid,String addressid);

    /**
     * 查找默认地址
     * @param openid
     * @return
     */
    UserAddressInfoVO selectDefaultAddress(String openid);

}
