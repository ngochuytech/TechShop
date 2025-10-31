package com.project.techstore.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.Promotion;
import com.project.techstore.models.User;
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
    public ResponseEntity<?> getAllPromotions(@AuthenticationPrincipal User user)
            throws Exception {
        List<Promotion> promotions = promotionService.getAvailablePromotions(user);
        List<PromotionResponse> promotionResponses = promotions.stream()
                .map(PromotionResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(promotionResponses));
    }
}
