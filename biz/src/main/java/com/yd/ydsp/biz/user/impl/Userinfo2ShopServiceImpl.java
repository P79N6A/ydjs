package com.yd.ydsp.biz.user.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.address.AddressSysService;
import com.yd.ydsp.biz.user.Userinfo2ShopService;
import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.YdPaypointConsumerAddress;
import com.yd.ydsp.dal.entity.YdWeixinUserInfo;
import com.yd.ydsp.dal.mapper.YdPaypointConsumerAddressMapper;
import com.yd.ydsp.dal.mapper.YdWeixinUserInfoMapper;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Userinfo2ShopServiceImpl implements Userinfo2ShopService {
    private static final Logger logger = LoggerFactory.getLogger(Userinfo2ShopServiceImpl.class);


    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private YdPaypointConsumerAddressMapper ydPaypointConsumerAddressMapper;
    @Resource
    private AddressSysService addressSysService;
    @Resource
    private YdWeixinUserInfoMapper ydWeixinUserInfoMapper;


    @Override
    public boolean deleteByAddressid(String unionid, String addressid) {
        YdPaypointConsumerAddress consumerAddress = ydPaypointConsumerAddressMapper.selectByAddressid(addressid);
        if(consumerAddress==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "地址不存在!");
        }
        if(!StringUtil.equals(consumerAddress.getUnionid(),unionid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(ydPaypointConsumerAddressMapper.deleteByAddressId(addressid)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "删除地址信息失败!");
        }

        return true;
    }

    @Override
    public String newAddress(String unionid, UserAddressInfoVO userAddressInfoVO) {
        if(unionid==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "unionid不能为空!");
        }
        if(StringUtil.isNotEmpty(userAddressInfoVO.getAddressid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "地址id不为空，怎么可能是新增地址呢?");
        }
        logger.info("userAddressInfoVO is :"+ JSON.toJSONString(userAddressInfoVO));
        if(StringUtil.isEmpty(userAddressInfoVO.getName())||StringUtil.isEmpty(userAddressInfoVO.getMobile())||
                StringUtil.isEmpty(userAddressInfoVO.getDistrict())||StringUtil.isEmpty(userAddressInfoVO.getProvince())||StringUtil.isEmpty(userAddressInfoVO.getCity())||
                StringUtil.isEmpty(userAddressInfoVO.getAddress())){

            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "地址信息不完整!");

        }
        if(StringUtil.isEmpty(userAddressInfoVO.getZipcode())){
            userAddressInfoVO.setZipcode("000000");
        }
        if(StringUtil.isEmpty(userAddressInfoVO.getCountry())){
            userAddressInfoVO.setCountry("中国");
        }
        if(userAddressInfoVO.getIsDefault()==null){
            userAddressInfoVO.setIsDefault(0);
        }
        if(userAddressInfoVO.getIsDefault()<0||userAddressInfoVO.getIsDefault()>1){
            userAddressInfoVO.setIsDefault(0);
        }

        YdPaypointConsumerAddress consumerAddress = new YdPaypointConsumerAddress();

        String addressId = RandomUtil.getSNCode(TypeEnum.ADDRESS);
        consumerAddress.setAddressid(addressId);
        consumerAddress.setUnionid(unionid);
        consumerAddress.setCountry(userAddressInfoVO.getCountry());
        consumerAddress.setProvince(userAddressInfoVO.getProvince());
        consumerAddress.setCity(userAddressInfoVO.getCity());
        consumerAddress.setDistrict(userAddressInfoVO.getDistrict());
        consumerAddress.setAddress(userAddressInfoVO.getAddress());
        consumerAddress.setZipcode(userAddressInfoVO.getZipcode());
        consumerAddress.setIsDefault(userAddressInfoVO.getIsDefault());
        consumerAddress.setMobile(userAddressInfoVO.getMobile());
        consumerAddress.setName(userAddressInfoVO.getName());
        consumerAddress.setIsDelete(0);
        /**
         * 经纬度获取
         */
        Map<String ,String > addressMap = addressSysService.getGeoCode(consumerAddress.getProvince()+consumerAddress.getCity()+consumerAddress.getDistrict()+consumerAddress.getAddress(),consumerAddress.getCity());
        if(addressMap!=null){
            consumerAddress.setZipcode(addressMap.get(Constant.ZIPCODE));
            consumerAddress.setLongitude(addressMap.get(Constant.LONGITUDE));
            consumerAddress.setLatitude(addressMap.get(Constant.LATITUDE));
        }

        if(ydPaypointConsumerAddressMapper.insert(consumerAddress)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "新增地址信息失败!");
        }

        return addressId;
    }

    @Override
    public boolean updateAddress(String unionid, UserAddressInfoVO userAddressInfoVO) {
        if(StringUtil.isEmpty(userAddressInfoVO.getAddressid())||StringUtil.isEmpty(userAddressInfoVO.getName())||StringUtil.isEmpty(userAddressInfoVO.getMobile())||
                StringUtil.isEmpty(userAddressInfoVO.getDistrict())||StringUtil.isEmpty(userAddressInfoVO.getProvince())||StringUtil.isEmpty(userAddressInfoVO.getCity())||
                StringUtil.isEmpty(userAddressInfoVO.getAddress())){

            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "地址信息不完整!");

        }
        if(StringUtil.isEmpty(userAddressInfoVO.getZipcode())){
            userAddressInfoVO.setZipcode("000000");
        }
        if(StringUtil.isEmpty(userAddressInfoVO.getCountry())){
            userAddressInfoVO.setCountry("中国");
        }
        if(userAddressInfoVO.getIsDefault()==null){
            userAddressInfoVO.setIsDefault(0);
        }
        if(userAddressInfoVO.getIsDefault()<0||userAddressInfoVO.getIsDefault()>1){
            userAddressInfoVO.setIsDefault(0);
        }

        YdPaypointConsumerAddress consumerAddress = ydPaypointConsumerAddressMapper.selectByAddressid(userAddressInfoVO.getAddressid());
        if(consumerAddress==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "地址不存在!");
        }

        if(!StringUtil.equals(consumerAddress.getUnionid(),unionid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        consumerAddress.setCountry(userAddressInfoVO.getCountry());
        consumerAddress.setProvince(userAddressInfoVO.getProvince());
        consumerAddress.setCity(userAddressInfoVO.getCity());
        consumerAddress.setDistrict(userAddressInfoVO.getDistrict());
        consumerAddress.setAddress(userAddressInfoVO.getAddress());
        consumerAddress.setZipcode(userAddressInfoVO.getZipcode());
        consumerAddress.setIsDefault(userAddressInfoVO.getIsDefault());
        consumerAddress.setMobile(userAddressInfoVO.getMobile());
        consumerAddress.setName(userAddressInfoVO.getName());
        consumerAddress.setIsDelete(0);
        /**
         * 经纬度获取
         */
        Map<String ,String > addressMap = addressSysService.getGeoCode(consumerAddress.getProvince()+consumerAddress.getCity()+consumerAddress.getDistrict()+consumerAddress.getAddress(),consumerAddress.getCity());
        if(addressMap!=null){
            consumerAddress.setZipcode(addressMap.get(Constant.ZIPCODE));
            consumerAddress.setLongitude(addressMap.get(Constant.LONGITUDE));
            consumerAddress.setLatitude(addressMap.get(Constant.LATITUDE));
        }

        if(ydPaypointConsumerAddressMapper.updateByPrimaryKeySelective(consumerAddress)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新地址信息失败!");
        }

        return true;
    }

    @Override
    public UserAddressInfoVO selectByAddressid(String unionid, String addressid) {
        YdPaypointConsumerAddress consumerAddress = ydPaypointConsumerAddressMapper.selectByAddressid(addressid);
        if(consumerAddress==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "地址不存在!");
        }
        if(!StringUtil.equals(consumerAddress.getUnionid(),unionid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        UserAddressInfoVO userAddressInfoVO = doMapper.map(consumerAddress,UserAddressInfoVO.class);
        return userAddressInfoVO;
    }

    @Override
    public List<UserAddressInfoVO> selectByUnionId(String unionid) {
        List<YdPaypointConsumerAddress> consumerAddressList = ydPaypointConsumerAddressMapper.selectByUnionId(unionid);
        if(consumerAddressList==null){
            return null;
        }

        List<UserAddressInfoVO> userAddressInfoVOS = new ArrayList<>();

        for(YdPaypointConsumerAddress consumerAddress : consumerAddressList){
            UserAddressInfoVO userAddressInfoVO = doMapper.map(consumerAddress,UserAddressInfoVO.class);
            userAddressInfoVOS.add(userAddressInfoVO);
        }

        return userAddressInfoVOS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setDefaultAddress(String unionid, String addressid) {
        YdPaypointConsumerAddress consumerAddress = ydPaypointConsumerAddressMapper.selectByAddressid(addressid);
        if(consumerAddress==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "地址不存在!");
        }
        if(!StringUtil.equals(consumerAddress.getUnionid(),unionid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        List<YdPaypointConsumerAddress> consumerAddressList = ydPaypointConsumerAddressMapper.selectByUnionId(unionid);
        if(consumerAddressList==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "地址不存在!");
        }

        for (YdPaypointConsumerAddress ydPaypointConsumerAddress:consumerAddressList){

            if(StringUtil.equals(consumerAddress.getAddressid(),ydPaypointConsumerAddress.getAddressid())){
                ydPaypointConsumerAddress.setIsDefault(1);
            }else {
                ydPaypointConsumerAddress.setIsDefault(0);
            }

            if(ydPaypointConsumerAddressMapper.updateByPrimaryKeySelective(ydPaypointConsumerAddress)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "设置默认地址失败!");
            }

        }

        return true;
    }

    @Override
    public UserAddressInfoVO selectDefaultAddress(String unionid) {
        List<YdPaypointConsumerAddress> consumerAddressList = ydPaypointConsumerAddressMapper.selectByUnionId(unionid);
        if(consumerAddressList==null||consumerAddressList.size()<=0){
            return null;
        }

        for (YdPaypointConsumerAddress consumerAddress: consumerAddressList){
            if(consumerAddress.getIsDefault().intValue()>0){
                UserAddressInfoVO userAddressInfoVO = doMapper.map(consumerAddress,UserAddressInfoVO.class);
                return userAddressInfoVO;
            }
        }

        UserAddressInfoVO userAddressInfoVO = doMapper.map(consumerAddressList.get(0),UserAddressInfoVO.class);

        return userAddressInfoVO;
    }

    @Override
    public YdWeixinUserInfo selectWeiXinUserInfo(String unionid) {

        return ydWeixinUserInfoMapper.selectByUnionid(unionid);
    }
}
