/*
 * Copyright 2015 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.yd.ydsp.common.security.url;

/**
 * ��FilePath.java��ʵ��������TODO ��ʵ������
 * 
 * @author axman 2015��6��18�� ����3:11:35
 */
public class FilePath {

    public static String pathFilter(String path) {
        while (path.indexOf("%") != -1) {
            try {
                path = java.net.URLDecoder.decode(path, "UTF-8");
            } catch (Exception e) {
                break;
            }
        }
        if (path.indexOf("..") != -1 || path.charAt(0) == '/') return null;
        return path;
    }
}
