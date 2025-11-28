package com.example.tourismsystem.repository;

import com.example.tourismsystem.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    List<Destination> findByCountry(String country);
    List<Destination> findByCity(String city);
    List<Destination> findByNameContainingIgnoreCase(String name);

    @Query(value = """
        SELECT d.name as destinationName, COUNT(td.tour_id) as tourCount
        FROM destinations d
        LEFT JOIN tour_destinations td ON d.id = td.destination_id
        LEFT JOIN tours t ON td.tour_id = t.id AND t.status = 'ACTIVE'
        GROUP BY d.id, d.name
        ORDER BY tourCount DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Map<String, Object>> findPopularDestinations();
}