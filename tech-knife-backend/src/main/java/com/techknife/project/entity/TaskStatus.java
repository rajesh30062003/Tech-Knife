package com.techknife.project.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    REVIEW("Code Review"),
    IN_REVIEW("Code Review"),
    TESTING("Testing"),
    DONE("Completed"),
    COMPLETED("Completed"),
    BLOCKED("Blocked");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static TaskStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return TODO;
        }
        String clean = value.trim().toUpperCase().replace("-", "_").replace(" ", "_");

        switch (clean) {
            case "TO_DO":
            case "TODO":
            case "BACKLOG":
                return TODO;
            case "IN_PROGRESS":
            case "INPROGRESS":
            case "PROGRESS":
                return IN_PROGRESS;
            case "CODE_REVIEW":
            case "REVIEW":
            case "IN_REVIEW":
                return REVIEW;
            case "TESTING":
            case "TEST":
                return TESTING;
            case "COMPLETED":
            case "DONE":
            case "FINISH":
            case "FINISHED":
                return DONE;
            case "BLOCKED":
            case "BLOCK":
                return BLOCKED;
            default:
                for (TaskStatus status : TaskStatus.values()) {
                    if (status.name().equalsIgnoreCase(clean) || status.displayName.equalsIgnoreCase(value.trim())) {
                        return status;
                    }
                }
                return TODO;
        }
    }
}
