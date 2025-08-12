package com.project.techstore.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @JsonProperty("total_price")
    @Positive(message = "Discount price must be positive")
    private Long totalPrice;

    @JsonProperty("discount_price")
    @Positive(message = "Discount price must be positive")
    private Long discountPrice;

    @JsonProperty("payment_method")
    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "The maximum length of payment method is 50 characters")
    private String paymentMethod;

    @JsonProperty("status")
    @Size(max = 50, message = "The maximum length of status is 50 characters")
    private String status;

    @JsonProperty("user_id")
    @NotBlank(message = "User is required")
    private String userId;

    @JsonProperty("promotion_id")
    private String promotionId;

    @JsonProperty("address_id")
    private String addressId;
}
