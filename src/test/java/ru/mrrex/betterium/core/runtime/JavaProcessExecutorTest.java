package ru.mrrex.betterium.core.runtime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

/**
 * Test suite for the {@link JavaProcessExecutor} class, verifying the correct
 * construction of command line arguments and input validation.
 * This tests uses {@link Mockito} to isolate the {@link JavaProcessExecutor} from the
 * actual {@link ProcessBuilder} execution, ensuring that only the argument
 * building logic is tested.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Java Process Executor Tests")
class JavaProcessExecutorTest {

    private static final String PATH_SEPARATOR = File.pathSeparator;
    private static final Path MOCK_JAVA_PATH = Path.of("/usr/bin/java");

    @Mock
    private JavaRuntime javaRuntime;

    @InjectMocks
    private JavaProcessExecutor javaProcessExecutor;

    @Mock
    private ProcessBuilder processBuilder;

    @Mock
    private Process process;

    @BeforeEach
    void setUp() throws IOException {
        lenient()
                .when(javaRuntime.createProcessBuilder(anyList()))
                .thenReturn(processBuilder);

        lenient()
                .when(processBuilder.start())
                .thenReturn(process);

        lenient()
                .when(javaRuntime.javaExecutablePath())
                .thenReturn(MOCK_JAVA_PATH);
    }

    /**
     * Tests for constructor validation.
     */
    @Nested
    @DisplayName("A. Constructor Validation")
    class ConstructorTests {

        /**
         * Verifies that the constructor throws a {@link NullPointerException}
         * if a {@code null} {@link JavaRuntime} is provided.
         */
        @Test
        @DisplayName("Failure: Null JavaRuntime throws NullPointerException")
        void testNullJavaRuntime() {
            assertThrows(
                    NullPointerException.class,
                    () -> new JavaProcessExecutor(null),
                    "Constructor must reject null JavaRuntime instance"
            );
        }
    }

    /**
     * Tests for execution validation and error scenarios.
     */
    @Nested
    @DisplayName("B. Execution Input Validation")
    class ExecutionValidationTests {

        /**
         * Verifies that the execute method throws a {@link NullPointerException}
         * if a {@code null} {@link JavaApplicationConfiguration} is provided.
         */
        @Test
        @DisplayName("Failure: Null configuration throws NullPointerException")
        void testNullConfiguration() {
            assertThrows(
                    NullPointerException.class,
                    () -> javaProcessExecutor.execute(null),
                    "Execute method must reject null configuration"
            );
        }

        /**
         * Verifies that the execute method throws an {@link IllegalStateException}
         * if the classpath list is empty.
         */
        @Test
        @DisplayName("Failure: Empty classpath list throws IllegalStateException")
        void testEmptyClasspath() {
            JavaApplicationConfiguration configuration = JavaApplicationConfiguration.builder()
                    .withMainClass("ru.app.Main")
                    .withClasspathEntries(Collections.emptyList())
                    .build();

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> javaProcessExecutor.execute(configuration),
                    "Execute method must reject configuration with empty classpath"
            );

            assertTrue(exception.getMessage().toLowerCase().contains("classpath"));
        }
    }

    /**
     * Tests for correct command line arguments assembly (the core logic).
     */
    @Nested
    @DisplayName("C. Command Line Assembly (Order and Content)")
    class CommandAssemblyTests {

        /**
         * Tests the full command line arguments construction with all
         * possible arguments, ensuring the order is correct:
         * [JVM_ARGS] -cp [CLASSPATH] [MAIN_CLASS] [APP_ARGS]
         *
         * @throws IOException If the process start fails.
         */
        @Test
        @DisplayName("Success: Full configuration command line order")
        void testFullCommandLineOrder() throws IOException {
            List<Path> classpathEntries = List.of(
                    Path.of("libraries", "core.jar"),
                    Path.of("configuration.jar")
            );

            List<String> jvmArguments = List.of("-Xmx512m", "-Denv=test");
            List<String> applicationArguments = List.of("--config-file", "settings.xml");

            String mainClass = "ru.app.Example";

            JavaApplicationConfiguration configuration = JavaApplicationConfiguration.builder()
                    .withMainClass(mainClass)
                    .withClasspathEntries(classpathEntries)
                    .withApplicationArguments(applicationArguments)
                    .withJvmArguments(jvmArguments)
                    .build();

            javaProcessExecutor.execute(configuration);

            List<String> expectedCommandLineArguments = List.of(
                    "-Xmx512m",
                    "-Denv=test",
                    "-cp",
                    "libraries" + File.separator + "core.jar" + PATH_SEPARATOR + "configuration.jar",
                    "ru.app.Example",
                    "--config-file",
                    "settings.xml"
            );

            verify(javaRuntime).createProcessBuilder(expectedCommandLineArguments);
        }

        /**
         * Tests the command line arguments constructor when only required
         * arguments are provided, ensuring that optional list (JVM and
         * application arguments) are correctly omitted.
         *
         * @throws IOException If the process start fails.
         */
        @Test
        @DisplayName("Success: Minimal configuration command line arguments")
        void testMinimalCommandLine() throws IOException {
            List<Path> classpathEntries = List.of(Path.of("single.jar"));
            String mainClass = "ru.app.Example";

            JavaApplicationConfiguration configuration = JavaApplicationConfiguration.builder()
                    .withMainClass(mainClass)
                    .withClasspathEntries(classpathEntries)
                    .build();

            javaProcessExecutor.execute(configuration);

            List<String> expectedCommandLineArguments = List.of(
                    "-cp",
                    "single.jar",
                    "ru.app.Example"
            );

            verify(javaRuntime).createProcessBuilder(expectedCommandLineArguments);
        }
    }
}