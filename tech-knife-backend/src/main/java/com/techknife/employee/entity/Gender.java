package com.techknife.employee.entity;

/**
 * Enumeration representing gender identity for an employee profile.
 */
public enum Gender {
    MALE,
    FEMALE,
    NON_BINARY,
    OTHER,
    PREFER_NOT_TO_SAY,
    UNSPECIFIED;

    @com.fasterxml.jackson.annotation.JsonCreator
    public static Gender fromString(String value) {
        if (value == null || value.isBlank()) {
            return UNSPECIFIED;
        }
        String normalized = value.trim().toUpperCase().replace(" ", "_").replace("-", "_");
        for (Gender g : values()) {
            if (g.name().equals(normalized)) {
                return g;
            }
        }
        return UNSPECIFIED;
    }
}

