package com.project.techstore.controllers.admin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.techstore.dtos.product.ProductVariantDTO;
import com.project.techstore.models.ProductVariant;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.product.IProductVariantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/product-variants")
@RequiredArgsConstructor
public class AdminProductVariantController {
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

}
