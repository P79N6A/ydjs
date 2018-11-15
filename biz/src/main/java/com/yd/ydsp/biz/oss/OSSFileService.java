package com.yd.ydsp.biz.oss;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface OSSFileService {

    Map<String, Object> getOssPayPointAuthentication(String key) throws UnsupportedEncodingException;

    String uploadWeiXinSmallImage(byte[] content,String code);

    void releaseFile(String fileIds);

    String readFile(String fileName);

    String uploadShopIndexPageData(byte[] content,String shopid,String indexPageUrl);

    String getShopIndexPageFileName(String indexPageUrl);

    String getShopIndexPageData(String indexPageUrl);

}
