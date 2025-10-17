package com.example.tourismsystem.controller;

import com.example.tourismsystem.entity.Tour;
import com.example.tourismsystem.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    @Autowired
    private TourService tourService;

    // GET все туры
    @GetMapping
    public List<Tour> getAllTours() {
        return tourService.getAllTours();
    }

    // GET тур по ID
    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable Long id) {
        Optional<Tour> tour = tourService.getTourById(id);
        return tour.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST создать тур
    @PostMapping
    public Tour createTour(@RequestBody Tour tour) {
        return tourService.createTour(tour);
    }

    // PUT обновить тур
    @PutMapping("/{id}")
    public ResponseEntity<Tour> updateTour(@PathVariable Long id,
                                           @RequestBody Tour tourDetails) {
        Tour updatedTour = tourService.updateTour(id, tourDetails);
        if (updatedTour != null) {
            return ResponseEntity.ok(updatedTour);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE удалить тур
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        if (tourService.deleteTour(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GET доступные туры (свободные места)
    @GetMapping("/available")
    public List<Tour> getAvailableTours() {
        return tourService.getAvailableTours();
    }
}