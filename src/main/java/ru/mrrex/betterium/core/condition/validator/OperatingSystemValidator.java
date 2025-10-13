package ru.mrrex.betterium.core.condition.validator;

import ru.mrrex.betterium.core.condition.ConditionValidator;
import ru.mrrex.betterium.core.condition.EnvironmentContext;
import ru.mrrex.betterium.core.condition.exception.NoSuchPropertyException;

import java.util.Map;

public class OperatingSystemValidator implements ConditionValidator {

    private static final String CONDITION_CONTAINS = "os.name.contains";
    private static final String OS_NAME_PROPERTY = "os.name";

    @Override
    public boolean validate(EnvironmentContext context, Map<String, String> conditions) {
        String conditionValue = conditions.get(CONDITION_CONTAINS);

        if (conditionValue == null || conditionValue.isBlank())
            return true;

        String propertyValue = context.getProperty(OS_NAME_PROPERTY);

        if (propertyValue == null || propertyValue.isBlank())
            throw new NoSuchPropertyException(context, OS_NAME_PROPERTY);

        return propertyValue.contains(conditionValue);
    }
}
