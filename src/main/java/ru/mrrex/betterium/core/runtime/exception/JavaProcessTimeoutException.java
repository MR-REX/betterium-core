package ru.mrrex.betterium.core.runtime.exception;

import java.io.IOException;
import java.time.Duration;

/**
 * Signals that the external Java process execution exceeded the predefined time limit.
 * Extends {@link IOException} as it represents a failure during I/O operations (process execution).
 */
public class JavaProcessTimeoutException extends IOException {

    private final Duration duration;

    /**
     * Constructs the exception indicating a timeout failure.
     *
     * @param message A description of the execution failure.
     * @param duration The duration after witch the process was terminated.
     */
    public JavaProcessTimeoutException(String message, Duration duration) {
        super(message);
        this.duration = duration;
    }

    /**
     * Constructs the exception indicating a timeout failure.
     *
     * @param duration The duration after witch the process was terminated.
     */
    public JavaProcessTimeoutException(Duration duration) {
        this("Java process timed out after " + duration.toSeconds() + " seconds", duration);
    }

    /**
     * Gets the configured timeout duration for which the process was waiting.
     *
     * @return The timeout duration.
     */
    public Duration getDuration() {
        return duration;
    }
}
