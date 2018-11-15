package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.ArrayUtil;
import com.yd.ydsp.common.lang.ClassLoaderUtil;
import com.yd.ydsp.common.lang.ClassUtil;
import com.yd.ydsp.common.lang.ExceptionUtil;
import com.yd.ydsp.common.lang.FileUtil;
import com.yd.ydsp.common.lang.MathUtil;
import com.yd.ydsp.common.lang.MessageUtil;
import com.yd.ydsp.common.lang.ObjectUtil;
import com.yd.ydsp.common.lang.StringEscapeUtil;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.lang.SystemUtil;
import com.yd.ydsp.common.lang.enumeration.EnumUtil;
import com.yd.ydsp.common.lang.i18n.LocaleUtil;
import com.yd.ydsp.common.lang.io.StreamUtil;
import java.util.Collections;
import java.util.Map;

public class Util {
    private static final ArrayUtil ARRAY_UTIL = new ArrayUtil();
    private static final ClassLoaderUtil CLASS_LOADER_UTIL = new ClassLoaderUtil();
    private static final ClassUtil CLASS_UTIL = new ClassUtil();
    private static final EnumUtil ENUM_UTIL = new EnumUtil();
    private static final ExceptionUtil EXCEPTION_UTIL = new ExceptionUtil();
    private static final FileUtil FILE_UTIL = new FileUtil();
    private static final LocaleUtil LOCALE_UTIL = new LocaleUtil();
    private static final MathUtil MATH_UTIL = new MathUtil();
    private static final MessageUtil MESSAGE_UTIL = new MessageUtil();
    private static final ObjectUtil OBJECT_UTIL = new ObjectUtil();
    private static final StreamUtil STREAM_UTIL = new StreamUtil();
    private static final StringEscapeUtil STRING_ESCAPE_UTIL = new StringEscapeUtil();
    private static final StringUtil STRING_UTIL = new StringUtil();
    private static final SystemUtil SYSTEM_UTIL = new SystemUtil();
    private static final Map ALL_UTILS;

    public Util() {
    }

    public static ArrayUtil getArrayUtil() {
        return ARRAY_UTIL;
    }

    public static ClassLoaderUtil getClassLoaderUtil() {
        return CLASS_LOADER_UTIL;
    }

    public static ClassUtil getClassUtil() {
        return CLASS_UTIL;
    }

    public static EnumUtil getEnumUtil() {
        return ENUM_UTIL;
    }

    public static ExceptionUtil getExceptionUtil() {
        return EXCEPTION_UTIL;
    }

    public static FileUtil getFileUtil() {
        return FILE_UTIL;
    }

    public static LocaleUtil getLocaleUtil() {
        return LOCALE_UTIL;
    }

    public static MathUtil getMathUtil() {
        return MATH_UTIL;
    }

    public static MessageUtil getMessageUtil() {
        return MESSAGE_UTIL;
    }

    public static ObjectUtil getObjectUtil() {
        return OBJECT_UTIL;
    }

    public static StreamUtil getStreamUtil() {
        return STREAM_UTIL;
    }

    public static StringEscapeUtil getStringEscapeUtil() {
        return STRING_ESCAPE_UTIL;
    }

    public static StringUtil getStringUtil() {
        return STRING_UTIL;
    }

    public static SystemUtil getSystemUtil() {
        return SYSTEM_UTIL;
    }

    public static Map getUtils() {
        return ALL_UTILS;
    }

    static {
        ALL_UTILS = Collections.unmodifiableMap(ArrayUtil.toMap(new Object[][]{{"arrayUtil", ARRAY_UTIL}, {"classLoaderUtil", CLASS_LOADER_UTIL}, {"classUtil", CLASS_UTIL}, {"enumUtil", ENUM_UTIL}, {"exceptionUtil", EXCEPTION_UTIL}, {"fileUtil", FILE_UTIL}, {"localeUtil", LOCALE_UTIL}, {"mathUtil", MATH_UTIL}, {"messageUtil", MESSAGE_UTIL}, {"objectUtil", OBJECT_UTIL}, {"streamUtil", STREAM_UTIL}, {"stringEscapeUtil", STRING_ESCAPE_UTIL}, {"stringUtil", STRING_UTIL}, {"systemUtil", SYSTEM_UTIL}}));
    }
}
