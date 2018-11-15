package com.yd.ydsp.biz.user.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.user.YDUserCpAddressService;
import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.client.domian.paypoint.CpUserInfoDTO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.YdPaypointCpAddress;
import com.yd.ydsp.dal.entity.YdPaypointCpuserInfo;
import com.yd.ydsp.dal.mapper.YdPaypointCpAddressMapper;
import com.yd.ydsp.dal.mapper.YdPaypointCpuserInfoMapper;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zengyixun
 * @date 17/10/3
 */
public class YDUserCpAddressServiceImpl implements YDUserCpAddressService {

    @Resource
    private DozerBeanMapper doMapper;

    @Resource
    private YdPaypointCpAddressMapper ydPaypointCpAddressMapper;
    @Resource
    private YdPaypointCpuserInfoMapper ydPaypointCpuserInfoMapper;

    @Resource
    private UserinfoService userinfoService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteByAddressid(String openid,String addressid) {
        YdPaypointCpAddress addressInfo = this.selectByAddressid(openid,addressid);
        if(addressInfo==null){
            return 0;
        }
        YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openid);
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfo,CpUserInfoDTO.class);
        cpUserInfoDTO.delListFeature(Constant.CPADDRESS,addressid);
        cpuserInfo.setFeature(cpUserInfoDTO.getFeature());
        ydPaypointCpuserInfoMapper.updateByPrimaryKey(cpuserInfo);
        return ydPaypointCpAddressMapper.deleteByAddressId(addressInfo.getAddressid());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insert(YdPaypointCpAddress record) {
        if(record==null){
            return 0;
        }
        YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(record.getOpenid());
        List<String> addressInfos = userinfoService.getCpAddressIdList(record.getOpenid());

        if(addressInfos!=null){
            if(addressInfos.size()>=5){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"每个用户的地址数量不能超过5个！");
            }
        }

        record.setAddressid(RandomUtil.getSNCode(TypeEnum.ADDRESS));
        /**
         * 新增不可以直接设置为默认地址
         */
        record.setIsDefault(0);
        record.setIsDelete(0);
        int i = ydPaypointCpAddressMapper.insert(record);
        if(i>0) {

            CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfo, CpUserInfoDTO.class);
            cpUserInfoDTO.addListFeature(Constant.CPADDRESS,record.getAddressid());
            cpuserInfo.setFeature(cpUserInfoDTO.getFeature());
            ydPaypointCpuserInfoMapper.updateByPrimaryKey(cpuserInfo);

        }


        return i;
    }

    @Override
    public int insert(String openid, String mobile, String name, String address, String zipcode, String district,
                      String city, String province, String country) {
        /**
         * 检查参数
         */
        if(StringUtil.isBlank(mobile)||StringUtil.isBlank(name)||StringUtil.isBlank(address)||StringUtil.isBlank(district)
                ||StringUtil.isBlank(city)||StringUtil.isBlank(province)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"请准确的填写完整地址信息！");
        }
        if(StringUtil.isBlank(country)){
            country = "中国";
        }
        if(StringUtil.isBlank(zipcode)){
            zipcode = "000000";
        }
        YdPaypointCpAddress record = new YdPaypointCpAddress();
        record.setOpenid(openid);
        record.setMobile(mobile);
        record.setName(name);
        record.setAddress(address);
        record.setZipcode(zipcode);
        record.setDistrict(district);
        record.setCity(city);
        record.setProvince(province);
        record.setCountry(country);
        return this.insert(record);
    }

    @Override
    public int update(String openid, String addressid, String mobile, String name, String address, String zipcode,
                      String district, String city, String province, String country) {
        /**
         * 检查参数
         */
        if(StringUtil.isBlank(mobile)||StringUtil.isBlank(name)||StringUtil.isBlank(address)||StringUtil.isBlank(district)
                ||StringUtil.isBlank(city)||StringUtil.isBlank(province)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"请准确的填写完整地址信息！");
        }
        YdPaypointCpAddress cpAddress = this.selectByAddressid(openid,addressid);
        if(cpAddress==null){
            return 0;
        }
        if(StringUtil.isNotBlank(mobile)){
            cpAddress.setMobile(mobile);
        }
        if(StringUtil.isNotBlank(name)){
            cpAddress.setName(name);
        }
        if(StringUtil.isNotBlank(address)){
            cpAddress.setAddress(address);
        }
        if(StringUtil.isNotBlank(zipcode)){
            cpAddress.setZipcode(zipcode);
        }
        if(StringUtil.isNotBlank(district)){
            cpAddress.setDistrict(district);
        }
        if(StringUtil.isNotBlank(city)){
            cpAddress.setCity(city);
        }
        if(StringUtil.isNotBlank(province)){
            cpAddress.setProvince(province);
        }
        if(StringUtil.isNotBlank(country)){
            cpAddress.setCountry(country);
        }
        return ydPaypointCpAddressMapper.updateByPrimaryKey(cpAddress);
    }

    @Override
    public YdPaypointCpAddress selectByAddressid(String openid,String addressid) {
        if(!this.hasAddress(openid,addressid)){
            return null;
        }
        return ydPaypointCpAddressMapper.selectByAddressid(addressid);
    }

    @Override
    public List<UserAddressInfoVO> selectByOpendId(String openid) {

        List<String> addressInfos = userinfoService.getCpAddressIdList(openid);
        if(addressInfos==null||addressInfos.size()<=0){
            return null;
        }

        List<UserAddressInfoVO> result = new ArrayList<>();
        for(String address : addressInfos){
            YdPaypointCpAddress cpAddress = ydPaypointCpAddressMapper.selectByAddressid(address);
            if(cpAddress!=null){
                result.add(doMapper.map(cpAddress,UserAddressInfoVO.class));

            }
        }

        return result;

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setDefaultAddress(String openid, String addressid) {
        List<UserAddressInfoVO> addressInfos = this.selectByOpendId(openid);
        if(addressInfos==null||addressInfos.size()<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"没有找到收货地址！");
        }
        for(UserAddressInfoVO cpAddress : addressInfos){
            if(cpAddress.getAddressid().equals(addressid.trim())){
                YdPaypointCpAddress address = this.selectByAddressid(openid,cpAddress.getAddressid());
                if(address!=null) {
                    address.setIsDefault(1);
                    ydPaypointCpAddressMapper.updateByPrimaryKey(address);
                }
            }else{
                YdPaypointCpAddress address = this.selectByAddressid(openid,cpAddress.getAddressid());
                if(address!=null) {
                    address.setIsDefault(0);
                    ydPaypointCpAddressMapper.updateByPrimaryKey(address);
                }
            }
        }

        return true;
    }

    @Override
    public UserAddressInfoVO selectDefaultAddress(String openid) {
        List<UserAddressInfoVO> addressInfos = this.selectByOpendId(openid);
        if(addressInfos==null||addressInfos.size()<=0){
            return null;
        }
        for(UserAddressInfoVO cpAddress : addressInfos){
            if(cpAddress.getIsDefault().intValue()==1){
                return cpAddress;
            }
        }
        return null;
    }

    private boolean hasAddress(String openid ,String addressid){
        List<String> addressInfos = userinfoService.getCpAddressIdList(openid);
        if(addressInfos==null){
            return false;
        }
        return addressInfos.contains(addressid);

    }
}
