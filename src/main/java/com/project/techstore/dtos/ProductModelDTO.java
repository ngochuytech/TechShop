package com.project.techstore.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModelDTO {
    @JsonProperty("name")
    @NotBlank(message = "Name is required")
    @Size(max = 500, message = "The maximum length of the name is 500 characters")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category_id")
    @NotNull(message = "Category is required")
    private Long categoryId;

    @JsonProperty("brand_id")
    @NotNull(message = "Brand is required")
    private Long brandId;
}
