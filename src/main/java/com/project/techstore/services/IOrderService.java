package com.project.techstore.services;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.OrderDTO;
import com.project.techstore.models.Order;

import java.util.List;

public interface IOrderService {
    List<Order> getOrderByStatus(String status) throws Exception;

    List<Order> getOrderByUser(String userId) throws Exception;

    Order createOrder(OrderDTO orderDTO, AddressDTO addressDTO) throws Exception;

    Order udpateOrder(String id, OrderDTO orderDTO, AddressDTO addressDTO) throws Exception;

    Order updateStatusOrder(String id, String newStatus) throws Exception;
}
