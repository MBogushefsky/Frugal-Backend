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
public class ChangePasswordDTO {
    @JsonProperty
    private String currentPassword;
    @JsonProperty
    private String newPassword;
    @JsonProperty
    private String newPasswordConfirm;
}
