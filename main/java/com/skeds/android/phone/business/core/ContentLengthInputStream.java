package com.skeds.android.phone.business.core;

import java.io.IOException;
import java.io.InputStream;

public class ContentLengthInputStream extends InputStream {

    private final InputStream stream;
    private final long length;

    private long pos;

    public ContentLengthInputStream(InputStream stream, long length) {
        this.stream = stream;
        this.length = length;
    }

    @Override
    public synchronized int available() {
        return (int) (length - pos);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void mark(final int readlimit) {
        pos = readlimit;
        stream.mark(readlimit);
    }

    @Override
    public int read() throws IOException {
        pos++;
        return stream.read();
    }

    @Override
    public int read(final byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    @Override
    public int read(final byte[] buffer, final int byteOffset, final int byteCount) throws IOException {
        pos += byteCount;
        return stream.read(buffer, byteOffset, byteCount);
    }

    @Override
    public synchronized void reset() throws IOException {
        pos = 0;
        stream.reset();
    }

    @Override
    public long skip(final long byteCount) throws IOException {
        pos += byteCount;
        return stream.skip(byteCount);
    }
}
