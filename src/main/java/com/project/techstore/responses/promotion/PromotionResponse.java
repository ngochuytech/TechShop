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
    private String type;
    private Long discount;
    private LocalDateTime expiry;
    private Long minOrder;
    private Long maxDiscount;

    public static PromotionResponse fromEntity(com.project.techstore.models.Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .title(promotion.getTitle())
                .code(promotion.getCode())
                .description(promotion.getDescription())
                .type(promotion.getDiscountType())
                .discount(promotion.getDiscountValue())
                .expiry(promotion.getEndTime())
                .minOrder(promotion.getMinOrderValue())
                .maxDiscount(promotion.getMaxDiscount())
                .build();
    }
}
