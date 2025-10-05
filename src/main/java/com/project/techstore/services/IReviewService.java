package com.project.techstore.services;

import com.project.techstore.dtos.ReviewDTO;
import com.project.techstore.models.Review;
import com.project.techstore.models.User;

import java.util.List;

public interface IReviewService {
    List<Review> getReviewByUser(String userEmail) throws Exception;

    List<Review> getReviewByProduct(String productId) throws Exception;

    Review createReview(User user, ReviewDTO reviewDTO) throws Exception;

    Review updateReview(String id, ReviewDTO reviewDTO) throws Exception;

    void deleteReview(String id) throws Exception;
}
