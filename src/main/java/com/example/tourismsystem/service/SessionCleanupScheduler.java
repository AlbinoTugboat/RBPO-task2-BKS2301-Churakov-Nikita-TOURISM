package com.example.tourismsystem.service;

import com.example.tourismsystem.repository.UserSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class SessionCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SessionCleanupScheduler.class);

    @Autowired
    private UserSessionRepository userSessionRepository;

    /**
     * Очистка устаревших сессий каждый день в 2:00 ночи
     */
    @Scheduled(cron = "0 0 2 * * ?") // Каждый день в 2:00
    @Transactional
    public void cleanupExpiredSessions() {
        try {
            int expiredCount = userSessionRepository.expireSessions(LocalDateTime.now());
            logger.info("Successfully expired {} inactive sessions", expiredCount);
        } catch (Exception e) {
            logger.error("Error during session cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Дополнительно: очистка каждые 30 минут для тестирования
     * (в продакшене можно отключить)
     */
    @Scheduled(cron = "0 */30 * * * ?") // Каждые 30 минут
    @Transactional
    public void cleanupExpiredSessionsFrequently() {
        try {
            int expiredCount = userSessionRepository.expireSessions(LocalDateTime.now());
            if (expiredCount > 0) {
                logger.debug("Cleaned up {} expired sessions", expiredCount);
            }
        } catch (Exception e) {
            logger.error("Error during frequent session cleanup: {}", e.getMessage(), e);
        }
    }
}