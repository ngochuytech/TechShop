package com.project.techstore.services.brand;

import com.project.techstore.models.Brand;
import com.project.techstore.repositories.BrandRepository;
import com.project.techstore.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService implements IBrandService {

    private final BrandRepository brandRepository;

    private final CloudinaryService cloudinaryService;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));
        return brand;
    }

    @Transactional
    public Brand createBrand(String name, MultipartFile image) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Brand name cannot be empty.");
        }
        if (brandRepository.existsByName(name)) {
            throw new RuntimeException("Brand with name '" + name + "' already exists.");
        }
        Brand brand = Brand.builder()
                .name(name)
                .build();

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadFile(image, "brands", "");
                brand.setImage(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload brand image: " + e.getMessage());
            }
        }

        Brand savedBrand = brandRepository.save(brand);
        return savedBrand;
    }

    @Transactional
    public Brand updateBrand(Long id, String name, MultipartFile image) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));

        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Brand name cannot be empty.");
        }
        brand.setName(name);

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadFile(image, "brands", "");
                brand.setImage(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload brand image: " + e.getMessage());
            }
        }

        Brand updatedBrand = brandRepository.save(brand);
        return updatedBrand;
    }

    @Transactional
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found with id: " + id);
        }
        brandRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return brandRepository.existsById(id);
    }

    public List<Brand> getBrandsByCategory(String categoryName) {
        return brandRepository.findByCategoryName(categoryName);
    }
}
