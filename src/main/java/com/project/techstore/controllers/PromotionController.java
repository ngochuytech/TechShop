package com.project.techstore.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.Promotion;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.promotion.PromotionResponse;
import com.project.techstore.services.promotion.IPromotionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final IPromotionService promotionService;
    
    @GetMapping("/available")
    public ResponseEntity<?> getAllPromotions(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Promotion> promotions = promotionService.getAllPromotions(pageable);
        Page<PromotionResponse> promotionResponses = promotions.map(PromotionResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.ok(promotionResponses));
    }
}
