package ru.mrrex.betterium.core.runtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.mrrex.betterium.core.jackson.RelativePathDeserializer;
import ru.mrrex.betterium.core.jackson.RelativePathSerializer;

import java.nio.file.Path;
import java.util.*;

/**
 * Represents the immutable configuration required to launch a specific
 * Java application. This record ensures type safety and consistency for
 * running external Java processes.
 *
 * @param mainClass The fully qualified name of the main class to execute (e.g., "ru.app.Main").
 * @param classpathEntries A list of paths (directories of JAR files) to include in the Java classpath.
 * @param applicationArguments A list of arguments to pass directly to the main method of the application.
 * @param jvmArguments A list of arguments (e.g., memory, GC settings) to pass to the Java Virtual Machine.
 */
public record JavaApplicationConfiguration(
        @JsonProperty("main_class")
        String mainClass,

        @JsonProperty("classpath_entries")
        @JsonSerialize(contentUsing = RelativePathSerializer.class)
        @JsonDeserialize(contentUsing = RelativePathDeserializer.class)
        List<Path> classpathEntries,

        @JsonProperty("application_arguments")
        List<String> applicationArguments,

        @JsonProperty("jvm_arguments")
        List<String> jvmArguments
) {

    /**
     * Compact constructor used for the validation and defensive copying.
     * Ensures all non-null fields are set and makes the list immutable.
     *
     * @throws NullPointerException If {@link #mainClass} or {@link #classpathEntries} is null.
     */
    public JavaApplicationConfiguration {
        Objects.requireNonNull(mainClass, "Main class name must not be null");
        Objects.requireNonNull(classpathEntries, "Classpath entries list must not be null");

        classpathEntries = List.copyOf(classpathEntries);

        applicationArguments = (applicationArguments != null)
                ? List.copyOf(applicationArguments)
                : Collections.emptyList();

        jvmArguments = (jvmArguments != null)
                ? List.copyOf(jvmArguments)
                : Collections.emptyList();
    }

    /**
     * Static factory method to obtain a new {@link Builder} instance.
     *
     * @return A new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Nested static Builder class.
     * Allows flexible assembly of the {@link JavaApplicationConfiguration} object
     * using the Fluent API design pattern.
     */
    public static class Builder {

        private String mainClass;
        private List<Path> classpathEntries;
        private List<String> applicationArguments;
        private List<String> jvmArguments;

        /**
         * Private constructor to enforce use of {@link JavaApplicationConfiguration.Builder}.
         */
        private Builder() {}

        /**
         * Sets the fully qualified name of the main application class.
         *
         * @param mainClass The main application class name.
         * @return The {@link Builder} instance for method chaining.
         */
        public Builder withMainClass(String mainClass) {
            this.mainClass = mainClass;
            return this;
        }

        /**
         * Sets the list of paths to be included in the classpath.
         *
         * @param classpathEntries The list of {@link Path} objects for the classpath.
         * @return The {@link Builder} instance for method chaining.
         */
        public Builder withClasspathEntries(List<Path> classpathEntries) {
            this.classpathEntries = classpathEntries;
            return this;
        }

        /**
         * Sets the arguments to be passed to the main method of the application.
         *
         * @param applicationArguments The list of application arguments.
         * @return The {@link Builder} instance for method chaining.
         */
        public Builder withApplicationArguments(List<String> applicationArguments) {
            this.applicationArguments = applicationArguments;
            return this;
        }

        /**
         * Sets the arguments (e.g, memory, GC settings) to be passed to the Java Virtual Machine.
         *
         * @param jvmArguments The list of JVM arguments (e.g., "-Xmx512m").
         * @return The {@link Builder} instance for method chaining.
         */
        public Builder withJvmArguments(List<String> jvmArguments) {
            this.jvmArguments = jvmArguments;
            return this;
        }

        /**
         * Builds and returns the final immutable {@link JavaApplicationConfiguration} instance.
         *
         * @return The constructed {@link JavaApplicationConfiguration} object.
         * @throws IllegalStateException If any required field ({@link #mainClass} or {@link #classpathEntries} is missing.
         */
        public JavaApplicationConfiguration build() {
            if (mainClass == null)
                throw new IllegalStateException("Main class (mainClass) must be set before building configuration");

            if (classpathEntries == null)
                throw new IllegalStateException("Classpath entries (classpathEntries) must be set before building configuration");

            return new JavaApplicationConfiguration(
                    mainClass,
                    classpathEntries,
                    applicationArguments,
                    jvmArguments
            );
        }
    }
}
