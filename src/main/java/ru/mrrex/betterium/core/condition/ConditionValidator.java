package ru.mrrex.betterium.core.condition;

import java.util.Map;

public interface ConditionValidator {

    boolean validate(EnvironmentContext context, Map<String, String> conditions);
}
