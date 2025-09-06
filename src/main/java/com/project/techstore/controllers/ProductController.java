package com.project.techstore.controllers;

import com.project.techstore.dtos.ProductDTO;
import com.project.techstore.dtos.ProductFilterDTO;
import com.project.techstore.models.Product;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.product.ProductRespone;
import com.project.techstore.services.product.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;

    @GetMapping("/product-model/{id}")
    public ResponseEntity<?> getProductByProductModel(@PathVariable("id") Long productModelId){
        try {
            List<Product> productList = productService.getProductByProductModel(productModelId);
            return ResponseEntity.ok(productList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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

    @GetMapping("/")
    public ResponseEntity<?> getProductByCategoryAndBrand(@RequestParam("category") String categoryName,
                                                          @RequestParam("brand") String brandName){
        try {
            List<ProductRespone> productResponeList = productService.getProductByCategoryAndBrand(categoryName, brandName);
            return ResponseEntity.ok(ApiResponse.ok(productResponeList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/filters")
    public ResponseEntity<?> getProductBySpecs(
            @RequestBody ProductFilterDTO filter) throws Exception {
        try{
            List<ProductRespone> productResponeList = productService.filterProducts(filter);
            return ResponseEntity.ok(ApiResponse.ok(productResponeList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody @Valid ProductDTO productDTO, BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            productService.createProduct(productDTO);
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
}
