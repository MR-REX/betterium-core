package ru.mrrex.betterium.core.download.exception;

import ru.mrrex.betterium.core.download.downloader.DownloadRequest;

public class UnsupportedDownloadRequestFormatException extends RuntimeException {

    private final DownloadRequest downloadRequest;

    public UnsupportedDownloadRequestFormatException(String message, DownloadRequest downloadRequest) {
        super(message);
        this.downloadRequest = downloadRequest;
    }

    public UnsupportedDownloadRequestFormatException(DownloadRequest downloadRequest) {
        this("Download request format is not supported", downloadRequest);
    }

    public DownloadRequest getDownloadRequest() {
        return downloadRequest;
    }
}
