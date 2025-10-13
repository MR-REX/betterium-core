package ru.mrrex.betterium.core.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mrrex.betterium.core.client.config.PlayerConfiguration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the {@link PlayerConfiguration} record and its
 * associated {@link PlayerConfiguration.Builder}.
 */
@DisplayName("PlayerConfiguration Tests")
class PlayerConfigurationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String userName;
    private String sessionId;
    private UUID playerUuid;

    @BeforeEach
    void setUp() {
        userName = "Player" + System.currentTimeMillis();
        sessionId = "SessionID:" + UUID.randomUUID();
        playerUuid = UUID.randomUUID();
    }

    /**
     * Tests the successful creation of a {@link PlayerConfiguration} object
     * using the {@link PlayerConfiguration.Builder} and verifies the immutability
     * of the final record fields.
     */
    @Test
    @DisplayName("Player configuration builder usage")
    void testBuilderAndImmutability() {
        PlayerConfiguration configuration = PlayerConfiguration.builder()
                .withUserName(userName)
                .withSessionId(sessionId)
                .withPlayerUuid(playerUuid)
                .build();

        assertEquals(userName, configuration.userName());
        assertEquals(sessionId, configuration.sessionId());
        assertEquals(playerUuid, configuration.playerUuid());
    }

    /**
     * Ensures that the {@link PlayerConfiguration.Builder} throws an
     * {@link IllegalStateException} when required fields are missing
     * before the {@code build()} method is called.
     */
    @Test
    @DisplayName("Player configuration builder failure on missing required field")
    void testBuilderFailureOnMissingField() {
        PlayerConfiguration.Builder configurationBuilder = PlayerConfiguration.builder()
                .withUserName(userName)
                .withSessionId(sessionId);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                configurationBuilder::build
        );

        assertTrue(
                exception.getMessage().contains("playerUuid"),
                "Should throw an error about incomplete build"
        );
    }

    /**
     * Tests the validation logic within the compact constructor, ensuring
     * a {@link IllegalStateException} is thrown then a required field is
     * explicitly passed as {@code null}.
     */
    @Test
    @DisplayName("Compact constructor failure on null value")
    void testCompactConstructorValidation() {
        PlayerConfiguration.Builder configurationBuilder = PlayerConfiguration.builder()
                .withUserName(userName)
                .withSessionId(sessionId)
                .withPlayerUuid(null);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                configurationBuilder::build
        );

        assertTrue(
                exception.getMessage().toLowerCase().contains("player uuid"),
                "Should throw an error about null player UUID (playerUuid) field"
        );
    }

    /**
     * Verifies that the {@link PlayerConfiguration} record can be correctly
     * serialized into a JSON string by the Jackson {@link ObjectMapper},
     * respecting the {@code @JsonProperty} names.
     *
     * @throws JsonProcessingException If an error occurs during serialization (e.g., failed property access).
     */
    @Test
    @DisplayName("Jackson serialization (Record to JSON)")
    void testJacksonSerialization() throws JsonProcessingException {
        PlayerConfiguration configuration = new PlayerConfiguration(userName, null, playerUuid);

        String expectedJson = "{" +
                "\"user_name\":\"" + userName + "\"," +
                "\"player_uuid\":\"" + playerUuid + "\"" +
                "}";

        String serializedConfiguration = objectMapper.writeValueAsString(configuration);
        assertEquals(expectedJson, serializedConfiguration);
    }

    /**
     * Verifies that the {@link PlayerConfiguration} record can be correctly
     * deserialized from a JSON string by Jackson {@link ObjectMapper},
     * mapping the JSON properties back to the Record fields.
     *
     * @throws JsonProcessingException If an error occurs during serialization (e.g., failed property access).
     */
    @Test
    @DisplayName("Jackson deserialization (JSON to Record)")
    void testJacksonDeserialization() throws JsonProcessingException {
        String serializedConfiguration = "{" +
                "\"user_name\":\"" + userName + "\"," +
                "\"player_uuid\":\"" + playerUuid + "\"" +
                "}";

        PlayerConfiguration deserializedConfiguration = objectMapper.readValue(
                serializedConfiguration,
                PlayerConfiguration.class
        );

        PlayerConfiguration expectedConfiguration = new PlayerConfiguration(userName, null, playerUuid);
        assertEquals(expectedConfiguration, deserializedConfiguration);
    }
}