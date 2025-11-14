package com.project.techstore.controllers.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.brand.IBrandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/brands")
@RequiredArgsConstructor
public class AdminBrandController {
    private final IBrandService brandService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createBrand(
            @RequestPart(value = "name") String name,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        brandService.createBrand(name, image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo thương hiệu thành công"));
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateBrand(
            @PathVariable Long id,
            @RequestPart(value = "name") String name,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        brandService.updateBrand(id, name, image);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật thương hiệu thành công"));
    }
}
