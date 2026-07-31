package com.techknife.employee.entity;

/**
 * Enumeration representing the employment classification type for an employee.
 */
public enum EmploymentType {
    FULL_TIME,
    PART_TIME,
    CONTRACT,
    INTERN,
    PROBATION,
    TEMPORARY,
    UNSPECIFIED;

    @com.fasterxml.jackson.annotation.JsonCreator
    public static EmploymentType fromString(String value) {
        if (value == null || value.isBlank()) {
            return UNSPECIFIED;
        }
        String normalized = value.trim().toUpperCase().replace(" ", "_").replace("-", "_");
        for (EmploymentType et : values()) {
            if (et.name().equals(normalized)) {
                return et;
            }
        }
        return UNSPECIFIED;
    }
}

