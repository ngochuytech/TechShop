package com.project.techstore.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.Brand;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.brand.BrandResponse;
import com.project.techstore.services.brand.IBrandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/brands")
@RequiredArgsConstructor
public class BrandController {

    private final IBrandService brandService;

    @GetMapping
    public ResponseEntity<?> getAllBrands() throws Exception {
        List<Brand> brands = brandService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.ok(brands));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable Long id) throws Exception {
        Brand brand = brandService.getBrandById(id);
        return ResponseEntity.ok(ApiResponse.ok(BrandResponse.fromBrand(brand)));
    }

    @GetMapping("/by-category")
    public ResponseEntity<?> getBrandsByCategory(@RequestParam String category) throws Exception {
        List<Brand> brands = brandService.getBrandsByCategory(category);
        List<BrandResponse> brandResponses = brands.stream()
                .map(BrandResponse::fromBrand)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(brandResponses));
    }

}
