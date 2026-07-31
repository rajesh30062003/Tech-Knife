package com.techknife.employee.entity;

/**
 * Enumeration representing human blood groups along with their standard display values.
 */
public enum BloodGroup {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-"),
    UNSPECIFIED("");

    private final String displayValue;

    BloodGroup(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    @com.fasterxml.jackson.annotation.JsonCreator
    public static BloodGroup fromString(String value) {
        if (value == null || value.isBlank()) {
            return UNSPECIFIED;
        }
        String trimmed = value.trim();
        for (BloodGroup bg : values()) {
            if (bg.name().equalsIgnoreCase(trimmed) || bg.displayValue.equalsIgnoreCase(trimmed)) {
                return bg;
            }
        }
        if (trimmed.equalsIgnoreCase("A+")) return A_POSITIVE;
        if (trimmed.equalsIgnoreCase("A-")) return A_NEGATIVE;
        if (trimmed.equalsIgnoreCase("B+")) return B_POSITIVE;
        if (trimmed.equalsIgnoreCase("B-")) return B_NEGATIVE;
        if (trimmed.equalsIgnoreCase("AB+")) return AB_POSITIVE;
        if (trimmed.equalsIgnoreCase("AB-")) return AB_NEGATIVE;
        if (trimmed.equalsIgnoreCase("O+")) return O_POSITIVE;
        if (trimmed.equalsIgnoreCase("O-")) return O_NEGATIVE;
        return UNSPECIFIED;
    }
}

