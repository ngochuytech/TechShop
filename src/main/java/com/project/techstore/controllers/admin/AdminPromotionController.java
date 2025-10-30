package com.project.techstore.controllers.admin;

import com.project.techstore.dtos.admin.promotion.PromotionDTO;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.promotion.IPromotionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/admin/promotions")
@RequiredArgsConstructor
public class AdminPromotionController {
    private final IPromotionService promotionService;

    @PostMapping
    public ResponseEntity<?> createPromotion(@RequestBody @Valid PromotionDTO promotionDTO) throws Exception {
        promotionService.createPromotion(promotionDTO);
        return ResponseEntity.ok("Tạo mã khuyến mãi thành công");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable("id") String promotionId,
            @RequestBody @Valid PromotionDTO promotionDTO) throws Exception {
        promotionService.updatePromotion(promotionId, promotionDTO);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật mã khuyến mãi thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePromotion(@PathVariable("id") String promotionId) throws Exception {
        promotionService.deletePromotion(promotionId);
        return ResponseEntity.ok(ApiResponse.ok("Hủy mã khuyến mãi thành công"));
    }
}
