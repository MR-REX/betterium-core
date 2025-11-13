package ru.mrrex.betterium.core.checksum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
        for (ChecksumAlgorithm algorithm : ChecksumAlgorithm.values())
            if (algorithmName.equalsIgnoreCase(algorithm.name()))
                return algorithm;

        return null;
    }

    public static ChecksumAlgorithm getByName(String algorithmName) {
        ChecksumAlgorithm foundAlgorithm = findByName(algorithmName);

        if (foundAlgorithm == null)
            throw new IllegalArgumentException("No such checksum algorithm: " + algorithmName);

        return foundAlgorithm;
    }
}
