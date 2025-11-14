package com.project.techstore.services.banner;

import com.project.techstore.dtos.admin.BannerDTO;
import com.project.techstore.models.Banner;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBannerService {
    List<Banner> getAllBanners();
    List<Banner> getActiveBanners();
    Banner getBannerById(Long id);
    Banner createBanner(BannerDTO bannerDTO, MultipartFile image);
    Banner updateBanner(Long id, BannerDTO bannerDTO, MultipartFile image);
    void deleteBanner(Long id);
    void reorderBanners(List<BannerDTO> banners);
}
