package com.techknife.project.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectStatus {
    PLANNED,
    REQUIREMENT_GATHERING,
    DESIGN,
    BACKEND_DEVELOPMENT,
    FRONTEND_DEVELOPMENT,
    FULLSTACK_DEVELOPMENT,
    API_INTEGRATION,
    TESTING,
    QA,
    CODE_REVIEW,
    IN_PROGRESS,
    UAT,
    DEPLOYMENT,
    LIVE,
    MAINTENANCE,
    ON_HOLD,
    BLOCKED,
    COMPLETED,
    ARCHIVED,
    CANCELLED;

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
        if ("IN_PROGRESS".equals(clean) || "INPROGRESS".equals(clean) || "PROGRESS".equals(clean)) {
            return IN_PROGRESS;
        }
        if ("PLANNING".equals(clean)) return PLANNED;
        if ("UI_DESIGN".equals(clean)) return DESIGN;
        if ("DEVELOPMENT".equals(clean)) return IN_PROGRESS;
        if ("ACTIVE".equals(clean)) return LIVE;

        for (ProjectStatus status : values()) {
            if (status.name().equalsIgnoreCase(clean) || status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        return PLANNED;
    }
}
