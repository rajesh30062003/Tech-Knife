package com.techknife.backend.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * Utility class providing validation methods for emails, mobile numbers, and password complexity criteria.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern MOBILE_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{1,14}$|^[0-9]{10}$|^\\+?[0-9]{1,4}[-\\s]?[0-9]{3,4}[-\\s]?[0-9]{3,4}$"
    );

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!\\-_*?&])(?=\\S+$).{8,}$"
    );

    /**
     * Validate whether an email address matches RFC 5322 standard format.
     *
     * @param email email address string to validate
     * @return true if valid email format, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate whether a mobile number format matches valid telephone formats.
     *
     * @param mobile mobile number string to validate
     * @return true if valid mobile number format, false otherwise
     */
    public static boolean isValidMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return false;
        }
        String sanitized = mobile.replaceAll("[\\s()-]", "");
        return MOBILE_PATTERN.matcher(sanitized).matches();
    }

    /**
     * Validate whether a password meets security complexity policy requirements.
     *
     * @param password password string to validate
     * @return true if password satisfies security constraints, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
