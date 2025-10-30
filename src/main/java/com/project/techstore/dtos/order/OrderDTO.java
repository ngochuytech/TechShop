package com.project.techstore.dtos.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "The maximum length of payment method is 50 characters")
    private String paymentMethod;

    @Size(max = 255, message = "The maximum length of note is 255 characters")
    private String note;

    @NotNull(message = "Shipping fee is required")
    @PositiveOrZero(message = "Shipping fee must be positive or zero")
    private Long shippingFee;

    @Valid
    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemDTO> orderItemDTOs;

    private String promotionCode; // Mã khuyến mãi (optional)
}
