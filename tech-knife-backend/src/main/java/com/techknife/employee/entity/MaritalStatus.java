package com.techknife.employee.entity;

public enum MaritalStatus {
    SINGLE,
    MARRIED,
    DIVORCED,
    WIDOWED,
    UNSPECIFIED;

    @com.fasterxml.jackson.annotation.JsonCreator
    public static MaritalStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return UNSPECIFIED;
        }
        String normalized = value.trim().toUpperCase().replace(" ", "_");
        for (MaritalStatus ms : values()) {
            if (ms.name().equals(normalized)) {
                return ms;
            }
        }
        return UNSPECIFIED;
    }
}
