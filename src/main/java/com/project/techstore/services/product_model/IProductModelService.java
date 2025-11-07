package com.project.techstore.services.product_model;

import com.project.techstore.dtos.product.ProductModelDTO;
import com.project.techstore.models.ProductModel;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductModelService {
    Page<ProductModel> getAllProductModelsWithoutDeleted(String search, Long categoryId, Long brandId, Pageable pageable) throws Exception;

    List<ProductModel> getProductModelByCategory(Long categoryId) throws Exception;

    List<ProductModel> getProductModelByBrand(Long brandId) throws Exception;

    ProductModel createProductModel(ProductModelDTO productModelDTO) throws Exception;

    ProductModel updateProductModel(Long id, ProductModelDTO productModelDTO) throws Exception;

    void deleteProductModel(Long id) throws Exception;
}
