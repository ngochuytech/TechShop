package com.project.techstore.services;

import com.project.techstore.models.ReviewAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IReviewAttachmentService {
    List<ReviewAttachment> getByReview(String reviewId) throws Exception;

    ReviewAttachment createReviewAttachment(String reviewId, MultipartFile file, String comment) throws Exception;
}
