package com.project.techstore.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("province")
    @NotBlank(message = "Province is required")
    @Size(max = 255, message = "The maximum length of the province is 255 characters")
    private String province;

    @JsonProperty("district")
    @NotBlank(message = "District is required")
    @Size(max = 255, message = "The maximum length of the district is 255 characters")
    private String district;

    @JsonProperty("ward")
    @NotBlank(message = "Ward is required")
    @Size(max = 255, message = "The maximum length of the ward is 255 characters")
    private String ward;

    @JsonProperty("home_address")
    @NotBlank(message = "Home address is required")
    @Size(max = 200, message = "The maximum length of the ward is 200 characters")
    private String homeAddress;

    @JsonProperty("suggested_name")
    @Size(max = 200, message = "The maximum length of the suggested name is 200 characters")
    private String suggestedName;
}
