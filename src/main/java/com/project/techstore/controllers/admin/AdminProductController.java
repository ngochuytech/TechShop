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

import com.project.techstore.dtos.product.ProductDTO;
import com.project.techstore.dtos.product.ProductFilterDTO;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.product.ProductRespone;
import com.project.techstore.services.product.IProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final IProductService productService;

    @PostMapping("/filters")
    public ResponseEntity<?> getProductBySpecs(
            @RequestBody @Valid ProductFilterDTO filter, BindingResult result) throws Exception {
        try {
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

    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createProduct(@RequestPart @Valid ProductDTO productDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            productService.createProduct(productDTO, images);
            return ResponseEntity.ok("Create a new product successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") String id, @RequestBody @Valid ProductDTO productDTO,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
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
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Delete a product successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
