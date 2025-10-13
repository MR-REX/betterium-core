package ru.mrrex.betterium.core.condition.validator;

import ru.mrrex.betterium.core.condition.ConditionValidator;
import ru.mrrex.betterium.core.condition.EnvironmentContext;
import ru.mrrex.betterium.core.condition.exception.NoSuchPropertyException;

import java.util.Map;

public class CpuArchitectureValidator implements ConditionValidator {

    private static final String CONDITION_CONTAINS = "cpu.architecture.contains";
    private static final String CPU_ARCHITECTURE_PROPERTY = "cpu.architecture";

    @Override
    public boolean validate(EnvironmentContext context, Map<String, String> conditions) {
        String conditionValue = conditions.get(CONDITION_CONTAINS);

        if (conditionValue == null || conditionValue.isBlank())
            return true;

        String propertyValue = context.getProperty(CPU_ARCHITECTURE_PROPERTY);

        if (propertyValue == null || propertyValue.isBlank())
            throw new NoSuchPropertyException(context, CPU_ARCHITECTURE_PROPERTY);

        return propertyValue.contains(conditionValue);
    }
}
