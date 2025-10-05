package com.project.techstore.services.product;

import com.project.techstore.dtos.ProductVariantDTO;
import com.project.techstore.models.ProductVariant;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductVariantService {
    ProductVariant createVariant(ProductVariantDTO productVariantDTO, MultipartFile image) throws Exception;
    
    ProductVariant updateVariant(String variantId, ProductVariantDTO productVariantDTO) throws Exception;
    
    void deleteVariant(String variantId) throws Exception;
    
    List<ProductVariant> getVariantsByProductId(String productId) throws Exception;
    
    ProductVariant getVariantById(String variantId) throws Exception;
}