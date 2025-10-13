package ru.mrrex.betterium.core.download.downloader.http;

import ru.mrrex.betterium.core.download.downloader.FileDownloader;
import ru.mrrex.betterium.core.download.exception.UnsupportedDownloadRequestFormatException;
import ru.mrrex.betterium.core.download.listener.DownloadCompletionListener;
import ru.mrrex.betterium.core.download.listener.DownloadProgressListener;
import ru.mrrex.betterium.core.download.downloader.DownloadRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpFileDownloader implements FileDownloader {

    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";

    private static final int DEFAULT_THREAD_POOL_SIZE = 1;
    private static final int EXECUTOR_TERMINATION_TIMEOUT_SECONDS = 30;

    private final ExecutorService executorService;

    private final HttpClient httpClient;
    private final Queue<DownloadRequest> requestQueue;

    private final AtomicBoolean hasActiveDownloads;

    private DownloadProgressListener downloadProgressListener;
    private DownloadCompletionListener downloadCompletionListener;

    public HttpFileDownloader(int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);

        this.httpClient = createHttpClient();
        this.requestQueue = new ConcurrentLinkedQueue<>();

        this.hasActiveDownloads = new AtomicBoolean(false);
    }

    public HttpFileDownloader() {
        this(DEFAULT_THREAD_POOL_SIZE);
    }

    @Override
    public void setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
    }

    @Override
    public void setDownloadCompletionListener(DownloadCompletionListener downloadCompletionListener) {
        this.downloadCompletionListener = downloadCompletionListener;
    }

    @Override
    public boolean canHandle(DownloadRequest downloadRequest) {
        Objects.requireNonNull(downloadRequest, "Download request must not be null");

        URI sourceFileUri = downloadRequest.sourceFileUri();

        if (sourceFileUri == null)
            return false;

        String scheme = sourceFileUri.getScheme();

        return scheme.equalsIgnoreCase(HTTP_SCHEME) || scheme.equalsIgnoreCase(HTTPS_SCHEME);
    }

    @Override
    public boolean isBusy() {
        return hasActiveDownloads.get();
    }

    @Override
    public void enqueue(DownloadRequest downloadRequest) {
        Objects.requireNonNull(downloadRequest, "Download request must not be null");

        if (!canHandle(downloadRequest))
            throw new UnsupportedDownloadRequestFormatException(downloadRequest);

        requestQueue.add(downloadRequest);
    }

    @Override
    public void enqueue(Collection<DownloadRequest> downloadRequests) {
        Objects.requireNonNull(downloadRequests, "Download requests collection must not be null");

        if (downloadRequests.isEmpty())
            return;

        downloadRequests.forEach(request -> {
            if (request != null)
                enqueue(request);
        });
    }

    @Override
    public void download() {
        if (requestQueue.isEmpty())
            return;

        List<Callable<Duration>> downloadTasks = requestQueue.stream()
                .map(this::createTask)
                .toList();

        hasActiveDownloads.set(true);

        try {
            executorService.invokeAll(downloadTasks);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        } finally {
            requestQueue.clear();
            hasActiveDownloads.set(false);
        }
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    private Callable<Duration> createTask(DownloadRequest downloadRequest) {
        return () -> {
            try {
                HttpDownloadTask downloadTask = new HttpDownloadTask(httpClient, downloadRequest);

                if (downloadProgressListener != null)
                    downloadTask.setProgressListener(downloadProgressListener);

                Duration downloadDuration = downloadTask.call();
                handleDownloadSuccess(downloadRequest, downloadDuration);

                return downloadDuration;
            } catch (InterruptedException exception) {
                handleDownloadFailure(downloadRequest, exception);
                Thread.currentThread().interrupt();

                return null;
            } catch (Exception exception) {
                handleDownloadFailure(downloadRequest, exception);

                return null;
            }
        };
    }

    private void handleDownloadSuccess(DownloadRequest downloadRequest, Duration downloadDuration) {
        if (downloadCompletionListener == null)
            return;

        downloadCompletionListener.onSuccess(downloadRequest, downloadDuration);
    }

    private void handleDownloadFailure(DownloadRequest downloadRequest, Throwable throwable) {
        if (downloadCompletionListener == null)
            return;

        downloadCompletionListener.onFailure(downloadRequest, throwable);
    }

    private void shutdownExecutorService() {
        executorService.shutdown();

        try {
            if (executorService.awaitTermination(EXECUTOR_TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                return;

            executorService.shutdownNow();

            if (!executorService.awaitTermination(EXECUTOR_TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                throw new IllegalStateException("Executor service failed to terminate even after .shutdownNow()");
        } catch (InterruptedException _) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        if (executorService != null)
            shutdownExecutorService();

        if (httpClient != null)
            httpClient.close();

        if (requestQueue != null)
            requestQueue.clear();
    }
}
