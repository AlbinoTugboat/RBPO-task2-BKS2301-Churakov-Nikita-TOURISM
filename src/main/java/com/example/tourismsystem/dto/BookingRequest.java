package com.example.tourismsystem.dto;

public class BookingRequest {
    private String customerName;
    private String customerEmail;
    private Long tourId;

    // Конструкторы
    public BookingRequest() {}

    // Геттеры и сеттеры
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
}