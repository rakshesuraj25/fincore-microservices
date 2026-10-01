package com.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class RegisterRequest {

    @JsonProperty("username")
    @NotNull
    @NotEmpty
    @Schema(example = "suraj123")
    private String username;

    @JsonProperty("firstName")
    @NotNull
    @NotEmpty
    @Schema(example = "suraj")
    private String firstName;

    @JsonProperty("middleName")
    @NotNull
    @NotEmpty
    @Schema(example = "suresh")
    private String middleName;

    @JsonProperty("lastName")
    @NotNull
    @NotEmpty
    @Schema(example = "rakshe")
    private String lastName;

    @JsonProperty("email")
    @NotNull
    @NotEmpty
    @Email(message = "Invalid email format")
    @Schema(example = "suraj123@gmail.com")
    private String email;

    @JsonProperty("mobileNumber")
    @NotNull
    @NotEmpty
    @Schema(example = "7083125808")
    private String mobileNumber;

    @JsonProperty("password")
    @NotNull
    @NotEmpty
    @Schema(example = "suraj123")
    private String password;
}