package com.example.tourismsystem.controller;

import com.example.tourismsystem.entity.Review;
import com.example.tourismsystem.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    // GET все отзывы
    @GetMapping
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // GET отзыв по ID
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        return review.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST создать отзыв
    @PostMapping
    public Review createReview(@RequestBody Review review) {
        review.setReviewDate(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    // PUT обновить отзыв
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id,
                                               @RequestBody Review reviewDetails) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setReviewText(reviewDetails.getReviewText());
            review.setRating(reviewDetails.getRating());
            review.setAuthorName(reviewDetails.getAuthorName());
            return ResponseEntity.ok(reviewRepository.save(review));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE удалить отзыв
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GET отзывы по ID тура
    @GetMapping("/tour/{tourId}")
    public List<Review> getReviewsByTourId(@PathVariable Long tourId) {
        return reviewRepository.findByTourId(tourId);
    }

    // GET отзывы по автору
    @GetMapping("/author/{authorName}")
    public List<Review> getReviewsByAuthor(@PathVariable String authorName) {
        return reviewRepository.findByAuthorName(authorName);
    }
}