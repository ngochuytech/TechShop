package com.project.techstore.services.promotion;

import com.project.techstore.models.Order;
import com.project.techstore.models.Promotion;
import com.project.techstore.models.User;
import com.project.techstore.models.UserPromotionUsage;
import com.project.techstore.repositories.UserPromotionUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPromotionUsageService {
    private final UserPromotionUsageRepository userPromotionUsageRepository;

    //Ghi nhận việc sử dụng promotion
    @Transactional
    public UserPromotionUsage recordUsage(User user, Promotion promotion, Order order, Long discountAmount) {
        UserPromotionUsage usage = UserPromotionUsage.builder()
                .user(user)
                .promotion(promotion)
                .order(order)
                .usedAt(LocalDateTime.now())
                .discountAmount(discountAmount)
                .isRefunded(false)
                .build();
        
        return userPromotionUsageRepository.save(usage);
    }

    // Đếm số lần user đã sử dụng promotion (không bao gồm refunded)
    public long countUserUsage(User user, Promotion promotion) {
        return userPromotionUsageRepository.countByUserAndPromotionAndIsRefundedFalse(user, promotion);
    }

    // Đếm tổng số lần promotion đã được sử dụng (không bao gồm refunded)
    public long countTotalUsage(Promotion promotion) {
        return userPromotionUsageRepository.countByPromotionAndIsRefundedFalse(promotion);
    }

    // Kiểm tra user đã sử dụng promotion này chưa
    public boolean hasUserUsedPromotion(User user, Promotion promotion) {
        return userPromotionUsageRepository.existsByUserAndPromotion(user, promotion);
    }

    // Lấy danh sách promotion usage của user
    public List<UserPromotionUsage> getUserPromotionUsages(User user) {
        return userPromotionUsageRepository.findByUser(user);
    }

    // Lấy danh sách promotion usage của promotion
    public List<UserPromotionUsage> getPromotionUsages(Promotion promotion) {
        return userPromotionUsageRepository.findByPromotion(promotion);
    }

    // Đánh dấu usage là refunded khi đơn hàng bị hủy/hoàn tiền
    @Transactional
    public void markAsRefunded(User user, Promotion promotion, String orderId) {
        UserPromotionUsage usage = userPromotionUsageRepository
                .findByUserAndPromotionAndOrderId(user, promotion, orderId);
        
        if (usage != null) {
            usage.setIsRefunded(true);
            userPromotionUsageRepository.save(usage);
        }
    }
}
