package com.librishuffle.bookinfo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
class ImageLinks {
    @JsonProperty("smallThumbnail")
    URL smallThumbNail;

    @JsonProperty("thumbNail")
    URL thumbNail;
}
