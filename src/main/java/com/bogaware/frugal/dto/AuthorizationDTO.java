package com.bogaware.frugal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
public class AuthorizationDTO {
    @JsonProperty
    private final String token;
}
