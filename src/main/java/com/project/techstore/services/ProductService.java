package com.project.techstore.services;

import com.project.techstore.dtos.ProductDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Product;
import com.project.techstore.models.ProductModel;
import com.project.techstore.repositories.CategoryRepository;
import com.project.techstore.repositories.ProductModelRepository;
import com.project.techstore.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;

    private final ProductModelRepository productModelRepository;

    private final CategoryRepository categoryRepository;

    @Override
    public List<Product> getProductByProductModel(Long productModelId) throws Exception{
        if(!productModelRepository.existsById(productModelId))
            throw new DataNotFoundException("This product model doesn't exist");
        return productRepository.findByProductModelId(productModelId);
    }

    @Override
    public List<Product> getProductByCategory(Long categoryId) throws Exception {
        if(!categoryRepository.existsById(categoryId))
            throw new DataNotFoundException("Category doesn't exist");
        return productRepository.findByCategory(categoryId);
    }

    @Override
    public Product createProduct(ProductDTO productDTO) throws Exception {
        ProductModel productModel = productModelRepository.findById(productDTO.getProductModelId())
                .orElseThrow(() -> new DataNotFoundException("This product model doesn't exist"));
        Product product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .priceDiscount(productDTO.getPriceDiscount())
                .stock(productDTO.getStock())
                .spec(productDTO.getSpec())
                .productModel(productModel)
                .build();
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String id, ProductDTO productDTO) throws Exception {
        Product productExisting = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("This product doesn't exist"));
        ProductModel productModel = productModelRepository.findById(productDTO.getProductModelId())
                .orElseThrow(() -> new DataNotFoundException("This product model doesn't exist"));
        productExisting.setName(productDTO.getName());
        productExisting.setPrice(productDTO.getPrice());
        productExisting.setPriceDiscount(productDTO.getPriceDiscount());
        productExisting.setStock(productDTO.getStock());
        productExisting.setSpec(productDTO.getSpec());
        productExisting.setProductModel(productModel);
        return productRepository.save(productExisting);
    }

    @Override
    public void deleteProduct(String id) throws Exception {
        if(!productRepository.existsById(id))
            throw new DataNotFoundException("This product doesn't exist");
        productRepository.deleteById(id);
    }
}
