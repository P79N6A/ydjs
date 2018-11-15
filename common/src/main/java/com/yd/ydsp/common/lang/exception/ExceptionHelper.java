package com.yd.ydsp.common.lang.exception;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ExceptionHelper {
    private static final String STRING_EXCEPTION_MESSAGE = ": ";
    private static final String STRING_CAUSED_BY = "Caused by: ";
    private static final String STRING_MORE_PREFIX = "\t... ";
    private static final String STRING_MORE_SUFFIX = " more";
    private static final String STRING_STACK_TRACE_PREFIX = "\tat ";
    private static final String STRING_CR = "\r";
    private static final String STRING_LF = "\n";
    private static final String GET_STACK_TRACE_METHOD_NAME = "getStackTrace";
    private static Method GET_STACK_TRACE_METHOD;

    public ExceptionHelper() {
    }

    private static Throwable getThrowable(ChainedThrowable throwable) {
        return throwable instanceof ChainedThrowableDelegate?((ChainedThrowableDelegate)throwable).delegatedThrowable:(Throwable)throwable;
    }

    public static ChainedThrowable getChainedThrowable(Throwable throwable) {
        return (ChainedThrowable)(throwable != null && !(throwable instanceof ChainedThrowable)?new ChainedThrowableDelegate(throwable):(ChainedThrowable)throwable);
    }

    public static ChainedThrowable getChainedThrowableCause(ChainedThrowable throwable) {
        return getChainedThrowable(throwable.getCause());
    }

    public static void printStackTrace(ChainedThrowable throwable) {
        printStackTrace(throwable, (PrintStream)System.err);
    }

    public static void printStackTrace(ChainedThrowable throwable, PrintStream stream) {
        printStackTrace(throwable, (PrintWriter)(new PrintWriter(stream)));
    }

    public static void printStackTrace(ChainedThrowable throwable, PrintWriter writer) {
        synchronized(writer) {
            String[] currentStack = analyzeStackTrace(throwable);
            printThrowableMessage(throwable, writer, false);

            for(int i = 0; i < currentStack.length; ++i) {
                writer.println(currentStack[i]);
            }

            printStackTraceRecursive(throwable, writer, currentStack);
            writer.flush();
        }
    }

    private static void printStackTraceRecursive(ChainedThrowable throwable, PrintWriter writer, String[] currentStack) {
        ChainedThrowable cause = getChainedThrowableCause(throwable);
        if(cause != null) {
            String[] causeStack = analyzeStackTrace(cause);
            int i = currentStack.length - 1;

            int j;
            for(j = causeStack.length - 1; i >= 0 && j >= 0 && currentStack[i].equals(causeStack[j]); --j) {
                --i;
            }

            printThrowableMessage(cause, writer, true);

            for(i = 0; i <= j; ++i) {
                writer.println(causeStack[i]);
            }

            if(j < causeStack.length - 1) {
                writer.println("\t... " + (causeStack.length - j - 1) + " more");
            }

            printStackTraceRecursive(cause, writer, causeStack);
        }

    }

    private static void printThrowableMessage(ChainedThrowable throwable, PrintWriter writer, boolean cause) {
        StringBuffer buffer = new StringBuffer();
        if(cause) {
            buffer.append("Caused by: ");
        }

        Throwable t = getThrowable(throwable);
        buffer.append(t.getClass().getName());
        String message = t.getMessage();
        if(message != null) {
            buffer.append(": ").append(message);
        }

        writer.println(buffer.toString());
    }

    private static String[] analyzeStackTrace(ChainedThrowable throwable) {
        if(GET_STACK_TRACE_METHOD != null) {
            Throwable t = getThrowable(throwable);

            try {
                Object[] e = (Object[])((Object[])GET_STACK_TRACE_METHOD.invoke(t, new Object[0]));
                String[] list = new String[e.length];

                for(int i = 0; i < e.length; ++i) {
                    list[i] = "\tat " + e[i];
                }

                return list;
            } catch (IllegalAccessException var5) {
                ;
            } catch (IllegalArgumentException var6) {
                ;
            } catch (InvocationTargetException var7) {
                ;
            }
        }

        return (new ExceptionHelper.StackTraceAnalyzer(throwable)).getLines();
    }

    static {
        try {
            GET_STACK_TRACE_METHOD = Throwable.class.getMethod("getStackTrace", new Class[0]);
        } catch (NoSuchMethodException var1) {
            ;
        }

    }

    private static class StackTraceAnalyzer {
        private Throwable throwable;
        private String message;
        private ExceptionHelper.StackTraceAnalyzer.StackTraceEntry currentEntry = new ExceptionHelper.StackTraceAnalyzer.StackTraceEntry();
        private ExceptionHelper.StackTraceAnalyzer.StackTraceEntry selectedEntry;
        private ExceptionHelper.StackTraceAnalyzer.StackTraceEntry entry;

        StackTraceAnalyzer(ChainedThrowable throwable) {
            this.selectedEntry = this.currentEntry;
            this.throwable = ExceptionHelper.getThrowable(throwable);
            this.message = this.throwable.getMessage();
            StringWriter writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            throwable.printCurrentStackTrace(pw);
            String stackTraceDump = writer.toString();
            int p = 0;
            int i = -1;
            int j = -1;
            int k = -1;

            while(p < stackTraceDump.length()) {
                boolean includesMessage = false;
                int s = p;
                if(i == -1 && this.message != null) {
                    i = stackTraceDump.indexOf(this.message, p);
                }

                if(j == -1) {
                    j = stackTraceDump.indexOf("\r", p);
                }

                if(k == -1) {
                    k = stackTraceDump.indexOf("\n", p);
                }

                if(i != -1 && (j == -1 || i <= j) && (k == -1 || i <= k)) {
                    includesMessage = true;
                    p = i + this.message.length();
                    i = -1;
                    if(j < p) {
                        j = -1;
                    }

                    if(k < p) {
                        k = -1;
                    }
                }

                if(j != -1 && (k == -1 || j < k)) {
                    p = j + 1;
                    if(p < stackTraceDump.length() && stackTraceDump.charAt(p) == "\n".charAt(0)) {
                        ++p;
                    }

                    this.addLine(stackTraceDump.substring(s, j), includesMessage);
                    j = -1;
                    if(k < p) {
                        k = -1;
                    }
                } else {
                    int q;
                    if(k != -1) {
                        q = k + 1;
                        this.addLine(stackTraceDump.substring(p, k), includesMessage);
                        p = q;
                        k = -1;
                    } else {
                        q = stackTraceDump.length();
                        if(p + 1 < q) {
                            this.addLine(stackTraceDump.substring(s), includesMessage);
                            p = q;
                        }
                    }
                }
            }

            if(this.currentEntry.compareTo(this.selectedEntry) < 0) {
                this.selectedEntry = this.currentEntry;
            }

        }

        private void addLine(String line, boolean includesMessage) {
            ExceptionHelper.StackTraceAnalyzer.StackTraceEntry nextEntry = this.currentEntry.accept(line, includesMessage);
            if(nextEntry != null) {
                if(this.currentEntry.compareTo(this.selectedEntry) < 0) {
                    this.selectedEntry = this.currentEntry;
                }

                this.currentEntry = nextEntry;
            }

        }

        String[] getLines() {
            return (String[])((String[])this.selectedEntry.lines.toArray(new String[this.selectedEntry.lines.size()]));
        }

        private class StackTraceEntry implements Comparable {
            private List lines;
            private int includesMessage;
            private int includesThrowable;
            private int count;

            private StackTraceEntry() {
                this.lines = new ArrayList(10);
                this.includesMessage = 0;
                this.includesThrowable = 0;
                this.count = 0;
            }

            ExceptionHelper.StackTraceAnalyzer.StackTraceEntry accept(String line, boolean includesMessage) {
                if(line.startsWith("\tat ")) {
                    this.lines.add(line);
                    ++this.count;
                    return null;
                } else if(this.count > 0) {
                    ExceptionHelper.StackTraceAnalyzer.StackTraceEntry newEntry = StackTraceAnalyzer.this.new StackTraceEntry();
                    newEntry.accept(line, includesMessage);
                    return newEntry;
                } else {
                    if(includesMessage) {
                        this.includesMessage = 1;
                    }

                    if(line.indexOf(StackTraceAnalyzer.this.throwable.getClass().getName()) != -1) {
                        this.includesThrowable = 1;
                    }

                    return null;
                }
            }

            public int compareTo(Object o) {
                ExceptionHelper.StackTraceAnalyzer.StackTraceEntry otherEntry = (ExceptionHelper.StackTraceAnalyzer.StackTraceEntry)o;
                int thisWeight = this.includesMessage + this.includesThrowable;
                int otherWeight = otherEntry.includesMessage + otherEntry.includesThrowable;
                return thisWeight == otherWeight?this.count - otherEntry.count:otherWeight - thisWeight;
            }
        }
    }
}
