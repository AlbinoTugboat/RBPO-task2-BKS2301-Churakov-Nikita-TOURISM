package com.example.tourismsystem.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class PasswordValidatorService {

    private static final int MIN_LENGTH = 6;
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");

    public PasswordValidationResult validatePassword(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return new PasswordValidationResult(false,
                    "Password must be at least " + MIN_LENGTH + " characters long");
        }

        StringBuilder errors = new StringBuilder();

        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.append("Password must contain at least one special character. ");
        }

        if (!DIGIT_PATTERN.matcher(password).find()) {
            errors.append("Password must contain at least one digit. ");
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            errors.append("Password must contain at least one uppercase letter. ");
        }

        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            errors.append("Password must contain at least one lowercase letter. ");
        }

        if (errors.length() > 0) {
            return new PasswordValidationResult(false, errors.toString().trim());
        }

        return new PasswordValidationResult(true, "Password is valid");
    }

    public static class PasswordValidationResult {
        private final boolean valid;
        private final String message;

        public PasswordValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}