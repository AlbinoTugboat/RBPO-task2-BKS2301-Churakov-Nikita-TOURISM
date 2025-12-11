package com.example.tourismsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // ← ДОБАВЬТЕ ЭТУ АННОТАЦИЮ
public class TourismSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TourismSystemApplication.class, args);
    }
}