package com.project.techstore.services;

import com.project.techstore.dtos.BrandDTO;
import com.project.techstore.dtos.CreateBrandRequest;
import com.project.techstore.dtos.UpdateBrandRequest;
import com.project.techstore.models.Brand;
import com.project.techstore.repositories.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {
    
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    /**
     * Lấy tất cả brands
     */
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brand -> modelMapper.map(brand, BrandDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Lấy brand theo ID
     */
    public BrandDTO getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));
        return modelMapper.map(brand, BrandDTO.class);
    }

    /**
     * Tạo brand mới
     */
    @Transactional
    public BrandDTO createBrand(CreateBrandRequest request) {
        Brand brand = new Brand();
        brand.setName(request.getName());
        
        Brand savedBrand = brandRepository.save(brand);
        return modelMapper.map(savedBrand, BrandDTO.class);
    }

    /**
     * Cập nhật brand
     */
    @Transactional
    public BrandDTO updateBrand(Long id, UpdateBrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));
        
        brand.setName(request.getName());
        
        Brand updatedBrand = brandRepository.save(brand);
        return modelMapper.map(updatedBrand, BrandDTO.class);
    }

    /**
     * Xóa brand
     */
    @Transactional
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found with id: " + id);
        }
        brandRepository.deleteById(id);
    }

    /**
     * Kiểm tra brand có tồn tại không
     */
    public boolean existsById(Long id) {
        return brandRepository.existsById(id);
    }
}
