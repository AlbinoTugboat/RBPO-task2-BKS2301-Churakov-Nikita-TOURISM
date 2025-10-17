package com.example.tourismsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    private Integer rating;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Column(name = "author_name")
    private String authorName;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;

    // Конструкторы
    public Review() {}

    public Review(String reviewText, Integer rating, String authorName, Tour tour) {
        this.reviewText = reviewText;
        this.rating = rating;
        this.authorName = authorName;
        this.tour = tour;
        this.reviewDate = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
}