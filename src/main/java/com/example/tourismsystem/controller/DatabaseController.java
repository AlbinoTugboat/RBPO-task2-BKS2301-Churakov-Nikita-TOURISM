package com.example.tourismsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/db")
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/tables")
    public List<String> getAllTables() {
        return jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
                String.class
        );
    }

    @GetMapping("/destinations")
    public List<Map<String, Object>> getDestinations() {
        return jdbcTemplate.queryForList("SELECT * FROM destinations");
    }

    @GetMapping("/guides")
    public List<Map<String, Object>> getGuides() {
        return jdbcTemplate.queryForList("SELECT * FROM guides");
    }

    @GetMapping("/tours")
    public List<Map<String, Object>> getTours() {
        return jdbcTemplate.queryForList("SELECT * FROM tours");
    }

    @GetMapping("/bookings")
    public List<Map<String, Object>> getBookings() {
        return jdbcTemplate.queryForList("SELECT * FROM bookings");
    }

    @GetMapping("/reviews")
    public List<Map<String, Object>> getReviews() {
        return jdbcTemplate.queryForList("SELECT * FROM reviews");
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Integer destinations = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM destinations", Integer.class);
        Integer guides = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM guides", Integer.class);
        Integer tours = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tours", Integer.class);
        Integer bookings = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM bookings", Integer.class);
        Integer reviews = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reviews", Integer.class);

        return Map.of(
                "destinations", destinations,
                "guides", guides,
                "tours", tours,
                "bookings", bookings,
                "reviews", reviews
        );
    }
}