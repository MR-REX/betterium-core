package ru.mrrex.betterium.core.artifact.implementation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.mrrex.betterium.core.artifact.Artifact;
import ru.mrrex.betterium.core.library.NativeLibrary;
import ru.mrrex.betterium.core.resource.DownloadableResource;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record MavenArtifact(
        @JsonProperty("group_id")
        String groupId,

        @JsonProperty("artifact_id")
        String artifactId,

        @JsonProperty("version")
        String version,

        @JsonProperty("dependencies")
        Set<NativeLibrary> dependencies
) implements Artifact, DownloadableResource {

    private static final URI MAVEN_CENTRAL_BASE_URI = URI.create("https://repo1.maven.org/maven2");

    @JsonCreator
    public MavenArtifact {
        Objects.requireNonNull(groupId, "Group ID (groupId) must not be null");
        Objects.requireNonNull(artifactId, "Artifact ID (artifactId) must not be null");
        Objects.requireNonNull(version, "Version must not be null");

        dependencies = (dependencies != null)
                ? Set.copyOf(dependencies)
                : Collections.emptySet();
    }

    @Override
    public Set<NativeLibrary> getDependencies() {
        return dependencies;
    }

    @Override
    public URI getSourceUri() {
        return MAVEN_CENTRAL_BASE_URI.resolve(getRelativePath());
    }

    private String getRelativePath() {
        return "%s/%s/%s/%s-%s.jar".formatted(
                groupId.replace('.', '/'),
                artifactId,
                version,
                artifactId,
                version
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String groupId;
        private String artifactId;
        private String version;

        private final Set<NativeLibrary> dependencies = new HashSet<>();

        private Builder() {}

        public Builder withGroupId(String groupId) {
            this.groupId = Objects.requireNonNull(groupId, "Group ID (groupId) must not be null");
            return this;
        }

        public Builder withArtifactId(String artifactId) {
            this.artifactId = Objects.requireNonNull(artifactId, "Artifact ID (artifactId) must not be null");
            return this;
        }

        public Builder withVersion(String version) {
            this.version = Objects.requireNonNull(version, "Version must not be null");
            return this;
        }

        public Builder withDependencies(Set<NativeLibrary> dependencies) {
            Objects.requireNonNull(dependencies, "Dependencies set must not be null");
            this.dependencies.addAll(dependencies);

            return this;
        }

        public Builder addDependency(NativeLibrary nativeLibrary) {
            Objects.requireNonNull(nativeLibrary, "Native library must not be null");
            dependencies.add(nativeLibrary);

            return this;
        }

        public MavenArtifact build() {
            if (groupId == null)
                throw new IllegalStateException("Group ID (groupId) must be set before building configuration");

            if (artifactId == null)
                throw new IllegalStateException("Artifact ID (artifactId) must be set before building configuration");

            if (version == null)
                throw new IllegalStateException("Version (version) must be set before building configuration");

            return new MavenArtifact(
                    groupId,
                    artifactId,
                    version,
                    dependencies
            );
        }
    }
}
