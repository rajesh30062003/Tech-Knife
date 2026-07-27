package com.techknife.backend.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"));

    private DateTimeUtils() {
        // Utility class
    }

    public static String formatIsoUtc(Instant instant) {
        if (instant == null) return null;
        return ISO_FORMATTER.format(instant);
    }
}
