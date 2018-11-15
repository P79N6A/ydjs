package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageUtil {
    public MessageUtil() {
    }

    public static String getMessage(ResourceBundle bundle, String key, Object[] params) {
        if(bundle != null && key != null) {
            try {
                String e = bundle.getString(key);
                return formatMessage(e, (Object[])params);
            } catch (MissingResourceException var4) {
                return key;
            }
        } else {
            return key;
        }
    }

    public static String getMessage(ResourceBundle bundle, String key, Object param1) {
        return getMessage(bundle, key, (Object[])(new Object[]{param1}));
    }

    public static String getMessage(ResourceBundle bundle, String key, Object param1, Object param2) {
        return getMessage(bundle, key, (Object[])(new Object[]{param1, param2}));
    }

    public static String getMessage(ResourceBundle bundle, String key, Object param1, Object param2, Object param3) {
        return getMessage(bundle, key, (Object[])(new Object[]{param1, param2, param3}));
    }

    public static String getMessage(ResourceBundle bundle, String key, Object param1, Object param2, Object param3, Object param4) {
        return getMessage(bundle, key, (Object[])(new Object[]{param1, param2, param3, param4}));
    }

    public static String getMessage(ResourceBundle bundle, String key, Object param1, Object param2, Object param3, Object param4, Object param5) {
        return getMessage(bundle, key, (Object[])(new Object[]{param1, param2, param3, param4, param5}));
    }

    public static String formatMessage(String message, Object[] params) {
        return message != null && params != null && params.length != 0? MessageFormat.format(message, params):message;
    }

    public static String formatMessage(String message, Object param1) {
        return formatMessage(message, (Object[])(new Object[]{param1}));
    }

    public static String formatMessage(String message, Object param1, Object param2) {
        return formatMessage(message, (Object[])(new Object[]{param1, param2}));
    }

    public static String formatMessage(String message, Object param1, Object param2, Object param3) {
        return formatMessage(message, (Object[])(new Object[]{param1, param2, param3}));
    }

    public static String formatMessage(String message, Object param1, Object param2, Object param3, Object param4) {
        return formatMessage(message, (Object[])(new Object[]{param1, param2, param3, param4}));
    }

    public static String formatMessage(String message, Object param1, Object param2, Object param3, Object param4, Object param5) {
        return formatMessage(message, (Object[])(new Object[]{param1, param2, param3, param4, param5}));
    }
}
