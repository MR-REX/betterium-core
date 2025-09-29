package ru.mrrex.betterium.core.runtime.exception;

import java.io.IOException;

/**
 * Signals that the external Java process execution failed for reasons related to
 * the operating system (non-zero exit code).
 * Extends {@link IOException} as it represents a failure during I/O operations
 * (process execution).
 */
public class JavaProcessExecutionException extends IOException {

    private final int exitCode;
    private final String processOutput;

    /**
     * Constructs the exception with a specific message, exit code, and captured
     * process output.
     *
     * @param message A description of the execution failure.
     * @param exitCode The non-zero exit code returned by the process.
     * @param processOutput The full process output (stdout and stderr) captured from the external process.
     */
    public JavaProcessExecutionException(String message, int exitCode, String processOutput) {
        super(message);

        this.exitCode = exitCode;
        this.processOutput = processOutput;
    }

    /**
     * Constructs the exception using the default message for a non-zero exit-code.
     *
     * @param exitCode The non-zero exit code returned by the process.
     * @param processOutput The full process output (stdout and stderr) captured from the external process.
     */
    public JavaProcessExecutionException(int exitCode, String processOutput) {
        this("Java process finished with exit code: " + exitCode + ", process output: " + processOutput, exitCode, processOutput);
    }

    /**
     * Gets the exit code of the failed process.
     *
     * @return The process exit code (non-zero for failure).
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Gets the full output captured from the external process (useful for debugging).
     *
     * @return The process output (stdout/stderr combined).
     */
    public String getProcessOutput() {
        return processOutput;
    }
}
