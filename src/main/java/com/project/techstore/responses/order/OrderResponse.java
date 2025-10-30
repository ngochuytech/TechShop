package com.project.techstore.responses.order;

import java.util.List;

import com.project.techstore.models.Media;
import com.project.techstore.models.Order;

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
                .phone(order.getUser().getPhone())
                .build();
        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
            .map((com.project.techstore.models.OrderItem orderItem) -> OrderItemResponse.builder()
                    .id(orderItem.getId())
                    .productId(orderItem.getProduct().getId())
                    .productName(orderItem.getProduct().getName())
                    .productImage(orderItem.getProduct().getMediaList().stream()
                        .filter(img -> img.getIsPrimary() == true)
                        .findFirst()
                        .map(Media::getMediaPath)
                        .orElse(null))
                    .quantity(Long.valueOf(orderItem.getQuantity()))
                    .price(orderItem.getPrice().doubleValue())
                    .build())
            .toList();
        return OrderResponse.builder()
                .id(order.getId())
                .shippingAddress(shippingAddress)
                .totalPrice(order.getTotalPrice())
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
