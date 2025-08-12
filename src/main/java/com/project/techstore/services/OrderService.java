package com.project.techstore.services;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.OrderDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.exceptions.InvalidParamException;
import com.project.techstore.models.*;
import com.project.techstore.repositories.AddressRepository;
import com.project.techstore.repositories.OrderRepository;
import com.project.techstore.repositories.PromotionRepository;
import com.project.techstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final PromotionRepository promotionRepository;

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
    public Order createOrder(OrderDTO orderDTO, AddressDTO addressDTO) throws Exception {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("This user doesn't exist"));
        Address address = addressRepository.findById(orderDTO.getAddressId())
                .orElseGet(() -> {
                    Address newAddress = Address.builder()
                            .province(addressDTO.getProvince())
                            .district(addressDTO.getDistrict())
                            .ward(addressDTO.getWard())
                            .homeAddress(addressDTO.getHomeAddress())
                            .suggestedName(addressDTO.getSuggestedName())
                            .build();
                    return addressRepository.save(newAddress);
                });
        Promotion promotion = promotionRepository.findById(orderDTO.getPromotionId())
                .orElse(null);
        Order order = Order.builder()
                .totalPrice(orderDTO.getTotalPrice())
                .discountPrice(orderDTO.getDiscountPrice())
                .paymentMethod(orderDTO.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .user(user)
                .address(address)
                .build();
        if(promotion != null)
            order.setPromotion(promotion);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order udpateOrder(String id, OrderDTO orderDTO, AddressDTO addressDTO) throws Exception {
        Order orderExisting = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("This order doesn't exist"));
        orderExisting.setPaymentMethod(orderDTO.getPaymentMethod());
        if(!orderDTO.getStatus().equals(OrderStatus.PENDING) && !orderDTO.getStatus().equals(OrderStatus.PROCESSING) &&
                !orderDTO.getStatus().equals(OrderStatus.SHIPPED) && !orderDTO.getStatus().equals(OrderStatus.DELIVERED) &&
                !orderDTO.getStatus().equals(OrderStatus.CANCELLED))
            throw new InvalidParamException("Status invalid param");
        orderExisting.setStatus(orderDTO.getStatus());
        if(addressDTO != null){
            Address existingAddress = orderExisting.getAddress();
            existingAddress.setProvince(addressDTO.getProvince());
            existingAddress.setDistrict(addressDTO.getDistrict());
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
}
