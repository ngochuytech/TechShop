package com.project.techstore.services;

import com.project.techstore.dtos.OrderItemDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Order;
import com.project.techstore.models.OrderItem;
import com.project.techstore.models.Product;
import com.project.techstore.repositories.OrderItemRepository;
import com.project.techstore.repositories.OrderRepository;
import com.project.techstore.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService implements IOrderItemService{
    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    @Override
    public OrderItem createOrderItem(OrderItemDTO orderItemDTO) throws Exception {
        Product product = productRepository.findById(orderItemDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product doesn't exist"));
        Order order = orderRepository.findById(orderItemDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Order doesn't exist"));
        OrderItem orderItem = OrderItem.builder()
                .quantity(orderItemDTO.getQuantity())
                .price(orderItemDTO.getPrice())
                .order(order)
                .product(product)
                .build();
        return orderItemRepository.save(orderItem);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrder(String orderId) throws Exception {
        if(!orderRepository.existsById(orderId))
            throw new DataNotFoundException("This order doesn't exist");
        return orderItemRepository.findByOrderId(orderId);
    }
}
