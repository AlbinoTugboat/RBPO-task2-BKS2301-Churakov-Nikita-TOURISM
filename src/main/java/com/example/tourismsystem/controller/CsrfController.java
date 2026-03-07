package com.example.tourismsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CsrfController {

    private final CsrfTokenRepository csrfTokenRepository;

    public CsrfController(CsrfTokenRepository csrfTokenRepository) {
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @GetMapping({"/api/csrf-token", "/csrf/token"})
    public ResponseEntity<?> getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken csrfToken = csrfTokenRepository.loadToken(request);

        if (csrfToken == null) {
            csrfToken = csrfTokenRepository.generateToken(request);
            csrfTokenRepository.saveToken(csrfToken, request, response);
        }

        Map<String, String> payload = new HashMap<>();
        payload.put("token", csrfToken.getToken());
        payload.put("headerName", csrfToken.getHeaderName());
        payload.put("parameterName", csrfToken.getParameterName());

        return ResponseEntity.ok(payload);
    }
}
