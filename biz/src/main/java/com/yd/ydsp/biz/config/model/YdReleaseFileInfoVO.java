package com.yd.ydsp.biz.config.model;

import com.yd.ydsp.common.redis.SerializeUtils;

/**
 * @author zengyixun
 * @date 17/12/28
 */
public class YdReleaseFileInfoVO extends SerializeUtils {

    /**
     * id
     */
    Integer id;

    /**
     * 文件在oss的bucketName
     */
    String bucketName;
    /**
     * 文件在oss的objectName
     */
    String objectName;

    /**
     * 下载到本地的什么位置及文件名称
     */
    String localFile;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
