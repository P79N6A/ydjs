/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.yd.ydsp.common.security.url;

/**
 * 类SafwURLHost.java的实现描述：TODO 类实现描述
 * 
 * @author axman 2014年5月16日 上午10:49:44
 */
public class SafeURLHost {

    private static char[] stopWords = { '/', '?', '#' };

    
    //delete path and queryString from url.
    public static String getHostFormURL(String url) {

        int idx = url.indexOf("://");
        if (idx == -1) {
            if (url.startsWith("//")) {
                idx = 2;
            } else {
                return url;
            }
        } else {
            idx += 3;
        }
        for (; idx < url.length() - 1; idx++) {
            if (url.charAt(idx) != '/') {
                break; //skip '/' after schema
            }
        }
        int off = idx;
        for (char c : stopWords) {
            idx = url.indexOf(c, off);
            if (idx != -1) {
                url = url.substring(0, idx);
                break;
            }
        }
        return url;
    }
}
