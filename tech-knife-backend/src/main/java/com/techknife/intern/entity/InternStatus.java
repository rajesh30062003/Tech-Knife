package com.techknife.intern.entity;

public enum InternStatus {
    ACTIVE,
    COMPLETED,
    TERMINATED,
    EXTENDED,
    CONVERTED,
    UNSPECIFIED;

    @com.fasterxml.jackson.annotation.JsonCreator
    public static InternStatus fromString(String value) {
        if (value == null || value.isBlank() || "ALL".equalsIgnoreCase(value.trim())) {
            return UNSPECIFIED;
        }
        String normalized = value.trim().toUpperCase().replace(" ", "_").replace("-", "_");
        for (InternStatus status : values()) {
            if (status.name().equals(normalized)) {
                return status;
            }
        }
        return UNSPECIFIED;
    }
}
