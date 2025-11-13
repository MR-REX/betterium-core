package ru.mrrex.betterium.core.hash;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public enum HashAlgorithm {

    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    SHA512("SHA-512");

    private final String messageDigestInstanceName;

    HashAlgorithm(String messageDigestInstanceName) {
        this.messageDigestInstanceName = messageDigestInstanceName;
    }

    public String getMessageDigestInstanceName() {
        return messageDigestInstanceName;
    }

    @JsonCreator
    public static HashAlgorithm fromValue(String value) {
        return findByName(value);
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

    public static HashAlgorithm findByName(String algorithmName) {
        Objects.requireNonNull(algorithmName, "Hash algorithm name must not be null");

        if (algorithmName.isBlank())
            return null;

        for (HashAlgorithm algorithm : values())
            if (algorithm.name().equalsIgnoreCase(algorithmName))
                return algorithm;

        return null;
    }

    public static HashAlgorithm getByName(String algorithmName) throws NoSuchAlgorithmException {
        HashAlgorithm foundAlgorithm = findByName(algorithmName);

        if (foundAlgorithm == null)
            throw new NoSuchAlgorithmException("No such hash algorithm: " + algorithmName);

        return foundAlgorithm;
    }
}
