package com.project.techstore.services.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.techstore.dtos.product.ProductVariantDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Product;
import com.project.techstore.models.ProductVariant;
import com.project.techstore.repositories.ProductRepository;
import com.project.techstore.repositories.ProductVariantRepository;
import com.project.techstore.services.CloudinaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductVariantService implements IProductVariantService {
    
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    
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
            // Upload lên Cloudinary
            String imageUrl = cloudinaryService.uploadVariantImage(image, variant.getId());
            
            // Cập nhật đường dẫn ảnh
            variant.setImage(imageUrl);
            variant = productVariantRepository.save(variant);
        }
        
        // Cập nhật price và stock của Product parent dựa trên variants
        updateProductPriceAndStockFromVariants(productVariantDTO.getProductId());
        
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
        
        ProductVariant updatedVariant = productVariantRepository.save(existingVariant);
        
        // Cập nhật price và stock của Product parent dựa trên variants
        updateProductPriceAndStockFromVariants(existingVariant.getProduct().getId());
        
        return updatedVariant;
    }
    
    @Override
    @Transactional
    public void deleteVariant(String variantId) throws Exception {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new DataNotFoundException("Product variant not found with ID: " + variantId));
        
        String productId = variant.getProduct().getId();
        
        // Xóa ảnh từ Cloudinary nếu có
        if (variant.getImage() != null && !variant.getImage().isEmpty()) {
            try {
                cloudinaryService.deleteImageByUrl(variant.getImage());
            } catch (Exception e) {
                // Log lỗi nhưng không làm gián đoạn xóa database
                System.err.println("Failed to delete variant image from Cloudinary: " + variant.getImage() + ", error: " + e.getMessage());
            }
        }
        
        productVariantRepository.deleteById(variantId);
        
        // Cập nhật price và stock của Product parent dựa trên variants còn lại
        updateProductPriceAndStockFromVariants(productId);
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
    
    /**
     * Cập nhật price và stock của Product dựa trên các ProductVariant
     */
    @Transactional
    public void updateProductPriceAndStockFromVariants(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        
        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        
        if (variants.isEmpty()) {
            // Nếu không có variant nào, giữ nguyên price và stock hiện tại
            return;
        }
        
        // Tính price = giá thấp nhất của các variant (chỉ tính variant có price khác null)
        Long minPrice = variants.stream()
                .filter(variant -> variant.getPrice() != null)
                .mapToLong(ProductVariant::getPrice)
                .min()
                .orElse(product.getPrice() != null ? product.getPrice() : 0L);
        
        // Tính stock = tổng stock của các variant
        Integer totalStock = variants.stream()
                .filter(variant -> variant.getStock() != null)
                .mapToInt(ProductVariant::getStock)
                .sum();
        
        product.setPrice(minPrice);
        product.setStock(totalStock);
        productRepository.save(product);
    }
}