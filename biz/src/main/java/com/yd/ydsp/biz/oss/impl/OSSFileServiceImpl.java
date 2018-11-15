package com.yd.ydsp.biz.oss.impl;

import com.yd.ydsp.biz.config.DiamondReleaseConfig;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.config.model.YdReleaseFileInfoVO;
import com.yd.ydsp.biz.oss.OSSFileService;
import com.yd.ydsp.biz.utils.OSSUtils;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.DateUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

public class OSSFileServiceImpl implements OSSFileService {


    @Override
    public Map<String, Object> getOssPayPointAuthentication(String key) throws UnsupportedEncodingException {

        if(StringUtil.isBlank(key)){
            key = DiamondYdSystemConfigHolder.getInstance().payPointBucketKey;
        }
        return OSSUtils.getOssAuthentication("paypoint",key);
    }

    @Override
    public String uploadWeiXinSmallImage(byte[] content, String code) {
        String contentStr = StringUtil.byteArrayToStr(content);
        if(StringUtil.contains(contentStr,"errcode")){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), contentStr);
        }

        String dir = "weixin/"+ DateUtils.date2StrWithYearAndMonthAndDay(new Date(),"yyyy/MM/dd/");
        return OSSUtils.uploadFileByNet("paypoint",dir,code,content);
    }

    @Override
    public void releaseFile(String fileIds){
        if(StringUtil.isEmpty(fileIds)){
            return;
        }

        String[] fileIdListStr = fileIds.split(",");
        for(String idStr : fileIdListStr){
            Integer id = new Integer(idStr.trim());
            YdReleaseFileInfoVO ydReleaseFileInfoVO = DiamondReleaseConfig.getFileInfo(id);
            if(ydReleaseFileInfoVO!=null){
                OSSUtils.downloadFile(ydReleaseFileInfoVO.getBucketName(),ydReleaseFileInfoVO.getObjectName(),
                        ydReleaseFileInfoVO.getLocalFile());
            }
        }

    }

    @Override
    public String readFile(String fileName) {
        return OSSUtils.readFile("paypoint",fileName).trim();
    }

    @Override
    public String uploadShopIndexPageData(byte[] content, String shopid,String indexPageUrl) {
        String fileName = "shopindexpage";
        if(StringUtil.isEmpty(indexPageUrl)) {
            fileName = fileName+"/"+DateUtils.date2StrWithYearAndMonthAndDay(new Date(), "yyyy/MM/dd/"+shopid);
        }else {
            fileName = this.getShopIndexPageFileName(indexPageUrl);
        }
        return OSSUtils.uploadFileByNet("paypoint","",fileName,content);
    }

    @Override
    public String getShopIndexPageFileName(String indexPageUrl) {
        String[] content = indexPageUrl.trim().split("shopindexpage");
        if(content.length==2){
            return "shopindexpage"+content[1];
        }
        return null;
    }

    @Override
    public String getShopIndexPageData(String indexPageUrl) {
        String fileName = this.getShopIndexPageFileName(indexPageUrl);
        if(StringUtil.isEmpty(fileName)){
            return null;
        }
        return this.readFile(fileName);
    }

}
