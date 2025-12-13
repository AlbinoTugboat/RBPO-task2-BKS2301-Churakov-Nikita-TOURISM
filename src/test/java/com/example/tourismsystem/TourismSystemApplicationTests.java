package com.example.tourismsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Эта строка загрузит application-test.properties
class TourismSystemApplicationTests {
    @Test
    void contextLoads() {
        // Тест проверяет, загружается ли контекст приложения
    }
}