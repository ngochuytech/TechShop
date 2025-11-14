package com.project.techstore.controllers.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.Order;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.order.OrderResponse;
import com.project.techstore.services.order.IOrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final IOrderService orderService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) throws Exception {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> pagedOrders = orderService.searchOrders(status, customerName, startDate, endDate, pageable);
        Page<OrderResponse> orderResponses = pagedOrders.map(OrderResponse::fromOrder);
        return ResponseEntity.ok(ApiResponse.ok(orderResponses));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable("orderId") String orderId) throws Exception {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.ok(OrderResponse.fromOrder(order)));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<?>> getRecentOrders(
            @RequestParam(value = "limit", defaultValue = "5") int limit) throws Exception {
        List<Order> orders = orderService.getRecentOrders(limit);
        List<OrderResponse> orderResponses = orders.stream()
                .map(OrderResponse::fromOrder)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(orderResponses));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<?>> getOrderStatistics() throws Exception {
        Map<String, Long> statistics = orderService.getOrderStatistics();
        return ResponseEntity.ok(ApiResponse.ok(statistics));
    }

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
}
