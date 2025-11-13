package ru.mrrex.betterium.core.checksum;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.Checksum;

public class ChecksumCalculator {

    private static final int BUFFER_SIZE = 8 * 1024;

    private final Checksum checksum;

    public ChecksumCalculator(Checksum checksum) {
        this.checksum = Objects.requireNonNull(checksum, "Checksum instance must not be null");
    }

    public long calculate(byte[] data) {
        checksum.reset();
        checksum.update(data);

        return checksum.getValue();
    }

    public long calculate(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        checksum.reset();

        while (inputStream.read(buffer) != -1) {
            checksum.update(buffer);
        }

        return checksum.getValue();
    }

    public long calculate(Path filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return calculate(inputStream);
        }
    }
}
