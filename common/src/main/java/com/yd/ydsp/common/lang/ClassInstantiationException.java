package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */
import com.yd.ydsp.common.lang.exception.ChainedException;

public class ClassInstantiationException extends ChainedException {
    private static final long serialVersionUID = 3258408422113555761L;

    public ClassInstantiationException() {
    }

    public ClassInstantiationException(String message) {
        super(message);
    }

    public ClassInstantiationException(Throwable cause) {
        super(cause);
    }

    public ClassInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
