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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.techstore.dtos.product.ProductVariantDTO;
import com.project.techstore.models.ProductVariant;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.product.VariantResponse;
import com.project.techstore.services.product_variant.IProductVariantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/product-variants")
@RequiredArgsConstructor
public class AdminProductVariantController {
    private final IProductVariantService productVariantService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getVariantByProduct(@PathVariable("productId") String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "color") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) throws Exception {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductVariant> productVariants = productVariantService.getVariantsByProductId(productId, pageable);
        Page<VariantResponse> variantResponses = productVariants.map(VariantResponse::fromProductVariant);
        return ResponseEntity.ok(ApiResponse.ok(variantResponses));
    }

    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createVariant(@RequestPart @Valid ProductVariantDTO productVariantDTO,
            @RequestPart(value = "image", required = false) MultipartFile image,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMessages.toString()));
        }

        productVariantService.createVariant(productVariantDTO, image);
        return ResponseEntity.ok(ApiResponse.ok("Tạo mới mẫu màu sản phẩm thành công"));
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateVariant(@PathVariable("id") String variantId,
            @Valid @RequestPart("productVariantDTO") ProductVariantDTO productVariantDTO,
            @RequestPart(value = "image", required = false) MultipartFile image,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMessages.toString()));
        }

        ProductVariant variant = productVariantService.updateVariant(variantId, productVariantDTO, image);
        return ResponseEntity.ok(ApiResponse.ok(variant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVariant(@PathVariable("id") String variantId) throws Exception {
        productVariantService.deleteVariant(variantId);
        return ResponseEntity.ok(ApiResponse.ok("Variant deleted successfully"));

    }

}
