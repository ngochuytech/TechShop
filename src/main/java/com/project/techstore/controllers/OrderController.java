package com.project.techstore.controllers;

import com.project.techstore.dtos.order.CreateOrderRequest;
import com.project.techstore.models.Order;
import com.project.techstore.models.User;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.order.OrderResponse;
import com.project.techstore.services.order.IOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<?>> getOrderByStatus(@RequestParam String status) throws Exception {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrderByStatus(status)));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<?>> getOrderByUser(@AuthenticationPrincipal User user) throws Exception {
        List<Order> orders = orderService.getOrderByUser(user.getId());
        List<OrderResponse> orderResponses = orders.stream()
                .map(OrderResponse::fromOrder)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(orderResponses));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable("orderId") String orderId) throws Exception {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.ok(OrderResponse.fromOrder(order)));
    }


    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            @AuthenticationPrincipal User user,
            BindingResult result) throws Exception {
        if(result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(String.join(", ", errorMessages)));
        }
        orderService.createOrder(user.getId(), request.getOrderDTO(), request.getAddressDTO());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create order successful"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateOrder(
            @PathVariable("id") String id,
            @RequestBody @Valid CreateOrderRequest request,
            BindingResult result) throws Exception {
        if(result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(String.join(", ", errorMessages)));
        }
        orderService.updateOrder(id, request.getOrderDTO(), request.getAddressDTO());
        return ResponseEntity.ok(ApiResponse.ok("Update order successful"));
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<ApiResponse<?>> updateStatusOrder(
            @PathVariable("id") String id, 
            @RequestParam String status) throws Exception {
        orderService.updateStatusOrder(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Update order status successful"));
    }
}
