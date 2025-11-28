package com.example.tourismsystem.repository;

import com.example.tourismsystem.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTourId(Long tourId);
    List<Review> findByAuthorName(String authorName);

    @Query("SELECT r FROM Review r WHERE r.rating >= :minRating")
    List<Review> findByRatingGreaterThanEqual(@Param("minRating") Integer minRating);

    boolean existsByBookingId(Long bookingId);

    @Query("SELECT r FROM Review r JOIN r.tour t JOIN t.guide g WHERE g.id = :guideId")
    List<Review> findByGuideId(@Param("guideId") Long guideId);
}