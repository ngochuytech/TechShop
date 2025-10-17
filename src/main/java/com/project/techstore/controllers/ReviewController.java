package com.project.techstore.controllers;

import com.project.techstore.components.JwtTokenProvider;
import com.project.techstore.dtos.ReviewDTO;
import com.project.techstore.models.Review;
import com.project.techstore.models.User;
import com.project.techstore.responses.review.ReviewResponse;
import com.project.techstore.services.IReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final IReviewService reviewService;

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReviewsByUser(@PathVariable("userId") String userId){
        try {
            return ResponseEntity.ok(reviewService.getReviewByUser(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getReviewsByProduct(@PathVariable("productId") String productId){
        try {
            List<Review> reviews = reviewService.getReviewByProduct(productId);
            List<ReviewResponse> reviewResponses = reviews.stream()
                    .map(ReviewResponse::fromReview)
                    .toList();

            return ResponseEntity.ok(reviewResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReview(@RequestBody @Valid ReviewDTO reviewDTO,
        @AuthenticationPrincipal User user
        , BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            reviewService.createReview(user, reviewDTO);
            return ResponseEntity.ok("Create a new review successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable("id") String id ,@RequestBody @Valid ReviewDTO reviewDTO,
                                          BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            reviewService.updateReview(id, reviewDTO);
            return ResponseEntity.ok("Update a review successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable("id") String id){
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok("Delete a review successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
