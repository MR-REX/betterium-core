package ru.mrrex.betterium.core.download.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class CountingInputStream extends InputStream {

    private final InputStream delegate;
    private final StreamProgressListener progressListener;
    private final long totalBytes;

    private long bytesRead = 0;

    public CountingInputStream(InputStream delegate, long totalBytes, StreamProgressListener progressListener) {
        this.delegate = Objects.requireNonNull(delegate, "Input stream must not be null");
        this.progressListener = Objects.requireNonNull(progressListener, "Download progress listener most not be null");
        this.totalBytes = totalBytes;
    }

    private void updateProgress(int bytesReadCount) {
        if (bytesReadCount < 1)
            return;

        bytesRead += bytesReadCount;
        progressListener.onProgress(bytesRead, totalBytes);
    }

    @Override
    public int read() throws IOException {
        int byteValue = delegate.read();

        if (byteValue != -1)
            updateProgress(1);

        return byteValue;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesReadCount = delegate.read(b, off, len);
        updateProgress(bytesReadCount);

        return bytesReadCount;
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
