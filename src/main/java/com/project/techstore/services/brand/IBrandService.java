package com.project.techstore.services.brand;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.project.techstore.models.Brand;

public interface IBrandService {
    public List<Brand> getAllBrands();

    public Brand getBrandById(Long id);

    public Brand createBrand(String name, MultipartFile image);

    public Brand updateBrand(Long id, String name, MultipartFile image);

    public void deleteBrand(Long id);

    public boolean existsById(Long id);

    public List<Brand> getBrandsByCategory(String categoryName);

}
