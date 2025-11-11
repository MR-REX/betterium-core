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

        @JsonProperty("main_class")
        String mainClass,

        @JsonProperty("artifacts")
        Set<Artifact> artifacts,

        @JsonProperty("jvm_arguments")
        Set<String> jvmArguments,

        @JsonProperty("arguments")
        Set<String> arguments
) {

    private static final String DEFAULT_NAME = "Unnamed Client Configuration";
    private static final String DEFAULT_VERSION = "0.0.0";
    private static final String DEFAULT_AUTHOR = "N/A";

    @JsonCreator
    public ClientConfiguration {
        Objects.requireNonNull(mainClass, "Main class (mainClass) must not be null");

        name = (name != null && !name.isBlank()) ? name : DEFAULT_NAME;
        version = (version != null && !version.isBlank()) ? version : DEFAULT_VERSION;
        author = (author != null && !author.isBlank()) ? author : DEFAULT_AUTHOR;

        if (mainClass.isBlank())
            throw new IllegalArgumentException("Client configuration must define runnable main class");

        if (artifacts == null)
            throw new IllegalArgumentException("Client configuration must define artifacts");

        if (artifacts.isEmpty())
            throw new IllegalArgumentException("Client configuration must contain at least one artifact");

        artifacts = Set.copyOf(artifacts);
        jvmArguments = Set.copyOf(jvmArguments);
        arguments = Set.copyOf(arguments);
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private String version;
        private String author;

        private String mainClass;

        private final Set<Artifact> artifacts = new HashSet<>();

        private final Set<String> jvmArguments = new HashSet<>();
        private final Set<String> arguments = new HashSet<>();

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

        public Builder withMainClass(String mainClass) {
            Objects.requireNonNull(mainClass, "Main class must not be null");

            if (mainClass.isBlank())
                throw new IllegalArgumentException("Main class must not be empty string");

            this.mainClass = mainClass;
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

        public Builder withJvmArguments(Set<String> jvmArguments) {
            Objects.requireNonNull(jvmArguments, "JVM Arguments must not be null");
            this.jvmArguments.addAll(jvmArguments);

            return this;
        }

        public Builder addJvmArgument(String jvmArgument) {
            Objects.requireNonNull(jvmArgument, "JVM Argument must not be null");
            this.jvmArguments.add(jvmArgument);

            return this;
        }

        public Builder withArguments(Set<String> arguments) {
            Objects.requireNonNull(arguments, "Arguments must not be null");
            this.arguments.addAll(arguments);

            return this;
        }

        public Builder addArgument(String argument) {
            Objects.requireNonNull(argument, "Argument must not be null");
            this.arguments.add(argument);

            return this;
        }

        public ClientConfiguration build() {
            if (mainClass == null || mainClass.isBlank())
                throw new IllegalStateException("Client configuration must contain main class");

            if (artifacts.isEmpty())
                throw new IllegalStateException("Client configuration must contain at least one artifact");

            return new ClientConfiguration(
                    name,
                    version,
                    author,
                    mainClass,
                    artifacts,
                    jvmArguments,
                    arguments
            );
        }
    }
}
