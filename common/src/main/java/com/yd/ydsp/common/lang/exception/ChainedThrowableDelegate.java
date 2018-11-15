package com.yd.ydsp.common.lang.exception;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.exception.ChainedThrowable;
import com.yd.ydsp.common.lang.exception.ExceptionHelper;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class ChainedThrowableDelegate implements ChainedThrowable {
    private static final long serialVersionUID = 3257288032683241523L;
    protected static final Throwable NO_CAUSE = new Throwable();
    private static final String[] CAUSE_METHOD_NAMES = new String[]{"getNested", "getNestedException", "getNextException", "getTargetException", "getException", "getSourceException", "getCausedByException", "getRootCause", "getCause"};
    private static final String[] CAUSE_FIELD_NAMES = new String[]{"detail"};
    protected Throwable delegatedThrowable;

    public ChainedThrowableDelegate(Throwable throwable) {
        this.delegatedThrowable = throwable;
    }

    public Throwable getCause() {
        Throwable cause = this.getCauseByWellKnownTypes(this.delegatedThrowable);

        for(Class throwableClass = this.delegatedThrowable.getClass(); cause == null && Throwable.class.isAssignableFrom(throwableClass); throwableClass = throwableClass.getSuperclass()) {
            int i;
            for(i = 0; cause == null && i < CAUSE_METHOD_NAMES.length; ++i) {
                cause = this.getCauseByMethodName(this.delegatedThrowable, throwableClass, CAUSE_METHOD_NAMES[i]);
            }

            for(i = 0; cause == null && i < CAUSE_FIELD_NAMES.length; ++i) {
                cause = this.getCauseByFieldName(this.delegatedThrowable, throwableClass, CAUSE_FIELD_NAMES[i]);
            }
        }

        if(cause == this.delegatedThrowable) {
            cause = null;
        }

        return cause == NO_CAUSE?null:cause;
    }

    protected Throwable getCauseByWellKnownTypes(Throwable throwable) {
        Object cause = null;
        boolean isWellKnownType = false;
        if(throwable instanceof ChainedThrowable) {
            isWellKnownType = true;
            cause = ((ChainedThrowable)throwable).getCause();
        } else if(throwable instanceof SQLException) {
            isWellKnownType = true;
            cause = ((SQLException)throwable).getNextException();
        } else if(throwable instanceof InvocationTargetException) {
            isWellKnownType = true;
            cause = ((InvocationTargetException)throwable).getTargetException();
        } else if(throwable instanceof RemoteException) {
            isWellKnownType = true;
            cause = ((RemoteException)throwable).detail;
        }

        return (Throwable)(isWellKnownType && cause == null?NO_CAUSE:cause);
    }

    protected Throwable getCauseByMethodName(Throwable throwable, Class throwableClass, String methodName) {
        Method method = null;

        try {
            method = throwableClass.getMethod(methodName, new Class[0]);
        } catch (NoSuchMethodException var10) {
            ;
        }

        if(method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            Throwable cause = null;

            try {
                cause = (Throwable)method.invoke(throwable, new Object[0]);
            } catch (IllegalAccessException var7) {
                ;
            } catch (IllegalArgumentException var8) {
                ;
            } catch (InvocationTargetException var9) {
                ;
            }

            return cause == null?NO_CAUSE:cause;
        } else {
            return null;
        }
    }

    protected Throwable getCauseByFieldName(Throwable throwable, Class throwableClass, String fieldName) {
        Field field = null;

        try {
            field = throwableClass.getField(fieldName);
        } catch (NoSuchFieldException var9) {
            ;
        }

        if(field != null && Throwable.class.isAssignableFrom(field.getType())) {
            Throwable cause = null;

            try {
                cause = (Throwable)field.get(throwable);
            } catch (IllegalAccessException var7) {
                ;
            } catch (IllegalArgumentException var8) {
                ;
            }

            return cause == null?NO_CAUSE:cause;
        } else {
            return null;
        }
    }

    public void printStackTrace() {
        ExceptionHelper.printStackTrace(this);
    }

    public void printStackTrace(PrintStream stream) {
        ExceptionHelper.printStackTrace(this, stream);
    }

    public void printStackTrace(PrintWriter writer) {
        ExceptionHelper.printStackTrace(this, writer);
    }

    public void printCurrentStackTrace(PrintWriter writer) {
        if(this.delegatedThrowable instanceof ChainedThrowable) {
            ((ChainedThrowable)this.delegatedThrowable).printCurrentStackTrace(writer);
        } else {
            this.delegatedThrowable.printStackTrace(writer);
        }

    }
}
