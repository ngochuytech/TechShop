package com.project.techstore.dtos.product;

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
    @NotNull(message = "Product model ID is required")
    private Long productModelId;

    @NotBlank(message = "Description is required")
    private String description;

    @JsonProperty("price")
    private Long price;

    @JsonProperty("stock")
    private Integer stock;
}
