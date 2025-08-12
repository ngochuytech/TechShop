package com.project.techstore.services;

import com.project.techstore.dtos.ProductDTO;
import com.project.techstore.models.Product;

import java.util.List;

public interface IProductService {
    List<Product> getProductByProductModel(Long productModelId) throws Exception;

    List<Product> getProductByCategory(Long categoryId) throws Exception;

    Product createProduct(ProductDTO productDTO) throws Exception;

    Product updateProduct(String id, ProductDTO productDTO) throws Exception;

    void deleteProduct(String id) throws Exception;

}
