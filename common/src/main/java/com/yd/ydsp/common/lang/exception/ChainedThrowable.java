package com.yd.ydsp.common.lang.exception;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;

public interface ChainedThrowable extends Serializable {
    Throwable getCause();

    void printStackTrace();

    void printStackTrace(PrintStream var1);

    void printStackTrace(PrintWriter var1);

    void printCurrentStackTrace(PrintWriter var1);
}
