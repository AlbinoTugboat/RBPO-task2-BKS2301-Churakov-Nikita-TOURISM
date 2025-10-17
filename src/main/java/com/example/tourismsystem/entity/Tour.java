package com.example.tourismsystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "max_participants")
    private Integer maxParticipants = 10; // Значение по умолчанию

    @Column(name = "current_participants")
    private Integer currentParticipants = 0;

    private BigDecimal price = BigDecimal.ZERO; // Значение по умолчанию

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Guide guide;

    @ManyToMany
    @JoinTable(
            name = "tour_destinations",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "destination_id")
    )
    private List<Destination> destinations = new ArrayList<>();

    // Конструкторы
    public Tour() {}

    public Tour(String title, String description, LocalDate startDate, LocalDate endDate,
                Integer maxParticipants, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxParticipants = maxParticipants != null ? maxParticipants : 10;
        this.price = price != null ? price : BigDecimal.ZERO;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getMaxParticipants() {
        return maxParticipants != null ? maxParticipants : 10;
    }
    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants != null ? maxParticipants : 10;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants != null ? currentParticipants : 0;
    }
    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants != null ? currentParticipants : 0;
    }

    public BigDecimal getPrice() {
        return price != null ? price : BigDecimal.ZERO;
    }
    public void setPrice(BigDecimal price) {
        this.price = price != null ? price : BigDecimal.ZERO;
    }

    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }

    public List<Destination> getDestinations() { return destinations; }
    public void setDestinations(List<Destination> destinations) { this.destinations = destinations; }

    // Бизнес-методы с проверками на null
    public boolean hasAvailableSpots() {
        return getCurrentParticipants() < getMaxParticipants();
    }

    public boolean addParticipant() {
        if (hasAvailableSpots()) {
            setCurrentParticipants(getCurrentParticipants() + 1);
            return true;
        }
        return false;
    }

    public boolean removeParticipant() {
        if (getCurrentParticipants() > 0) {
            setCurrentParticipants(getCurrentParticipants() - 1);
            return true;
        }
        return false;
    }
}