package com.project.techstore.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.techstore.models.Order;
import com.project.techstore.models.Product;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    @JsonProperty("quantity")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @JsonProperty("price")
    @Positive(message = "Price must be positive")
    private Long price;

    @JsonProperty("order_id")
    @NotBlank(message = "Items in this order must be linked to the order")
    private String orderId;

    @JsonProperty("product_id")
    @NotBlank(message = "Items in this order must be linked to the product")
    private String productId;

}
