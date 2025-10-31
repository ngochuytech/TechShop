package com.project.techstore.controllers.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.dtos.order.CreateOrderRequest;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.order.IOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final IOrderService orderService;

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelOrder(
            @PathVariable("orderId") String orderId) throws Exception {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.ok("Hủy đơn hàng thành công"));
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<?>> confirmOrder(
            @PathVariable("orderId") String orderId) throws Exception {
        orderService.confirmOrder(orderId);
        return ResponseEntity.ok(ApiResponse.ok("Xác nhận đơn hàng thành công"));
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<ApiResponse<?>> shipOrder(
            @PathVariable("orderId") String orderId) throws Exception {
        orderService.shipOrder(orderId);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái đơn hàng thành đang giao hàng thành công"));
    }

    @PutMapping("/{orderId}/delivered")
    public ResponseEntity<ApiResponse<?>> deliveredOrder(
            @PathVariable("orderId") String orderId) throws Exception {
        orderService.deliveredOrder(orderId);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái đơn hàng thành đã giao hàng thành công"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateOrder(
            @PathVariable("id") String id,
            @RequestBody @Valid CreateOrderRequest request,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
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
}
