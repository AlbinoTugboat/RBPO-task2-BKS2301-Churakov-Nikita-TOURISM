package com.example.tourismsystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ReviewDTO {
    private Long id;
    private String reviewText;
    private Integer rating;
    private String authorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewDate;

    private Long tourId;
    private Long bookingId;

    // Конструкторы
    public ReviewDTO() {}

    public ReviewDTO(Long id, String reviewText, Integer rating,
                     String authorName, LocalDateTime reviewDate,
                     Long tourId, Long bookingId) {
        this.id = id;
        this.reviewText = reviewText;
        this.rating = rating;
        this.authorName = authorName;
        this.reviewDate = reviewDate;
        this.tourId = tourId;
        this.bookingId = bookingId;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Long getTourId() {
        return tourId;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
}