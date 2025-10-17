package com.project.techstore.services;

import com.project.techstore.dtos.product.ProductModelDTO;
import com.project.techstore.models.ProductModel;

import java.util.List;

public interface IProductModelService {
    List<ProductModel> getAllProductModels() throws Exception;

    List<ProductModel> getProductModelByCategory(Long categoryId) throws Exception;

    List<ProductModel> getProductModelByBrand(Long brandId) throws Exception;

    ProductModel createProductModel(ProductModelDTO productModelDTO) throws Exception;

    ProductModel updateProductModel(Long id, ProductModelDTO productModelDTO) throws Exception;

    void deleteProductModel(Long id) throws Exception;
}
