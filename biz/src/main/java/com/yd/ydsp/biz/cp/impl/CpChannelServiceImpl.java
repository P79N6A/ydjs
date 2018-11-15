package com.yd.ydsp.biz.cp.impl;

import com.yd.ydsp.biz.config.DiamondWeiXinInfoConfigHolder;
import com.yd.ydsp.biz.config.DiamondYDdeliveryConfig;
import com.yd.ydsp.biz.cp.CpChannelService;
import com.yd.ydsp.biz.cp.model.YdDeliveryInfoVO;
import com.yd.ydsp.biz.manage.CpAgentService;
import com.yd.ydsp.biz.oss.OSSFileService;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.weixin.WeixinSamll2ShopService;
import com.yd.ydsp.client.domian.openshop.YdCpChannelDTO;
import com.yd.ydsp.client.domian.openshop.YdCpChannelVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class CpChannelServiceImpl implements CpChannelService {

    public static final Logger logger = LoggerFactory.getLogger(CpChannelServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private UserinfoService userinfoService;
    @Resource
    private YdCpChannelMapper ydCpChannelMapper;
    @Resource
    private YdPaypointCpdeviceInfoMapper ydPaypointCpdeviceInfoMapper;

    @Resource
    private YdManageUserInfoMapper ydManageUserInfoMapper;
    @Resource
    private YdManageUserWhiteListMapper ydManageUserWhiteListMapper;
    @Resource
    private YdPaypointShopInfoExtMapper ydPaypointShopInfoExtMapper;
    @Resource
    private CpAgentService cpAgentService;
    @Resource
    private WeixinSamll2ShopService weixinSamll2ShopService;
    @Resource
    private OSSFileService ossFileService;


    @Override
    public List<YdDeliveryInfoVO> getDeliveryConfigInfo(String openid, String shopid) {
        return DiamondYDdeliveryConfig.config;
    }

    @Override
    public String addChannel(String openid, YdCpChannelVO cpChannelVO) throws Exception {
        if(StringUtil.isBlank(cpChannelVO.getShopid())||StringUtil.isBlank(cpChannelVO.getChannelName())||
                StringUtil.isBlank(cpChannelVO.getCity())||StringUtil.isBlank(cpChannelVO.getDistrict())||
                StringUtil.isBlank(cpChannelVO.getProvince())||StringUtil.isBlank(cpChannelVO.getAddress())||
                StringUtil.isBlank(cpChannelVO.getOwnerMobile())||StringUtil.isBlank(cpChannelVO.getOwnerName())||
                cpChannelVO.getDeliveryType()==null||cpChannelVO.getChannelType()==null||StringUtil.isEmpty(cpChannelVO.getChannelid())){

            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "请完整填写渠道信息!");

        }
        if(TypeEnum.getTypeOfSN(cpChannelVO.getChannelid())!=TypeEnum.CHANNELQRCODE){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "渠道ID格式不正确!");
        }
        if(!(userinfoService.checkIsOwner(openid,cpChannelVO.getShopid())||userinfoService.checkIsManager(openid,cpChannelVO.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointCpuserInfo ydPaypointCpuserInfo = userinfoService.select2BByMobile(cpChannelVO.getOwnerMobile());
        if(ydPaypointCpuserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "此渠道的负责人手机没有绑定引灯公众号!");
        }

        byte[] content = weixinSamll2ShopService.getWxAcodeUnlimit(cpChannelVO.getShopid(),cpChannelVO.getChannelid(), DiamondWeiXinInfoConfigHolder.getInstance().getDefaultOnlinMainPage());
        if(content==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "生成小程序码失败，导致新增渠道失败!");
        }
        String qrcodeImageUrl = ossFileService.uploadWeiXinSmallImage(content,cpChannelVO.getChannelid());
        if(StringUtil.isEmpty(qrcodeImageUrl)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "转码失败，导致新增渠道失败!");
        }

        YdCpChannel ydCpChannel = doMapper.map(cpChannelVO,YdCpChannel.class);
        YdCpChannelDTO ydCpChannelDTO = doMapper.map(ydCpChannel,YdCpChannelDTO.class);
        ydCpChannelDTO.addFeature(Constant.QRCodeUrl,qrcodeImageUrl);
        ydCpChannel.setChannelid(cpChannelVO.getChannelid());


        ydCpChannel.setStatus(0);
        ydCpChannel.setFeature(ydCpChannelDTO.getFeature());
        if(ydCpChannelMapper.insert(ydCpChannel)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "新增渠道失败!");
        }



        return ydCpChannel.getChannelid();
    }

    @Override
    public boolean modifyChannel(String openid, YdCpChannelVO cpChannelVO) {
        if(StringUtil.isBlank(cpChannelVO.getShopid())||StringUtil.isBlank(cpChannelVO.getChannelName())||
                StringUtil.isBlank(cpChannelVO.getCity())||StringUtil.isBlank(cpChannelVO.getDistrict())||
                StringUtil.isBlank(cpChannelVO.getProvince())||StringUtil.isBlank(cpChannelVO.getAddress())||
                StringUtil.isBlank(cpChannelVO.getOwnerMobile())||StringUtil.isBlank(cpChannelVO.getOwnerName())||
                cpChannelVO.getDeliveryType()==null||cpChannelVO.getChannelType()==null){

            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "请完整填写渠道信息!");

        }
        if(StringUtil.isBlank(cpChannelVO.getChannelid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "缺少渠道id!");
        }
        if(!(userinfoService.checkIsOwner(openid,cpChannelVO.getShopid())||userinfoService.checkIsManager(openid,cpChannelVO.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdCpChannel ydCpChannel = ydCpChannelMapper.selectByChannelid(cpChannelVO.getChannelid());
        if(ydCpChannel==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "渠道不存在!");
        }
        if(!StringUtil.equals(ydCpChannel.getShopid(),cpChannelVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "该渠道不属于此店铺!");
        }
        YdPaypointCpuserInfo ydPaypointCpuserInfo = userinfoService.select2BByMobile(cpChannelVO.getOwnerMobile());
        if(ydPaypointCpuserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "此渠道的负责人手机没有绑定引灯公众号!");
        }
        if(cpChannelVO.getPrinterNum()==null){
            cpChannelVO.setPrinterNum("");
        }

        YdCpChannel cpChannel = doMapper.map(cpChannelVO,YdCpChannel.class);
        cpChannel.setId(ydCpChannel.getId());
        ydCpChannel.setChannelid(RandomUtil.getSNCode(TypeEnum.QRCODE));
        cpChannel.setStatus(0);
        if(ydCpChannelMapper.updateByPrimaryKeySelective(cpChannel)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "更新渠道信息失败!");
        }

        return true;
    }

    @Override
    public boolean disableChannel(String openid, String channelid) {
        YdCpChannel ydCpChannel = ydCpChannelMapper.selectByChannelid(channelid);
        if(ydCpChannel==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "渠道不存在!");
        }
        if(!(userinfoService.checkIsOwner(openid,ydCpChannel.getShopid())||userinfoService.checkIsManager(openid,ydCpChannel.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        ydCpChannel.setStatus(1);
        if(ydCpChannelMapper.updateByPrimaryKeySelective(ydCpChannel)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "删除渠道失败!");
        }
        return true;
    }

    @Override
    public List<YdCpChannelVO> queryChannelList(String openid, String shopid) {
        if(!(userinfoService.checkIsOwner(openid,shopid)||userinfoService.checkIsManager(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        List<YdCpChannel> ydCpChannels = ydCpChannelMapper.selectByShopid(shopid);
        List<YdCpChannelVO> ydCpChannelVOList = new ArrayList<>();
        if(ydCpChannels!=null){

            for (YdCpChannel cpChannel:ydCpChannels){
                YdCpChannelVO ydCpChannelVO = doMapper.map(cpChannel,YdCpChannelVO.class);
                YdCpChannelDTO ydCpChannelDTO = doMapper.map(cpChannel,YdCpChannelDTO.class);
                ydCpChannelVO.setQrCodeUrl(ydCpChannelDTO.getFeature(Constant.QRCodeUrl));
                YdPaypointCpdeviceInfo cpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(cpChannel.getPrinterNum());
                if(cpdeviceInfo==null){
                    ydCpChannelVO.setPrinterNum(null);
                }else {
                    ydCpChannelVO.setPrinterName(cpdeviceInfo.getName());
                }
                ydCpChannelVOList.add(ydCpChannelVO);
            }

        }

        return ydCpChannelVOList;
    }

    @Override
    public List<YdCpChannelVO> queryChannelList(String openid, String shopid, SourceEnum sourceEnum) {
        if(sourceEnum==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"来源不能为空");
        }

        if (!(SourceEnum.WEIXINAGENT.equals(sourceEnum)||SourceEnum.WEIXINXIAOER.equals(sourceEnum))){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"来源错误");
        }

        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }

        YdManageUserWhiteList ydManageUserWhiteList = ydManageUserWhiteListMapper.selectByMobile(ydManageUserInfo.getMobile());
        if (ydManageUserWhiteList==null){
            YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopid);
            if(ydPaypointShopInfoExt==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"店铺不存在！");
            }
            if (!StringUtil.equals(cpAgentService.getDefualtAgent(openid),ydPaypointShopInfoExt.getAgentid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"店铺不在代理商下！");
            }
        }
        List<YdCpChannel> ydCpChannels = ydCpChannelMapper.selectByShopid(shopid);
        List<YdCpChannelVO> ydCpChannelVOList = new ArrayList<>();
        if(ydCpChannels!=null){

            for (YdCpChannel cpChannel:ydCpChannels){
                YdCpChannelVO ydCpChannelVO = doMapper.map(cpChannel,YdCpChannelVO.class);
                YdCpChannelDTO ydCpChannelDTO = doMapper.map(cpChannel,YdCpChannelDTO.class);
                ydCpChannelVO.setQrCodeUrl(ydCpChannelDTO.getFeature(Constant.QRCodeUrl));
                YdPaypointCpdeviceInfo cpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(cpChannel.getPrinterNum());
                if(cpdeviceInfo==null){
                    ydCpChannelVO.setPrinterNum(null);
                }else {
                    ydCpChannelVO.setPrinterName(cpdeviceInfo.getName());
                }
                ydCpChannelVOList.add(ydCpChannelVO);
            }

        }

        return ydCpChannelVOList;
    }

    @Override
    public YdCpChannelVO queryChannelInfo(String openid, String channelid) {
        YdCpChannel ydCpChannel = ydCpChannelMapper.selectByChannelid(channelid);
        if(ydCpChannel==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "渠道不存在!");
        }
        if(!(userinfoService.checkIsOwner(openid,ydCpChannel.getShopid())||userinfoService.checkIsManager(openid,ydCpChannel.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdCpChannelVO ydCpChannelVO = doMapper.map(ydCpChannel,YdCpChannelVO.class);
        YdPaypointCpdeviceInfo cpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(ydCpChannelVO.getPrinterNum());
        YdCpChannelDTO ydCpChannelDTO = doMapper.map(ydCpChannel,YdCpChannelDTO.class);
        ydCpChannelVO.setQrCodeUrl(ydCpChannelDTO.getFeature(Constant.QRCodeUrl));

        if(cpdeviceInfo==null){
            ydCpChannelVO.setPrinterNum(null);
        }else {
            ydCpChannelVO.setPrinterName(cpdeviceInfo.getName());
        }

        return ydCpChannelVO;
    }

    @Override
    public YdCpChannelVO queryChannelInfo(String channelid) {
        YdCpChannel ydCpChannel = ydCpChannelMapper.selectByChannelid(channelid);
        if(ydCpChannel==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "渠道不存在!");
        }

        YdCpChannelVO ydCpChannelVO = doMapper.map(ydCpChannel,YdCpChannelVO.class);
        YdPaypointCpdeviceInfo cpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(ydCpChannelVO.getPrinterNum());
        YdCpChannelDTO ydCpChannelDTO = doMapper.map(ydCpChannel,YdCpChannelDTO.class);
        ydCpChannelVO.setQrCodeUrl(ydCpChannelDTO.getFeature(Constant.QRCodeUrl));

        if(cpdeviceInfo==null){
            ydCpChannelVO.setPrinterNum(null);
        }else {
            ydCpChannelVO.setPrinterName(cpdeviceInfo.getName());
        }

        return ydCpChannelVO;
    }
}
