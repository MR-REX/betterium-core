package ru.mrrex.betterium.core.runtime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Executes a Java application process configured by a {@link JavaApplicationConfiguration}
 * using the provided {@link JavaRuntime}. This class is responsible for assembling
 * the command line arguments, including classpath entries and all arguments.
 */
public class JavaProcessExecutor {

    private static final String CLASSPATH_ARGUMENT = "-cp";
    private static final String PATH_SEPARATOR = File.pathSeparator;

    private final JavaRuntime javaRuntime;

    /**
     * Constructs a {@link JavaProcessExecutor} instance.
     *
     * @param javaRuntime The Java Runtime Environment configuration used to start the process.
     * @throws NullPointerException If {@code javaRuntime} is null.
     */
    public JavaProcessExecutor(JavaRuntime javaRuntime) {
        this.javaRuntime = Objects.requireNonNull(javaRuntime, "Java runtime cannot be null");
    }

    /**
     * Assembles the full command line based on the provided configuration and starts
     * the Java process.
     * The command is constructed in the standard order:
     * {@code [JAVA_EXECUTABLE] [JVM_ARGS] -cp [CLASSPATH] [MAIN_CLASS] [APP_ARGS]}
     *
     * @param configuration The immutable configuration detailing the application to run.
     * @return The started {@link Process} instance.
     * @throws NullPointerException If {@code configuration} is null.
     * @throws IOException If an I/O error occurs during process execution.
     * @throws IllegalStateException If the classpath entries list is empty, as an application cannot run without a classpath.
     */
    public Process execute(JavaApplicationConfiguration configuration) throws IOException {
        Objects.requireNonNull(configuration, "Java application configuration must not be null");

        if (configuration.classpathEntries().isEmpty())
            throw new IllegalStateException("Classpath entries list cannot be empty");

        List<String> commandLineArguments = buildCommandLineArguments(configuration);
        ProcessBuilder processBuilder = javaRuntime.createProcessBuilder(commandLineArguments);

        return processBuilder.start();
    }

    /**
     * Builds the complete list of arguments for the Java executable in the correct order.
     *
     * @param configuration The Java application configuration.
     * @return A list of command line arguments.
     */
    private List<String> buildCommandLineArguments(JavaApplicationConfiguration configuration) {
        List<String> arguments = new ArrayList<>(configuration.jvmArguments());

        arguments.add(CLASSPATH_ARGUMENT);

        String classpathValue = configuration.classpathEntries().stream()
                .map(p -> p.toString() + (Files.isDirectory(p) ? "/*" : ""))
                .collect(Collectors.joining(PATH_SEPARATOR));

        arguments.add(classpathValue);

        arguments.add(configuration.mainClass());
        arguments.addAll(configuration.applicationArguments());

        return arguments;
    }
}
