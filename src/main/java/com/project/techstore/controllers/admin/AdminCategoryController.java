package com.project.techstore.controllers.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.category.ICategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final ICategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestParam String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tên danh mục không được để trống"));
        }

        categoryService.createCategory(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm danh mục thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestParam String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tên danh mục không được để trống"));
        }

        categoryService.updateCategory(id, name);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật danh mục thành công"));
    }
}
