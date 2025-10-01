package ru.mrrex.betterium.core.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.nio.file.Path;

public class RelativePathSerializer extends JsonSerializer<Path> {

    @Override
    public void serialize(Path value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
