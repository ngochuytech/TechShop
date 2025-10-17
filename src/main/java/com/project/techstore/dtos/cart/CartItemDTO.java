package com.project.techstore.dtos.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private String id;
    private String productId;
    private String productVariantId;
    private String productName;
    private String color;
    private String image;
    private Integer quantity;
    private Long price;
    private Long subtotal;
    private Integer availableStock;
    private boolean isVariant; // true nếu là variant, false nếu là product thường
}
