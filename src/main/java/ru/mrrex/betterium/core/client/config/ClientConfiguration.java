package ru.mrrex.betterium.core.client.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.mrrex.betterium.core.artifact.Artifact;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record ClientConfiguration(
        @JsonProperty("name")
        String name,

        @JsonProperty("version")
        String version,

        @JsonProperty("author")
        String author,

        @JsonProperty("artifacts")
        Set<Artifact> artifacts
) {

    private static final String DEFAULT_NAME = "Unnamed Client Configuration";
    private static final String DEFAULT_VERSION = "0.0.0";
    private static final String DEFAULT_AUTHOR = "N/A";

    @JsonCreator
    public ClientConfiguration {
        name = (name != null) ? name : DEFAULT_NAME;
        version = (version != null) ? version : DEFAULT_VERSION;
        author = (author != null) ? author : DEFAULT_AUTHOR;

        if (artifacts == null)
            throw new IllegalArgumentException("Client configuration must define artifacts");

        if (artifacts.isEmpty())
            throw new IllegalArgumentException("Client configuration must contain at least one artifact");

        artifacts = Set.copyOf(artifacts);
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private String version;
        private String author;

        private final Set<Artifact> artifacts = new HashSet<>();

        private Builder() {}

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder withArtifacts(Set<Artifact> artifacts) {
            Objects.requireNonNull(artifacts, "Artifacts set must not be null");
            this.artifacts.addAll(artifacts);

            return this;
        }

        public Builder addArtifact(Artifact artifact) {
            Objects.requireNonNull(artifact, "Artifact must not be null");
            this.artifacts.add(artifact);

            return this;
        }

        public ClientConfiguration build() {
            if (artifacts.isEmpty())
                throw new IllegalStateException("Client configuration must contain at least one artifact");

            return new ClientConfiguration(
                    name,
                    version,
                    author,
                    artifacts
            );
        }
    }
}
