package ru.mrrex.betterium.core.client;

import ru.mrrex.betterium.core.client.config.ClientConfiguration;
import ru.mrrex.betterium.core.client.config.PlayerConfiguration;
import ru.mrrex.betterium.core.runtime.JavaApplicationConfiguration;

import java.util.Objects;

public record Client(
        ClientConfiguration clientConfiguration,
        PlayerConfiguration playerConfiguration,
        JavaApplicationConfiguration javaApplicationConfiguration,
        Process process
) {

    public Client {
        Objects.requireNonNull(clientConfiguration, "Client configuration must not be null");
        Objects.requireNonNull(playerConfiguration, "Player configuration must not be null");

        Objects.requireNonNull(javaApplicationConfiguration, "Java application configuration must not be null");
        Objects.requireNonNull(process, "Process must not be null");

        if (!process.isAlive())
            throw new IllegalStateException("Process must be active");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ClientConfiguration clientConfiguration;
        private PlayerConfiguration playerConfiguration;

        private JavaApplicationConfiguration javaApplicationConfiguration;
        private Process process;

        private Builder() {}

        public Builder withClientConfiguration(ClientConfiguration clientConfiguration) {
            this.clientConfiguration = Objects.requireNonNull(
                    clientConfiguration,
                    "Client configuration must not be null"
            );

            return this;
        }

        public Builder withPlayerConfiguration(PlayerConfiguration playerConfiguration) {
            this.playerConfiguration = Objects.requireNonNull(
                    playerConfiguration,
                    "Player configuration must not be null"
            );

            return this;
        }

        public Builder withJavaApplicationConfiguration(JavaApplicationConfiguration javaApplicationConfiguration) {
            this.javaApplicationConfiguration = Objects.requireNonNull(
                    javaApplicationConfiguration,
                    "Java application configuration must not be null"
            );

            return this;
        }

        public Builder withProcess(Process process) {
            this.process = Objects.requireNonNull(process, "Process must not be null");
            return this;
        }

        public Client build() {
            if (clientConfiguration == null)
                throw new IllegalStateException("Client configuration must be set before building client instance");

            if (playerConfiguration == null)
                throw new IllegalStateException("Player configuration must be set before building client instance");

            if (javaApplicationConfiguration == null)
                throw new IllegalStateException("Java application configuration must be set before building client instance");

            if (process == null)
                throw new IllegalStateException("Process must be set before building client instance");

            return new Client(
                    clientConfiguration,
                    playerConfiguration,
                    javaApplicationConfiguration,
                    process
            );
        }
    }
}
