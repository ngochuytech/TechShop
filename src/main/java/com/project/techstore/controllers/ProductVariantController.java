package com.project.techstore.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.ProductVariant;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.product.IProductVariantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final IProductVariantService productVariantService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getVariantsByProductId(@PathVariable("productId") String productId) throws Exception {
        List<ProductVariant> variants = productVariantService.getVariantsByProductId(productId);
        return ResponseEntity.ok(ApiResponse.ok(variants));

    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getVariantById(@PathVariable("id") String variantId) throws Exception {
        ProductVariant variant = productVariantService.getVariantById(variantId);
        return ResponseEntity.ok(ApiResponse.ok(variant));

    }
}