package com.project.techstore.services;

import com.project.techstore.dtos.ProductModelDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Brand;
import com.project.techstore.models.Category;
import com.project.techstore.models.ProductModel;
import com.project.techstore.models.Role;
import com.project.techstore.repositories.BrandRepository;
import com.project.techstore.repositories.CategoryRepository;
import com.project.techstore.repositories.ProductModelRepository;
import com.project.techstore.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductModelService implements IProductModelService{
    private final ProductModelRepository productModelRepository;

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    private final ProductRepository productRepository;
    @Override
    public List<ProductModel> getProductModelByCategory(Long categoryId) throws Exception {
        return productModelRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<ProductModel> getProductModelByBrand(Long brandId) throws Exception {
        return productModelRepository.findByBrandId(brandId);
    }

    @Override
    public ProductModel createProductModel(ProductModelDTO productModelDTO) throws Exception {
        Category category = categoryRepository.findById(productModelDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category doesn't exist"));
        Brand brand = brandRepository.findById(productModelDTO.getBrandId())
                .orElseThrow(() -> new DataNotFoundException("Brand doesn't exist"));

        ProductModel productModel = ProductModel.builder()
                .name(productModelDTO.getName())
                .description(productModelDTO.getDescription())
                .category(category)
                .brand(brand)
                .build();
        return productModelRepository.save(productModel);
    }

    @Override
    public ProductModel updateProductModel(Long id, ProductModelDTO productModelDTO) throws Exception {
        ProductModel productModelExist = productModelRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product model doesn't exist"));
        Category category = categoryRepository.findById(productModelDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category doesn't exist"));
        Brand brand = brandRepository.findById(productModelDTO.getBrandId())
                .orElseThrow(() -> new DataNotFoundException("Brand doesn't exist"));
        productModelExist.setName(productModelDTO.getName());
        productModelExist.setDescription(productModelDTO.getDescription());
        productModelExist.setCategory(category);
        productModelExist.setBrand(brand);
        return productModelRepository.save(productModelExist);
    }

    @Override
    @Transactional
    public void deleteProductModel(Long id) throws Exception {
        ProductModel productModel = productModelRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product model doesn't exist"));
        productRepository.deleteByProductModelId(id);

        productModelRepository.delete(productModel);
    }
}
