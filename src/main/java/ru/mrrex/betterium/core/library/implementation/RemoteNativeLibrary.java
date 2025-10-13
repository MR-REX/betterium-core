package ru.mrrex.betterium.core.library.implementation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.mrrex.betterium.core.library.NativeLibrary;
import ru.mrrex.betterium.core.resource.DownloadableResource;
import ru.mrrex.betterium.core.resource.ConditionalResource;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record RemoteNativeLibrary(
        @JsonProperty("source_uri")
        URI sourceUri,

        @JsonProperty("conditions")
        Map<String, String> conditions
) implements NativeLibrary, DownloadableResource, ConditionalResource {

    @JsonCreator
    public RemoteNativeLibrary {
        Objects.requireNonNull(sourceUri, "Source URI (sourceUri) must not be null");

        conditions = (conditions != null)
                ? Map.copyOf(conditions)
                : Collections.emptyMap();
    }

    @Override
    public URI getSourceUri() {
        return sourceUri;
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

        private final Map<String, String> conditions = new HashMap<>();

        private Builder() {}

        public Builder withSourceUri(URI sourceUri) {
            this.sourceUri = Objects.requireNonNull(sourceUri, "Source URI (sourceUri) must not be null");
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
                    conditions
            );
        }
    }
}
