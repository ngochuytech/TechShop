package com.project.techstore.services.promotion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.techstore.dtos.admin.promotion.PromotionDTO;
import com.project.techstore.models.Promotion;

public interface IPromotionService {
    Promotion createPromotion(PromotionDTO promotionDTO) throws Exception;

    Promotion updatePromotion(String promotionId, PromotionDTO promotionDTO) throws Exception;

    void setIsActive(String promotionId, boolean isActive) throws Exception;

    void deletePromotion(String promotionId) throws Exception;

    Page<Promotion> getAllPromotions(Pageable pageable) throws Exception;
}
