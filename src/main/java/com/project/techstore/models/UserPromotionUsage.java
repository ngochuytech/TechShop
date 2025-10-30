package com.project.techstore.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_promotion_usage", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "promotion_id", "order_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPromotionUsage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "discount_amount")
    private Long discountAmount;

    @Column(name = "is_refunded")
    private Boolean isRefunded;
}
