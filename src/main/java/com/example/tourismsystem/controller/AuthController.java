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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.tourismsystem.service.UserDetailsServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.Map;



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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        System.out.println("=== TEST LOGIN START ===");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Password length: " + password.length());

        // 1. Найти пользователя
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            System.out.println("ERROR: User not found in DB");
            return ResponseEntity.status(404).body("User not found");
        }

        System.out.println("User found: " + user.getUsername());
        System.out.println("Stored hash: " + user.getPassword());
        System.out.println("Hash length: " + user.getPassword().length());
        System.out.println("Is active: " + user.getIsActive());

        // 2. Проверить пароль напрямую
        boolean matches = encoder.matches(password, user.getPassword());
        System.out.println("Password matches (direct): " + matches);

        // 3. Попробовать аутентификацию через AuthenticationManager
        try {
            System.out.println("Trying AuthenticationManager...");
            Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
            Authentication result = authenticationManager.authenticate(auth);
            System.out.println("AuthenticationManager success: " + result.isAuthenticated());
            System.out.println("Authorities: " + result.getAuthorities());
        } catch (Exception e) {
            System.out.println("AuthenticationManager error: " + e.getMessage());
            e.printStackTrace();
        }

        // 4. Проверить UserDetailsService
        try {
            System.out.println("Testing UserDetailsService...");
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("UserDetails loaded: " + userDetails.getUsername());
            System.out.println("UserDetails authorities: " + userDetails.getAuthorities());
            System.out.println("UserDetails enabled: " + userDetails.isEnabled());
        } catch (Exception e) {
            System.out.println("UserDetailsService error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== TEST LOGIN END ===");

        Map<String, Object> response = new HashMap<>();
        response.put("passwordMatches", matches);
        response.put("userFound", true);
        response.put("userActive", user.getIsActive());

        return ResponseEntity.ok(response);
    }

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