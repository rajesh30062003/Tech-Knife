package com.techknife.project.mapper;

import com.techknife.project.entity.ProjectStatus;

public class ProjectStatusMapper {

    public static ProjectStatus toEnum(String statusStr) {
        return ProjectStatus.fromString(statusStr);
    }

    public static String toDisplayLabel(ProjectStatus status) {
        if (status == null) return "Planned";
        String raw = status.name().replace('_', ' ').toLowerCase();
        StringBuilder builder = new StringBuilder();
        for (String word : raw.split(" ")) {
            if (!word.isEmpty()) {
                builder.append(Character.toUpperCase(word.charAt(0)))
                       .append(word.substring(1))
                       .append(" ");
            }
        }
        return builder.toString().trim();
    }
}
