package com.project.techstore.dtos;

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
    private String id;

    @NotBlank(message = "Province is required")
    @Size(max = 255, message = "The maximum length of the province is 255 characters")
    private String province;

    @NotBlank(message = "Ward is required")
    @Size(max = 255, message = "The maximum length of the ward is 255 characters")
    private String ward;

    @NotBlank(message = "Home address is required")
    @Size(max = 200, message = "The maximum length of the ward is 200 characters")
    private String homeAddress;

    @Size(max = 200, message = "The maximum length of the suggested name is 200 characters")
    private String suggestedName;

    @NotBlank(message = "Phone number is required")
    @Size(max = 10, message = "The maximum length of the phone number is 10 characters")
    private String phoneNumber;
}
