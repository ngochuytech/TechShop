package com.project.techstore.services.promotion;

import com.project.techstore.dtos.admin.promotion.PromotionDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.exceptions.InvalidParamException;
import com.project.techstore.models.Promotion;
import com.project.techstore.models.User;
import com.project.techstore.repositories.OrderRepository;
import com.project.techstore.repositories.PromotionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService implements IPromotionService {
    private final PromotionRepository promotionRepository;

    private final OrderRepository orderRepository;

    private final UserPromotionUsageService userPromotionUsageService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void deactivatedExpiredPromotions() {
        promotionRepository.deactivateExpiredPromotions(LocalDateTime.now());
    }

    private boolean validatePromotionDTO(PromotionDTO promotionDTO) throws InvalidParamException {
        if (promotionDTO == null)
            throw new InvalidParamException("Mã khuyến mãi không hợp lệ");
        if (promotionDTO.getEndTime().isBefore(promotionDTO.getStartDate()))
            throw new InvalidParamException("Thời gian kết thúc không thể trước thời gian bắt đầu");
        String discountType = promotionDTO.getDiscountType();
        if (!discountType.equals(Promotion.DiscountType.FIXED.name())
                && !discountType.equals(Promotion.DiscountType.PERCENTAGE.name())
                && !discountType.equals(Promotion.DiscountType.SHIPPING.name()))
            throw new InvalidParamException("Loại giảm giá không hợp lệ");
        if (discountType.equals(Promotion.DiscountType.PERCENTAGE.name())
                && (promotionDTO.getDiscountValue() <= 0 || promotionDTO.getDiscountValue() > 100))
            throw new InvalidParamException("Giá trị giảm giá phần trăm không hợp lệ");
        if ((discountType.equals(Promotion.DiscountType.FIXED.name())
                || discountType.equals(Promotion.DiscountType.SHIPPING.name()))
                && promotionDTO.getMaxDiscount() != null)
            throw new InvalidParamException("Giá trị giảm giá tối đa không hợp lệ, chỉ áp dụng cho giảm giá phần trăm");
        if (promotionRepository.existsByCode(promotionDTO.getCode()))
            throw new InvalidParamException("Mã khuyến mãi đã tồn tại");
        return true;
    }

    @Override
    public Promotion createPromotion(PromotionDTO promotionDTO) throws Exception {
        validatePromotionDTO(promotionDTO);

        Promotion promotion = Promotion.builder()
                .title(promotionDTO.getTitle())
                .code(promotionDTO.getCode())
                .description(promotionDTO.getDescription())
                .discountType(promotionDTO.getDiscountType())
                .discountValue(promotionDTO.getDiscountValue())
                .startDate(promotionDTO.getStartDate())
                .endTime(promotionDTO.getEndTime())
                .minOrderValue(promotionDTO.getMinOrderValue())
                .maxDiscount(promotionDTO.getMaxDiscount())
                .usageLimitPerUser(promotionDTO.getUsageLimitPerUser())
                .isForNewCustomer(
                        promotionDTO.getIsForNewCustomer() != null ? promotionDTO.getIsForNewCustomer() : false)
                .totalUsageLimit(promotionDTO.getTotalUsageLimit())
                .isActive(true)
                .build();
        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public Promotion updatePromotion(String promotionId, PromotionDTO promotionDTO) throws Exception {
        Promotion promotionExisting = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new DataNotFoundException("Mã khuyến mãi không tồn tại"));
        if (promotionDTO == null)
            throw new InvalidParamException("Mã khuyến mãi không hợp lệ");
        if (promotionDTO.getEndTime().isBefore(promotionDTO.getStartDate()))
            throw new InvalidParamException("Thời gian kết thúc không thể trước thời gian bắt đầu");
        String discountType = promotionDTO.getDiscountType();
        if (!discountType.equals(Promotion.DiscountType.FIXED.name())
                && !discountType.equals(Promotion.DiscountType.PERCENTAGE.name())
                && !discountType.equals(Promotion.DiscountType.SHIPPING.name()))
            throw new InvalidParamException("Loại giảm giá không hợp lệ");

        BeanUtils.copyProperties(promotionDTO, promotionExisting, "id");
        return promotionRepository.save(promotionExisting);
    }

    @Override
    public void deletePromotion(String promotionId) throws Exception {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new DataNotFoundException("Mã khuyến mãi không tồn tại"));
        promotion.setIsActive(false);
        promotionRepository.save(promotion);
    }

    @Override
    public Page<Promotion> getAllPromotions(Pageable pageable) throws Exception {
        return promotionRepository.findByIsActiveTrue(pageable);
    }

    @Override
    public void setIsActive(String promotionId, boolean isActive) throws Exception {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new DataNotFoundException("Mã khuyến mãi không tồn tại"));
        promotion.setIsActive(isActive);
        promotionRepository.save(promotion);
    }

    @Override
    public List<Promotion> getAvailablePromotions(User user) throws Exception {
        List<Promotion> promotions = promotionRepository
                .findByStartDateBeforeAndEndTimeAfterAndIsActiveTrue(LocalDateTime.now(), LocalDateTime.now());
        List<Promotion> availablePromotions = new ArrayList<>();
        for (Promotion promo : promotions) {
            if (promo.getTotalUsageLimit() <= userPromotionUsageService.countTotalUsage(promo)) {
                continue;
            }
            if (promo.getIsForNewCustomer()) {
                long completedOrderCount = orderRepository.countByUserAndStatus(user, "DELIVERED");
                if (completedOrderCount > 0) {
                    continue;
                }
            }
            if (promo.getUsageLimitPerUser() > 0) {
                long userUsageCount = userPromotionUsageService.countUserUsage(user, promo);
                if (userUsageCount >= promo.getUsageLimitPerUser()) {
                    continue;
                }
            }
            availablePromotions.add(promo);
        }
        return availablePromotions;
    }
}
