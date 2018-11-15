package com.yd.ydsp.common.lang.io;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OutputEngineInputStream extends InputStream {
    private static final int DEFAULT_INITIAL_BUFFER_SIZE = 8192;
    private OutputEngine engine;
    private byte[] buffer;
    private int index;
    private int limit;
    private int capacity;
    private boolean closed;
    private boolean eof;
    private byte[] one;

    public OutputEngineInputStream(OutputEngine engine) throws IOException {
        this(engine, 8192);
    }

    public OutputEngineInputStream(OutputEngine engine, int initialBufferSize) throws IOException {
        this.one = new byte[1];
        this.engine = engine;
        this.capacity = initialBufferSize;
        this.buffer = new byte[this.capacity];
        engine.open(new OutputEngineInputStream.OutputStreamImpl());
    }

    public int read() throws IOException {
        int amount = this.read(this.one, 0, 1);
        return amount < 0?-1:this.one[0] & 255;
    }

    public int read(byte[] data, int offset, int length) throws IOException {
        if(data == null) {
            throw new NullPointerException();
        } else if(offset >= 0 && offset + length <= data.length && length >= 0) {
            if(this.closed) {
                throw new IOException("Stream closed");
            } else {
                while(this.index >= this.limit) {
                    if(this.eof) {
                        return -1;
                    }

                    this.engine.execute();
                }

                if(this.limit - this.index < length) {
                    length = this.limit - this.index;
                }

                System.arraycopy(this.buffer, this.index, data, offset, length);
                this.index += length;
                return length;
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public long skip(long amount) throws IOException {
        if(this.closed) {
            throw new IOException("Stream closed");
        } else if(amount <= 0L) {
            return 0L;
        } else {
            while(this.index >= this.limit) {
                if(this.eof) {
                    return 0L;
                }

                this.engine.execute();
            }

            if((long)(this.limit - this.index) < amount) {
                amount = (long)(this.limit - this.index);
            }

            this.index += (int)amount;
            return amount;
        }
    }

    public int available() throws IOException {
        if(this.closed) {
            throw new IOException("Stream closed");
        } else {
            return this.limit - this.index;
        }
    }

    public void close() throws IOException {
        if(!this.closed) {
            this.closed = true;
            this.engine.close();
        }

    }

    private void writeImpl(byte[] data, int offset, int length) {
        if(this.index >= this.limit) {
            this.index = this.limit = 0;
        }

        if(this.limit + length > this.capacity) {
            this.capacity = this.capacity * 2 + length;
            byte[] tmp = new byte[this.capacity];
            System.arraycopy(this.buffer, this.index, tmp, 0, this.limit - this.index);
            this.buffer = tmp;
            this.limit -= this.index;
            this.index = 0;
        }

        System.arraycopy(data, offset, this.buffer, this.limit, length);
        this.limit += length;
    }

    private class OutputStreamImpl extends OutputStream {
        private OutputStreamImpl() {
        }

        public void write(int datum) throws IOException {
            OutputEngineInputStream.this.one[0] = (byte)datum;
            this.write(OutputEngineInputStream.this.one, 0, 1);
        }

        public void write(byte[] data, int offset, int length) throws IOException {
            if(data == null) {
                throw new NullPointerException();
            } else if(offset >= 0 && offset + length <= data.length && length >= 0) {
                if(OutputEngineInputStream.this.eof) {
                    throw new IOException("Stream closed");
                } else {
                    OutputEngineInputStream.this.writeImpl(data, offset, length);
                }
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public void close() {
            OutputEngineInputStream.this.eof = true;
        }
    }
}
