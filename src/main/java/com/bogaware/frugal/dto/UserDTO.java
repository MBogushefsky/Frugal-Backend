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
public class UserDTO {
    @JsonProperty
    private String Id;
    @JsonProperty
    private String username;
    @JsonProperty
    private String passwordHash;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private String email;
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private String profileImage;
}
