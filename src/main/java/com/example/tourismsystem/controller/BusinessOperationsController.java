package com.example.tourismsystem.controller;

import com.example.tourismsystem.service.TourismBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class BusinessOperationsController {

    @Autowired
    private TourismBusinessService businessService;

    /**
     * Операция 1: Полное бронирование тура
     */
    @PostMapping("/tours/{tourId}/book")
    public ResponseEntity<?> bookTourCompletely(
            @PathVariable Long tourId,
            @RequestBody Map<String, String> request) {
        try {
            String customerName = request.get("customerName");
            String customerEmail = request.get("customerEmail");

            var booking = businessService.bookTourCompletely(tourId, customerName, customerEmail);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Операция 2: Отмена бронирования
     */
    @PostMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            boolean success = businessService.cancelBooking(bookingId);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Операция 3: Поиск туров по критериям
     */
    @GetMapping("/tours/search")
    public ResponseEntity<List<?>> searchTours(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minRating) {

        var tours = businessService.searchTours(destination, startDate, endDate, maxPrice, minRating);
        return ResponseEntity.ok(tours);
    }

    /**
     * Операция 4: Статистика по гиду
     */
    @GetMapping("/guides/{guideId}/statistics")
    public ResponseEntity<?> getGuideStatistics(@PathVariable Long guideId) {
        try {
            var stats = businessService.getGuideStatistics(guideId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Операция 5: Создание отзыва с проверкой
     */
    @PostMapping("/reviews/create-with-validation")
    public ResponseEntity<?> createReviewWithValidation(@RequestBody Map<String, Object> request) {
        try {
            Long bookingId = Long.valueOf(request.get("bookingId").toString());
            String reviewText = (String) request.get("reviewText");
            Integer rating = (Integer) request.get("rating");
            String authorName = (String) request.get("authorName");

            var review = businessService.createReviewWithValidation(bookingId, reviewText, rating, authorName);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Операция 6: Популярные направления
     */
    @GetMapping("/destinations/popular")
    public ResponseEntity<List<Map<String, Object>>> getPopularDestinations() {
        var destinations = businessService.getPopularDestinations();
        return ResponseEntity.ok(destinations);
    }

    /**
     * Операция 7: Полная отмена тура
     */
    @PostMapping("/tours/{tourId}/cancel-completely")
    public ResponseEntity<?> cancelTourCompletely(@PathVariable Long tourId) {
        boolean success = businessService.cancelTourCompletely(tourId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Tour and all bookings cancelled successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}