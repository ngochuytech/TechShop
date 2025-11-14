package com.project.techstore.controllers.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.Review;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.review.ReviewResponse;
import com.project.techstore.services.review.IReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final IReviewService reviewService;

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentReviews(@RequestParam(value = "limit", defaultValue = "5") Integer limit) throws Exception {
        List<Review> recentReviews = reviewService.getRecentReviews(limit);
        List<ReviewResponse> reviewResponses = recentReviews.stream()
                .map(ReviewResponse::fromReview)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(reviewResponses));
    }
}
