package com.example.tourismsystem.controller;

import com.example.tourismsystem.dto.*;
import com.example.tourismsystem.entity.ERole;
import com.example.tourismsystem.entity.Role;
import com.example.tourismsystem.entity.User;
import com.example.tourismsystem.repository.RoleRepository;
import com.example.tourismsystem.repository.UserRepository;
import com.example.tourismsystem.service.PasswordValidatorService;
import com.example.tourismsystem.service.TokenPairService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private PasswordValidatorService passwordValidator;

    @Autowired
    private TokenPairService tokenPairService;

    /**
     * Регистрация нового пользователя
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Проверяем существование username
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body("Error: Username is already taken!");
        }

        // Проверяем существование email
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Error: Email is already in use!");
        }

        // Валидируем пароль
        PasswordValidatorService.PasswordValidationResult validationResult =
                passwordValidator.validatePassword(signUpRequest.getPassword());

        if (!validationResult.isValid()) {
            return ResponseEntity.badRequest()
                    .body("Error: " + validationResult.getMessage());
        }

        // Создаем нового пользователя
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());

        Set<Role> roles = new HashSet<>();

        // По умолчанию даем роль USER
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseGet(() -> {
                    // Если роль не найдена, создаем ее
                    Role newRole = new Role(ERole.ROLE_USER);
                    return roleRepository.save(newRole);
                });
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * Аутентификация пользователя и получение пары токенов (access + refresh)
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletRequest request) {
        try {
            // Получаем IP-адрес и User-Agent из запроса
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            // Аутентифицируем пользователя и получаем токены
            TokenPairResponse tokenPair = tokenPairService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword(),
                    ipAddress,
                    userAgent
            );

            // Формируем ответ
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Authentication successful");
            response.put("accessToken", tokenPair.getAccessToken());
            response.put("refreshToken", tokenPair.getRefreshToken());
            response.put("tokenType", "Bearer");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: Invalid username or password - " + e.getMessage());
        }
    }

    /**
     * Обновление пары токенов с помощью refresh токена
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest,
                                          HttpServletRequest request) {
        try {
            // Получаем IP-адрес и User-Agent из запроса
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            // Обновляем токены
            TokenPairResponse tokenPair = tokenPairService.refreshTokens(
                    refreshRequest.getRefreshToken(),
                    ipAddress,
                    userAgent
            );

            // Формируем ответ
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tokens refreshed successfully");
            response.put("accessToken", tokenPair.getAccessToken());
            response.put("refreshToken", tokenPair.getRefreshToken());
            response.put("tokenType", "Bearer");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * Выход из системы (отзыв refresh токена)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        try {
            tokenPairService.revokeSession(refreshRequest.getRefreshToken());
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * Проверка состояния аутентификации
     */
    @GetMapping("/status")
    public ResponseEntity<?> checkAuthStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Authentication endpoint is working");
        response.put("endpoints", new String[]{
                "POST /api/auth/signup - Register new user",
                "POST /api/auth/login - Login and get tokens",
                "POST /api/auth/refresh - Refresh tokens",
                "POST /api/auth/logout - Logout"
        });
        return ResponseEntity.ok(response);
    }

    /**
     * Вспомогательный метод для получения IP-адреса клиента
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}