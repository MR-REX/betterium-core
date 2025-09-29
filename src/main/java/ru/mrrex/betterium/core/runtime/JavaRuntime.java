package ru.mrrex.betterium.core.runtime;

import ru.mrrex.betterium.core.runtime.exception.JavaProcessExecutionException;
import ru.mrrex.betterium.core.runtime.exception.JavaProcessTimeoutException;
import ru.mrrex.betterium.core.runtime.exception.JavaVersionParseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an immutable configuration for a specific Java Runtime Environment (JRE/JDK).
 * This record acts as a factory for creating {@link ProcessBuilder} instances tied to this
 * executable and provides methods for self-diagnostics (e.g., checking the version).
 *
 * @param javaExecutablePath The absolute or relative path to the 'java' executable file.
 */
public record JavaRuntime(Path javaExecutablePath) {

    private static final String VERSION_ARGUMENT = "-version";
    private static final Pattern JAVA_VERSION_PATTERN = Pattern.compile("version\\s+\"([^\"]+)\"");
    private static final Duration PROCESS_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Compact constructor used for validating the {@code javaExecutablePath}.
     * Ensures the specified path is not null, exists, is a regular file, and is executable.
     *
     * @param javaExecutablePath The absolute or relative path to the 'java' executable file.
     * @throws NullPointerException If {@code javaExecutablePath} is null.
     * @throws IllegalArgumentException If the path does not exist, is not a regular file or is not executable.
     */
    public JavaRuntime {
        Objects.requireNonNull(javaExecutablePath);

        if (!Files.exists(javaExecutablePath))
            throw new IllegalArgumentException("Java executable not found: " + javaExecutablePath.toAbsolutePath());

        if (!Files.isRegularFile(javaExecutablePath))
            throw new IllegalArgumentException("Specified path is not a file: " + javaExecutablePath.toAbsolutePath());

        if (!Files.isExecutable(javaExecutablePath))
            throw new IllegalArgumentException("Java executable is not accessible: " + javaExecutablePath.toAbsolutePath());
    }

    /**
     * Creates a {@link ProcessBuilder} initialized with the Java executable path
     * followed by the provided list of arguments.
     *
     * @param arguments A list of command-line arguments to be passed to the Java process
     *                  (e.g., "-classpath", "MainClass", "program arguments").
     * @return A {@link ProcessBuilder} ready for configuration or execution.
     */
    public ProcessBuilder createProcessBuilder(List<String> arguments) {
        List<String> command = new ArrayList<>();
        command.add(javaExecutablePath.toString());
        command.addAll(arguments);

        return new ProcessBuilder(command);
    }

    /**
     * Creates a {@link ProcessBuilder} initialized with the Java executable path
     * followed by a variable number of arguments.
     * This is a convenience method wrapping {@link #createProcessBuilder(List)}
     *
     * @param arguments A variable number of command-line arguments (varargs).
     * @return A {@link ProcessBuilder} ready for configuration or execution.
     */
    public ProcessBuilder createProcessBuilder(String... arguments) {
        return createProcessBuilder(Arrays.asList(arguments));
    }

    /**
     * Executes the 'java -version' command on the associated executable
     * and parses the output to determine the Java version number.
     *
     * @return The Java version string (e.g., "25" or "1.8.0_442").
     * @throws JavaProcessExecutionException If the process execution fails due to I/O error or non-zero exit code.
     * @throws JavaProcessTimeoutException If the process execution exceeds the predefined time limit.
     * @throws JavaVersionParseException If the version string could not be successfully parsed from the process output (semantic error).
     * @throws IOException If an I/O error occurs while trying to start the process or read its output streams.
     * @throws InterruptedException If the current thread is interrupted while waiting for the process to terminate.
     */
    public String getVersion() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = createProcessBuilder(VERSION_ARGUMENT);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        boolean isProcessFinished = process.waitFor(PROCESS_TIMEOUT);

        if (!isProcessFinished) {
            process.destroyForcibly();
            throw new JavaProcessTimeoutException(PROCESS_TIMEOUT);
        }

        String processOutput;

        try (InputStream inputStream = process.getInputStream()) {
            processOutput = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();
        }

        int exitCode = process.exitValue();

        if (exitCode != 0)
            throw new JavaProcessExecutionException(exitCode, processOutput);

        Matcher versionMatcher = JAVA_VERSION_PATTERN.matcher(processOutput);

        if (!versionMatcher.find())
            throw new JavaVersionParseException(processOutput);

        return versionMatcher.group(1);
    }
}
