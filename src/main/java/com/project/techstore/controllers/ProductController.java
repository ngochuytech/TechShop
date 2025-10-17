package com.project.techstore.controllers;

import com.project.techstore.dtos.product.ProductDTO;
import com.project.techstore.dtos.product.ProductFilterDTO;
import com.project.techstore.models.Product;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.product.ProductRespone;
import com.project.techstore.services.product.IProductService;
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

    @PostMapping("/filters")
    public ResponseEntity<?> getProductBySpecs(
            @RequestBody @Valid ProductFilterDTO filter, BindingResult result) throws Exception {
        try{
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(ApiResponse.error(String.join(", ", errorMessages)));
            }
            List<ProductRespone> productResponeList = productService.filterProducts(filter);
            return ResponseEntity.ok(ApiResponse.ok(productResponeList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createProduct(@RequestPart @Valid ProductDTO productDTO,
                                           @RequestPart(value = "images", required = false) List<MultipartFile> images){
        try {
            productService.createProduct(productDTO, images);
            return ResponseEntity.ok("Create a new product successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") String id, @RequestBody @Valid ProductDTO productDTO,
                                           BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            productService.updateProduct(id, productDTO);
            return ResponseEntity.ok("Update a product successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String id){
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Delete a product successful");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
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
