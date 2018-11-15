package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.exception.ChainedRuntimeException;

public class CloneNotSupportedException extends ChainedRuntimeException {
    private static final long serialVersionUID = 3257281439807584562L;

    public CloneNotSupportedException() {
    }

    public CloneNotSupportedException(String message) {
        super(message);
    }

    public CloneNotSupportedException(Throwable cause) {
        super(cause);
    }

    public CloneNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}