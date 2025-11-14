package com.project.techstore.models;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "total_price",nullable = false)
    private Long totalPrice;

    @Column(name = "subtotal_price")
    private Long subtotalPrice;

    @Column(name = "discount_amount")
    private Long discountAmount;

    @Column(name = "shipping_fee")
    private Long shippingFee;

    @Column(length = 50, nullable = false)
    private String paymentMethod;

    @Column(length = 50, nullable = false)
    private String status;

    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    public enum Status {
        PENDING,
        CONFIRMED,
        SHIPPING,
        DELIVERED,
        CANCELLED
    }
}
