package com.project.techstore.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.models.Review;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.responses.review.ReviewResponse;
import com.project.techstore.services.review.IReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final IReviewService reviewService;

    @GetMapping("/product/{productId}/star-count")
    public ResponseEntity<?> getReviewStarCount(@PathVariable("productId") String productId) throws Exception {
        var starCount = reviewService.countReviewByStar(productId);
        return ResponseEntity.ok(ApiResponse.ok(starCount));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getReviewsByProduct(@PathVariable("productId") String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Review> reviews = reviewService.getReviewByProduct(productId, pageable);
        Page<ReviewResponse> reviewResponses = reviews.map(ReviewResponse::fromReview);

        return ResponseEntity.ok(ApiResponse.ok(reviewResponses));
    }

    @GetMapping("/product/{productId}/star/{star}")
    public ResponseEntity<?> getReviewsByProductAndStar(@PathVariable("productId") String productId,
            @PathVariable("star") int star,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
            throws Exception {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Review> reviews = reviewService.getReviewByProductAndStar(productId, star, pageable);
        Page<ReviewResponse> reviewResponses = reviews.map(ReviewResponse::fromReview);

        return ResponseEntity.ok(ApiResponse.ok(reviewResponses));
    }

}
