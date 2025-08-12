package com.project.techstore.repositories;

import com.project.techstore.models.ReviewAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewAttachmentRepository extends JpaRepository<ReviewAttachment, String> {
    List<ReviewAttachment> findByReviewId(String reviewId);
}
