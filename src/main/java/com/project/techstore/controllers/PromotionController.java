package com.project.techstore.controllers;

import com.project.techstore.dtos.PromotionDTO;
import com.project.techstore.models.Promotion;
import com.project.techstore.services.IPromotionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final IPromotionService promotionService;

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getPromotionByCategory(@PathVariable("id") @Valid @Positive Long categoryId){
        try {
            List<Promotion> promotionList = promotionService.getPromotionByCategory(categoryId);
            return ResponseEntity.ok(promotionList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/brand/{id}")
    public ResponseEntity<?> getPromotionByBrand(@PathVariable("id") @Valid @Positive Long brandId){
        try {
            List<Promotion> promotionList = promotionService.getPromotionByBrand(brandId);
            return ResponseEntity.ok(promotionList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/category/all/{id}")
    public ResponseEntity<?> getAllPromotionByCategory(@PathVariable("id") @Valid @Positive Long categoryId){
        try {
            List<Promotion> promotionList = promotionService.getAllPromotionByCategory(categoryId);
            return ResponseEntity.ok(promotionList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/brand/all/{id}")
    public ResponseEntity<?> getAllPromotionByBrand(@PathVariable("id") @Valid @Positive Long brandId){
        try {
            List<Promotion> promotionList = promotionService.getAllPromotionByCategory(brandId);
            return ResponseEntity.ok(promotionList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPromotion(@RequestBody @Valid PromotionDTO promotionDTO, BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            promotionService.createPromotion(promotionDTO);
            return ResponseEntity.ok("Create a new promotion successful");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable("id") String promotionId, @RequestBody @Valid PromotionDTO promotionDTO,
                                             BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            promotionService.updatePromotion(promotionId, promotionDTO);
            return ResponseEntity.ok("Update a promotion successful");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePromotion(@PathVariable("id") String promotionId){
        try {
            promotionService.deletePromotion(promotionId);
            return ResponseEntity.ok("Delete a promotion successful");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
