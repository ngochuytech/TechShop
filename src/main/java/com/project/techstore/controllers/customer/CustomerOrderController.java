package com.project.techstore.controllers.customer;

import com.project.techstore.dtos.order.CreateOrderRequest;
import com.project.techstore.models.Order;
import com.project.techstore.models.User;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.order.OrderResponse;
import com.project.techstore.services.order.IOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/customer/orders")
@RequiredArgsConstructor
public class CustomerOrderController {
    private final IOrderService orderService;

    @GetMapping("/count-by-status")
    public ResponseEntity<?> getOrderCountByStatus(@AuthenticationPrincipal User user) throws Exception {
        Map<String, Long> statusCounts = orderService.getOrderCountByStatusForUser(user);
        return ResponseEntity.ok(ApiResponse.ok(statusCounts));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getOrderByStatus(@RequestParam String status, 
    @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderService.getOrderByStatus(status, pageable);
        Page<OrderResponse> orderResponses = orders.map(OrderResponse::fromOrder);
        return ResponseEntity.ok(ApiResponse.ok(orderResponses));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getOrderByUser(@AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderService.getOrderByUser(user, pageable);
        Page<OrderResponse> orderResponses = orders.map(OrderResponse::fromOrder);
        return ResponseEntity.ok(ApiResponse.ok(orderResponses));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable("orderId") String orderId) throws Exception {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.ok(OrderResponse.fromOrder(order)));
    }

    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            @AuthenticationPrincipal User user,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(String.join(", ", errorMessages)));
        }
        Order order = orderService.createOrder(user, request.getOrderDTO(), request.getAddressDTO());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(OrderResponse.fromOrder(order)));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@AuthenticationPrincipal User user,
            @PathVariable("orderId") String orderId) throws Exception {
        orderService.cancelOrder(user.getId(), orderId);
        return ResponseEntity.ok(ApiResponse.ok("Hủy đơn hàng thành công"));
    }
}
