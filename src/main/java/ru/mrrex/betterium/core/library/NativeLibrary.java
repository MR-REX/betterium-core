package ru.mrrex.betterium.core.library;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.mrrex.betterium.core.library.implementation.RemoteNativeLibrary;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RemoteNativeLibrary.class, name = "remote")
})
public sealed interface NativeLibrary permits RemoteNativeLibrary { }
