package com.example.tourismsystem.repository;

import com.example.tourismsystem.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    // Найти туры с доступными местами
    @Query("SELECT t FROM Tour t WHERE t.currentParticipants < t.maxParticipants")
    List<Tour> findAvailableTours();
}