package com.example.tourismsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/csrf-demo")
public class CsrfDemoController {

    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submit() {
        return ResponseEntity.ok(Map.of("message", "CSRF check passed"));
    }
}
