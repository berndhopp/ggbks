package com.librishuffle.bookinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class Item {
    @JsonProperty("volumeInfo")
    VolumeInfo volumeInfo;
}
