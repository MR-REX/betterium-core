package ru.mrrex.betterium.core.preprocessor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preprocessor {

    private final Pattern variablePattern;
    private final Map<String, Object> variables;

    public Preprocessor(Pattern variablePattern, Map<String, Object> variables) {
        this.variablePattern = variablePattern;
        this.variables = (variables != null)
                ? Map.copyOf(variables)
                : new HashMap<>();
    }

    public Preprocessor(Pattern variablePattern) {
        this(variablePattern, null);
    }

    public Preprocessor(String variablePattern) {
        this(Pattern.compile(variablePattern));
    }

    public void clearVariables() {
        this.variables.clear();
    }

    public Set<String> getVariables() {
        return Set.copyOf(variables.keySet());
    }

    public void setVariables(Map<String, Object> variables) {
        Objects.requireNonNull(variables, "Variables map must not be null");
        this.variables.putAll(variables);
    }

    public void setVariable(String variable, Object value) {
        Objects.requireNonNull(variable, "Variable name must not be null");
        this.variables.put(variable, value);
    }

    public Object getValue(String variable, Object defaultValue) {
        return variables.getOrDefault(variable, defaultValue);
    }

    private String getStringValue(String variable, String defaultValue) {
        return getValue(variable, defaultValue).toString();
    }

    public String preprocess(String string) {
        Matcher matcher = variablePattern.matcher(string);
        StringBuilder stringBuilder = new StringBuilder();

        String variableName;
        String replacement;

        while (matcher.find()) {
            variableName = matcher.group(1);
            replacement = getStringValue(variableName, matcher.group(0));

            matcher.appendReplacement(stringBuilder, Matcher.quoteReplacement(replacement));
        }

        return stringBuilder.toString();
    }

    public List<String> preprocess(List<String> words) {
        return words.stream()
                .map(this::preprocess)
                .toList();
    }
}
