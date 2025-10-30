package com.project.techstore.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.Product;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.product.ProductRespone;
import com.project.techstore.services.product.IProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @GetMapping("")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> productList = productService.getAllProducts();
            List<ProductRespone> productResponse = productList.stream()
                    .map(ProductRespone::fromProduct).toList();
            return ResponseEntity.ok(ApiResponse.ok(productResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/product-model/{id}")
    public ResponseEntity<?> getProductByProductModel(@PathVariable("id") Long productModelId){
        try {
            List<Product> productList = productService.getProductByProductModel(productModelId);
            List<ProductRespone> productResponses = productList.stream()
                    .map(ProductRespone::fromProduct)
                    .toList();
            return ResponseEntity.ok(productResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") String productId){
        try {
            ProductRespone productRespone = productService.getProductById(productId);
            return ResponseEntity.ok(ApiResponse.ok(productRespone));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getProductByCategory(@PathVariable("id") Long categoryId){
        try {
            List<ProductRespone> productResponeList = productService.getProductByCategory(categoryId);
            return ResponseEntity.ok(ApiResponse.ok(productResponeList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/category")
    public ResponseEntity<?> getProductByCategory(@RequestParam("category") String categoryName){
        try {
            List<ProductRespone> productResponeList = productService.getProductByCategory(categoryName);
            return ResponseEntity.ok(ApiResponse.ok(productResponeList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/category/{category}/brand/{brand}")
    public ResponseEntity<?> getProductByCategoryAndBrand(@PathVariable("category") String categoryName,
                                                          @PathVariable("brand") String brandName){
        try {
            List<ProductRespone> productResponeList = productService.getProductByCategoryAndBrand(categoryName, brandName);
            return ResponseEntity.ok(ApiResponse.ok(productResponeList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/similar")
    public ResponseEntity<?> getSimilarProducts(
            @PathVariable("id") String productId,
            @RequestParam(defaultValue = "6") int limit) {
        try {
            List<ProductRespone> similarProducts = productService.getSimilarProducts(productId, limit);
            return ResponseEntity.ok(ApiResponse.ok(similarProducts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
