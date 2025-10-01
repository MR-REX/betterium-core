package ru.mrrex.betterium.core.runtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the {@link JavaApplicationConfiguration} record and
 * its {@link JavaApplicationConfiguration.Builder}.
 * Focuses on immutability, validation, default list handling,
 * and Jackson compatibility.
 */
@DisplayName("Java Application Configuration Tests")
class JavaApplicationConfigurationTest {

    private static final String MOCK_MAIN_CLASS = "ru.app.Main";
    private static final List<Path> MOCK_CLASSPATH = List.of(Path.of("/mock/libraries/application.jar"));

    /**
     * Tests related to the correct behaviour of the
     * {@link JavaApplicationConfiguration.Builder}.
     */
    @Nested
    @DisplayName("A. Builder Tests")
    class BuilderTests {

        /**
         * Verifies that the build method throws {@link IllegalStateException}
         * if the {@code mainClass} is not set.
         */
        @Test
        @DisplayName("Success: Full configuration with defaults")
        void testSuccessfulConfigurationWithDefaults() {
            JavaApplicationConfiguration configuration = JavaApplicationConfiguration.builder()
                    .withMainClass(MOCK_MAIN_CLASS)
                    .withClasspathEntries(MOCK_CLASSPATH)
                    .build();

            assertAll(
                    "Full Configuration Checks",
                    () -> assertEquals(MOCK_MAIN_CLASS, configuration.mainClass(), "Main class should match set value"),
                    () -> assertEquals(MOCK_CLASSPATH, configuration.classpathEntries(), "Classpath should match set list"),
                    () -> assertTrue(configuration.applicationArguments().isEmpty(), "Application arguments should default to empty list"),
                    () -> assertTrue(configuration.jvmArguments().isEmpty(), "JVM arguments should default to empty list")
            );
        }

        /**
         * Verifies that the {@code build()} method throws {@link IllegalStateException}
         * if the {@code classpathEntries} are not set.
         */
        @Test
        @DisplayName("Failure: Missing mainClass should throw IllegalStateException")
        void testMissingMainClass() {
            JavaApplicationConfiguration.Builder configurationBuilder = JavaApplicationConfiguration.builder()
                    .withClasspathEntries(MOCK_CLASSPATH);

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    configurationBuilder::build
            );

            assertTrue(
                    exception.getMessage().contains("mainClass"),
                    "Exception message should mention missing mainClass field"
            );
        }


        /**
         * Verifies that the {@code build()} method throws {@link IllegalStateException}
         * if the {@code classpathEntries} are not set.
         */
        @Test
        @DisplayName("Failure: Missing classpathEntries should throw IllegalStateException")
        void testMissingClasspathEntries() {
            JavaApplicationConfiguration.Builder configurationBuilder = JavaApplicationConfiguration.builder()
                    .withMainClass(MOCK_MAIN_CLASS);

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    configurationBuilder::build
            );

            assertTrue(
                    exception.getMessage().contains("classpathEntries"),
                    "Exception message should mention missing classpathEntries field"
            );
        }

        /**
         * Verifies that passing a {@code null} list to the builder methods results
         * in a {@link NullPointerException} when the compact constructor
         * is executed.
         */
        @Test
        @DisplayName("Failure: Passing null list to builder should fail in compact constructor")
        void testNullListFailsInCompactConstructor() {
            assertThrows(
                    NullPointerException.class,
                    () -> new JavaApplicationConfiguration(
                            null,
                            MOCK_CLASSPATH,
                            null,
                            null
                    )
            );
        }
    }

    /**
     * Tests related to immutability and defensive copying.
     */
    @Nested
    @DisplayName("B. Immutability and Defensive Copying Tests")
    class ImmutabilityTests {

        /**
         * Verifies that the internal lists are immutable copies and
         * cannot be modified by changing the original lists passed
         * into the builder.
         */
        @Test
        @DisplayName("Defensive Copy: Modifying original lists should not affect configuration")
        void testDefensiveCopying() {
            List<String> originalArguments = new ArrayList<>(List.of("start"));
            List<Path> originalClasspath = new ArrayList<>(MOCK_CLASSPATH);

            JavaApplicationConfiguration configuration = JavaApplicationConfiguration.builder()
                    .withMainClass(MOCK_MAIN_CLASS)
                    .withClasspathEntries(originalClasspath)
                    .withApplicationArguments(originalArguments)
                    .build();

            originalArguments.add("additional_value");
            originalClasspath.add(Path.of("/additional/file.jar"));

            assertAll(
                    "Immutability Checks",
                    () -> assertEquals(1, configuration.applicationArguments().size(), "Configuration arguments list should remain its original size (1)"),
                    () -> assertEquals(1, configuration.classpathEntries().size(), "Configuration classpath list should remain its original size (1)")
            );

            List<String> configurationArguments = configuration.applicationArguments();

            assertThrows(
                    UnsupportedOperationException.class,
                    () -> configurationArguments.add("test"),
                    "Internal lists must be immutable"
            );
        }
    }

    /**
     * Tests related to Jackson serialization and deserialization.
     */
    @Nested
    @DisplayName("C. Jackson Serialization Tests")
    class JacksonTests {

        private final ObjectMapper objectMapper = new ObjectMapper();

        private JavaApplicationConfiguration configuration;
        private String serializedConfiguration;

        @BeforeEach
        void setUp() {
            Random random = new Random();

            String mainClass = "ru.client.Class" + random.nextInt();

            List<Path> classpathEntries = random.ints(10)
                    .mapToObj(n -> Path.of("mock/libraries/jar" + n + ".jar"))
                    .toList();

            String serializedClasspathEntries = classpathEntries.stream()
                    .map(p -> '"' + p.toString().replace("\\", "\\\\") + '"')
                    .collect(Collectors.joining(","));

            List<String> applicationArguments = random.ints(10)
                    .mapToObj(n -> "-app-argument" + n)
                    .toList();

            String serializedApplicationArguments = applicationArguments.stream()
                    .map(a -> '"' + a + '"')
                    .collect(Collectors.joining(","));

            List<String> jvmArguments = random.ints(10)
                    .mapToObj(n -> "--jvm-argument" + n)
                    .toList();

            String serializedJvmArguments = jvmArguments.stream()
                    .map(a -> '"' + a + '"')
                    .collect(Collectors.joining(","));

            configuration = JavaApplicationConfiguration.builder()
                    .withMainClass(mainClass)
                    .withClasspathEntries(classpathEntries)
                    .withApplicationArguments(applicationArguments)
                    .withJvmArguments(jvmArguments)
                    .build();

            serializedConfiguration = "{" +
                    "\"main_class\":\"" + mainClass + "\"," +
                    "\"classpath_entries\":[" + serializedClasspathEntries + "]," +
                    "\"application_arguments\":[" + serializedApplicationArguments + "]," +
                    "\"jvm_arguments\":[" + serializedJvmArguments + "]" +
                    "}";
        }

        /**
         * Tests whether a configured object can be correctly
         * serialized into JSON.
         *
         * @throws JsonProcessingException If JSON processing fails.
         */
        @Test
        @DisplayName("Jackson serialization (Record to JSON)")
        void testJacksonSerialization() throws JsonProcessingException {
            String serializationResult = objectMapper.writeValueAsString(configuration);
            assertEquals(serializedConfiguration, serializationResult);
        }

        /**
         * Tests whether a JSON string can be correctly
         * deserialized back into an object.
         *
         * @throws JsonProcessingException If JSON processing fails.
         */
        @Test
        @DisplayName("Jackson deserialization (JSON to Record)")
        void testJacksonDeserialization() throws JsonProcessingException {
            JavaApplicationConfiguration deserializedConfiguration = objectMapper.readValue(
                    serializedConfiguration,
                    JavaApplicationConfiguration.class
            );

            assertEquals(configuration, deserializedConfiguration);
        }
    }
}