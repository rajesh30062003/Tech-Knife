package com.techknife.project.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent"),
    CRITICAL("Critical");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static TaskPriority fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return MEDIUM;
        }
        String clean = value.trim().toUpperCase();
        switch (clean) {
            case "URGENT":
            case "CRITICAL":
                return HIGH;
            case "HIGH":
                return HIGH;
            case "LOW":
                return LOW;
            case "MEDIUM":
            default:
                return MEDIUM;
        }
    }
}
