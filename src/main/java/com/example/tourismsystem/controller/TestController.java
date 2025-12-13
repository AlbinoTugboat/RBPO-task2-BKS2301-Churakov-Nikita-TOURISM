package com.example.tourismsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/ssl")
    public String testSSL() {
        return "✅ TLS/SSL работает! Время сервера: " + LocalDateTime.now();
    }

    @GetMapping("/cert-info")
    public String certInfo() {
        return """
                Информация о сертификате:
                • Владелец: Чураков Никита
                • Студенческий ID: 23238
                • Цепочка: 3 сертификата
                • Срок действия: 365 дней
                """;
    }
}