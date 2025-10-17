package com.project.techstore.services.order;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.order.OrderDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.exceptions.InvalidParamException;
import com.project.techstore.models.*;
import com.project.techstore.repositories.AddressRepository;
import com.project.techstore.repositories.OrderItemRepository;
import com.project.techstore.repositories.OrderRepository;
import com.project.techstore.repositories.ProductRepository;
import com.project.techstore.repositories.ProductVariantRepository;
import com.project.techstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    @Override
    public List<Order> getOrderByStatus(String status) throws Exception {
        if(!status.equals(OrderStatus.PENDING) && !status.equals(OrderStatus.PROCESSING) &&
                !status.equals(OrderStatus.SHIPPED) && !status.equals(OrderStatus.DELIVERED) &&
                !status.equals(OrderStatus.CANCELLED))
            throw new InvalidParamException("Status invalid param");
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> getOrderByUser(String userId) throws Exception {
        if(!userRepository.existsById(userId))
            throw new DataNotFoundException("This user doesn't exist");
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Order createOrder(String userId, OrderDTO orderDTO, AddressDTO addressDTO) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("This user doesn't exist"));
        Address address = Address.builder()
            .province(addressDTO.getProvince())
            .ward(addressDTO.getWard())
            .homeAddress(addressDTO.getHomeAddress())
            .build();

        if (orderDTO.getOrderItemDTOs() == null || orderDTO.getOrderItemDTOs().isEmpty()) {
            throw new InvalidParamException("Order must have at least one item");
        }
        long totalAmount = orderDTO.getOrderItemDTOs().stream()
            .mapToLong(item -> {
                int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                long price = item.getPrice() != null ? item.getPrice() : 0L;
                return quantity * price;
            })
            .sum();

        Order order = Order.builder()
            .totalPrice(totalAmount)
            .paymentMethod(orderDTO.getPaymentMethod())
            .status(OrderStatus.PENDING)
            .note(orderDTO.getNote() != null ? orderDTO.getNote() : "")
            .user(user)
            .address(address)
            .build();
        addressRepository.save(address);
        Order savedOrder = orderRepository.save(order);

        if (orderDTO.getOrderItemDTOs() != null) {
            List<OrderItem> orderItems = new java.util.ArrayList<>();
            for (var itemDTO : orderDTO.getOrderItemDTOs()) {
                Product product = null;
                ProductVariant productVariant = null;
                if (itemDTO.getProductId() != null) {
                    product = productRepository.findById(itemDTO.getProductId()).orElse(null);
                }
                if (itemDTO.getProductVariantId() != null) {
                    productVariant = productVariantRepository.findById(itemDTO.getProductVariantId()).orElse(null);
                }
                else if(itemDTO.getProductId() == null) {
                    throw new InvalidParamException("Sản phẩm thanh toán không hợp lệ");
                }
                OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .productVariant(productVariant)
                    .quantity(itemDTO.getQuantity())
                    .price(itemDTO.getPrice())
                    .build();
                orderItems.add(orderItem);
            }
            orderItemRepository.saveAll(orderItems);
        }
        return savedOrder;
    }

    @Override
    @Transactional
    public Order updateOrder(String id, OrderDTO orderDTO, AddressDTO addressDTO) throws Exception {
        Order orderExisting = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("This order doesn't exist"));
        orderExisting.setPaymentMethod(orderDTO.getPaymentMethod());
        if(addressDTO != null){
            Address existingAddress = orderExisting.getAddress();
            existingAddress.setProvince(addressDTO.getProvince());
            existingAddress.setWard(addressDTO.getWard());
            existingAddress.setHomeAddress(addressDTO.getHomeAddress());
            existingAddress.setSuggestedName(addressDTO.getSuggestedName());
        }
        return orderRepository.save(orderExisting);
    }

    @Override
    public Order updateStatusOrder(String id, String newStatus) throws Exception {
        if(!newStatus.equals(OrderStatus.PENDING) && !newStatus.equals(OrderStatus.PROCESSING) &&
                !newStatus.equals(OrderStatus.SHIPPED) && !newStatus.equals(OrderStatus.DELIVERED) &&
                !newStatus.equals(OrderStatus.CANCELLED))
            throw new InvalidParamException("Status invalid param");
        Order orderExisting = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("This order doesn't exist"));
        orderExisting.setStatus(newStatus);
        return orderRepository.save(orderExisting);
    }

    @Override
    public Order getOrderById(String orderId) throws Exception {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("This order doesn't exist"));
    }
}
