package com.project.techstore.dtos.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password is required")
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("full_name")
    @NotBlank(message = "Full name is required")
    private String fullName;

    @JsonProperty("google_id")
    private String googleId;

    @JsonProperty("phone")
    @Nullable
    @Size(min = 10, max = 10, message = "The phone number must be exactly 10 digits")
    private String phone;

    private LocalDate dateOfBirth;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("address_id")
    private String addressId;

    @JsonProperty("role_id")
    @NotNull(message = "Role id is required")
    private Long roleId = 1L;
}
