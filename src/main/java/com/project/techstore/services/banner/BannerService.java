package com.project.techstore.services.banner;

import com.project.techstore.dtos.admin.BannerDTO;
import com.project.techstore.models.Banner;
import com.project.techstore.repositories.BannerRepository;
import com.project.techstore.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService implements IBannerService {

    private final BannerRepository bannerRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<Banner> getAllBanners() {
        return bannerRepository.findAllByOrderByOrderAsc();
    }

    @Override
    public List<Banner> getActiveBanners() {
        return bannerRepository.findAllActiveBanners();
    }

    @Override
    public Banner getBannerById(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with id: " + id));
    }

    @Override
    @Transactional
    public Banner createBanner(BannerDTO bannerDTO, MultipartFile image) {
        if (bannerDTO.getTitle() == null || bannerDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Banner title cannot be empty.");
        }

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Banner image is required.");
        }

        Banner banner = Banner.builder()
                .title(bannerDTO.getTitle())
                .description(bannerDTO.getDescription())
                .link(bannerDTO.getLink())
                .order(bannerDTO.getOrder() != null ? bannerDTO.getOrder() : 0)
                .active(bannerDTO.getActive() != null ? bannerDTO.getActive() : true)
                .build();

        try {
            String imageUrl = cloudinaryService.uploadFile(image, "banners", "");
            banner.setImage(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload banner image: " + e.getMessage());
        }

        return bannerRepository.save(banner);
    }

    @Override
    @Transactional
    public Banner updateBanner(Long id, BannerDTO bannerDTO, MultipartFile image) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with id: " + id));

        if (bannerDTO.getTitle() != null && !bannerDTO.getTitle().trim().isEmpty()) {
            banner.setTitle(bannerDTO.getTitle());
        }

        if (bannerDTO.getDescription() != null) {
            banner.setDescription(bannerDTO.getDescription());
        }

        if (bannerDTO.getLink() != null) {
            banner.setLink(bannerDTO.getLink());
        }

        if (bannerDTO.getOrder() != null) {
            banner.setOrder(bannerDTO.getOrder());
        }

        if (bannerDTO.getActive() != null) {
            banner.setActive(bannerDTO.getActive());
        }

        if (image != null && !image.isEmpty()) {
            try {
                if (banner.getImage() != null) {
                    try {
                        cloudinaryService.deleteFile(banner.getImage());
                    } catch (Exception e) {
                        // Continue even if delete fails
                    }
                }

                String imageUrl = cloudinaryService.uploadFile(image, "banners", "");
                banner.setImage(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload banner image: " + e.getMessage());
            }
        }

        return bannerRepository.save(banner);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with id: " + id));

        // Delete image from Cloudinary
        if (banner.getImage() != null) {
            try {
                cloudinaryService.deleteFile(banner.getImage());
            } catch (Exception e) {
                // Continue even if delete fails
            }
        }

        bannerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void reorderBanners(List<BannerDTO> banners) {
        for (BannerDTO bannerDTO : banners) {
            if (bannerDTO.getId() != null) {
                Banner banner = bannerRepository.findById(bannerDTO.getId())
                        .orElse(null);
                if (banner != null && bannerDTO.getOrder() != null) {
                    banner.setOrder(bannerDTO.getOrder());
                    bannerRepository.save(banner);
                }
            }
        }
    }
}
