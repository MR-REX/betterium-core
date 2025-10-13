package ru.mrrex.betterium.core.download.stream;

public interface StreamProgressListener {

    void onProgress(long bytesRead, long totalBytes);
}
