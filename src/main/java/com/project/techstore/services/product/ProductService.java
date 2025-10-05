package com.project.techstore.services.product;

import com.project.techstore.dtos.ProductDTO;
import com.project.techstore.dtos.ProductFilterDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.*;
import com.project.techstore.repositories.*;
import com.project.techstore.responses.product.ProductRespone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;

    private final ProductRepositoryCustom productRepositoryCustom;

    private final ProductModelRepository productModelRepository;

    private final CategoryRepository categoryRepository;

    private final MediaRepository mediaRepository;

    private final AttributeRepository attributeRepository;

    private final ProductAttributeRepository productAttributeRepository;

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
                .productModel(productModel)
                .mediaList(new ArrayList<>())
                .build();
        productRepository.save(product);

        // Xử lý upload images nếu có
        if (images != null && !images.isEmpty()) {
            String uploadDir = "uploads/products/" + product.getId();
            Files.createDirectories(Path.of(uploadDir));

            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                if (!file.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir, fileName);
                    Files.copy(file.getInputStream(), filePath);

                    Media media = new Media();
                    media.setMediaPath("/" + uploadDir + "/" + fileName);
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
        if (!productRepository.existsById(id)) {
            throw new DataNotFoundException("This product doesn't exist");
        }

        // Xóa thư mục ảnh trong hệ thống file
        String uploadDir = "uploads/products/" + id;
        Path uploadDirPath = Paths.get(uploadDir);
        try {
            if (Files.exists(uploadDirPath)) {
                Files.walk(uploadDirPath)
                        .sorted((a, b) -> b.compareTo(a)) // Xóa file trước, thư mục sau
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                // Log lỗi nhưng không throw để tiếp tục xóa database
                                System.err.println("Failed to delete file/folder: " + path + ", error: " + e.getMessage());
                            }
                        });
            }
        } catch (IOException e) {
            // Log lỗi nhưng không làm gián đoạn xóa database
            System.err.println("Failed to delete product images directory: " + uploadDir + ", error: " + e.getMessage());
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
}
