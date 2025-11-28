package com.example.tourismsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Start date is mandatory")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is mandatory")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Min(value = 1, message = "Max participants must be at least 1")
    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants = 10;

    @Column(name = "current_participants")
    private Integer currentParticipants = 0;

    @NotNull(message = "Price is mandatory")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TourStatus status = TourStatus.ACTIVE;

    @ManyToMany
    @JoinTable(
            name = "tour_destinations",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "destination_id")
    )
    private List<Destination> destinations = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public TourStatus getStatus() { return status; }
    public void setStatus(TourStatus status) { this.status = status; }

    public List<Destination> getDestinations() { return destinations; }
    public void setDestinations(List<Destination> destinations) { this.destinations = destinations; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

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