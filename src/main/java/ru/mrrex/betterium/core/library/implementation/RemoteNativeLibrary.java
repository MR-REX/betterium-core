package ru.mrrex.betterium.core.library.implementation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.mrrex.betterium.core.checksum.ChecksumAlgorithm;
import ru.mrrex.betterium.core.hash.HashAlgorithm;
import ru.mrrex.betterium.core.jackson.HexLongDeserializer;
import ru.mrrex.betterium.core.jackson.HexLongSerializer;
import ru.mrrex.betterium.core.library.NativeLibrary;
import ru.mrrex.betterium.core.resource.CheckableResource;
import ru.mrrex.betterium.core.resource.DownloadableResource;
import ru.mrrex.betterium.core.resource.ConditionalResource;

import java.net.URI;
import java.util.*;

public record RemoteNativeLibrary(
        @JsonProperty("source_uri")
        URI sourceUri,

        @JsonProperty("checksums")
        @JsonSerialize(contentUsing = HexLongSerializer.class)
        @JsonDeserialize(contentUsing = HexLongDeserializer.class)
        Map<ChecksumAlgorithm, Long> checksums,

        @JsonProperty("hashes")
        Map<HashAlgorithm, String> hashes,

        @JsonProperty("conditions")
        Map<String, String> conditions
) implements NativeLibrary, DownloadableResource, ConditionalResource, CheckableResource {

    @JsonCreator
    public RemoteNativeLibrary {
        Objects.requireNonNull(sourceUri, "Source URI (sourceUri) must not be null");

        checksums = (checksums != null)
                ? Map.copyOf(checksums)
                : Collections.emptyMap();

        hashes = (hashes != null)
                ? Map.copyOf(hashes)
                : Collections.emptyMap();

        conditions = (conditions != null)
                ? Map.copyOf(conditions)
                : Collections.emptyMap();
    }

    @Override
    public URI getSourceUri() {
        return sourceUri;
    }

    @Override
    public Map<ChecksumAlgorithm, Long> getChecksums() {
        return Map.copyOf(checksums);
    }

    @Override
    public Map<HashAlgorithm, String> getHashes() {
        return Map.copyOf(hashes);
    }

    @Override
    public Map<String, String> getConditions() {
        return conditions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private URI sourceUri;

        private final Map<ChecksumAlgorithm, Long> checksums = new EnumMap<>(ChecksumAlgorithm.class);
        private final Map<HashAlgorithm, String> hashes = new EnumMap<>(HashAlgorithm.class);

        private final Map<String, String> conditions = new HashMap<>();

        private Builder() {}

        public Builder withSourceUri(URI sourceUri) {
            this.sourceUri = Objects.requireNonNull(sourceUri, "Source URI (sourceUri) must not be null");
            return this;
        }

        public Builder withChecksums(Map<ChecksumAlgorithm, Long> checksums) {
            Objects.requireNonNull(checksums, "Checksums map must not be null");
            this.checksums.putAll(checksums);

            return this;
        }

        public Builder withChecksum(ChecksumAlgorithm algorithm, long checksumValue) {
            Objects.requireNonNull(algorithm, "Checksum algorithm must not be null");
            this.checksums.put(algorithm, checksumValue);

            return this;
        }

        public Builder withHashes(Map<HashAlgorithm, String> hashes) {
            Objects.requireNonNull(hashes, "Hash map must not be null");
            this.hashes.putAll(hashes);

            return this;
        }

        public Builder withHash(HashAlgorithm algorithm, String hashValue) {
            Objects.requireNonNull(algorithm, "Hash algorithm must not be null");
            this.hashes.put(algorithm, hashValue);

            return this;
        }

        public Builder withConditions(Map<String, String> conditions) {
            Objects.requireNonNull(conditions, "Conditions map must not be null");
            this.conditions.putAll(conditions);

            return this;
        }

        public Builder addCondition(String conditionId, String value) {
            Objects.requireNonNull(conditionId, "Condition ID must not be null");
            Objects.requireNonNull(value, "Condition value must not be null");

            this.conditions.put(conditionId, value);

            return this;
        }

        public RemoteNativeLibrary build() {
            if (sourceUri == null)
                throw new IllegalStateException("Source URI (sourceUri) must be set before building configuration");

            return new RemoteNativeLibrary(
                    sourceUri,
                    checksums,
                    hashes,
                    conditions
            );
        }
    }
}
