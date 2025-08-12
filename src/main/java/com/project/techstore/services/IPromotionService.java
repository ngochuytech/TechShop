package com.project.techstore.services;

import com.project.techstore.dtos.PromotionDTO;
import com.project.techstore.models.Promotion;

import java.util.List;

public interface IPromotionService {
    Promotion createPromotion(PromotionDTO promotionDTO) throws Exception;

    Promotion updatePromotion(String promotionId, PromotionDTO promotionDTO) throws Exception;

    void deletePromotion(String promotionId) throws Exception;

    List<Promotion> getAllPromotionByCategory(Long categoryId) throws Exception;

    List<Promotion> getAllPromotionByBrand(Long brandId) throws Exception;

    // Only get valid promotions
    List<Promotion> getPromotionByCategory(Long categoryId) throws Exception;

    List<Promotion> getPromotionByBrand(Long brandId) throws Exception;

}
