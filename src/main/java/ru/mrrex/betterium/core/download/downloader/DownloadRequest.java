package ru.mrrex.betterium.core.download.downloader;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

public record DownloadRequest(
        URI sourceFileUri,
        Path destinationFilePath,
        Duration timeout,
        int retries
) {

    public DownloadRequest {
        Objects.requireNonNull(sourceFileUri, "Source file URI must not be null");
        Objects.requireNonNull(destinationFilePath, "Destination file path must not be null");
        Objects.requireNonNull(timeout, "Timeout must not be null");

        if (timeout.isNegative() || timeout.isZero())
            throw new IllegalArgumentException("Timeout must be greater than zero");

        if (retries < 1)
            throw new IllegalArgumentException("Retries must be greater than zero");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private URI sourceFileUri;
        private Path destinationFilePath;

        private Duration timeout = Duration.ofMinutes(5);
        private int retries = 1;

        private Builder() {}

        public Builder withSourceFileUri(URI sourceFileUri) {
            this.sourceFileUri = Objects.requireNonNull(sourceFileUri, "Source file URI (sourceFileUri) must not be null");
            return this;
        }

        public Builder withDestinationFilePath(Path destinationFilePath) {
            this.destinationFilePath = Objects.requireNonNull(destinationFilePath, "Destination file path (destinationFilePath) must not be null");
            return this;
        }

        public Builder withTimeout(Duration timeout) {
            this.timeout = Objects.requireNonNull(timeout, "Timeout must not be null");
            return this;
        }

        public Builder withRetries(int retries) {
            this.retries = retries;
            return this;
        }

        public DownloadRequest build() {
            if (sourceFileUri == null)
                throw new IllegalStateException("Source file URI (sourceFileUri) must be set before building request");

            if (destinationFilePath == null)
                throw new IllegalStateException("Destination file URI (destinationFilePath) must be set before building request");

            return new DownloadRequest(
                    sourceFileUri,
                    destinationFilePath,
                    timeout,
                    retries
            );
        }
    }
}
