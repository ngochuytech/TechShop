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
import com.project.techstore.repositories.PromotionRepository;
import com.project.techstore.services.promotion.PromotionValidationService;
import com.project.techstore.services.promotion.UserPromotionUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    private final PromotionRepository promotionRepository;

    private final PromotionValidationService promotionValidationService;

    private final UserPromotionUsageService userPromotionUsageService;

    @Override
    public List<Order> getOrderByStatus(String status) throws Exception {
        if (!status.equals(Order.Status.PENDING.name()) && !status.equals(Order.Status.CONFIRMED.name()) &&
                !status.equals(Order.Status.SHIPPING.name()) && !status.equals(Order.Status.DELIVERED.name()) &&
                !status.equals(Order.Status.CANCELLED.name()))
            throw new InvalidParamException("Status invalid param");
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> getOrderByUser(String userId) throws Exception {
        if (!userRepository.existsById(userId))
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
                .phone(addressDTO.getPhoneNumber())
                .isDeleted(false)
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

        Long shippingFee = orderDTO.getShippingFee();

        // Xử lý promotion nếu có
        Promotion promotion = null;
        Long discountAmount = 0L;

        if (orderDTO.getPromotionCode() != null && !orderDTO.getPromotionCode().trim().isEmpty()) {
            promotion = promotionRepository.findByCode(orderDTO.getPromotionCode())
                    .orElseThrow(() -> new DataNotFoundException("Mã khuyến mãi không tồn tại"));

            // Validate promotion
            promotionValidationService.validatePromotionForUser(user, promotion);
            promotionValidationService.validateOrderValueForPromotion(totalAmount, promotion);

            // Tính discount với shippingFee nếu là SHIPPING promotion
            discountAmount = promotionValidationService.calculateDiscountAmount(totalAmount, promotion, shippingFee);
        }

        // Tính tổng tiền sau khi giảm giá + phí ship
        long finalAmount = totalAmount - discountAmount + shippingFee;
        if (finalAmount < 0) {
            finalAmount = 0L;
        }

        Order order = Order.builder()
                .totalPrice(finalAmount)
                .paymentMethod(orderDTO.getPaymentMethod())
                .status(Order.Status.PENDING.name())
                .note(orderDTO.getNote() != null ? orderDTO.getNote() : "")
                .user(user)
                .promotion(promotion)
                .address(address)
                .build();
        if (orderDTO.getPromotionCode() != null
                && promotion.getDiscountType().equals(Promotion.DiscountType.SHIPPING.name())) {
            order.setShippingFee(Math.max(shippingFee - promotion.getDiscountValue(), 0));
        } else {
            order.setShippingFee(shippingFee);
        }
        addressRepository.save(address);
        Order savedOrder = orderRepository.save(order);

        // Ghi nhận việc sử dụng promotion
        if (promotion != null) {
            userPromotionUsageService.recordUsage(user, promotion, savedOrder, discountAmount);
        }

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
                } else if (itemDTO.getProductId() == null) {
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
            savedOrder.setOrderItems(orderItems);
        }
        return savedOrder;
    }

    @Override
    @Transactional
    public Order updateOrder(String id, OrderDTO orderDTO, AddressDTO addressDTO) throws Exception {
        Order orderExisting = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("This order doesn't exist"));
        orderExisting.setPaymentMethod(orderDTO.getPaymentMethod());

        // Cập nhật shipping fee nếu có
        if (orderDTO.getShippingFee() != null) {
            orderExisting.setShippingFee(orderDTO.getShippingFee());
        }

        if (addressDTO != null) {
            Address existingAddress = orderExisting.getAddress();
            existingAddress.setProvince(addressDTO.getProvince());
            existingAddress.setWard(addressDTO.getWard());
            existingAddress.setHomeAddress(addressDTO.getHomeAddress());
            existingAddress.setSuggestedName(addressDTO.getSuggestedName());
        }
        return orderRepository.save(orderExisting);
    }

    @Override
    public Order getOrderById(String orderId) throws Exception {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("This order doesn't exist"));
    }

    @Override
    public void cancelOrder(String userId, String id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng"));
        if (!order.getUser().getId().equals(userId)) {
            throw new InvalidParamException("Bạn không có quyền hủy đơn hàng này");
        }
        if (!order.getStatus().equals(Order.Status.PENDING.name())) {
            throw new InvalidParamException("Chỉ có thể hủy đơn hàng ở trạng thái đang chờ xử lý");
        }
        if (order.getPromotion() != null) {
            userPromotionUsageService.markAsRefunded(order.getUser(), order.getPromotion(), order.getId());
        }
        order.setStatus(Order.Status.CANCELLED.name());
        orderRepository.save(order);
    }

    @Override
    public void cancelOrder(String id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng"));
        if (order.getPromotion() != null) {
            userPromotionUsageService.markAsRefunded(order.getUser(), order.getPromotion(), order.getId());
        }
        order.setStatus(Order.Status.CANCELLED.name());
        orderRepository.save(order);
    }

    @Override
    public void confirmOrder(String id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng"));
        if (!order.getStatus().equals(Order.Status.PENDING.name())) {
            throw new InvalidParamException("Chỉ có thể xác nhận đơn hàng ở trạng thái đang chờ xử lý");
        }
        order.setStatus(Order.Status.CONFIRMED.name());
        orderRepository.save(order);
    }

    @Override
    public void shipOrder(String id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng"));
        order.setStatus(Order.Status.SHIPPING.name());
        orderRepository.save(order);
    }

    @Override
    public void deliveredOrder(String id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng"));
        order.setStatus(Order.Status.DELIVERED.name());
        orderRepository.save(order);
    }
}
