package ru.mrrex.betterium.core.hash;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public class Hash {

    private final HashAlgorithm algorithm;
    private final byte[] bytes;

    public Hash(HashAlgorithm algorithm, byte[] bytes) {
        this.algorithm = Objects.requireNonNull(algorithm, "Hash algorithm must not be null");

        Objects.requireNonNull(bytes, "Hash bytes must not be null");

        if (bytes.length < 1)
            throw new IllegalArgumentException("Hash bytes must not be empty");

        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public HashAlgorithm getAlgorithm() {
        return algorithm;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public String toHexString() {
        return HexFormat.of().formatHex(bytes);
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Hash hash))
            return false;

        return algorithm == hash.algorithm && Arrays.equals(bytes, hash.bytes);
    }

    @Override
    public int hashCode() {
        return 31 * algorithm.hashCode() + Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return "Hash [algorithm='%s', hex='%s']".formatted(algorithm, toHexString());
    }
}
