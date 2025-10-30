package com.project.techstore.services.order;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.order.OrderDTO;
import com.project.techstore.models.Order;

import java.util.List;

public interface IOrderService {
    List<Order> getOrderByStatus(String status) throws Exception;

    List<Order> getOrderByUser(String userId) throws Exception;

    Order getOrderById(String orderId) throws Exception;

    Order createOrder(String userId, OrderDTO orderDTO, AddressDTO addressDTO) throws Exception;

    Order updateOrder(String id, OrderDTO orderDTO, AddressDTO addressDTO) throws Exception;

    void cancelOrder(String id) throws Exception;
    void confirmOrder(String id) throws Exception;
    void shipOrder(String id) throws Exception;
    void deliveredOrder(String id) throws Exception;
}
