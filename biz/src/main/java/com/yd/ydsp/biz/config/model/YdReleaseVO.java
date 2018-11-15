package com.yd.ydsp.biz.config.model;

import com.yd.ydsp.common.redis.SerializeUtils;

import java.util.List;

/**
 * @author zengyixun
 * @date 17/12/28
 */
public class YdReleaseVO extends SerializeUtils {

    /**
     * 商家后台版本号
     */
    Integer cpSysVersion;
    /**
     * 要下载的文件信息
     */
    List<YdReleaseFileInfoVO> ydReleaseFileInfoVOList;


    public Integer getCpSysVersion() {
        return cpSysVersion;
    }

    public void setCpSysVersion(Integer cpSysVersion) {
        this.cpSysVersion = cpSysVersion;
    }

    public List<YdReleaseFileInfoVO> getYdReleaseFileInfoVOList() {
        return ydReleaseFileInfoVOList;
    }

    public void setYdReleaseFileInfoVOList(List<YdReleaseFileInfoVO> ydReleaseFileInfoVOList) {
        this.ydReleaseFileInfoVOList = ydReleaseFileInfoVOList;
    }
}
