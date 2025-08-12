package com.project.techstore.services;

import com.project.techstore.dtos.ReviewDTO;
import com.project.techstore.models.Review;

import java.util.List;

public interface IReviewService {
    List<Review> getReviewByUser(String userId) throws Exception;

    List<Review> getReviewByProduct(String productId) throws Exception;

    Review createReview(ReviewDTO reviewDTO) throws Exception;

    Review updateReview(String id, ReviewDTO reviewDTO) throws Exception;

    void deleteReview(String id) throws Exception;
}
