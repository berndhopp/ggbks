package com.librishuffle.bookinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class IndustryIdentifier {
    @JsonProperty("type")
    String type;
    @JsonProperty("identifier")
    String identifier;
}
