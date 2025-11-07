package com.project.techstore.services.product_variant;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.project.techstore.dtos.product.ProductVariantDTO;
import com.project.techstore.models.ProductVariant;

public interface IProductVariantService {
    ProductVariant createVariant(ProductVariantDTO productVariantDTO, MultipartFile image) throws Exception;
    
    ProductVariant updateVariant(String variantId, ProductVariantDTO productVariantDTO, MultipartFile image) throws Exception;
    
    void deleteVariant(String variantId) throws Exception;
    
    List<ProductVariant> getVariantsByProductId(String productId) throws Exception;

    Page<ProductVariant> getVariantsByProductId(String productId, Pageable pageable) throws Exception;
    
    ProductVariant getVariantById(String variantId) throws Exception;
}