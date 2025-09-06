package com.project.techstore.services.product;

import com.project.techstore.dtos.ProductDTO;
import com.project.techstore.dtos.ProductFilterDTO;
import com.project.techstore.models.Product;
import com.project.techstore.responses.product.ProductRespone;

import java.util.List;
import java.util.Map;

public interface IProductService {
    List<Product> getProductByProductModel(Long productModelId) throws Exception;

    List<ProductRespone> getProductByCategory(Long categoryId) throws Exception;

    List<ProductRespone> getProductByCategory(String category) throws Exception;

    List<ProductRespone> getProductByCategoryAndBrand(String category, String brand) throws Exception;

    List<ProductRespone> filterProducts(ProductFilterDTO productFilterDTO);

    Product createProduct(ProductDTO productDTO) throws Exception;

    Product updateProduct(String id, ProductDTO productDTO) throws Exception;

    void deleteProduct(String id) throws Exception;

}
