package com.project.techstore.dtos;

import com.project.techstore.dtos.order.OrderDTO;

import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class CreateOrderRequest {
    @Valid
    private OrderDTO orderDTO;

    @Valid
    private AddressDTO addressDTO;
}
