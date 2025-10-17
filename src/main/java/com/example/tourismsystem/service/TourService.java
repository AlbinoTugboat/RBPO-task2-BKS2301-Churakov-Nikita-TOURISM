package com.example.tourismsystem.service;

import com.example.tourismsystem.entity.Tour;
import com.example.tourismsystem.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TourService {

    @Autowired
    private TourRepository tourRepository;

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    public Optional<Tour> getTourById(Long id) {
        return tourRepository.findById(id);
    }

    public Tour createTour(Tour tour) {
        return tourRepository.save(tour);
    }

    public Tour updateTour(Long id, Tour tourDetails) {
        Optional<Tour> optionalTour = tourRepository.findById(id);
        if (optionalTour.isPresent()) {
            Tour tour = optionalTour.get();
            tour.setTitle(tourDetails.getTitle());
            tour.setDescription(tourDetails.getDescription());
            tour.setStartDate(tourDetails.getStartDate());
            tour.setEndDate(tourDetails.getEndDate());
            tour.setMaxParticipants(tourDetails.getMaxParticipants());
            tour.setPrice(tourDetails.getPrice());
            tour.setGuide(tourDetails.getGuide());
            tour.setDestinations(tourDetails.getDestinations());
            return tourRepository.save(tour);
        }
        return null;
    }

    public boolean deleteTour(Long id) {
        if (tourRepository.existsById(id)) {
            tourRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Tour> getAvailableTours() {
        return tourRepository.findAvailableTours();
    }
}