package com.bogaware.frugal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
public class SearchProductFilterDTO {
    @JsonProperty
    private String keyword;
    @JsonProperty
    private Store[] stores;
    @JsonProperty
    private String zipCode;
    @JsonProperty
    private int pageNum;
    @JsonProperty
    private SortBy sortBy;

    public enum Store {
        AMAZON("Amazon"),
        EBAY("eBay"),
        WALMART("Walmart"),
        GOOGLE_SHOPPING("Google Shopping");

        public final String label;

        private Store(String label) {
            this.label = label;
        }
    }

    public enum SortBy {
        RELEVANCE("Relevance"),
        PRICE_LOW_TO_HIGH("Price Low To High"),
        PRICE_HIGH_TO_LOW("Price High To Low"),
        RATING_HIGH_TO_LOW("Rating High To Low");

        public final String label;

        private SortBy(String label) {
            this.label = label;
        }
    }
}
