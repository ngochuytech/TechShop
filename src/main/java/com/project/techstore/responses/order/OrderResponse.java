package com.project.techstore.responses.order;

import java.util.List;

import com.project.techstore.models.Media;
import com.project.techstore.models.Order;
import com.project.techstore.models.OrderItem;
import com.project.techstore.models.Product;
import com.project.techstore.models.ProductVariant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private String id;
    private ShippingAddress shippingAddress;
    private Long totalPrice;
    private Long subtotalPrice;
    private Long discountAmount;
    private Long shippingFee;
    private String paymentMethod;
    private String status;
    private String note;
    private List<OrderItemResponse> orderItems;
    private String createdAt;
    private String updatedAt;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    static class ShippingAddress {
        private String province;
        private String ward;
        private String homeAddress;
        
        private String fullName;
        private String phone;
        private String email;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    static class OrderItemResponse {
        private String id;
        private String productId;
        private String productName;
        private String productImage;
        private Long quantity;
        private Double price;
    }

    public static OrderResponse fromOrder(Order order){
        ShippingAddress shippingAddress = ShippingAddress.builder()
                .province(order.getAddress().getProvince())
                .ward(order.getAddress().getWard())
                .homeAddress(order.getAddress().getHomeAddress())
                .fullName(order.getUser().getFullName())
                .phone(order.getAddress().getPhone())
                .email(order.getUser().getEmail())
                .build();
        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
            .map((OrderItem orderItem) -> {
                // Xác định sản phẩm (có thể là Product hoặc ProductVariant)
                Product product = orderItem.getProduct();
                ProductVariant variant = orderItem.getProductVariant();
                String productId = null;
                String productName = null;
                String productImage = null;
                
                if (variant != null) {
                    // Trường hợp có ProductVariant
                    product = variant.getProduct();
                    productId = variant.getId();
                    productName = product != null ? product.getName() + " - " + variant.getColor() : "N/A";
                    
                    if (variant.getImage() != null && !variant.getImage().isEmpty()) {
                        productImage = variant.getImage();
                    } else if (product != null && product.getMediaList() != null) {
                        productImage = product.getMediaList().stream()
                            .filter(img -> img.getIsPrimary() == true)
                            .findFirst()
                            .map(Media::getMediaPath)
                            .orElse(null);
                    }
                } else if (product != null) {
                    // Trường hợp chỉ có Product
                    productId = product.getId();
                    productName = product.getName();
                    productImage = product.getMediaList().stream()
                        .filter(img -> img.getIsPrimary() == true)
                        .findFirst()
                        .map(Media::getMediaPath)
                        .orElse(null);
                }
                
                return OrderItemResponse.builder()
                    .id(orderItem.getId())
                    .productId(productId)
                    .productName(productName)
                    .productImage(productImage)
                    .quantity(Long.valueOf(orderItem.getQuantity()))
                    .price(orderItem.getPrice().doubleValue())
                    .build();
            })
            .toList();
        return OrderResponse.builder()
                .id(order.getId())
                .shippingAddress(shippingAddress)
                .totalPrice(order.getTotalPrice())
                .subtotalPrice(order.getSubtotalPrice() != null ? order.getSubtotalPrice() : 0L)
                .discountAmount(order.getDiscountAmount() != null ? order.getDiscountAmount() : 0L)
                .shippingFee(order.getShippingFee() != null ? order.getShippingFee() : 0L)
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .note(order.getNote())
                .orderItems(orderItemResponses)
                .createdAt(order.getCreatedAt().toString())
                .updatedAt(order.getUpdatedAt().toString())
                .build();
    }
}
