package com.project.techstore.dtos.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private String id;
    private String userId;
    private List<CartItemDTO> items;
    private Integer totalQuantity;
    private Long totalPrice;
}
