package com.librishuffle.bookinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class QueryResult {
    @JsonProperty("items")
    private final List<Item> items = new ArrayList<>();

    List<Item> getItems() {
        return items;
    }
}
