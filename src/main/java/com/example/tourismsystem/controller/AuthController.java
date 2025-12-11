package com.example.tourismsystem.controller;

import com.example.tourismsystem.dto.LoginRequest;
import com.example.tourismsystem.dto.SignupRequest;
import com.example.tourismsystem.entity.ERole;
import com.example.tourismsystem.entity.Role;
import com.example.tourismsystem.entity.User;
import com.example.tourismsystem.repository.RoleRepository;
import com.example.tourismsystem.repository.UserRepository;
import com.example.tourismsystem.service.PasswordValidatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
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
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Для Basic Auth этот endpoint может просто возвращать успешный статус
        // В будущем здесь будет генерироваться JWT токен
        return ResponseEntity.ok("Authentication successful - use Basic Auth for API calls");
    }
}