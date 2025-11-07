package com.project.techstore.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<?> getAllProducts() throws Exception {
        List<Product> productList = productService.getAllProducts();
        List<ProductRespone> productResponse = productList.stream()
                .map(ProductRespone::fromProduct).toList();
        return ResponseEntity.ok(ApiResponse.ok(productResponse));
    }

    @GetMapping("/product-model/{id}")
    public ResponseEntity<?> getProductByProductModel(@PathVariable("id") Long productModelId) throws Exception {
        List<Product> productList = productService.getProductByProductModel(productModelId);
        List<ProductRespone> productResponses = productList.stream()
                .map(ProductRespone::fromProduct)
                .toList();
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") String productId) throws Exception {
        ProductRespone productRespone = productService.getProductById(productId);
        return ResponseEntity.ok(ApiResponse.ok(productRespone));
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getProductByCategory(@PathVariable("id") Long categoryId) throws Exception {
        List<ProductRespone> productResponeList = productService.getProductByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.ok(productResponeList));
    }

    @GetMapping("/category")
    public ResponseEntity<?> getProductByCategory(@RequestParam("category") String categoryName) throws Exception {
        List<ProductRespone> productResponeList = productService.getProductByCategory(categoryName);
        return ResponseEntity.ok(ApiResponse.ok(productResponeList));
    }

    @GetMapping("/category/{category}/brand/{brand}")
    public ResponseEntity<?> getProductByCategoryAndBrand(@PathVariable("category") String categoryName,
            @PathVariable("brand") String brandName) throws Exception {
        List<ProductRespone> productResponeList = productService.getProductByCategoryAndBrand(categoryName, brandName);
        return ResponseEntity.ok(ApiResponse.ok(productResponeList));
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<?> getSimilarProducts(
            @PathVariable("id") String productId,
            @RequestParam(defaultValue = "6") int limit) throws Exception {
        List<ProductRespone> similarProducts = productService.getSimilarProducts(productId, limit);
        return ResponseEntity.ok(ApiResponse.ok(similarProducts));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<?> searchVariants(@PathVariable("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> variants = productService.searchProducts(keyword, pageable);
        Page<ProductRespone> responsePage = variants.map(ProductRespone::fromProduct);
        return ResponseEntity.ok(ApiResponse.ok(responsePage));
    }

}
