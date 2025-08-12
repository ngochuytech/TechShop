package com.project.techstore.services;

import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Review;
import com.project.techstore.models.ReviewAttachment;
import com.project.techstore.repositories.ReviewAttachmentRepository;
import com.project.techstore.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewAttachmentService implements IReviewAttachmentService{
    private final ReviewAttachmentRepository reviewAttachmentRepository;

    private final ReviewRepository reviewRepository;

    @Override
    public List<ReviewAttachment> getByReview(String reviewId) throws Exception {
        if(!reviewRepository.existsById(reviewId))
            throw new DataNotFoundException("Review doesn't exist");

        return reviewAttachmentRepository.findByReviewId(reviewId);
    }


    @Override
    public ReviewAttachment createReviewAttachment(String reviewId, MultipartFile file, String comment) throws Exception {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new DataNotFoundException("Review doesn't exist"));

        String uploadDir = "uploads/reviews/";
        Files.createDirectories(Path.of(uploadDir));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.copy(file.getInputStream(), filePath);
        ReviewAttachment attachment = ReviewAttachment.builder()
                .attachmentPath(filePath.toString())
                .attachmentType(file.getContentType())
                .comment(comment)
                .review(review)
                .build();
        return reviewAttachmentRepository.save(attachment);
    }
}
