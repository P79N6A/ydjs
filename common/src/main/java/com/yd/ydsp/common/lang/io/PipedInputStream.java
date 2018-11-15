package com.yd.ydsp.common.lang.io;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

public class PipedInputStream extends InputStream {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final float DEFAULT_HYSTERESIS = 0.75F;
    private static final int DEFAULT_TIMEOUT_MS = 1000;
    private static final boolean READER = false;
    private static final boolean WRITER = true;
    private byte[] buffer;
    private int readx;
    private int writex;
    private int capacity;
    private int level;
    private boolean eof;
    private boolean closed;
    private boolean sleeping;
    private boolean nonBlocking;
    private Thread reader;
    private Thread writer;
    private IOException exception;
    private int timeout;
    private byte[] one;

    public PipedInputStream() {
        this(8192, 0.75F);
    }

    public PipedInputStream(int bufferSize) {
        this(bufferSize, 0.75F);
    }

    public PipedInputStream(int bufferSize, float hysteresis) {
        this.timeout = 1000;
        this.one = new byte[1];
        if((double)hysteresis >= 0.0D && (double)hysteresis <= 1.0D) {
            this.capacity = bufferSize;
            this.buffer = new byte[this.capacity];
            this.level = (int)((float)bufferSize * hysteresis);
        } else {
            throw new IllegalArgumentException("Hysteresis: " + hysteresis);
        }
    }

    public void setTimeout(int ms) {
        this.timeout = ms;
    }

    public void setNonBlocking(boolean nonBlocking) {
        this.nonBlocking = nonBlocking;
    }

    public int read() throws IOException {
        int amount = this.read(this.one, 0, 1);
        return amount < 0?-1:this.one[0] & 255;
    }

    public synchronized int read(byte[] data, int offset, int length) throws IOException {
        if(this.reader == null) {
            this.reader = Thread.currentThread();
        }

        if(data == null) {
            throw new NullPointerException();
        } else if(offset >= 0 && offset + length <= data.length && length >= 0) {
            this.closedCheck();
            this.exceptionCheck();
            if(length <= 0) {
                return 0;
            } else {
                int available = this.checkedAvailable(false);
                if(available < 0) {
                    return -1;
                } else {
                    int contiguous = this.capacity - this.readx % this.capacity;
                    int amount = length > available?available:length;
                    if(amount > contiguous) {
                        System.arraycopy(this.buffer, this.readx % this.capacity, data, offset, contiguous);
                        System.arraycopy(this.buffer, 0, data, offset + contiguous, amount - contiguous);
                    } else {
                        System.arraycopy(this.buffer, this.readx % this.capacity, data, offset, amount);
                    }

                    this.processed(false, amount);
                    return amount;
                }
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public synchronized long skip(long amount) throws IOException {
        if(this.reader == null) {
            this.reader = Thread.currentThread();
        }

        this.closedCheck();
        this.exceptionCheck();
        if(amount <= 0L) {
            return 0L;
        } else {
            int available = this.checkedAvailable(false);
            if(available < 0) {
                return 0L;
            } else {
                if(amount > (long)available) {
                    amount = (long)available;
                }

                this.processed(false, (int)amount);
                return amount;
            }
        }
    }

    private void processed(boolean rw, int amount) {
        if(!rw) {
            this.readx = (this.readx + amount) % (this.capacity * 2);
        } else {
            this.writex = (this.writex + amount) % (this.capacity * 2);
        }

        if(this.sleeping && this.available(!rw) >= this.level) {
            this.notify();
            this.sleeping = false;
        }

    }

    public synchronized int available() throws IOException {
        this.closedCheck();
        this.exceptionCheck();
        int amount = this.available(false);
        return amount < 0?0:amount;
    }

    private int checkedAvailable(boolean rw) throws IOException {
        try {
            while(true) {
                int ex;
                if((ex = this.available(rw)) == 0) {
                    if(!rw) {
                        this.exceptionCheck();
                    } else {
                        this.closedCheck();
                    }

                    this.brokenCheck(rw);
                    if(!this.nonBlocking) {
                        if(this.sleeping) {
                            this.notify();
                        }

                        this.sleeping = true;
                        this.wait((long)this.timeout);
                        continue;
                    }

                    throw new InterruptedIOException("Pipe " + (rw?"full":"empty"));
                }

                return ex;
            }
        } catch (InterruptedException var3) {
            throw new InterruptedIOException(var3.getMessage());
        }
    }

    private int available(boolean rw) {
        int used = (this.writex + this.capacity * 2 - this.readx) % (this.capacity * 2);
        return rw?this.capacity - used:(this.eof && used == 0?-1:used);
    }

    public void close() throws IOException {
        this.close(false);
    }

    private synchronized void close(boolean rw) throws IOException {
        if(!rw) {
            this.closed = true;
        } else if(!this.eof) {
            this.eof = true;
            if(this.available(false) > 0) {
                this.closedCheck();
                this.brokenCheck(true);
            }
        }

        if(this.sleeping) {
            this.notify();
            this.sleeping = false;
        }

    }

    private void exceptionCheck() throws IOException {
        if(this.exception != null) {
            IOException ex = this.exception;
            this.exception = null;
            throw ex;
        }
    }

    private void closedCheck() throws IOException {
        if(this.closed) {
            throw new IOException("Stream closed");
        }
    }

    private void brokenCheck(boolean rw) throws IOException {
        Thread thread = rw?this.reader:this.writer;
        if(thread != null && !thread.isAlive()) {
            throw new IOException("Broken pipe");
        }
    }

    private synchronized void writeImpl(byte[] data, int offset, int length) throws IOException {
        if(this.writer == null) {
            this.writer = Thread.currentThread();
        }

        if(!this.eof && !this.closed) {
            int written = 0;

            try {
                do {
                    int ex = this.checkedAvailable(true);
                    int contiguous = this.capacity - this.writex % this.capacity;
                    int amount = length > ex?ex:length;
                    if(amount > contiguous) {
                        System.arraycopy(data, offset, this.buffer, this.writex % this.capacity, contiguous);
                        System.arraycopy(data, offset + contiguous, this.buffer, 0, amount - contiguous);
                    } else {
                        System.arraycopy(data, offset, this.buffer, this.writex % this.capacity, amount);
                    }

                    this.processed(true, amount);
                    written += amount;
                } while(written < length);

            } catch (InterruptedIOException var8) {
                var8.bytesTransferred = written;
                throw var8;
            }
        } else {
            throw new IOException("Stream closed");
        }
    }

    private synchronized void setException(IOException ex) throws IOException {
        if(this.exception != null) {
            throw new IOException("Exception already set: " + this.exception);
        } else {
            this.brokenCheck(true);
            this.exception = ex;
            if(this.sleeping) {
                this.notify();
                this.sleeping = false;
            }

        }
    }

    public PipedInputStream.OutputStreamEx getOutputStream() {
        return new PipedInputStream.OutputStreamImpl();
    }

    public abstract static class OutputStreamEx extends OutputStream {
        public OutputStreamEx() {
        }

        public abstract void setException(IOException var1) throws IOException;
    }

    private class OutputStreamImpl extends PipedInputStream.OutputStreamEx {
        private byte[] one;

        private OutputStreamImpl() {
            this.one = new byte[1];
        }

        public void write(int datum) throws IOException {
            this.one[0] = (byte)datum;
            this.write(this.one, 0, 1);
        }

        public void write(byte[] data, int offset, int length) throws IOException {
            if(data == null) {
                throw new NullPointerException();
            } else if(offset >= 0 && offset + length <= data.length && length >= 0) {
                if(length > 0) {
                    PipedInputStream.this.writeImpl(data, offset, length);
                }

            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public void close() throws IOException {
            PipedInputStream.this.close(true);
        }

        public void setException(IOException ex) throws IOException {
            PipedInputStream.this.setException(ex);
        }
    }
}
