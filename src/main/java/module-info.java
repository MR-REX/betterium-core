module ru.mrrex.betterium.core {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    exports ru.mrrex.betterium.core.artifact;
    exports ru.mrrex.betterium.core.artifact.implementation;

    exports ru.mrrex.betterium.core.checksum;

    exports ru.mrrex.betterium.core.client;
    exports ru.mrrex.betterium.core.client.config;

    exports ru.mrrex.betterium.core.condition;
    exports ru.mrrex.betterium.core.condition.exception;
    exports ru.mrrex.betterium.core.condition.validator;

    exports ru.mrrex.betterium.core.download.downloader;
    exports ru.mrrex.betterium.core.download.downloader.http;
    exports ru.mrrex.betterium.core.download.exception;
    exports ru.mrrex.betterium.core.download.listener;
    exports ru.mrrex.betterium.core.download.stream;

    exports ru.mrrex.betterium.core.jackson;

    exports ru.mrrex.betterium.core.library;
    exports ru.mrrex.betterium.core.library.implementation;

    exports ru.mrrex.betterium.core.preprocessor;

    exports ru.mrrex.betterium.core.resource;

    exports ru.mrrex.betterium.core.runtime;
    exports ru.mrrex.betterium.core.runtime.exception;

    opens ru.mrrex.betterium.core.client.config;
    opens ru.mrrex.betterium.core.library.implementation;
}