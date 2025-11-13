package ru.mrrex.betterium.core.hash;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class HashCalculator {

    private static final int BUFFER_SIZE = 8 * 1024;

    private final HashAlgorithm algorithm;
    private final MessageDigest messageDigest;

    public HashCalculator(HashAlgorithm algorithm) throws NoSuchAlgorithmException {
        this.algorithm = Objects.requireNonNull(algorithm, "Hash algorithm must not be null");
        this.messageDigest = MessageDigest.getInstance(algorithm.getMessageDigestInstanceName());
    }

    public HashAlgorithm getAlgorithm() {
        return algorithm;
    }

    public Hash calculate(byte[] data) {
        messageDigest.reset();
        messageDigest.update(data);

        return toHash(messageDigest.digest());
    }

    public Hash calculate(String string) {
        return calculate(string.getBytes(StandardCharsets.UTF_8));
    }

    public Hash calculate(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        messageDigest.reset();

        while (inputStream.read(buffer) != -1) {
            messageDigest.update(buffer);
        }

        return toHash(messageDigest.digest());
    }

    public Hash calculate(Path filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return calculate(inputStream);
        }
    }

    private Hash toHash(byte[] hashBytes) {
        return new Hash(algorithm, hashBytes);
    }
}
