package com.yd.ydsp.common.lang.io;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.io.IOException;
import java.io.OutputStream;

public interface OutputEngine {
    OutputEngine.OutputStreamFactory DEFAULT_OUTPUT_STREAM_FACTORY = new OutputEngine.OutputStreamFactory() {
        public OutputStream getOutputStream(OutputStream out) {
            return out;
        }
    };

    void open(OutputStream var1) throws IOException;

    void execute() throws IOException;

    void close() throws IOException;

    public interface OutputStreamFactory {
        OutputStream getOutputStream(OutputStream var1) throws IOException;
    }
}
