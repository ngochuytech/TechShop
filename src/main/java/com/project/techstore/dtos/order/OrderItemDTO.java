package com.project.techstore.dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    @JsonProperty("quantity")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @JsonProperty("price")
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Long price;

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("productVariantId")
    private String productVariantId;
}
