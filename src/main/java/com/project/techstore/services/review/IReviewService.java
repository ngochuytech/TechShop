package com.project.techstore.services.review;

import com.project.techstore.dtos.ReviewDTO;
import com.project.techstore.models.Review;
import com.project.techstore.models.User;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReviewService {
    List<Review> getReviewByUser(String userEmail) throws Exception;

    List<Review> getReviewByProduct(String productId) throws Exception;

    Map<Integer, Long> countReviewByStar(String productId) throws Exception;

    Page<Review> getReviewByProduct(String productId, Pageable pageable) throws Exception;

    Page<Review> getReviewByProductAndStar(String productId, int star, Pageable pageable) throws Exception;

    Review createReview(User user, ReviewDTO reviewDTO) throws Exception;

    Review updateReview(String id, ReviewDTO reviewDTO) throws Exception;

    void deleteReview(String id) throws Exception;
}
