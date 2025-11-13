package ru.mrrex.betterium.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class HexLongDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();

        if (value == null || value.trim().isBlank())
            return 0L;

        String preparedValue = value.trim();

        try {
            return Long.parseLong(preparedValue, 10);
        } catch (NumberFormatException _) {
            try {
                return Long.parseLong(preparedValue, 16);
            } catch (NumberFormatException e) {
                throw new IOException("Incorrect HEX number format: '" + value + "'", e);
            }
        }
    }
}
