package ru.mrrex.betterium.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.nio.file.Path;

public class RelativePathDeserializer extends JsonDeserializer<Path> {

    @Override
    public Path deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String pathString = p.getValueAsString();
        return Path.of(pathString);
    }
}
