package com.example.tourismsystem.service;

import com.example.tourismsystem.entity.*;
import com.example.tourismsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TourismBusinessService {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    /**
     * Бизнес-операция 1: Полное бронирование тура с проверкой доступности
     */
    @Transactional
    public Booking bookTourCompletely(Long tourId, String customerName, String customerEmail) {
        Optional<Tour> tourOpt = tourRepository.findById(tourId);
        if (tourOpt.isEmpty()) {
            throw new RuntimeException("Tour not found");
        }

        Tour tour = tourOpt.get();

        // Проверяем доступность мест
        if (!tour.hasAvailableSpots()) {
            throw new RuntimeException("No available spots for this tour");
        }

        // Проверяем что тур активен
        if (tour.getStatus() != TourStatus.ACTIVE) {
            throw new RuntimeException("Tour is not active for booking");
        }

        // Проверяем что дата тура еще не прошла
        if (tour.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot book past tours");
        }

        // Создаем бронирование
        Booking booking = new Booking();
        booking.setCustomerName(customerName);
        booking.setCustomerEmail(customerEmail);
        booking.setTour(tour);
        booking.setTotalPrice(tour.getPrice());
        booking.setStatus(BookingStatus.CONFIRMED);

        // Увеличиваем счетчик участников
        tour.setCurrentParticipants(tour.getCurrentParticipants() + 1);

        // Сохраняем изменения
        tourRepository.save(tour);
        return bookingRepository.save(booking);
    }

    /**
     * Бизнес-операция 2: Отмена бронирования с возвратом мест
     */
    @Transactional
    public boolean cancelBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return false;
        }

        Booking booking = bookingOpt.get();
        Tour tour = booking.getTour();

        // Можно отменять только подтвержденные бронирования
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed bookings can be cancelled");
        }

        // Нельзя отменять бронирования на уже начавшиеся туры
        if (tour.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot cancel booking for started tour");
        }

        // Возвращаем место
        tour.setCurrentParticipants(tour.getCurrentParticipants() - 1);
        booking.setStatus(BookingStatus.CANCELLED);

        tourRepository.save(tour);
        bookingRepository.save(booking);

        return true;
    }

    /**
     * Бизнес-операция 3: Поиск туров по комплексным критериям
     */
    @Transactional(readOnly = true)
    public List<Tour> searchTours(String destination, LocalDate startDate,
                                  LocalDate endDate, BigDecimal maxPrice,
                                  Integer minRating) {
        return tourRepository.findToursByComplexCriteria(destination, startDate, endDate, maxPrice, minRating);
    }

    /**
     * Бизнес-операция 4: Получение статистики по гиду
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getGuideStatistics(Long guideId) {
        Optional<Guide> guideOpt = guideRepository.findById(guideId);
        if (guideOpt.isEmpty()) {
            throw new RuntimeException("Guide not found");
        }

        Guide guide = guideOpt.get();
        List<Tour> guideTours = tourRepository.findByGuideId(guideId);

        long totalTours = guideTours.size();
        long activeTours = guideTours.stream()
                .filter(tour -> tour.getStatus() == TourStatus.ACTIVE)
                .count();
        long completedTours = guideTours.stream()
                .filter(tour -> tour.getStatus() == TourStatus.COMPLETED)
                .count();

        double averageRating = reviewRepository.findByGuideId(guideId)
                .stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        int totalParticipants = guideTours.stream()
                .mapToInt(Tour::getCurrentParticipants)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("guideName", guide.getName());
        stats.put("totalTours", totalTours);
        stats.put("activeTours", activeTours);
        stats.put("completedTours", completedTours);
        stats.put("averageRating", Math.round(averageRating * 10.0) / 10.0);
        stats.put("totalParticipants", totalParticipants);
        stats.put("experienceYears", guide.getExperienceYears());

        return stats;
    }

    /**
     * Бизнес-операция 5: Создание отзыва с проверкой участия в туре
     */
    @Transactional
    public Review createReviewWithValidation(Long bookingId, String reviewText,
                                             Integer rating, String authorName) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }

        Booking booking = bookingOpt.get();

        // Проверяем что бронирование завершено
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Can only review completed tours");
        }

        // Проверяем что автор отзыва совпадает с именем в бронировании
        if (!booking.getCustomerName().equals(authorName)) {
            throw new RuntimeException("Author name must match booking customer name");
        }

        // Проверяем что отзыв еще не оставлен
        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new RuntimeException("Review already exists for this booking");
        }

        // Проверяем что тур завершен
        Tour tour = booking.getTour();
        if (tour.getEndDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Cannot review ongoing tour");
        }

        Review review = new Review();
        review.setTour(tour);
        review.setBooking(booking);
        review.setReviewText(reviewText);
        review.setRating(rating);
        review.setAuthorName(authorName);

        return reviewRepository.save(review);
    }

    /**
     * Бизнес-операция 6: Получение популярных направлений
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPopularDestinations() {
        return destinationRepository.findPopularDestinations();
    }

    /**
     * Бизнес-операция 7: Полная отмена тура с возвратом всех бронирований
     */
    @Transactional
    public boolean cancelTourCompletely(Long tourId) {
        Optional<Tour> tourOpt = tourRepository.findById(tourId);
        if (tourOpt.isEmpty()) {
            return false;
        }

        Tour tour = tourOpt.get();

        // Отменяем все бронирования
        List<Booking> bookings = bookingRepository.findByTourId(tourId);
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.CONFIRMED) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            }
        }

        // Обновляем статус тура
        tour.setStatus(TourStatus.CANCELLED);
        tourRepository.save(tour);

        return true;
    }
}