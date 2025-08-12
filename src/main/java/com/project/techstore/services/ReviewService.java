package com.project.techstore.services;

import com.project.techstore.dtos.ReviewDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Product;
import com.project.techstore.models.Review;
import com.project.techstore.models.User;
import com.project.techstore.repositories.ProductRepository;
import com.project.techstore.repositories.ReviewRepository;
import com.project.techstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService{
    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    @Override
    public List<Review> getReviewByUser(String userId) throws Exception {
        if(!userRepository.existsById(userId))
            throw new DataNotFoundException("User doesn't exist");
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public List<Review> getReviewByProduct(String productId) throws Exception {
        if(!productRepository.existsById(productId))
            throw new DataNotFoundException("Product doesn't exist");
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public Review createReview(ReviewDTO reviewDTO) throws Exception {
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User doesn't exist"));
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Product doesn't exist"));
        Review review = Review.builder()
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .user(user)
                .product(product)
                .build();
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(String id, ReviewDTO reviewDTO) throws Exception {
        Review reviewExisting = reviewRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("This review doesn't exist"));
        // (OWNERSHIP) Cần check phải user chính chủ mới update review của họ được
        reviewExisting.setRating(reviewDTO.getRating());
        reviewExisting.setComment(reviewDTO.getComment());
        return reviewRepository.save(reviewExisting);
    }

    @Override
    public void deleteReview(String id) throws Exception {
        if(!reviewRepository.existsById(id))
            throw new DataNotFoundException("This review doesn't exist");
        reviewRepository.deleteById(id);
    }
}
