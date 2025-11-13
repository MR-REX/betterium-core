package ru.mrrex.betterium.core.checksum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public enum ChecksumAlgorithm {

    CRC32,
    CRC32C,
    ADLER32;

    @JsonCreator
    public static ChecksumAlgorithm fromValue(String value) {
        return findByName(value);
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

    public static ChecksumAlgorithm findByName(String algorithmName) {
        Objects.requireNonNull(algorithmName, "Checksum algorithm name must not be null");

        if (algorithmName.isBlank())
            return null;

        for (ChecksumAlgorithm algorithm : values())
            if (algorithmName.equalsIgnoreCase(algorithm.name()))
                return algorithm;

        return null;
    }

    public static ChecksumAlgorithm getByName(String algorithmName) throws NoSuchAlgorithmException {
        ChecksumAlgorithm foundAlgorithm = findByName(algorithmName);

        if (foundAlgorithm == null)
            throw new NoSuchAlgorithmException("No such checksum algorithm: " + algorithmName);

        return foundAlgorithm;
    }
}
