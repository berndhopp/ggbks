package com.librishuffle.bookinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class VolumeInfo {
    @JsonProperty("title")
    String title;
    @JsonProperty("subtitle")
    String subtitle;
    @JsonProperty("authors")
    String[] authors;
    @JsonProperty("publishedDate")
    String publishedDate;
    @JsonProperty("industryIdentifiers")
    List<IndustryIdentifier> industryIdentifiers;
    @JsonProperty("pageCount")
    short pageCount;
    @JsonProperty("imageLinks")
    ImageLinks imageLinks;
    @JsonProperty("language")
    String language;
}
