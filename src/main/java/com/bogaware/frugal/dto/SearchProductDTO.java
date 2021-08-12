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
public class SearchProductDTO {
    @JsonProperty
    private String id;
    @JsonProperty
    private String store;
    @JsonProperty
    private String title;
    @JsonProperty
    private String brand;
    @JsonProperty
    private double price;
    @JsonProperty
    private Condition condition;
    @JsonProperty
    private String link;
    @JsonProperty
    private String imageUrl;
    @JsonProperty
    private Double rating;
    @JsonProperty
    private Integer ratingCount;

    public enum Condition {
        NEW("new"),
        USED("used");

        public final String label;

        private Condition(String label) {
            this.label = label;
        }
    }
}
