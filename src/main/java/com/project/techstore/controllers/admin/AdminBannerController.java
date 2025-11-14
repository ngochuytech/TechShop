package com.project.techstore.controllers.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import com.project.techstore.dtos.admin.BannerDTO;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.banner.IBannerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {
    private final IBannerService bannerService;

    @GetMapping
    public ResponseEntity<?> getAllBanners() throws Exception {
        List<com.project.techstore.models.Banner> banners = bannerService.getAllBanners();
        return ResponseEntity.ok(ApiResponse.ok(banners));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createBanner(
            @RequestPart(value = "title") String title,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "link", required = false) String link,
            @RequestPart(value = "order", required = false) Integer order,
            @RequestPart(value = "active", required = false) Boolean active,
            @RequestPart(value = "image") MultipartFile image) throws Exception {

        BannerDTO bannerDTO = BannerDTO.builder()
                .title(title)
                .description(description)
                .link(link)
                .order(order)
                .active(active != null ? active : true)
                .build();

        bannerService.createBanner(bannerDTO, image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo banner thành công"));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateBanner(
            @PathVariable Long id,
            @RequestPart(value = "title", required = false) String title,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "link", required = false) String link,
            @RequestPart(value = "order", required = false) Integer order,
            @RequestPart(value = "active", required = false) Boolean active,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        BannerDTO bannerDTO = BannerDTO.builder()
                .id(id)
                .title(title)
                .description(description)
                .link(link)
                .order(order)
                .active(active)
                .build();

        bannerService.updateBanner(id, bannerDTO, image);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật banner thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBanner(@PathVariable Long id) throws Exception {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa banner thành công"));
    }

    @PutMapping("/reorder")
    public ResponseEntity<?> reorderBanners(@RequestBody List<BannerDTO> banners) throws Exception {
        bannerService.reorderBanners(banners);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật thứ tự banner thành công"));
    }
}
