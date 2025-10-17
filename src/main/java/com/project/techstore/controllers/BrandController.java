package com.project.techstore.controllers;

import com.project.techstore.dtos.BrandDTO;
import com.project.techstore.dtos.CreateBrandRequest;
import com.project.techstore.dtos.UpdateBrandRequest;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/brands")
@RequiredArgsConstructor
public class BrandController {
    
    private final BrandService brandService;

    /**
     * GET /api/v1/brands - Lấy tất cả brands
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandDTO>>> getAllBrands() {
        try {
            List<BrandDTO> brands = brandService.getAllBrands();
            return ResponseEntity.ok(ApiResponse.ok(brands));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error getting brands: " + e.getMessage()));
        }
    }

    /**
     * GET /api/v1/brands/{id} - Lấy brand theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandDTO>> getBrandById(@PathVariable Long id) {
        try {
            BrandDTO brand = brandService.getBrandById(id);
            return ResponseEntity.ok(ApiResponse.ok(brand));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error getting brand: " + e.getMessage()));
        }
    }

    /**
     * POST /api/v1/brands - Tạo brand mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BrandDTO>> createBrand(
            @Valid @RequestBody CreateBrandRequest request,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(String.join(", ", errorMessages)));
            }
            
            BrandDTO createdBrand = brandService.createBrand(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(createdBrand));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error creating brand: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/v1/brands/{id} - Cập nhật brand
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandDTO>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBrandRequest request,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(String.join(", ", errorMessages)));
            }
            
            BrandDTO updatedBrand = brandService.updateBrand(id, request);
            return ResponseEntity.ok(ApiResponse.ok(updatedBrand));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error updating brand: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/v1/brands/{id} - Xóa brand
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error deleting brand: " + e.getMessage()));
        }
    }
}
