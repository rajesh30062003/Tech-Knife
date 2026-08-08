package com.techknife.project.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectStatus {
    PLANNED,
    REQ_GATHERING,
    DESIGN,
    BACKEND_DEV,
    FRONTEND_DEV,
    FULLSTACK_DEV,
    API_INTEGRATION,
    TESTING,
    QA,
    UAT,
    DEPLOYMENT,
    LIVE,
    MAINTENANCE,
    COMPLETED,
    ON_HOLD,
    CANCELLED,
    // Legacy enums for backwards compatibility
    REQUIREMENT_GATHERING,
    BACKEND_DEVELOPMENT,
    FRONTEND_DEVELOPMENT,
    FULLSTACK_DEVELOPMENT,
    CODE_REVIEW,
    IN_PROGRESS,
    BLOCKED,
    ARCHIVED;

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @JsonCreator
    public static ProjectStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PLANNED;
        }
        String clean = value.trim().replaceAll("[-\\s]+", "_").toUpperCase();

        if ("REQUIREMENT_GATHERING".equals(clean)) return REQ_GATHERING;
        if ("BACKEND_DEVELOPMENT".equals(clean)) return BACKEND_DEV;
        if ("FRONTEND_DEVELOPMENT".equals(clean)) return FRONTEND_DEV;
        if ("FULLSTACK_DEVELOPMENT".equals(clean) || "IN_PROGRESS".equals(clean) || "DEVELOPMENT".equals(clean)) return FULLSTACK_DEV;
        if ("CODE_REVIEW".equals(clean) || "REVIEW".equals(clean)) return QA;

        for (ProjectStatus status : values()) {
            if (status.name().equalsIgnoreCase(clean) || status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        return PLANNED;
    }
}
