package com.example.tourismsystem.repository;

import com.example.tourismsystem.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findByGuideId(Long guideId);

    @Query("SELECT t FROM Tour t WHERE t.currentParticipants < t.maxParticipants AND t.status = 'ACTIVE'")
    List<Tour> findAvailableTours();

    @Query("SELECT t FROM Tour t JOIN t.destinations d WHERE " +
            "(:destination IS NULL OR d.name LIKE %:destination% OR d.city LIKE %:destination%) AND " +
            "(:startDate IS NULL OR t.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR t.endDate <= :endDate) AND " +
            "(:maxPrice IS NULL OR t.price <= :maxPrice) AND " +
            "t.status = 'ACTIVE'")
    List<Tour> findToursByComplexCriteria(@Param("destination") String destination,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("maxPrice") BigDecimal maxPrice);

    // Перегруженный метод для поиска с рейтингом
    @Query("SELECT t FROM Tour t JOIN t.destinations d LEFT JOIN t.reviews r WHERE " +
            "(:destination IS NULL OR d.name LIKE %:destination% OR d.city LIKE %:destination%) AND " +
            "(:startDate IS NULL OR t.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR t.endDate <= :endDate) AND " +
            "(:maxPrice IS NULL OR t.price <= :maxPrice) AND " +
            "(:minRating IS NULL OR (SELECT AVG(r2.rating) FROM Review r2 WHERE r2.tour = t) >= :minRating) AND " +
            "t.status = 'ACTIVE' " +
            "GROUP BY t HAVING (:minRating IS NULL OR AVG(r.rating) >= :minRating)")
    List<Tour> findToursByComplexCriteria(@Param("destination") String destination,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("maxPrice") BigDecimal maxPrice,
                                          @Param("minRating") Integer minRating);
}