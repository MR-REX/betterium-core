package ru.mrrex.betterium.core.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents an immutable configuration for a player session.
 * Utilizes a Java Record for immutability, ensuring thread-safe data handling.
 *
 * @param userName The user's unique name.
 * @param sessionId The identifier for the current session.
 * @param playerUuid The player's unique identifier.
 */
public record PlayerConfiguration(
        @JsonProperty("user_name") String userName,
        @JsonIgnore String sessionId,
        @JsonProperty("player_uuid") UUID playerUuid
) {

    /**
     * Compact constructor used for validating required fields.
     * Invoked directly or automatically by Jackson during deserialization.
     *
     * @throws NullPointerException If {@link #userName} or {@link #playerUuid} is null.
     * @throws IllegalArgumentException If {@link #userName} is blank (empty or contains only whitespaces).
     */
    public PlayerConfiguration {
        Objects.requireNonNull(userName, "User name (userName) must not be null");
        Objects.requireNonNull(playerUuid, "Player UUID (playerUuid) must not be null");

        if (userName.trim().isBlank())
            throw new IllegalArgumentException("User name (userName) must not be empty");
    }

    /**
     * Static factory method to obtain a new builder instance.
     *
     * @return A new Builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Nested static Builder class.
     * Allows flexible assembly of the {@link PlayerConfiguration} object
     * using the Fluent API design pattern.
     */
    public static class Builder {

        private String userName = null;
        private String sessionId = null;
        private UUID playerUuid = null;

        private Builder() {}

        /**
         * Sets the user's unique name.
         *
         * @param userName The user's name.
         * @return The Builder instance for method chaining.
         */
        public Builder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        /**
         * Sets the identifier for the current session.
         *
         * @param sessionId The session identifier.
         * @return The Builder instance for method chaining.
         */
        public Builder withSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        /**
         * Sets the player's unique identifier.
         *
         * @param playerUuid The player's UUID.
         * @return The Builder instance for method chaining.
         */
        public Builder withPlayerUuid(UUID playerUuid) {
            this.playerUuid = playerUuid;
            return this;
        }

        /**
         * Build method, creating the immutable PlayerConfiguration object
         * by calling the canonical Record constructor.
         *
         * @return A new immutable PlayerConfiguration object.
         * @throws IllegalStateException If any required field was not set before calling this method.
         */
        public PlayerConfiguration build() {
            if (userName == null)
                throw new IllegalStateException("User name (userName) must be set before building player configuration");

            if (playerUuid == null)
                throw new IllegalStateException("Player UUID (playerUuid) must be set before building player configuration");

            return new PlayerConfiguration(userName, sessionId, playerUuid);
        }
    }
}
