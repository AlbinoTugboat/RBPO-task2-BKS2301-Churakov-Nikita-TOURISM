package com.example.tourismsystem.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/hello")
    public String publicHello() {
        return "Hello from public endpoint!";
    }
}