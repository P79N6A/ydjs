package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
    public ExceptionUtil() {
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        throwable.printStackTrace(out);
        out.flush();
        return buffer.toString();
    }
}
