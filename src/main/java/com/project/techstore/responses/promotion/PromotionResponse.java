package com.project.techstore.responses.promotion;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionResponse {
    private String id;
    private String title;
    private String code;
    private String description;
    private String discountType;
    private Long discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endTime;
    private Long minOrderValue;
    private Long maxDiscount;
    private Boolean isActive;
    private Integer usageLimitPerUser;
    private Boolean isForNewCustomer;
    private Integer totalUsageLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PromotionResponse fromEntity(com.project.techstore.models.Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .title(promotion.getTitle())
                .code(promotion.getCode())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .startDate(promotion.getStartDate())
                .endTime(promotion.getEndTime())
                .minOrderValue(promotion.getMinOrderValue())
                .maxDiscount(promotion.getMaxDiscount())
                .isActive(promotion.getIsActive())
                .usageLimitPerUser(promotion.getUsageLimitPerUser())
                .isForNewCustomer(promotion.getIsForNewCustomer())
                .totalUsageLimit(promotion.getTotalUsageLimit())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .build();
    }
}
