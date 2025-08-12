package com.project.techstore.services;

import com.project.techstore.dtos.PromotionDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.exceptions.InvalidParamException;
import com.project.techstore.models.Brand;
import com.project.techstore.models.Category;
import com.project.techstore.models.DiscountType;
import com.project.techstore.models.Promotion;
import com.project.techstore.repositories.BrandRepository;
import com.project.techstore.repositories.CategoryRepository;
import com.project.techstore.repositories.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PromotionService implements IPromotionService{
    private final PromotionRepository promotionRepository;

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void deactivatedExpiredPromotions(){
        promotionRepository.deactivateExpiredPromotions(LocalDateTime.now());
    }

    @Override
    public Promotion createPromotion(PromotionDTO promotionDTO) throws Exception {
        if(promotionDTO == null)
            throw new InvalidParamException("This promotion invalid param");
        if(promotionDTO.getEndTime().isBefore(promotionDTO.getStartDate()))
            throw new InvalidParamException("The end time can't be before the start date");
        String discountType = promotionDTO.getDiscountType();
        if(!discountType.equals(DiscountType.FIXED) && !discountType.equals(DiscountType.PERCENTAGE))
            throw new InvalidParamException("Discount type invalid param");
        Set<Long> categories = promotionDTO.getCategories();
        for(Long category: categories){
            if(!categoryRepository.existsById(category))
                throw new DataNotFoundException("There is a category that doesn't exist");
        }
        Set<Long> brands = promotionDTO.getBrands();
        for(Long brand : brands){
            if(!brandRepository.existsById(brand))
                throw new DataNotFoundException("There is a brand that doesn't exist");
        }
        Set<Category> categorySet = categoryRepository.findByIds(categories);
        Set<Brand> brandSet = brandRepository.findByIds(brands);
        Promotion promotion = Promotion.builder()
                .name(promotionDTO.getName())
                .code(promotionDTO.getCode())
                .discountType(promotionDTO.getDiscountType())
                .discountValue(promotionDTO.getDiscountValue())
                .startDate(promotionDTO.getStartDate())
                .endTime(promotionDTO.getEndTime())
                .minOrderValue(promotionDTO.getMinOrderValue())
                .maxDiscount(promotionDTO.getMaxDiscount())
                .isActive(promotionDTO.getIsActive())
                .categories(categorySet)
                .brands(brandSet)
                .build();
        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public Promotion updatePromotion(String promotionId, PromotionDTO promotionDTO) throws Exception {
        Promotion promotionExisting = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new DataNotFoundException("Promotion doesn't exist"));
        if(promotionDTO == null)
            throw new InvalidParamException("New promotion invalid param");
        if(promotionDTO.getEndTime().isBefore(promotionDTO.getStartDate()))
            throw new InvalidParamException("The end time can't be before the start date");
        String discountType = promotionDTO.getDiscountType();
        if(!discountType.equals(DiscountType.FIXED) && !discountType.equals(DiscountType.PERCENTAGE))
            throw new InvalidParamException("Discount type invalid param");
        Set<Long> categories = promotionDTO.getCategories();

        for(Long category: categories){
            if(!categoryRepository.existsById(category))
                throw new DataNotFoundException("There is a category that doesn't exist");
        }
        Set<Long> brands = promotionDTO.getBrands();
        for(Long brand : brands){
            if(!brandRepository.existsById(brand))
                throw new DataNotFoundException("There is a brand that doesn't exist");
        }

        Set<Category> categorySet = categoryRepository.findByIds(categories);
        Set<Brand> brandSet = brandRepository.findByIds(brands);

        BeanUtils.copyProperties(promotionDTO, promotionExisting, "id");

        promotionExisting.getCategories().clear();
        promotionExisting.getBrands().clear();

        promotionExisting.setCategories(categorySet);
        promotionExisting.setBrands(brandSet);
        return promotionRepository.save(promotionExisting);
    }

    @Override
    public void deletePromotion(String promotionId) throws Exception {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new DataNotFoundException("The promotion doesn't exist"));
        promotion.setIsActive(false);
        promotionRepository.save(promotion);
    }

    @Override
    public List<Promotion> getAllPromotionByCategory(Long categoryId) throws Exception {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("Category doesn't exist"));
        return promotionRepository.findByCategoriesId(categoryId);
    }

    @Override
    public List<Promotion> getAllPromotionByBrand(Long brandId) throws Exception {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new DateTimeException("Brand doesn't exist"));
        return promotionRepository.findByBrandsId(brandId);

    }

    @Override
    public List<Promotion> getPromotionByCategory(Long categoryId) throws Exception {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("Category doesn't exist"));
        return promotionRepository.findByCategoriesIdAndIsActiveTrue(categoryId);
    }

    @Override
    public List<Promotion> getPromotionByBrand(Long brandId) throws Exception {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new DateTimeException("Brand doesn't exist"));
        return promotionRepository.findByBrandsIdAndIsActiveTrue(brandId);
    }
}
