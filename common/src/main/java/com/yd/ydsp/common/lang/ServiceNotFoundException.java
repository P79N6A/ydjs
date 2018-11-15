package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.exception.ChainedThrowable;
import com.yd.ydsp.common.lang.exception.ChainedThrowableDelegate;
import java.io.PrintStream;
import java.io.PrintWriter;

public class ServiceNotFoundException extends ClassNotFoundException implements ChainedThrowable {
    private static final long serialVersionUID = 3258126964232566584L;
    private final ChainedThrowable delegate = new ChainedThrowableDelegate(this);
    private Throwable cause;

    public ServiceNotFoundException() {
    }

    public ServiceNotFoundException(String message) {
        super(message);
    }

    public ServiceNotFoundException(Throwable cause) {
        super(cause == null?null:cause.getMessage());
        this.cause = cause;
    }

    public ServiceNotFoundException(String message, Throwable cause) {
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
