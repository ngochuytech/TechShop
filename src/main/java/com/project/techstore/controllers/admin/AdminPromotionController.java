package com.project.techstore.controllers.admin;

import com.project.techstore.dtos.admin.PromotionDTO;
import com.project.techstore.models.Promotion;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.promotion.PromotionResponse;
import com.project.techstore.services.promotion.IPromotionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {
    private final IPromotionService promotionService;

    @GetMapping("")
    public ResponseEntity<?> getAllPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) throws Exception {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Promotion> promotions = promotionService.getAllPromotions(pageable);
        Page<PromotionResponse> promotionResponses = promotions.map(PromotionResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.ok(promotionResponses));
    }

    @PostMapping
    public ResponseEntity<?> createPromotion(@RequestBody @Valid PromotionDTO promotionDTO) throws Exception {
        Promotion promotion = promotionService.createPromotion(promotionDTO);
        return ResponseEntity.ok(ApiResponse.ok(PromotionResponse.fromEntity(promotion)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable("id") String promotionId,
            @RequestBody @Valid PromotionDTO promotionDTO) throws Exception {
        Promotion promotion = promotionService.updatePromotion(promotionId, promotionDTO);
        return ResponseEntity.ok(ApiResponse.ok(PromotionResponse.fromEntity(promotion)));
    }

    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleActive(@PathVariable("id") String promotionId,
            @RequestParam("isActive") boolean isActive) throws Exception {
        promotionService.setIsActive(promotionId, isActive);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật trạng thái mã khuyến mãi thành công"));
    }
}
