package com.yd.ydsp.biz.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PolicyConditions;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.common.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/11/25
 */
public class OSSUtils {
    public static String accessKeyId = DiamondYdSystemConfigHolder.getInstance().ossAccessKeyId;
    public static String accessKeySecret = DiamondYdSystemConfigHolder.getInstance().ossAccessKeySecret;
    public static String endpoint = DiamondYdSystemConfigHolder.getInstance().ossEndpoint;
    public static final Logger logger = LoggerFactory.getLogger(OSSUtils.class);

    public static OSSClient client;

    static {
        client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 获取Oss异步上传权限
     * @param bucket Bucket名字
     * @param key Oss传的Key也就是指定可以读写的dir路径位置
     * @return Oss异步上传数据
     * @throws UnsupportedEncodingException 编码出错
     * @throws NullPointerException 参数为空
     * @author hengshu
     */
    public static Map<String, Object> getOssAuthentication(String bucket, String key) throws UnsupportedEncodingException, NullPointerException {
        if(StringUtil.isEmpty(bucket) || StringUtil.isEmpty(key) ) {
            throw new NullPointerException("arguments is null.");
        }

        String host = "//" + bucket + "." + "oss-cn-hangzhou.aliyuncs.com";

        long expireTime = 30; // 30s
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        java.sql.Date expiration = new java.sql.Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 10485760); // max: 10M
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, key);

        String postPolicy = client.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes("utf-8");
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = client.calculatePostSignature(postPolicy);

        Map<String, Object> respMap = new LinkedHashMap<String, Object>();
        respMap.put("accessid", accessKeyId);
        respMap.put("policy", encodedPolicy);
        respMap.put("signature", postSignature);
        respMap.put("dir", key);
        respMap.put("host", host);
        respMap.put("expire", String.valueOf(expireEndTime / 1000));

        return respMap;
    }
//http://paypoint.oss-cn-hangzhou.aliyuncs.com
    public static String uploadFileByNet(String bucketName,String dir,String fileName,byte[] content){

        String result = null;
        OSSClient ossClient = null;
        try {
            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            if(StringUtil.isEmpty(bucketName)) {
                bucketName = "paypoint";
            }
    //        String objectName = DateUtils.date2StrWithYearAndMonthAndDay(new Date(),"yyyy/MM/dd")+"/"+fileName;
            String objectName = dir+fileName;

            ossClient.putObject(bucketName,objectName,new ByteArrayInputStream(content));
            result = "http://paypoint.oss-cn-hangzhou.aliyuncs.com/"+objectName;

        }catch (Exception ex){
            logger.error("uploadFileByNet is error: ",ex);
        }finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return result;

    }

    public static void downloadFile(String bucketName,String objectName,String fileName){
        OSSClient ossClient = null;
        try{
            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            ossClient.getObject(new GetObjectRequest(bucketName,objectName),new File(fileName));
        }catch (Exception ex){
            logger.error("downloadFile is error: ",ex);
        }finally {
            if(ossClient!=null){
                ossClient.shutdown();
            }
        }
    }

    public static String readFile(String bucketName,String objectName){
        OSSClient ossClient = null;
        BufferedReader reader = null;
        String result = "";
        try {
            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
            reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
            while (true){
                String line = reader.readLine();
                if(line==null){
                    break;
                }
                result = result + line;
            }
        }catch (Exception ex){
            logger.error("readFile is error: ",ex);
        }finally {
            if(reader!=null){
                try {
                    reader.close();

                }catch (Exception ex){
                    logger.error("readerClose is error: ",ex);
                }finally {
                    if(ossClient!=null){
                        ossClient.shutdown();
                        ossClient = null;
                    }
                }

            }
            if(ossClient!=null){
                ossClient.shutdown();
            }
        }
        return result;
    }
}
