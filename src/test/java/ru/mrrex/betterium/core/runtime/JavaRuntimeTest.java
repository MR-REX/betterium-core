package ru.mrrex.betterium.core.runtime;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mrrex.betterium.core.runtime.exception.JavaProcessExecutionException;
import ru.mrrex.betterium.core.runtime.exception.JavaProcessTimeoutException;
import ru.mrrex.betterium.core.runtime.exception.JavaVersionParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the JavaRuntime record, covering constructor validation,
 * ProcessBuilder creation, and version retrieval logic.
 */
@DisplayName("Java Runtime Configuration Tests")
class JavaRuntimeTest {

    private static Path javaExecutablePath;

    @TempDir
    static Path tempDir;

    private static Path regularFilePath;
    private static Path directoryPath;

    private static Path mockExecutableReturnsError;
    private static Path mockExecutablePrintsGarbage;
    private static Path mockExecutableRunsTooLong;

    private static final String ERROR_MESSAGE = "This is error output";
    private static final String GARBAGE_MESSAGE = "Garbage output that cannot be parsed";

    /**
     * Initializes static resources (paths and mock files) before all tests.
     */
    @BeforeAll
    static void setUp() throws IOException {
        final String operatingSystemName = System.getProperty("os.name").toLowerCase();
        final boolean isWindows = operatingSystemName.contains("win");

        javaExecutablePath = Path.of(System.getProperty("java.home"), "bin", isWindows ? "java.exe" : "java");

        regularFilePath = tempDir.resolve("regular.txt");
        Files.createFile(regularFilePath);

        if (!isWindows) {
            Files.setPosixFilePermissions(regularFilePath, Set.of(
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE
            ));
        }

        directoryPath = tempDir.resolve("not_a_file");
        Files.createDirectory(directoryPath);

        mockExecutableReturnsError = tempDir.resolve("mock_error." + (isWindows ? "bat" : "sh"));

        if (isWindows) {
            Files.writeString(mockExecutableReturnsError, "@echo off\necho " + ERROR_MESSAGE + "\nexit 1");
        } else {
            Files.writeString(mockExecutableReturnsError, "#!/bin/sh\necho \"" + ERROR_MESSAGE + "\"\nexit 1");
            Files.setPosixFilePermissions(mockExecutableReturnsError, Set.of(
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.OWNER_READ
            ));
        }

        mockExecutablePrintsGarbage = tempDir.resolve("mock_garbage." + (isWindows ? "bat" : "sh"));

        if (isWindows) {
            Files.writeString(mockExecutablePrintsGarbage, "@echo off\necho " + GARBAGE_MESSAGE + "\nexit 0");
        } else {
            Files.writeString(mockExecutablePrintsGarbage, "#!/bin/sh\necho \"" + GARBAGE_MESSAGE + "\"\nexit 0");
            Files.setPosixFilePermissions(mockExecutablePrintsGarbage, Set.of(
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.OWNER_READ
            ));
        }

        mockExecutableRunsTooLong = tempDir.resolve("mock_too_long." + (isWindows ? "bat" : "sh"));

        if (isWindows) {
            Files.writeString(mockExecutableRunsTooLong, "@echo off\necho Long task\nping 127.0.0.1 -n 61 > NUL\nexit 0");
        } else {
            Files.writeString(mockExecutableRunsTooLong, "#!/bin/sh\necho \"Long task\"\nsleep 60\nexit 0");
            Files.setPosixFilePermissions(mockExecutableRunsTooLong, Set.of(
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.OWNER_READ
            ));
        }
    }

    // --- Constructor Validation Tests --- \\

    @Nested
    @DisplayName("A. Constructor Validation")
    class ConstructorValidationTests {

        /**
         * Tests that the constructor succeeds when given a valid, executable path.
         */
        @Test
        @DisplayName("Path is valid and executable")
        void constructorShouldSucceedWithValidExecutable() {
            assertDoesNotThrow(
                    () -> new JavaRuntime(regularFilePath),
                    "Constructor should succeed with a valid executable file"
            );
        }

        /**
         * Tests that the constructor throws NullPointerException when the path is null.
         */
        @Test
        @DisplayName("Path is null (throws NPE")
        void constructorShouldThrowNPEWhenPathIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> new JavaRuntime(null),
                    "Constructor must throw NullPointerException for a null path"
            );
        }

        static Stream<Arguments> argumentsForConstructorValidation() {
            return Stream.of(
                    Arguments.of(Path.of("non/existent/java/path"), "Java executable not found"),
                    Arguments.of(directoryPath, "Specified path is not a file")
            );
        }

        /**
         * Tests all IllegalArgumentException conditions using a parameterized test.
         */
        @ParameterizedTest(name = "Path: {0} ({1})")
        @MethodSource("argumentsForConstructorValidation")
        @DisplayName("Invalid paths (throws IllegalArgumentException")
        void shouldThrowIllegalArgumentExceptions(Path path, String expectedErrorSubstring) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new JavaRuntime(path),
                    "Constructor must throw IllegalArgumentException for invalid paths"
            );

            assertTrue(
                    exception.getMessage().contains(expectedErrorSubstring),
                    "Exception message must contain the specific reason"
            );
        }
    }

    // --- ProcessBuilder Factory Tests --- \\

    @Nested
    @DisplayName("B. ProcessBuilder Factory Methods")
    class ProcessBuilderFactoryTests {

        /**
         * Tests the list-based {@code createProcessBuilder(List<String> arguments)} method.
         */
        @Test
        @DisplayName("List<String> command assembly")
        void testCreateProcessBuilderWithLists() {
            final String firstArgument = "-Xmx512m";
            final String secondArgument = "ru.mr.rex.Main";

            JavaRuntime runtime = new JavaRuntime(javaExecutablePath);
            List<String> arguments = Arrays.asList(firstArgument, secondArgument);

            ProcessBuilder processBuilder = runtime.createProcessBuilder(arguments);
            List<String> storedCommands = processBuilder.command();

            assertEquals(3, storedCommands.size(), "Command size must be executable path + 2 arguments");
            assertEquals(javaExecutablePath.toString(), storedCommands.getFirst(), "First element must be the Java executable path");
            assertEquals(firstArgument, storedCommands.get(1));
            assertEquals(secondArgument, storedCommands.get(2));
        }

        /**
         * Tests the varargs-based {@code createProcessBuilder(String... arguments)} method.
         */
        @Test
        @DisplayName("Varargs command assembly")
        void testCreateProcessBuilderWithVarargs() {
            final String firstArgument = "-Xmx512m";
            final String secondArgument = "ru.mr.rex.Main";

            JavaRuntime runtime = new JavaRuntime(javaExecutablePath);

            ProcessBuilder processBuilder = runtime.createProcessBuilder(firstArgument, secondArgument);
            List<String> storedCommands = processBuilder.command();

            assertEquals(3, storedCommands.size(), "Command size must be executable path + 2 arguments");
            assertEquals(javaExecutablePath.toString(), storedCommands.getFirst(), "First element must be the Java executable path");
            assertEquals(firstArgument, storedCommands.get(1));
            assertEquals(secondArgument, storedCommands.get(2));
        }
    }

    // --- getVersion() Integration Tests --- \\

    @Nested
    @DisplayName("C. getVersion() Integration")
    class VersionRetrievalIntegrationTests {

        /**
         * Tests the successful retrieval and parsing of the version from the system Java executable.
         */
        @Test
        @DisplayName("Success: Retrieve and parse system Java version")
        void testGetVersionSuccess() throws IOException, InterruptedException {
            Assumptions.assumeTrue(
                    Files.exists(javaExecutablePath) && Files.isExecutable(javaExecutablePath),
                    "Skipping version success test because system Java executable is not available"
            );

            JavaRuntime runtime = new JavaRuntime(javaExecutablePath);
            String version = runtime.getVersion();

            assertNotNull(version, "Version string should not be null");

            assertTrue(
                    version.matches("^(\\d+\\.?\\d*)+(_\\d+)?$"),
                    "The parsed version must match standard Java version format (e.g., 25 or 1.8.0_442). Found: " + version
            );
        }

        /**
         * Tests that getVersion() throws JavaProcessExecutionException when the process
         * runs longer than the defined internal timeout (10 seconds).
         */
        @Test
        @DisplayName("Failure: Process times out (throws JavaProcessTimeoutException)")
        void shouldThrowTimeoutExceptionOnTimeout() {
            Assumptions.assumeTrue(
                    Files.isExecutable(mockExecutableRunsTooLong),
                    "Skipping mock timeout test because the mock script is not executable"
            );

            JavaRuntime runtime = new JavaRuntime(mockExecutableRunsTooLong);

            JavaProcessTimeoutException exception = assertThrows(
                    JavaProcessTimeoutException.class,
                    runtime::getVersion,
                    "getVersion() must throw JavaProcessTimeoutException when process times out"
            );

            assertTrue(
                    exception.getMessage().toLowerCase().contains("timed out"),
                    "Exception message must indicate a timeout"
            );
        }

        /**
         * Tests that {@code getVersion()} throws JavaProcessExecutionException
         * when the process exits with a non-zero code.
         */
        @Test
        @DisplayName("Failure: Non-zero exit code (throws JavaProcessExecutionException)")
        void shouldThrowExecutionExceptionOnNonZeroExitCode() {
            Assumptions.assumeTrue(
                    Files.isExecutable(mockExecutableReturnsError),
                    "Skipping mock executable test because the mock script is not executable"
            );

            JavaRuntime runtime = new JavaRuntime(mockExecutableReturnsError);

            JavaProcessExecutionException exception = assertThrows(
                    JavaProcessExecutionException.class,
                    runtime::getVersion,
                    "getVersion() must throw JavaProcessExecutionException on non-zero exit code"
            );

            assertAll(
                    "Execution exception details",
                    () -> assertEquals(1, exception.getExitCode(), "Exit code in exception must be 1"),
                    () -> assertEquals(ERROR_MESSAGE, exception.getProcessOutput(), "Process output must be captured"),
                    () -> assertTrue(exception.getMessage().contains("exit code"), "Message must contain the exit code")
            );
        }

        /**
         * Tests that {@code getVersion()} throws JavaVersionParseException
         * when the output cannot be parsed.
         */
        @Test
        @DisplayName("Failure: Unparsable output (throws JavaVersionParseException)")
        void shouldThrowParsingExceptionOnInvalidOutput() {
            Assumptions.assumeTrue(
                    Files.isExecutable(mockExecutablePrintsGarbage),
                    "Skipping mock executable test because the mock script is not executable"
            );

            JavaRuntime runtime = new JavaRuntime(mockExecutablePrintsGarbage);

            JavaVersionParseException exception = assertThrows(
                    JavaVersionParseException.class,
                    runtime::getVersion,
                    "getVersion() must throw JavaVersionParseException an unparsable output"
            );

            assertAll(
                    "Parsing exception details",
                    () -> assertEquals(
                            GARBAGE_MESSAGE, exception.getProcessOutput(),
                            "Process output must be captured"
                    ),
                    () -> assertTrue(
                            exception.getMessage().toLowerCase().contains("failed to parse"),
                            "Message must indicate parsing failure"
                    )
            );
        }
    }
}