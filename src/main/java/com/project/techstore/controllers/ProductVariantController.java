package com.project.techstore.controllers;

import com.project.techstore.dtos.product.ProductVariantDTO;
import com.project.techstore.models.ProductVariant;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.product.IProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {
    
    private final IProductVariantService productVariantService;
    
    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createVariant(@RequestPart @Valid ProductVariantDTO productVariantDTO,
                                         @RequestPart(value = "image", required = false) MultipartFile image,
                                         BindingResult result) {
        try {
            // Kiểm tra validation errors
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(ApiResponse.error(errorMessages.toString()));
            }
            
            productVariantService.createVariant(productVariantDTO, image);
            return ResponseEntity.ok(ApiResponse.ok("Tạo mới mẫu màu sản phẩm thành công"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVariant(@PathVariable("id") String variantId,
                                         @Valid @RequestBody ProductVariantDTO productVariantDTO,
                                         BindingResult result) {
        try {
            // Kiểm tra validation errors
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(ApiResponse.error(errorMessages.toString()));
            }
            
            ProductVariant variant = productVariantService.updateVariant(variantId, productVariantDTO);
            return ResponseEntity.ok(ApiResponse.ok(variant));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVariant(@PathVariable("id") String variantId) {
        try {
            productVariantService.deleteVariant(variantId);
            return ResponseEntity.ok(ApiResponse.ok("Variant deleted successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getVariantsByProductId(@PathVariable("productId") String productId) {
        try {
            List<ProductVariant> variants = productVariantService.getVariantsByProductId(productId);
            return ResponseEntity.ok(ApiResponse.ok(variants));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getVariantById(@PathVariable("id") String variantId) {
        try {
            ProductVariant variant = productVariantService.getVariantById(variantId);
            return ResponseEntity.ok(ApiResponse.ok(variant));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}