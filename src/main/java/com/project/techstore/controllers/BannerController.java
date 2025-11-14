package com.project.techstore.controllers;

import com.project.techstore.models.Banner;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.banner.IBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/banners")
@RequiredArgsConstructor
public class BannerController {

    private final IBannerService bannerService;

    @GetMapping
    public ResponseEntity<?> getAllBanners() throws Exception {
        List<Banner> banners = bannerService.getActiveBanners();
        return ResponseEntity.ok(ApiResponse.ok(banners));
    }
}
