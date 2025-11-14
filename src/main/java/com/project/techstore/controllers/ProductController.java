package com.project.techstore.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.dtos.product.ProductFilterDTO;
import com.project.techstore.models.Product;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.product.ProductRespone;
import com.project.techstore.services.product.IProductService;

import jakarta.validation.Valid;
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

    @GetMapping("/category")
    public ResponseEntity<?> getProductByCategory(@RequestParam("category") String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductRespone> productResponeList = productService.getProductByCategory(categoryName, pageable);
        return ResponseEntity.ok(ApiResponse.ok(productResponeList));
    }

    @GetMapping("/category/{category}/brand/{brand}")
    public ResponseEntity<?> getProductByCategoryAndBrand(@PathVariable("category") String categoryName,
            @PathVariable("brand") String brandName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductRespone> productResponeList = productService.getProductByCategoryAndBrand(categoryName, brandName,
                pageable);
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

    @PostMapping("/filters")
    public ResponseEntity<?> getProductBySpecs(
            @RequestBody @Valid ProductFilterDTO filter, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ApiResponse.error(String.join(", ", errorMessages)));
        }
        List<ProductRespone> productResponeList = productService.filterProducts(filter);
        return ResponseEntity.ok(ApiResponse.ok(productResponeList));
    }

}
