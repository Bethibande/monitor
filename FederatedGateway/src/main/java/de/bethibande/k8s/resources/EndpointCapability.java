package de.bethibande.k8s.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EndpointCapability {

    @JsonProperty("Translations")
    TRANSLATIONS,
    @JsonProperty("WebModule")
    WEB_MODULE,
    API,

}
