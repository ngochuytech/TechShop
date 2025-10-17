package com.project.techstore.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload file lên Cloudinary với folder path tùy chỉnh
     * @param file MultipartFile cần upload
     * @param category thể loại ảnh (vd: "products", "users", "reviews", "variants")
     * @param filePath đường dẫn file
     * @return URL của ảnh đã upload
     */
    public String uploadFile(MultipartFile file, String category,String filePath) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        Map<String, Object> options = new HashMap<>();

        if(category != null && !category.isEmpty()){
            options.put("folder", "techstore/" + category);
        } else {
            options.put("folder", "techstore/misc");
        }

        // Upload với các options
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

        return (String) uploadResult.get("secure_url");
    }

    /**
     * Upload ảnh sản phẩm lên Cloudinary
     */
    public String uploadProductImage(MultipartFile file, String productId) throws IOException {
        return uploadFile(file, "products", productId);
    }

    /**
     * Upload ảnh variant lên Cloudinary
     */
    public String uploadVariantImage(MultipartFile file, String variantId) throws IOException {
        return uploadFile(file, "variants", variantId);
    }

    /**
     * Upload ảnh review lên Cloudinary
     */
    public String uploadReviewImage(MultipartFile file, String reviewId) throws IOException {
        return uploadFile(file, "reviews", reviewId);
    }

    /**
     * Upload ảnh user/avatar lên Cloudinary
     */
    public String uploadUserImage(MultipartFile file, String userId) throws IOException {
        return uploadFile(file, "users", userId);
    }

    /**
     * Xóa file từ Cloudinary bằng public_id
     */
    public void deleteFile(String publicId) throws IOException {
        if (publicId == null || publicId.isEmpty()) {
            return;
        }
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    /**
     * Xóa ảnh từ Cloudinary bằng URL
     */
    public void deleteImageByUrl(String imageUrl) throws IOException {
        String publicId = extractPublicIdFromUrl(imageUrl);
        if (publicId != null) {
            deleteFile(publicId);
        }
    }

    /**
     * Lấy public_id từ Cloudinary URL
     */
    public String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        // URL format: https://res.cloudinary.com/[cloud_name]/image/upload/[version]/[public_id].[format]
        // Hoặc: https://res.cloudinary.com/[cloud_name]/image/upload/[public_id].[format]
        try {
            String[] parts = imageUrl.split("/");
            
            // Tìm index của "upload"
            int uploadIndex = -1;
            for (int i = 0; i < parts.length; i++) {
                if ("upload".equals(parts[i])) {
                    uploadIndex = i;
                    break;
                }
            }
            
            if (uploadIndex == -1 || uploadIndex >= parts.length - 1) {
                return null;
            }
            
            // Lấy tất cả parts sau "upload" và ghép lại thành public_id
            StringBuilder publicIdBuilder = new StringBuilder();
            for (int i = uploadIndex + 1; i < parts.length; i++) {
                if (i > uploadIndex + 1) {
                    publicIdBuilder.append("/");
                }
                String part = parts[i];
                // Bỏ extension khỏi part cuối cùng
                if (i == parts.length - 1 && part.contains(".")) {
                    part = part.substring(0, part.lastIndexOf('.'));
                }
                publicIdBuilder.append(part);
            }
            
            return publicIdBuilder.toString();
        } catch (Exception e) {
            return null;
        }
    }
}