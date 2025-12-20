package com.example.tourismsystem.controller;

import com.example.tourismsystem.dto.ReviewDTO;
import com.example.tourismsystem.entity.Review;
import com.example.tourismsystem.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    // Преобразование Entity в DTO
    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getReviewText(),
                review.getRating(),
                review.getAuthorName(),
                review.getReviewDate(),
                review.getTour() != null ? review.getTour().getId() : null,
                review.getBooking() != null ? review.getBooking().getId() : null
        );
    }

    // GET все отзывы
    @GetMapping
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // GET отзыв по ID
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        return review.map(r -> ResponseEntity.ok(convertToDTO(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST создать отзыв - теперь возвращаем DTO
    @PostMapping
    public ReviewDTO createReview(@RequestBody Review review) {
        review.setReviewDate(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    // PUT обновить отзыв - теперь возвращаем DTO
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id,
                                                  @RequestBody Review reviewDetails) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setReviewText(reviewDetails.getReviewText());
            review.setRating(reviewDetails.getRating());
            review.setAuthorName(reviewDetails.getAuthorName());
            Review updatedReview = reviewRepository.save(review);
            return ResponseEntity.ok(convertToDTO(updatedReview));
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
    public List<ReviewDTO> getReviewsByTourId(@PathVariable Long tourId) {
        return reviewRepository.findByTourId(tourId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // GET отзывы по автору
    @GetMapping("/author/{authorName}")
    public List<ReviewDTO> getReviewsByAuthor(@PathVariable String authorName) {
        return reviewRepository.findByAuthorName(authorName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}