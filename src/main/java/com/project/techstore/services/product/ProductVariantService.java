package com.project.techstore.services.product;

import com.project.techstore.dtos.ProductVariantDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Product;
import com.project.techstore.models.ProductVariant;
import com.project.techstore.repositories.ProductRepository;
import com.project.techstore.repositories.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductVariantService implements IProductVariantService {
    
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    
    @Override
    @Transactional
    public ProductVariant createVariant(ProductVariantDTO productVariantDTO, MultipartFile image) throws Exception {
        // Kiểm tra Product có tồn tại không
        Product product = productRepository.findById(productVariantDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product not found with ID: " + productVariantDTO.getProductId()));
        
        // Kiểm tra xem màu này đã tồn tại cho sản phẩm này chưa
        List<ProductVariant> existingVariants = productVariantRepository.findByProductId(productVariantDTO.getProductId());
        boolean colorExists = existingVariants.stream()
                .anyMatch(variant -> variant.getColor().equalsIgnoreCase(productVariantDTO.getColor()));
        
        if (colorExists) {
            throw new IllegalArgumentException("Color '" + productVariantDTO.getColor() + "' already exists for this product");
        }
        
        // Tạo ProductVariant mới
        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .color(productVariantDTO.getColor())
                .stock(productVariantDTO.getStock())
                .price(productVariantDTO.getPrice()) // Có thể null
                .image("") // Sẽ cập nhật sau khi upload
                .build();
        
        // Save variant trước để có ID
        variant = productVariantRepository.save(variant);
        
        // Xử lý upload ảnh nếu có
        if (image != null && !image.isEmpty()) {
            String uploadDir = "uploads/variants/" + variant.getId();
            Files.createDirectories(Path.of(uploadDir));
            
            // Generate unique filename
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(image.getInputStream(), filePath);
            
            // Cập nhật đường dẫn ảnh
            variant.setImage("/" + uploadDir + "/" + fileName);
            variant = productVariantRepository.save(variant);
        }
        
        return variant;
    }
    
    @Override
    @Transactional
    public ProductVariant updateVariant(String variantId, ProductVariantDTO productVariantDTO) throws Exception {
        ProductVariant existingVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new DataNotFoundException("Product variant not found with ID: " + variantId));
        
        // Cập nhật thông tin
        existingVariant.setColor(productVariantDTO.getColor());
        existingVariant.setStock(productVariantDTO.getStock());
        existingVariant.setPrice(productVariantDTO.getPrice());
        
        return productVariantRepository.save(existingVariant);
    }
    
    @Override
    @Transactional
    public void deleteVariant(String variantId) throws Exception {
        if (!productVariantRepository.existsById(variantId)) {
            throw new DataNotFoundException("Product variant not found with ID: " + variantId);
        }
        productVariantRepository.deleteById(variantId);
    }
    
    @Override
    public List<ProductVariant> getVariantsByProductId(String productId) throws Exception {
        if (!productRepository.existsById(productId)) {
            throw new DataNotFoundException("Product not found with ID: " + productId);
        }
        return productVariantRepository.findByProductId(productId);
    }
    
    @Override
    public ProductVariant getVariantById(String variantId) throws Exception {
        return productVariantRepository.findById(variantId)
                .orElseThrow(() -> new DataNotFoundException("Product variant not found with ID: " + variantId));
    }
}