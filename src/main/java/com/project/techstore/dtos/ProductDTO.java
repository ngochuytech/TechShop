package com.project.techstore.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @JsonProperty("name")
    @NotBlank(message = "Name is required")
    @Size(max = 500, message = "The maximum length of the name is 500 characters")
    private String name;

    @JsonProperty("image")
    private String image;

    @JsonProperty("price")
    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be a positive number")
    private Long price;

    @JsonProperty("price_discount")
    @PositiveOrZero(message = "Price discount must be a positive number")
    private Long priceDiscount;

    @JsonProperty("stock")
    @PositiveOrZero(message = "Stock must be a positive number")
    private Integer stock;

    @JsonProperty("spec")
    private String spec;

    @JsonProperty("product_model_id")
    private Long productModelId;
}
