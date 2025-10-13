package ru.mrrex.betterium.core.condition;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record EnvironmentContext(Map<String, String> properties) {

    public EnvironmentContext {
        Objects.requireNonNull(properties, "Environment context properties map must not be null");
        properties = Map.copyOf(properties);
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Map<String, String> properties = new HashMap<>();

        private Builder() {}

        public Builder withProperties(Map<String, String> properties) {
            Objects.requireNonNull(properties, "Properties map must not be null");
            this.properties.putAll(properties);

            return this;
        }

        public Builder addProperty(String name, String value) {
            Objects.requireNonNull(name, "Property name must not be null");
            this.properties.put(name, value);

            return this;
        }

        public EnvironmentContext build() {
            return new EnvironmentContext(properties);
        }
    }
}
