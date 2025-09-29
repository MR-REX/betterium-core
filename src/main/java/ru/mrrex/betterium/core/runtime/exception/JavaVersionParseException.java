package ru.mrrex.betterium.core.runtime.exception;

import java.io.IOException;

/**
 * Signals that the output of the external Java process was successfully retrieved
 * (exit code 0), but the content could not be parsed to extract the required
 * version information (semantic error).
 * Extends {@link IOException} as it represents a failure during I/O-related
 * parsing stage.
 */
public class JavaVersionParseException extends IOException {

    private final String processOutput;

    /**
     * Constructs the exception with a specific message and the problematic
     * process output.
     *
     * @param message A description of the parsing failure.
     * @param processOutput The full, unparsable output (stdout and stderr) captured from the process.
     */
    public JavaVersionParseException(String message, String processOutput) {
        super(message);
        this.processOutput = processOutput;
    }

    /**
     * Constructs the exception using the default parsing failure message.
     *
     * @param processOutput The full, unparsable output (stdout and stderr) captured from the process.
     */
    public JavaVersionParseException(String processOutput) {
        this("Failed to parse java runtime version from process output: " + processOutput, processOutput);
    }

    /**
     * Gets the full output captured from the external process
     * (useful for debugging the parsing failure).
     *
     * @return The process output (stdout/stderr combined).
     */
    public String getProcessOutput() {
        return processOutput;
    }
}
