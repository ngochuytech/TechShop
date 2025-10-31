package com.project.techstore.controllers.customer;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.techstore.dtos.ReviewDTO;
import com.project.techstore.models.User;
import com.project.techstore.services.review.IReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/customer/reviews")
@RequiredArgsConstructor
public class CustomerReviewController {
    private final IReviewService reviewService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReviewsByUser(@PathVariable("userId") String userId) {
        try {
            return ResponseEntity.ok(reviewService.getReviewByUser(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createReview(@RequestBody @Valid ReviewDTO reviewDTO,
            @AuthenticationPrincipal User user, BindingResult result) {
        try {
            if (result.hasErrors()) {
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable("id") String id, @RequestBody @Valid ReviewDTO reviewDTO,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable("id") String id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok("Delete a review successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
