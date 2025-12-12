package com.example.tourismsystem.service;

import com.example.tourismsystem.dto.TokenPairResponse;
import com.example.tourismsystem.entity.SessionStatus;
import com.example.tourismsystem.entity.User;
import com.example.tourismsystem.entity.UserSession;
import com.example.tourismsystem.repository.UserRepository;
import com.example.tourismsystem.repository.UserSessionRepository;
import com.example.tourismsystem.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class TokenPairService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public TokenPairResponse authenticate(String username, String password, String ipAddress, String userAgent) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // Генерируем токены
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Сохраняем сессию
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        UserSession session = new UserSession();
        session.setUser(user);
        session.setRefreshToken(refreshToken);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(getRefreshTokenExpiration())));
        session.setStatus(SessionStatus.ACTIVE);

        userSessionRepository.save(session);

        return new TokenPairResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenPairResponse refreshTokens(String refreshToken, String ipAddress, String userAgent) {
        // Проверяем refresh токен
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Проверяем сессию в БД
        UserSession session = userSessionRepository
                .findByRefreshTokenAndStatus(refreshToken, SessionStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Session not found or not active"));

        // Проверяем не истекла ли сессия
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setStatus(SessionStatus.EXPIRED);
            userSessionRepository.save(session);
            throw new RuntimeException("Refresh token expired");
        }

        // Помечаем старую сессию как обновленную
        session.setStatus(SessionStatus.REFRESHED);
        session.setRevokedAt(LocalDateTime.now());
        userSessionRepository.save(session);

        // Генерируем новую пару токенов
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // Загружаем пользователя с ролями
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Создаем UserDetails из User
        com.example.tourismsystem.service.UserDetailsImpl userDetails =
                com.example.tourismsystem.service.UserDetailsImpl.build(user);

        // Создаем Authentication объект с UserDetails
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Создаем новую сессию
        UserSession newSession = new UserSession();
        newSession.setUser(user);
        newSession.setRefreshToken(newRefreshToken);
        newSession.setIpAddress(ipAddress);
        newSession.setUserAgent(userAgent);
        newSession.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpiration())));
        newSession.setStatus(SessionStatus.ACTIVE);

        userSessionRepository.save(newSession);

        return new TokenPairResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void revokeSession(String refreshToken) {
        UserSession session = userSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setStatus(SessionStatus.REVOKED);
        session.setRevokedAt(LocalDateTime.now());
        userSessionRepository.save(session);
    }

    private long getRefreshTokenExpiration() {
        // Временно возвращаем 7 дней в миллисекундах
        return 7L * 24L * 60L * 60L * 1000L; // 7 дней в миллисекундах
    }
}