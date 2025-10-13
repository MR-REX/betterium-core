package ru.mrrex.betterium.core.download.downloader.http;

import ru.mrrex.betterium.core.download.listener.DownloadProgressListener;
import ru.mrrex.betterium.core.download.stream.CountingInputStream;
import ru.mrrex.betterium.core.download.stream.StreamProgressListener;
import ru.mrrex.betterium.core.download.downloader.DownloadRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;

public class HttpDownloadTask implements Callable<Duration> {

    private static final int HTTP_OK_STATUS_CODE = 200;
    private static final String HTTP_CONTENT_LENGTH_HEADER = "Content-Length";

    private static final int BUFFER_SIZE = 8096;

    private final HttpClient httpClient;
    private final DownloadRequest downloadRequest;

    private DownloadProgressListener downloadProgressListener;

    protected HttpDownloadTask(HttpClient httpClient, DownloadRequest downloadRequest) {
        this.httpClient = httpClient;
        this.downloadRequest = downloadRequest;
    }

    public void setProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = Objects.requireNonNull(downloadProgressListener, "Download progress listener must not be null");
    }

    @Override
    public Duration call() throws IOException, InterruptedException {
        long startedAt = System.currentTimeMillis();

        HttpResponse<InputStream> httpResponse = sendHttpRequest();
        handleHttpResponse(httpResponse);

        return Duration.ofMillis(System.currentTimeMillis() - startedAt);
    }

    private HttpRequest createHttpRequest() {
        return HttpRequest.newBuilder()
                .uri(downloadRequest.sourceFileUri())
                .timeout(downloadRequest.timeout())
                .GET()
                .build();
    }

    private HttpResponse<InputStream> sendHttpRequest() throws IOException, InterruptedException {
        return httpClient.send(
                createHttpRequest(),
                HttpResponse.BodyHandlers.ofInputStream()
        );
    }

    private long getTotalBytes(HttpResponse<InputStream> httpResponse) {
        return httpResponse.headers()
                .firstValueAsLong(HTTP_CONTENT_LENGTH_HEADER)
                .orElse(-1L);
    }

    private InputStream getInputStream(HttpResponse<InputStream> httpResponse) {
        if (downloadProgressListener == null)
            return httpResponse.body();

        StreamProgressListener streamProgressListener = (bytesRead, totalBytes) ->
                downloadProgressListener.onProgress(downloadRequest, bytesRead, totalBytes);

        return new CountingInputStream(
                httpResponse.body(),
                getTotalBytes(httpResponse),
                streamProgressListener
        );
    }

    private void handleHttpResponse(HttpResponse<InputStream> httpResponse) throws IOException {
        int statusCode = httpResponse.statusCode();

        if (statusCode != HTTP_OK_STATUS_CODE)
            throw new IOException("Failed to download file. HTTP status code is " + statusCode);

        try (InputStream inputStream = getInputStream(httpResponse);
             OutputStream outputStream = Files.newOutputStream(downloadRequest.destinationFilePath())
        ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);
        }
    }
}
