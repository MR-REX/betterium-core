package ru.mrrex.betterium.core.download.downloader;

import ru.mrrex.betterium.core.download.listener.DownloadCompletionListener;
import ru.mrrex.betterium.core.download.listener.DownloadProgressListener;

import java.util.Collection;

public interface FileDownloader extends AutoCloseable {

    void setDownloadProgressListener(DownloadProgressListener downloadProgressListener);
    void setDownloadCompletionListener(DownloadCompletionListener downloadCompletionListener);

    boolean canHandle(DownloadRequest downloadRequest);
    boolean isBusy();

    void enqueue(DownloadRequest downloadRequest);
    void enqueue(Collection<DownloadRequest> downloadRequests);

    void download();
}
