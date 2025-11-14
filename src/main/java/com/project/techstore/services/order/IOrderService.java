package com.project.techstore.services.order;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.order.OrderDTO;
import com.project.techstore.models.Order;
import com.project.techstore.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    Page<Order> getOrderByStatus(String status, Pageable pageable) throws Exception;

    Page<Order> getOrderByUser(User user, Pageable pageable) throws Exception;

    Order getOrderById(String orderId) throws Exception;

    Order createOrder(User user, OrderDTO orderDTO, AddressDTO addressDTO) throws Exception;

    // Customer actions
    void cancelOrder(String userId, String id) throws Exception;
    
    Map<String, Long> getOrderCountByStatusForUser(User user) throws Exception;
    
    // Admin actions
    Page<Order> searchOrders(String status, String customerName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) throws Exception;
    void cancelOrder(String id) throws Exception;
    void confirmOrder(String id) throws Exception;
    void shipOrder(String id) throws Exception;
    void deliveredOrder(String id) throws Exception;
    List<Order> getRecentOrders(int limit) throws Exception;
    Map<String, Long> getOrderStatistics() throws Exception;
}
