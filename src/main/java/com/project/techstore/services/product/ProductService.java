package com.project.techstore.services.product;

import com.project.techstore.dtos.product.ProductDTO;
import com.project.techstore.dtos.product.ProductFilterDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.*;
import com.project.techstore.repositories.*;
import com.project.techstore.responses.product.ProductRespone;
import com.project.techstore.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    private final ProductRepositoryCustom productRepositoryCustom;

    private final ProductModelRepository productModelRepository;

    private final CategoryRepository categoryRepository;

    private final MediaRepository mediaRepository;

    private final AttributeRepository attributeRepository;

    private final ProductAttributeRepository productAttributeRepository;

    private final ProductVariantRepository productVariantRepository;
    
    private final CloudinaryService cloudinaryService;

    @Override
    public List<Product> getProductByProductModel(Long productModelId) throws Exception{
        if(!productModelRepository.existsById(productModelId))
            throw new DataNotFoundException("This product model doesn't exist");
        return productRepository.findByProductModelId(productModelId);
    }

    @Override
    public List<ProductRespone> getProductByCategory(Long categoryId) throws Exception {
        if(!categoryRepository.existsById(categoryId))
            throw new DataNotFoundException("Category doesn't exist");
        List<Product> productList = productRepository.findByProductModel_CategoryId(categoryId);
      return productList.stream().map(product -> ProductRespone.fromProduct(product)).toList();
    }

    @Override
    public List<ProductRespone> getProductByCategory(String category) throws Exception {
        List<Product> productList = productRepository.findByProductModel_Category_Name(category);
        return productList.stream().map(product -> ProductRespone.fromProduct(product)).toList();

    }

    @Override
    public List<ProductRespone> getProductByCategoryAndBrand(String category, String brand) throws Exception {
       List<Product> productList = productRepository.findByProductModel_Category_NameAndProductModel_Brand_Name(category, brand);
       return productList.stream().map(product -> ProductRespone.fromProduct(product)).toList();
    }

    @Override
    public List<ProductRespone> filterProducts(ProductFilterDTO productFilterDTO) {
        if (productFilterDTO.getMinPrice() != null && productFilterDTO.getMaxPrice() != null
                && productFilterDTO.getMinPrice() > productFilterDTO.getMaxPrice()) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }
        List<Product> productList = productRepositoryCustom.findProductsByFilters(productFilterDTO);
        return productList.stream().map(product -> ProductRespone.fromProduct(product)).toList();

    }

    @Override
    public ProductRespone getProductById(String productId) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found!"));

        return ProductRespone.fromProduct(product);
    }


    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO, List<MultipartFile> images) throws Exception {
        ProductModel productModel = productModelRepository.findById(productDTO.getProductModelId())
                .orElseThrow(() -> new DataNotFoundException("This product model doesn't exist"));
        Product product = Product.builder()
                .name(productDTO.getName())
                .configurationSummary(productDTO.getConfigurationSummary())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .productModel(productModel)
                .mediaList(new ArrayList<>())
                .build();
        productRepository.save(product);

        // Xử lý upload images nếu có
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                if (!file.isEmpty()) {
                    // Upload lên Cloudinary
                    String imageUrl = cloudinaryService.uploadProductImage(file, product.getId());

                    Media media = new Media();
                    media.setMediaPath(imageUrl);
                    media.setMediaType(file.getContentType());
                    media.setIsPrimary(i == 0);
                    media.setProduct(product);

                    mediaRepository.save(media);
                    product.getMediaList().add(media);
                }
            }
            productRepository.save(product);

            Map<String, String> attributeMaps = productDTO.getAttributes();
            if(attributeMaps != null){
                for(Map.Entry<String, String> entry : attributeMaps.entrySet()){
                    String attrName = entry.getKey();
                    String value = entry.getValue();

                    Attribute attribute = attributeRepository.findByName(attrName)
                            .orElseGet(() -> {
                                Attribute newAttr = new Attribute();
                                newAttr.setName(attrName);
                                return attributeRepository.save(newAttr);
                            });
                    ProductAttribute productAttribute = ProductAttribute.builder()
                            .product(product)
                            .attribute(attribute)
                            .value(value)
                            .build();
                    productAttributeRepository.save(productAttribute);
                }
            }
        }

        return product;
    }

    @Override
    public Product updateProduct(String id, ProductDTO productDTO) throws Exception {
        Product productExisting = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("This product doesn't exist"));
        ProductModel productModel = productModelRepository.findById(productDTO.getProductModelId())
                .orElseThrow(() -> new DataNotFoundException("This product model doesn't exist"));
        productExisting.setName(productDTO.getName());
        productExisting.setConfigurationSummary(productDTO.getConfigurationSummary());
        productExisting.setProductModel(productModel);
        productExisting.setDescription(productDTO.getDescription());
        return productRepository.save(productExisting);
    }

    @Override
    public void deleteProduct(String id) throws Exception {
        // Kiểm tra sản phẩm có tồn tại không
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("This product doesn't exist"));

        // Xóa ảnh từ Cloudinary
        List<Media> mediaList = product.getMediaList();
        if (mediaList != null && !mediaList.isEmpty()) {
            for (Media media : mediaList) {
                try {
                    cloudinaryService.deleteImageByUrl(media.getMediaPath());
                } catch (IOException e) {
                    // Log lỗi nhưng không làm gián đoạn xóa database
                    System.err.println("Failed to delete image from Cloudinary: " + media.getMediaPath() + ", error: " + e.getMessage());
                }
            }
        }

        productRepository.deleteById(id);
    }
    
    @Override
    public List<ProductRespone> getSimilarProducts(String productId, int limit) throws Exception {
        // Tìm sản phẩm hiện tại
        Product currentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
        
        // Lấy thông tin category và brand từ sản phẩm hiện tại
        Long categoryId = currentProduct.getProductModel() != null ? 
                currentProduct.getProductModel().getCategory().getId() : null;
                
        // Nếu không có category, không thể tìm sản phẩm tương tự
        if (categoryId == null) {
            return List.of();
        }
        
        // Tìm các sản phẩm trong cùng category, nhưng không bao gồm sản phẩm hiện tại
        List<Product> similarProducts = productRepository.findBySimilarCategoryAndDifferentId(
                categoryId, productId, limit);
        
        // Convert sang ProductRespone và trả về, giới hạn theo limit
        return similarProducts.stream()
                .limit(limit)
                .map(product -> ProductRespone.fromProduct(product))
                .toList();
    }
    
    /**
     * Cập nhật price và stock của Product dựa trên các ProductVariant
     * - Price = giá thấp nhất của các variant
     * - Stock = tổng stock của các variant
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
        
        // Cập nhật product
        product.setPrice(minPrice);
        product.setStock(totalStock);
        productRepository.save(product);
    }
}
