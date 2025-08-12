package com.project.techstore.services;

import com.project.techstore.dtos.OrderItemDTO;
import com.project.techstore.models.OrderItem;

import java.util.List;

public interface IOrderItemService {
    OrderItem createOrderItem(OrderItemDTO orderItemDTO) throws Exception;

    List<OrderItem> getOrderItemsByOrder(String orderId) throws Exception;
}
