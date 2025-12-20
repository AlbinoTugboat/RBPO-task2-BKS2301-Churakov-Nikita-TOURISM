package com.example.tourismsystem.controller;

import com.example.tourismsystem.dto.BookingRequest;
import com.example.tourismsystem.entity.Booking;
import com.example.tourismsystem.entity.Tour;
import com.example.tourismsystem.repository.BookingRepository;
import com.example.tourismsystem.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TourRepository tourRepository;

    // GET все бронирования
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // GET бронирование по ID
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST создать бронирование (используем DTO)
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        // Проверяем обязательные поля
        if (bookingRequest.getCustomerName() == null || bookingRequest.getCustomerName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Customer name is required");
        }
        if (bookingRequest.getTourId() == null) {
            return ResponseEntity.badRequest().body("Tour ID is required");
        }

        // Находим тур
        Optional<Tour> optionalTour = tourRepository.findById(bookingRequest.getTourId());
        if (optionalTour.isEmpty()) {
            return ResponseEntity.badRequest().body("Tour not found");
        }

        Tour tour = optionalTour.get();

        // Проверяем доступность мест
        if (!tour.hasAvailableSpots()) {
            return ResponseEntity.badRequest().body("No available spots for this tour");
        }

        try {
            // Создаем бронирование
            Booking booking = new Booking();
            booking.setCustomerName(bookingRequest.getCustomerName());
            booking.setCustomerEmail(bookingRequest.getCustomerEmail());
            booking.setTour(tour);
            booking.setBookingDate(LocalDateTime.now());
            booking.setTotalPrice(tour.getPrice());
            // Увеличиваем счетчик участников
            boolean added = tour.addParticipant();
            if (!added) {
                return ResponseEntity.badRequest().body("Failed to add participant - no available spots");
            }

            // Сохраняем тур и бронирование
            tourRepository.save(tour);
            Booking savedBooking = bookingRepository.save(booking);

            return ResponseEntity.ok(savedBooking);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error creating booking: " + e.getMessage());
        }
    }

    // PUT обновить бронирование
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id,
                                                 @RequestBody Booking bookingDetails) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setCustomerName(bookingDetails.getCustomerName());
            booking.setCustomerEmail(bookingDetails.getCustomerEmail());
            return ResponseEntity.ok(bookingRepository.save(booking));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE удалить бронирование
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            // Уменьшаем счетчик участников при отмене бронирования
            Tour tour = booking.getTour();
            if (tour != null) {
                tour.removeParticipant();
                tourRepository.save(tour);
            }
            bookingRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GET бронирования по email клиента
    @GetMapping("/customer/{email}")
    public List<Booking> getBookingsByCustomerEmail(@PathVariable String email) {
        return bookingRepository.findByCustomerEmail(email);
    }

    // GET бронирования по ID тура
    @GetMapping("/tour/{tourId}")
    public List<Booking> getBookingsByTourId(@PathVariable Long tourId) {
        return bookingRepository.findByTourId(tourId);
    }
}