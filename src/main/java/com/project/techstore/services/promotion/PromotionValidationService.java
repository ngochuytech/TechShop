package com.project.techstore.services.promotion;

import com.project.techstore.exceptions.InvalidParamException;
import com.project.techstore.models.Promotion;
import com.project.techstore.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromotionValidationService {
    private final UserPromotionUsageService userPromotionUsageService;

    /**
     * Validate xem user có thể sử dụng promotion này không
     */
    public void validatePromotionForUser(User user, Promotion promotion) throws InvalidParamException {
        // 1. Kiểm tra promotion còn active không
        if (!promotion.getIsActive()) {
            throw new InvalidParamException("Mã khuyến mãi không còn hiệu lực");
        }

        // 2. Kiểm tra thời gian
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartDate())) {
            throw new InvalidParamException("Mã khuyến mãi chưa đến thời gian sử dụng");
        }
        if (now.isAfter(promotion.getEndTime())) {
            throw new InvalidParamException("Mã khuyến mãi đã hết hạn");
        }

        // 3. Kiểm tra promotion chỉ dành cho khách hàng mới
        if (promotion.getIsForNewCustomer() != null && promotion.getIsForNewCustomer()) {
            long userUsageCount = userPromotionUsageService.countUserUsage(user, promotion);
            if (userUsageCount > 0) {
                throw new InvalidParamException("Bạn đã sử dụng mã khuyến mãi này rồi");
            }
        }

        // 4. Kiểm tra số lần sử dụng của user với promotion này
        if (promotion.getUsageLimitPerUser() != null && promotion.getUsageLimitPerUser() > 0) {
            long userUsageCount = userPromotionUsageService.countUserUsage(user, promotion);
            
            if (userUsageCount >= promotion.getUsageLimitPerUser()) {
                throw new InvalidParamException(
                    String.format("Bạn đã sử dụng hết %d lần cho mã này", promotion.getUsageLimitPerUser())
                );
            }
        }

        // 5. Kiểm tra tổng số lần sử dụng của promotion
        if (promotion.getTotalUsageLimit() != null && promotion.getTotalUsageLimit() > 0) {
            long totalUsageCount = userPromotionUsageService.countTotalUsage(promotion);
            
            if (totalUsageCount >= promotion.getTotalUsageLimit()) {
                throw new InvalidParamException("Mã khuyến mãi đã hết lượt sử dụng");
            }
        }
    }

    /**
     * Validate giá trị đơn hàng có đủ điều kiện sử dụng promotion không
     */
    public void validateOrderValueForPromotion(Long orderValue, Promotion promotion) throws InvalidParamException {
        if (promotion.getMinOrderValue() != null && orderValue < promotion.getMinOrderValue()) {
            throw new InvalidParamException(
                String.format("Đơn hàng tối thiểu %,d VNĐ để sử dụng mã này", promotion.getMinOrderValue())
            );
        }
    }

    /**
     * Tính toán số tiền được giảm
     */
    public Long calculateDiscountAmount(Long orderValue, Promotion promotion) {
        Long discountAmount = 0L;
        
        switch (promotion.getDiscountType()) {
            case "PERCENTAGE":
                discountAmount = (orderValue * promotion.getDiscountValue()) / 100;
                // Áp dụng max discount nếu có
                if (promotion.getMaxDiscount() != null && discountAmount > promotion.getMaxDiscount()) {
                    discountAmount = promotion.getMaxDiscount();
                }
                break;
                
            case "FIXED":
                discountAmount = promotion.getDiscountValue();
                // Không thể giảm quá giá trị đơn hàng
                if (discountAmount > orderValue) {
                    discountAmount = orderValue;
                }
                break;
                
            case "SHIPPING":
                // Giảm giá ship, có thể tối đa bằng discountValue
                discountAmount = promotion.getDiscountValue();
                break;
                
            default:
                discountAmount = 0L;
        }
        
        return discountAmount;
    }

    /**
     * Tính toán số tiền được giảm với shipping fee
     * Dùng cho trường hợp promotion type = SHIPPING
     */
    public Long calculateDiscountAmount(Long orderValue, Promotion promotion, Long shippingFee) {
        Long discountAmount = 0L;
        
        switch (promotion.getDiscountType()) {
            case "PERCENTAGE":
                discountAmount = (orderValue * promotion.getDiscountValue()) / 100;
                // Áp dụng max discount nếu có
                if (promotion.getMaxDiscount() != null && discountAmount > promotion.getMaxDiscount()) {
                    discountAmount = promotion.getMaxDiscount();
                }
                break;
                
            case "FIXED":
                discountAmount = promotion.getDiscountValue();
                // Không thể giảm quá giá trị đơn hàng
                if (discountAmount > orderValue) {
                    discountAmount = orderValue;
                }
                break;
                
            case "SHIPPING":
                // Giảm giá ship, tối đa bằng shippingFee
                discountAmount = promotion.getDiscountValue();
                if (shippingFee != null && discountAmount > shippingFee) {
                    discountAmount = shippingFee;
                }
                break;
                
            default:
                discountAmount = 0L;
        }
        
        return discountAmount;
    }
}
