package ru.mrrex.betterium.core.condition;

import ru.mrrex.betterium.core.resource.ConditionalResource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConditionEvaluator {

    private final EnvironmentContext context;
    private final List<ConditionValidator> validators;

    public ConditionEvaluator(EnvironmentContext context, List<ConditionValidator> validators) {
        this.context = Objects.requireNonNull(context, "Environment context must not be null");

        Objects.requireNonNull(validators, "Validators set must not be null");
        this.validators = List.copyOf(validators);
    }

    public boolean isApplicable(ConditionalResource resource) {
        Map<String, String> conditions = resource.getConditions();

        if (conditions == null || conditions.isEmpty())
            return true;

        for (ConditionValidator validator : validators)
            if (!validator.validate(context, conditions))
                return false;

        return true;
    }
}
