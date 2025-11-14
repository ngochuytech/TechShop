package com.project.techstore.services.product;

import com.project.techstore.dtos.product.ProductDTO;
import com.project.techstore.dtos.product.ProductFilterDTO;
import com.project.techstore.models.Product;
import com.project.techstore.responses.product.ProductRespone;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts() throws Exception;

    Page<Product> getAllProductWithoutDeleted(String search, Long productModelId, Pageable pageable) throws Exception;

    List<Product> getProductByProductModel(Long productModelId) throws Exception;

    Page<ProductRespone> getProductByCategory(String category, Pageable pageable) throws Exception;

    Page<ProductRespone> getProductByCategoryAndBrand(String category, String brand, Pageable pageable) throws Exception;

    List<ProductRespone> filterProducts(ProductFilterDTO productFilterDTO);

    ProductRespone getProductById(String productId) throws Exception;

    Product createProduct(ProductDTO productDTO, List<MultipartFile> files, Integer primaryImageIndex) throws Exception;

    Product updateProduct(String id, ProductDTO productDTO) throws Exception;

    void updateProductImages(String productId, List<MultipartFile> images, Integer primaryImageIndex) throws Exception;

    void deleteProduct(String id) throws Exception;

    List<ProductRespone> getSimilarProducts(String productId, int limit) throws Exception;

    Page<Product> searchProducts(String keyword, Pageable pageable) throws Exception;
}
