package com.project.techstore.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Promotions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Promotion extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    private String description;

    @Column(name = "discount_type", length = 50, nullable = false)
    private String discountType;

    @Column(name = "discount_value", nullable = false)
    private Long discountValue;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "min_order_value")
    private Long minOrderValue;

    @Column(name = "max_discount")
    private Long maxDiscount;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser; // Giới hạn số lần sử dụng mỗi user (null = không giới hạn)

    @Column(name = "is_for_new_customer")
    private Boolean isForNewCustomer; // Chỉ dành cho khách hàng mới (chưa có đơn hàng nào)

    @Column(name = "total_usage_limit")
    private Integer totalUsageLimit; // Tổng số lần promotion có thể được sử dụng (null = không giới hạn)

    public enum DiscountType {
        PERCENTAGE,
        FIXED,
        SHIPPING
    }
}
