package com.project.techstore.services.review;

import com.project.techstore.dtos.ReviewDTO;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.Product;
import com.project.techstore.models.Review;
import com.project.techstore.models.User;
import com.project.techstore.repositories.ProductRepository;
import com.project.techstore.repositories.ReviewRepository;
import com.project.techstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    @Override
    public List<Review> getReviewByUser(String userEmail) throws Exception {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new DataNotFoundException("User doesn't exist"));
        return reviewRepository.findByUserId(user.getId());
    }

    @Override
    public List<Review> getReviewByProduct(String productId) throws Exception {
        if (!productRepository.existsById(productId))
            throw new DataNotFoundException("Product doesn't exist");
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public Page<Review> getReviewByProduct(String productId, Pageable pageable) throws Exception {
        if (!productRepository.existsById(productId))
            throw new DataNotFoundException("Product doesn't exist");
        return reviewRepository.findByProductId(productId, pageable);
    }

    @Override
    public Review createReview(User user, ReviewDTO reviewDTO) throws Exception {
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

        reviewExisting.setRating(reviewDTO.getRating());
        reviewExisting.setComment(reviewDTO.getComment());
        return reviewRepository.save(reviewExisting);
    }

    @Override
    public void deleteReview(String id) throws Exception {
        if (!reviewRepository.existsById(id))
            throw new DataNotFoundException("This review doesn't exist");
        reviewRepository.deleteById(id);
    }

    @Override
    public Page<Review> getReviewByProductAndStar(String productId, int star, Pageable pageable) throws Exception {
        Page<Review> reviews = reviewRepository.findByProductIdAndRating(productId, star, pageable);
        return reviews;
    }

    @Override
    public Map<Integer, Long> countReviewByStar(String productId) throws Exception {
        if (!productRepository.existsById(productId))
            throw new DataNotFoundException("Product doesn't exist");

        List<Object[]> results = reviewRepository.countReviewByStar(productId);
        Map<Integer, Long> starCount = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            starCount.put(i, 0L);
        }
        for (Object[] row : results) {
            Integer star = (Integer) row[0];
            Long count = (Long) row[1];
            starCount.put(star, count);
        }
        return starCount;
    }

    @Override
    public List<Review> getRecentReviews(int limit) throws Exception {
        return reviewRepository.getRecentReviews(limit);
    }
}
