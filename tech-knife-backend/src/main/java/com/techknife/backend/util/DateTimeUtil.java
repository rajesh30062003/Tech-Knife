package com.techknife.backend.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utility class providing helper methods for UTC date handling, ISO-8601 formatting, and timestamp conversions.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {

    public static final String ISO_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String ISO_DATE_PATTERN = "yyyy-MM-dd";

    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter
            .ofPattern(ISO_DATETIME_PATTERN)
            .withZone(ZoneOffset.UTC);

    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter
            .ofPattern(ISO_DATE_PATTERN)
            .withZone(ZoneOffset.UTC);

    /**
     * Get current UTC timestamp as Instant.
     *
     * @return current UTC Instant
     */
    public static Instant nowUtc() {
        return Instant.now();
    }

    /**
     * Format Instant to ISO-8601 UTC string representation.
     *
     * @param instant Instant to format
     * @return formatted ISO UTC string, or null if instant is null
     */
    public static String formatIsoUtc(Instant instant) {
        if (instant == null) {
            return null;
        }
        return ISO_DATETIME_FORMATTER.format(instant);
    }

    /**
     * Format LocalDate to yyyy-MM-dd string.
     *
     * @param date LocalDate to format
     * @return formatted date string, or null if date is null
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Parse ISO date string (yyyy-MM-dd) into LocalDate instance.
     *
     * @param dateStr String representation of date
     * @return parsed LocalDate or null if input is null/empty
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Convert Instant to ZonedDateTime in UTC zone.
     *
     * @param instant Instant
     * @return ZonedDateTime in UTC or null
     */
    public static ZonedDateTime toUtcZonedDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZoneOffset.UTC);
    }

    /**
     * Convert LocalDateTime to Instant using UTC zone offset.
     *
     * @param localDateTime LocalDateTime instance
     * @return Instant or null
     */
    public static Instant toUtcInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    /**
     * Convert java.util.Date to java.time.Instant.
     *
     * @param date Date instance
     * @return Instant or null
     */
    public static Instant toInstant(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant();
    }

    /**
     * Convert java.time.Instant to java.util.Date.
     *
     * @param instant Instant instance
     * @return Date or null
     */
    public static Date toDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Date.from(instant);
    }
}
