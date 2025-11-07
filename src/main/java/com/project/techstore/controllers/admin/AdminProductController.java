package com.project.techstore.controllers.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.techstore.dtos.product.ProductDTO;
import com.project.techstore.dtos.product.ProductFilterDTO;
import com.project.techstore.models.Product;
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

    @GetMapping("")
    public ResponseEntity<?> getAllProductModels(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long productModelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productModels = productService.getAllProductWithoutDeleted(search, productModelId, pageable);
        Page<ProductRespone> responses = productModels.map(ProductRespone::fromProduct);
        return ResponseEntity.ok(ApiResponse.ok(responses));
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

    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createProduct(@RequestPart @Valid ProductDTO productDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "primaryImageIndex", required = false) Integer primaryImageIndex) throws Exception {
        productService.createProduct(productDTO, images, primaryImageIndex);
        return ResponseEntity.ok("Tạo sản phẩm thành công");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") String id, @RequestBody @Valid ProductDTO productDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        productService.updateProduct(id, productDTO);
        return ResponseEntity.ok("Update a product successful");
    }

    @PutMapping("/{productId}/images")
    public ResponseEntity<?> updateProductImages(@PathVariable("productId") String productId,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "primaryImageIndex", required = false) Integer primaryImageIndex) throws Exception {
        productService.updateProductImages(productId, images, primaryImageIndex);
        return ResponseEntity.ok("Cập nhật hình ảnh sản phẩm thành công");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String id) throws Exception {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Delete a product successful");
    }
}
