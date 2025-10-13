package ru.mrrex.betterium.core.download.listener;

import ru.mrrex.betterium.core.download.downloader.DownloadRequest;

public interface DownloadProgressListener {

    void onProgress(DownloadRequest downloadRequest, long bytesRead, long totalBytes);
}
