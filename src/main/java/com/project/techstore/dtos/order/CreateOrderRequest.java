package com.project.techstore.dtos.order;

import com.project.techstore.dtos.AddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @Valid
    @NotNull(message = "Address information is required")
    private AddressDTO addressDTO;

    @Valid
    @NotNull(message = "Order information is required")
    private OrderDTO orderDTO;
}
