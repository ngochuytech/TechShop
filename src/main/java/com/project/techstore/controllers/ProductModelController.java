package com.project.techstore.controllers;


import com.project.techstore.dtos.product.ProductModelDTO;
import com.project.techstore.models.ProductModel;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.product.ProductModelResponse;
import com.project.techstore.services.IProductModelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/product-models")
@RequiredArgsConstructor
public class ProductModelController {

    private final IProductModelService productModelService;

    @GetMapping("")
    public ResponseEntity<?> getAllProductModels() {
        try {
            List<ProductModel> productModels = productModelService.getAllProductModels();
            List<ProductModelResponse> responses = productModels.stream()
                    .map(ProductModelResponse::fromProductModel)
                    .toList();
            return ResponseEntity.ok(ApiResponse.ok(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getProductModelByCategory(@PathVariable("id") Long categoryId){
        try {
            List<ProductModel> productModelList = productModelService.getProductModelByCategory(categoryId);
            List<ProductModelResponse> responses = productModelList.stream()
                    .map(ProductModelResponse::fromProductModel)
                    .toList();
            return ResponseEntity.ok(ApiResponse.ok(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/brand/{id}")
    public ResponseEntity<?> getProductModelByBrand(@PathVariable("id") Long brandId){
        try {
            List<ProductModel> productModelList = productModelService.getProductModelByBrand(brandId);
            List<ProductModelResponse> responses = productModelList.stream()
                    .map(ProductModelResponse::fromProductModel)
                    .toList();
            return ResponseEntity.ok(ApiResponse.ok(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProductModel(@RequestBody @Valid ProductModelDTO productModelDTO, BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            productModelService.createProductModel(productModelDTO);
            return ResponseEntity.ok("Create a new product model successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProductModel(@PathVariable("id") Long id, @RequestBody @Valid ProductModelDTO productModelDTO,
                                                BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            productModelService.updateProductModel(id, productModelDTO);
            return ResponseEntity.ok("Update a product model successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProductModel(@PathVariable("id") Long id){
        try {
            productModelService.deleteProductModel(id);
            return ResponseEntity.ok("Delete a product model successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
