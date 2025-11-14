package com.project.techstore.services.order;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.techstore.dtos.AddressDTO;
import com.project.techstore.dtos.order.OrderDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.exceptions.InvalidParamException;
import com.project.techstore.models.Address;
import com.project.techstore.models.Order;
import com.project.techstore.models.OrderItem;
import com.project.techstore.models.Product;
import com.project.techstore.models.ProductVariant;
import com.project.techstore.models.Promotion;
import com.project.techstore.models.User;
import com.project.techstore.repositories.AddressRepository;
import com.project.techstore.repositories.CartRepository;
import com.project.techstore.repositories.OrderItemRepository;
import com.project.techstore.repositories.OrderRepository;
import com.project.techstore.repositories.ProductRepository;
import com.project.techstore.repositories.ProductVariantRepository;
import com.project.techstore.repositories.PromotionRepository;
import com.project.techstore.services.notification.INotificationService;
import com.project.techstore.services.promotion.PromotionValidationService;
import com.project.techstore.services.promotion.UserPromotionUsageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final CartRepository cartRepository;

    private final AddressRepository addressRepository;

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    private final PromotionRepository promotionRepository;

    private final PromotionValidationService promotionValidationService;

    private final UserPromotionUsageService userPromotionUsageService;

    private final INotificationService notificationService;

    @Override
    public Page<Order> getOrderByStatus(String status, Pageable pageable) throws Exception {
        if (!status.equals(Order.Status.PENDING.name()) && !status.equals(Order.Status.CONFIRMED.name()) &&
                !status.equals(Order.Status.SHIPPING.name()) && !status.equals(Order.Status.DELIVERED.name()) &&
                !status.equals(Order.Status.CANCELLED.name()))
            throw new InvalidParamException("Status invalid param");
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Order> getOrderByUser(User user, Pageable pageable) throws Exception {
        return orderRepository.findByUserId(user.getId(), pageable);
    }

    @Override
    @Transactional
    public Order createOrder(User user, OrderDTO orderDTO, AddressDTO addressDTO) throws Exception {
        Address address = Address.builder()
                .province(addressDTO.getProvince())
                .ward(addressDTO.getWard())
                .homeAddress(addressDTO.getHomeAddress())
                .phone(addressDTO.getPhoneNumber())
                .isDeleted(false)
                .build();

        if (orderDTO.getOrderItemDTOs() == null || orderDTO.getOrderItemDTOs().isEmpty()) {
            throw new InvalidParamException("Đơn hàng phải có ít nhất một sản phẩm");
        }
        Map<String, Product> productMap = new HashMap<>();
        Map<String, ProductVariant> variantMap = new HashMap<>();

        // Kiểm tra stock trước khi tạo order
        for (var itemDTO : orderDTO.getOrderItemDTOs()) {
            if (itemDTO.getProductVariantId() != null) {
                ProductVariant variant = productVariantRepository.findById(itemDTO.getProductVariantId()).orElse(null);
                if (variant == null || variant.getStock() < itemDTO.getQuantity()) {
                    throw new InvalidParamException("Số lượng sản phẩm variant không đủ trong kho");
                }
                if (itemDTO.getProductId() == null) {
                    throw new InvalidParamException("Sản phẩm thanh toán không hợp lệ");

                }
                variantMap.put(itemDTO.getProductVariantId(), variant);
            } else if (itemDTO.getProductId() != null) {
                Product product = productRepository.findById(itemDTO.getProductId()).orElse(null);
                if (product == null || product.getStock() < itemDTO.getQuantity()) {
                    throw new InvalidParamException("Số lượng sản phẩm không đủ trong kho");
                }
                productMap.put(itemDTO.getProductId(), product);
            } else {
                throw new InvalidParamException("Sản phẩm thanh toán không hợp lệ");
            }
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
        Long finalAmount = totalAmount - discountAmount + shippingFee;
        if (finalAmount < 0) {
            finalAmount = 0L;
        }

        Order order = Order.builder()
                .totalPrice(finalAmount)
                .subtotalPrice(totalAmount)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .paymentMethod(orderDTO.getPaymentMethod())
                .status(Order.Status.PENDING.name())
                .note(orderDTO.getNote() != null ? orderDTO.getNote() : "")
                .user(user)
                .promotion(promotion)
                .address(address)
                .build();

        addressRepository.save(address);
        
        Order savedOrder = orderRepository.save(order);

        // Ghi nhận việc sử dụng promotion
        if (promotion != null) {
            userPromotionUsageService.recordUsage(user, promotion, savedOrder, discountAmount);
        }

        List<OrderItem> orderItems = new java.util.ArrayList<>();
        for (var itemDTO : orderDTO.getOrderItemDTOs()) {
            Product product = null;
            ProductVariant productVariant = null;
            if (itemDTO.getProductId() != null) {
                product = productMap.get(itemDTO.getProductId());
            }
            if (itemDTO.getProductVariantId() != null) {
                productVariant = variantMap.get(itemDTO.getProductVariantId());
            }
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .productVariant(productVariant)
                    .quantity(itemDTO.getQuantity())
                    .price(itemDTO.getPrice())
                    .build();
            orderItems.add(orderItem);

            if (productVariant != null) {
                if (productVariantRepository.decreaseStock(itemDTO.getProductVariantId(), itemDTO.getQuantity()) == 0) {
                    throw new InvalidParamException("Không đủ số lượng trong kho cho biến thể sản phẩm: " + itemDTO.getProductVariantId());
                }
            } else if (product != null) {
                if (productRepository.decreaseStock(itemDTO.getProductId(), itemDTO.getQuantity()) == 0) {
                    throw new InvalidParamException("Không đủ số lượng trong kho cho sản phẩm: " + itemDTO.getProductId());
                }
            }
        }
        
        orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);
        
        for (var itemDTO : orderDTO.getOrderItemDTOs()) {
            cartRepository.deleteCartItemByUserAndProduct(
                user.getId(), 
                itemDTO.getProductId(), 
                itemDTO.getProductVariantId()
            );
        }

        notificationService.createOrderNotification(user, savedOrder, Order.Status.PENDING.name());
        return savedOrder;
    }

    @Override
    public Order getOrderById(String orderId) throws Exception {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("This order doesn't exist"));
    }

    @Override
    @Transactional
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
        
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItem item : order.getOrderItems()) {
                if (item.getProductVariant() != null) {
                    productVariantRepository.increaseStock(item.getProductVariant().getId(), item.getQuantity());
                } else if (item.getProduct() != null) {
                    productRepository.increaseStock(item.getProduct().getId(), item.getQuantity());
                }
            }
        }
        
        order.setStatus(Order.Status.CANCELLED.name());
        orderRepository.save(order);

        // Tạo thông báo hủy đơn hàng
        notificationService.createOrderNotification(order.getUser(), order, Order.Status.CANCELLED.name());
    }

    @Override
    @Transactional
    public void cancelOrder(String id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng"));
        if (order.getPromotion() != null) {
            userPromotionUsageService.markAsRefunded(order.getUser(), order.getPromotion(), order.getId());
        }
        
        // Hoàn trả stock cho tất cả sản phẩm trong đơn hàng
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItem item : order.getOrderItems()) {
                if (item.getProductVariant() != null) {
                    productVariantRepository.increaseStock(item.getProductVariant().getId(), item.getQuantity());
                } else if (item.getProduct() != null) {
                    productRepository.increaseStock(item.getProduct().getId(), item.getQuantity());
                }
            }
        }
        
        order.setStatus(Order.Status.CANCELLED.name());
        orderRepository.save(order);

        // Tạo thông báo hủy đơn hàng
        notificationService.createOrderNotification(order.getUser(), order, Order.Status.CANCELLED.name());
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

        // Tạo thông báo xác nhận đơn hàng
        notificationService.createOrderNotification(order.getUser(), order, Order.Status.CONFIRMED.name());
    }

    @Override
    public void shipOrder(String id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng"));
        order.setStatus(Order.Status.SHIPPING.name());
        orderRepository.save(order);

        // Tạo thông báo đơn hàng đang giao
        notificationService.createOrderNotification(order.getUser(), order, Order.Status.SHIPPING.name());
    }

    @Override
    public void deliveredOrder(String id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng"));
        order.setStatus(Order.Status.DELIVERED.name());
        orderRepository.save(order);

        // Tạo thông báo đơn hàng đã giao thành công
        notificationService.createOrderNotification(order.getUser(), order, Order.Status.DELIVERED.name());
    }

    @Override
    public List<Order> getRecentOrders(int limit) throws Exception {
        List<Order> recentOrders = orderRepository.findRecentOrders(limit);
        return recentOrders;
    }

    @Override
    public Page<Order> searchOrders(String status, String customerName, LocalDateTime startDate,
            LocalDateTime endDate, Pageable pageable) throws Exception {
        return orderRepository.searchOrders(status, customerName, startDate, endDate, pageable);
    }

    @Override
    public java.util.Map<String, Long> getOrderStatistics() throws Exception {
        java.util.Map<String, Long> statistics = new java.util.HashMap<>();
        statistics.put("PENDING", orderRepository.countByStatus(Order.Status.PENDING.name()));
        statistics.put("CONFIRMED", orderRepository.countByStatus(Order.Status.CONFIRMED.name()));
        statistics.put("SHIPPING", orderRepository.countByStatus(Order.Status.SHIPPING.name()));
        statistics.put("DELIVERED", orderRepository.countByStatus(Order.Status.DELIVERED.name()));
        statistics.put("CANCELLED", orderRepository.countByStatus(Order.Status.CANCELLED.name()));
        return statistics;
    }

    @Override
    public Map<String, Long> getOrderCountByStatusForUser(User user) throws Exception {
        List<Object[]> results = orderRepository.getOrderCountByStatusForUser(user.getId());
        Map<String, Long> statusCounts = new HashMap<>();
        
        statusCounts.put("PENDING", 0L);
        statusCounts.put("CONFIRMED", 0L);
        statusCounts.put("SHIPPING", 0L);
        statusCounts.put("DELIVERED", 0L);
        statusCounts.put("CANCELLED", 0L);
        
        for (Object[] row : results) {
            String status = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            statusCounts.put(status, count);
        }
        
        return statusCounts;
    }

}
