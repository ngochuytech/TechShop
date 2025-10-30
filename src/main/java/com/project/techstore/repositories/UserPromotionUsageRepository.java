package com.project.techstore.repositories;

import com.project.techstore.models.Promotion;
import com.project.techstore.models.User;
import com.project.techstore.models.UserPromotionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPromotionUsageRepository extends JpaRepository<UserPromotionUsage, String> {
    
    /**
     * Đếm số lần user đã sử dụng một promotion cụ thể
     */
    long countByUserAndPromotion(User user, Promotion promotion);
    
    /**
     * Đếm số lần user đã sử dụng promotion và chưa bị refund
     */
    long countByUserAndPromotionAndIsRefundedFalse(User user, Promotion promotion);
    
    /**
     * Đếm tổng số lần promotion đã được sử dụng
     */
    long countByPromotion(Promotion promotion);
    
    /**
     * Đếm tổng số lần promotion đã được sử dụng và chưa bị refund
     */
    long countByPromotionAndIsRefundedFalse(Promotion promotion);
    
    /**
     * Kiểm tra user đã sử dụng promotion này chưa
     */
    boolean existsByUserAndPromotion(User user, Promotion promotion);
    
    /**
     * Lấy danh sách promotion usage của user
     */
    List<UserPromotionUsage> findByUser(User user);
    
    /**
     * Lấy danh sách promotion usage của một promotion
     */
    List<UserPromotionUsage> findByPromotion(Promotion promotion);
    
    /**
     * Tìm usage record theo user, promotion và order
     */
    @Query("SELECT upu FROM UserPromotionUsage upu WHERE upu.user = :user AND upu.promotion = :promotion AND upu.order.id = :orderId")
    UserPromotionUsage findByUserAndPromotionAndOrderId(@Param("user") User user, 
                                                          @Param("promotion") Promotion promotion, 
                                                          @Param("orderId") String orderId);
}
