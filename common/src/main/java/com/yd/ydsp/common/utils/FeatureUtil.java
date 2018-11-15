package com.yd.ydsp.common.utils;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/8/15.
 */
public class FeatureUtil {
    public static final String SP = ";";
    public static final String SSP = ":";
    public static final String SSSP = ",";
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String getFeature(String feature,String key){
        Map<String,String> featureMap = toMap(feature);
        return featureMap.get(key);
    }

    public static String putFeature(String feature, String key, String value){
        Map<String,String> featureMap = toMap(feature);
        featureMap.put(key,value);
        return toString(featureMap);
    }
    /**
     * map to string
     *
     * @param map the map
     * @return the string
     */
    public static String toString(final Map<String, String> map) {

        final StringBuilder featureSB = new StringBuilder(SP);
        if (MapUtils.isEmpty(map)) {
            return featureSB.toString();
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            final String k = entry.getKey();
            final String v = entry.getValue();

            if (null == k                    || null == v) {
                // 过滤掉无效的kv对
                continue;
            }

            try {
                featureSB
                        .append(encode(k, v))
                        .append(SP)
                ;
            } catch (UnsupportedEncodingException e) {
                //TODO : log this
            }

        }

        return featureSB.toString();

    }

    /**
     * map转换为feature时，不对kv进行encode
     *
     * @param map the map
     * @return the string
     */
    public static String toStringUnencode(final Map<String, String> map) {
        final StringBuilder featureSB = new StringBuilder(";");
        if (MapUtils.isEmpty(map)) {
            return featureSB.toString();
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            final String k = entry.getKey();
            final String v = entry.getValue();

            if (null == k || null == v) {
                // 过滤掉无效的kv对
                continue;
            }

            featureSB.append(k).append(SSP).append(v).append(SP);
        }

        return featureSB.toString();
    }

    /**
     * string to map
     *
     * @param featureString the string
     * @return the map
     */
    public static Map<String, String> toMap(final String featureString) {
        final Map<String, String> map = new HashMap<String, String>();

        if (StringUtils.isBlank(featureString)) {
            return map;
        }

        for (String kv : StringUtils.split(featureString, SP)) {

            if (StringUtils.isBlank(kv)) {
                // 过滤掉为空的字符串片段
                continue;
            }

            final String[] ar = StringUtils.split(kv, SSP, 2);
            if (ar.length != 2) {
                // 过滤掉不符合K:V单目的情况
                continue;
            }

            final String k = ar[0];
            final String v = ar[1];
            if (StringUtils.isNotBlank(k)
                    && StringUtils.isNotBlank(v)) {
                try {
                    decode(map, k, v);
                } catch (UnsupportedEncodingException e) {
                    // TODO : log this
                }
            }

        }

        return map;
    }

    /**
     * 解析kv时，不对其进行URL解码（订单的feature没有对其encode，存在%xx的内容，解码会出现异常）
     *
     * @param featureString
     * @return
     */
    public static Map<String, String> toMapUndecode(final String featureString) {
        final Map<String, String> map = new HashMap<String, String>();

        if (StringUtils.isBlank(featureString)) {
            return map;
        }

        for (String kv : StringUtils.split(featureString, SP)) {

            if (StringUtils.isBlank(kv)) {
                // 过滤掉为空的字符串片段
                continue;
            }

            final String[] ar = StringUtils.split(kv, SSP, 2);
            if (ar.length != 2) {
                // 过滤掉不符合K:V单目的情况
                continue;
            }

            final String k = ar[0];
            final String v = ar[1];
            if (StringUtils.isNotBlank(k) && StringUtils.isNotBlank(v)) {
                map.put(k, v.trim());
            }

        }

        return map;
    }

    /**
     * 将List转为逗号分隔的字串
     * @param list
     * @return
     */
    public static String listToString( final List list) {    return org.apache.commons.lang.StringUtils.join(list.toArray(),SSSP);    }

    /**
     *把以逗号分隔的字串转为list类型
     */
    public static List<String> strToList(final String v){

        String[] arr = v.split(",");
        List<String> list = new ArrayList<>(java.util.Arrays.asList(arr));
        return list;

    }

    private static String encode(final String k, final String v) throws UnsupportedEncodingException {
        return URLEncoder.encode(k, DEFAULT_CHARSET) + SSP + URLEncoder.encode(v, DEFAULT_CHARSET);
    }

    private static void decode(final Map<String, String> map, final String k, final String v) throws UnsupportedEncodingException {
        map.put(URLDecoder.decode(k, DEFAULT_CHARSET), URLDecoder.decode(v, DEFAULT_CHARSET));
    }

}
