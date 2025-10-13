package ru.mrrex.betterium.core.artifact;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.mrrex.betterium.core.artifact.implementation.MavenArtifact;
import ru.mrrex.betterium.core.library.NativeLibrary;

import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MavenArtifact.class, name = "maven")
})
public sealed interface Artifact permits MavenArtifact {

    Set<NativeLibrary> getDependencies();
}
