package ru.mrrex.betterium.core.download.listener;

import ru.mrrex.betterium.core.download.downloader.DownloadRequest;

import java.time.Duration;

public interface DownloadCompletionListener {

    void onSuccess(DownloadRequest downloadRequest, Duration downloadDuration);
    void onFailure(DownloadRequest downloadRequest, Throwable throwable);
}
