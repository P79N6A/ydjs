package com.yd.ydsp.common.utils;


import com.yd.ydsp.common.annotation.LogIgnore;
import com.yd.ydsp.common.annotation.LogShow;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 16/1/12
 */
public class YDLogUtil {
    /**
     *
     * @param object
     * @return
     */
    public static String reflectionToString(Object object) {
        if (object instanceof String || object instanceof Number || object instanceof Map || object instanceof Boolean) {
            return object.toString();
        }

        return new YDReflectionToStringBuilder(object, ToStringStyle.SHORT_PREFIX_STYLE) {
            @Override
            protected boolean accept(Field field) {
                return null == field.getAnnotation(LogIgnore.class) && !field.getName().equals("serialVersionUID");
            }
        }.toString();
    }

    public static String reflectionToString(List list) {
        if (CollectionUtil.isEmpty(list)) {
            return "";
        }

        List<String> strList = new ArrayList<String>(list.size());
        for (Object obj : list) {
            strList.add(reflectionToString(obj));
        }

        return strList.toString();
    }


    /**
     *
     * @param object
     * @return
     */
    public static String reflectionToStringWithShowFields(Object object) {
        if (object instanceof String || object instanceof Number || object instanceof Map || object instanceof Boolean) {
            return object.toString();
        }

        return new YDReflectionToStringBuilder(object, ToStringStyle.SHORT_PREFIX_STYLE) {
            @Override
            protected boolean accept(Field field) {
                return null != field.getAnnotation(LogShow.class);
            }
        }.toString();
    }

    public static String reflectionToStringWithShowFields(List list) {
        if (CollectionUtil.isEmpty(list)) {
            return "";
        }

        List<String> strList = new ArrayList<String>(list.size());
        for (Object obj : list) {
            strList.add(reflectionToStringWithShowFields(obj));
        }

        return strList.toString();
    }

}
