package com.project.techstore.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @JsonProperty("name")
    @NotBlank(message = "Name is required")
    @Size(max = 500, message = "The maximum length of the name is 500 characters")
    private String name;

    @JsonProperty("configuration_summary")
    @NotBlank(message = "Configuration summary is required")
    private String configurationSummary;

    @JsonProperty("attributes")
    private Map<String, String> attributes;

    @JsonProperty("product_model_id")
    private Long productModelId;

    private String description;
}
