package ru.mrrex.betterium.core.checksum;

import java.util.Objects;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

public abstract class ChecksumCalculatorFactory {

    private ChecksumCalculatorFactory() {}

    public static ChecksumCalculator createCalculator(ChecksumAlgorithm algorithm) {
        Objects.requireNonNull(algorithm, "Checksum algorithm must not be null");

        Checksum checksum = switch (algorithm) {
            case CRC32 -> new CRC32();
            case CRC32C -> new CRC32C();
            case ADLER32 -> new Adler32();
        };

        return new ChecksumCalculator(checksum);
    }
}
