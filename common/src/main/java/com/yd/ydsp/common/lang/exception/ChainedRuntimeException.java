package com.yd.ydsp.common.lang.exception;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.exception.ChainedThrowable;
import com.yd.ydsp.common.lang.exception.ChainedThrowableDelegate;
import java.io.PrintStream;
import java.io.PrintWriter;

public class ChainedRuntimeException extends RuntimeException implements ChainedThrowable {
    private static final long serialVersionUID = 3257005466717533235L;
    private final ChainedThrowable delegate = new ChainedThrowableDelegate(this);
    private Throwable cause;

    public ChainedRuntimeException() {
    }

    public ChainedRuntimeException(String message) {
        super(message);
    }

    public ChainedRuntimeException(Throwable cause) {
        super(cause == null?null:cause.getMessage());
        this.cause = cause;
    }

    public ChainedRuntimeException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public void printStackTrace() {
        this.delegate.printStackTrace();
    }

    public void printStackTrace(PrintStream stream) {
        this.delegate.printStackTrace(stream);
    }

    public void printStackTrace(PrintWriter writer) {
        this.delegate.printStackTrace(writer);
    }

    public void printCurrentStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
    }
}
