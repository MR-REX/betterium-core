package ru.mrrex.betterium.core.condition.exception;

import ru.mrrex.betterium.core.condition.EnvironmentContext;

import java.util.Objects;

public class NoSuchPropertyException extends RuntimeException {

    private final transient EnvironmentContext context;
    private final String propertyName;

    public NoSuchPropertyException(String message, EnvironmentContext context, String propertyName) {
        super(message);

        this.context = Objects.requireNonNull(context, "Environment context must not be null");
        this.propertyName = Objects.requireNonNull(propertyName, "Property name must not be null");
    }

    public NoSuchPropertyException(EnvironmentContext context, String propertyName) {
        this(
                "Property \"" + propertyName + "\" is empty or not defined in the environment context",
                context,
                propertyName
        );
    }

    public EnvironmentContext getContext() {
        return context;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
